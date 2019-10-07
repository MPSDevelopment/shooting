package tech.shooting.speedway;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TagReaderApplication {

	static ImpinjReader impinjReader;

	public static void main(String[] args) throws OctaneSdkException {
		impinjReader = new ImpinjReader();
		log.info("Reader has been started");

		impinjReader.connect("192.168.0.1");
	}
}