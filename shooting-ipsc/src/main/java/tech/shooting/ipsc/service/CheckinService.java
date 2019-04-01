package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.pojo.TokenUser;
import tech.shooting.ipsc.bean.CheckinBean;
import tech.shooting.ipsc.pojo.CheckIn;
import tech.shooting.ipsc.repository.CheckinRepository;

@Service
public class CheckinService {
	@Autowired
	private CheckinRepository checkinRepository;

	public CheckIn createCheck (TokenUser byToken, CheckinBean bean) {
		return null;
	}
}
