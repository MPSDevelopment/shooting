package tech.shooting.speedway;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.TagOpCompleteListener;
import com.impinj.octane.TagOpReport;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TagReaderApplication {

	static ImpinjReader impinjReader;

	public static void main(String[] args) throws OctaneSdkException, InterruptedException {
		impinjReader = new ImpinjReader();

		impinjReader.connect("192.168.31.212");

		log.info("Reader has been connected");

		impinjReader.applyDefaultSettings();

		impinjReader.start();

		log.info("Reader has been started");

		impinjReader.setTagOpCompleteListener(new TagOpCompleteListener() {

			@Override
			public void onTagOpComplete(ImpinjReader arg0, TagOpReport report) {
				log.info("Tag report %s", report.getResults());
			}
		});
		
		impinjReader.setTagReportListener(new TagReportListener() {
			
			@Override
			public void onTagReported(ImpinjReader arg0, TagReport report) {
				log.info("Tag report %s", report.getTags());
			}
		});

		Thread.sleep(1000);

		impinjReader.stop();

		log.info("Reader has been stopped");

		impinjReader.disconnect();

		log.info("Reader has been disconnected");
	}
}