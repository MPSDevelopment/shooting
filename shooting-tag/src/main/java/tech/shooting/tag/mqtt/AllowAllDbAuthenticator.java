package tech.shooting.tag.mqtt;

import io.moquette.broker.security.IAuthenticator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AllowAllDbAuthenticator implements IAuthenticator {

	public AllowAllDbAuthenticator() {
		log.info("DBAuthenticator created");
	}

	@Override
	public boolean checkValid(String clientId, String username, byte[] password) {
		log.info("Checking user: ClientId %s  username %s  password %s", clientId, username, new String(password));
		return true;
	}
}