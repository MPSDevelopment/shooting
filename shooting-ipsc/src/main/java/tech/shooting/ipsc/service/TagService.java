package tech.shooting.ipsc.service;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.TagOpCompleteListener;
import com.impinj.octane.TagOpReport;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.pojo.Tag;

@Service
@Slf4j
public class TagService {

	private ImpinjReader impinjReader;

	public void start() throws OctaneSdkException {

		impinjReader = new ImpinjReader();

		try {
			impinjReader.connect("192.168.31.212");
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

				log.info("On tag report %s", report.getTags().stream().map(item -> {
					Tag tag = new Tag();
					tag.setCrc(item.getCrc());
					tag.setFirstSeenTime(item.getFirstSeenTime().getLocalDateTime().getTime());
					tag.setLastSeenTime(item.getLastSeenTime().getLocalDateTime().getTime());
					return JacksonUtils.getJson(tag);
				}).collect(Collectors.joining(", ")));
			}
		});
	}

	public void stop() throws OctaneSdkException {
		if (impinjReader != null) {
			impinjReader.stop();

			log.info("Reader has been stopped");

			impinjReader.disconnect();

			log.info("Reader has been disconnected");
		}
	}

}
