package tech.shooting.ipsc.mqtt;

import io.moquette.broker.security.IAuthenticator;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.spring.ApplicationContextWrapper;
import tech.shooting.ipsc.config.IpscMqttSettings;
import tech.shooting.ipsc.security.TokenUtils;

@Slf4j
public class MqttAuthenticator implements IAuthenticator {

	private IpscMqttSettings settings;

	private TokenUtils tokenUtils;

	public MqttAuthenticator() {
		settings = ApplicationContextWrapper.getBean(IpscMqttSettings.class);
		tokenUtils = ApplicationContextWrapper.getBean(TokenUtils.class);
		log.info("DBAuthenticator created, settings are %s", settings);
	}

	@Override
	public boolean checkValid(String clientId, String username, byte[] password) {

		String passwordString = new String(password);
		log.info("Checking user: ClientId %s  username %s  password %s", clientId, username, passwordString);

		if (settings.getGuestLogin().equals(username) && settings.getGuestPassword().equals(passwordString)) {
			return true;
		}

		if (settings.getAdminLogin().equals(username) && settings.getAdminPassword().equals(passwordString)) {
			return true;
		}

		if (!tokenUtils.verifyToken(passwordString)) {
			return false;
		}

		if (username.equals(tokenUtils.getLoginFromToken(passwordString))) {
			return true;
		}

		return false;
	}
}