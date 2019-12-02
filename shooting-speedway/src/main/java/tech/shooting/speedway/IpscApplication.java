package tech.shooting.speedway;

import tech.shooting.speedway.service.TagService;

import java.io.IOException;

import com.impinj.octane.OctaneSdkException;

public class IpscApplication {

	public static void main(String[] args) throws IOException, OctaneSdkException {
		TagService tagService = new TagService();
		String message = tagService.start("192.168.31.212");
		
		System.out.println(message);
	}
}
