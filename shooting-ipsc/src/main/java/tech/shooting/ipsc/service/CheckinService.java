package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.commons.pojo.TokenUser;
import tech.shooting.ipsc.bean.CheckinBean;
import tech.shooting.ipsc.pojo.CheckIn;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.CheckinRepository;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;

import java.util.List;

@Service
public class CheckinService {
	@Autowired
	private CheckinRepository checkinRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	public CheckIn createCheck (TokenUser byToken, CheckinBean bean) throws BadRequestException {
		CheckIn check = new CheckIn();
		check.setPerson(checkPerson(bean)).setStatus(bean.getStatus()).setOfficer(checkUser(byToken));
		check = checkinRepository.save(check);
		return check;
	}

	private User checkUser (TokenUser byToken) throws BadRequestException {
		return userRepository.findById(byToken.getId()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect  user Id %s", byToken.getId())));
	}

	private Person checkPerson (CheckinBean bean) throws BadRequestException {
		return personRepository.findById(bean.getPerson()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect  person Id %s", bean.getPerson())));
	}

	public List<Person> getList (Long id) throws BadRequestException {
		return personRepository.findByDivision(checkDivision(id));
	}

	private Division checkDivision (Long id) throws BadRequestException {
		return divisionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect division bean %s", id)));
	}
}