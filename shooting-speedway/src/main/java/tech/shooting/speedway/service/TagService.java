package tech.shooting.speedway.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.ReportConfig;
import com.impinj.octane.Settings;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;

import tech.shooting.speedway.pojo.Tag;

public class TagService {

	private ImpinjReader impinjReader;

	private Map<String, Tag> map = new HashMap<>();

	public TagService() {

	}

	public String start(String address) throws OctaneSdkException {

		clear();
		impinjReader = new ImpinjReader();

		try {
			impinjReader.connect(address);
		} catch (OctaneSdkException e) {
			return "Cannot connect to the address " + address;
		}

		Settings settings = impinjReader.queryDefaultSettings();
		ReportConfig report = settings.getReport();
		report.setIncludeAntennaPortNumber(true);
		report.setIncludeSeenCount(true);
		report.setIncludeCrc(true);
		report.setIncludeFirstSeenTime(true);
		report.setIncludeLastSeenTime(true);

		impinjReader.applySettings(settings);

		impinjReader.start();

//		log.info("Reader has been started");

		impinjReader.setTagReportListener(new TagReportListener() {

			@Override
			public void onTagReported(ImpinjReader arg0, TagReport report) {
//					log.info("Tag report %s", report.getTags().stream().map(item-> item.getCrc()).collect(Collectors.toList()));
//					log.info("On tag report %s", report.getTags().stream().map(item-> JacksonUtils.getFullJson(item)).collect(Collectors.joining(", ")));

				List<String> list = new ArrayList<String>();

				report.getTags().forEach(item -> {
					list.add(String.valueOf(item.getCrc()));

					Tag tag = new Tag();
					tag.setCode(String.valueOf(item.getCrc()));
					tag.setFirstSeenTime(item.getFirstSeenTime().getLocalDateTime().getTime());
					tag.setLastSeenTime(item.getLastSeenTime().getLocalDateTime().getTime());

					if (map.get(tag.getCode()) == null) {
						map.put(tag.getCode(), tag);
					}
				});

				// remove tag from map if it is not in tagReport
				map.forEach((code, item) -> {
					if (!list.contains(code)) {
						map.remove(code);
					}
				});
			}
		});

		return "Started";
	}

	public void stop() throws OctaneSdkException {
		if (impinjReader != null) {
			impinjReader.stop();
			impinjReader.disconnect();
		}
	}

	public void clear() {
		if (map != null) {
			map.clear();
			map = new HashMap<>();
		}
	}

	public Map<String, Tag> getTags() {
		return map;
	}

}
