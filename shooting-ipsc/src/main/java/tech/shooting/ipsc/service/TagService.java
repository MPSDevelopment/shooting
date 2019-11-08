package tech.shooting.ipsc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.impinj.octane.ConnectionCloseEvent;
import com.impinj.octane.ConnectionCloseListener;
import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.TagOpCompleteListener;
import com.impinj.octane.TagOpReport;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.event.RunningOnConnectEvent;
import tech.shooting.ipsc.event.RunningOnDisconnectEvent;
import tech.shooting.ipsc.event.TagDetectedEvent;
import tech.shooting.ipsc.event.TagRestartEvent;
import tech.shooting.ipsc.event.TagUndetectedEvent;
import tech.shooting.ipsc.pojo.Tag;

@Service
@Slf4j
public class TagService {

	private ImpinjReader impinjReader;

	private Map<String, Tag> map = new HashMap<>();

	@Autowired
	private SettingsService settingsService;

	public void start() throws OctaneSdkException {

		impinjReader = new ImpinjReader();

		var serverSettings = settingsService.getSettings();

		try {
			// "192.168.31.212"
			impinjReader.connect(serverSettings.getTagServiceIp());
		} catch (OctaneSdkException e) {
			log.error("Cannot start Tag service : %s", e.getMessage());
			return;
		}

		log.info("Reader has been connected");

		var settings = impinjReader.queryDefaultSettings();
		var report = settings.getReport();
		report.setIncludeAntennaPortNumber(true);
		report.setIncludeSeenCount(true);
		report.setIncludeCrc(true);
		report.setIncludeFirstSeenTime(true);
		report.setIncludeLastSeenTime(true);

		impinjReader.applySettings(settings);

		impinjReader.start();

		log.info("Reader has been started");

		EventBus.subscribe(this);
		
		EventBus.publishEvent(new RunningOnConnectEvent());

		impinjReader.setTagOpCompleteListener(new TagOpCompleteListener() {

			@Override
			public void onTagOpComplete(ImpinjReader arg0, TagOpReport report) {
				log.info("Tag report complete %s", report.getResults());
			}
		});

		impinjReader.setTagReportListener(new TagReportListener() {

			@Override
			public void onTagReported(ImpinjReader arg0, TagReport report) {
//					log.info("Tag report %s", report.getTags().stream().map(item-> item.getCrc()).collect(Collectors.toList()));
//					log.info("On tag report %s", report.getTags().stream().map(item-> JacksonUtils.getFullJson(item)).collect(Collectors.joining(", ")));

				var list = new ArrayList<>();

				report.getTags().forEach(item -> {
					list.add(String.valueOf(item.getCrc()));

					Tag tag = new Tag();
					tag.setCode(String.valueOf(item.getCrc()));
					tag.setFirstSeenTime(item.getFirstSeenTime().getLocalDateTime().getTime());
					tag.setLastSeenTime(item.getLastSeenTime().getLocalDateTime().getTime());

					if (map.get(tag.getCode()) == null) {
						EventBus.publishEvent(new TagDetectedEvent(tag.getCode()).setTime(tag.getFirstSeenTime()));
						map.put(tag.getCode(), tag);
					}
				});

				// remove tag from map if it is not in tagReport
				map.forEach((code, item) -> {
					if (!list.contains(code)) {
						EventBus.publishEvent(new TagUndetectedEvent(code));
						map.remove(code);
					}
				});

				log.info("On tag report %s", report.getTags().stream().map(item -> {
					Tag tag = new Tag();
					tag.setCode(String.valueOf(item.getCrc()));
					tag.setFirstSeenTime(item.getFirstSeenTime().getLocalDateTime().getTime());
					tag.setLastSeenTime(item.getLastSeenTime().getLocalDateTime().getTime());
					return JacksonUtils.getJson(tag);
				}).collect(Collectors.joining(", ")));
			}
		});
		
		impinjReader.setConnectionCloseListener(new ConnectionCloseListener() {
			
			@Override
			public void onConnectionClose(ImpinjReader arg0, ConnectionCloseEvent arg1) {
				log.info("Reader connection has been closed");
				EventBus.publishEvent(new RunningOnDisconnectEvent());
			}
		});
	}

	@Handler
	public void handle(TagDetectedEvent event) {

	}
	
	@Handler
	public void handle(TagRestartEvent event) throws OctaneSdkException {
		
		stop();
		start();
	}

	public void stop() throws OctaneSdkException {
		if (impinjReader != null) {
			impinjReader.stop();

			log.info("Reader has been stopped");

			impinjReader.disconnect();

			log.info("Reader has been disconnected");
			
			EventBus.publishEvent(new RunningOnDisconnectEvent());
		}
	}

}
