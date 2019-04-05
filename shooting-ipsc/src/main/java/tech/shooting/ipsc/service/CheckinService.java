package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.commons.pojo.TokenUser;
import tech.shooting.ipsc.bean.AggBean;
import tech.shooting.ipsc.bean.CheckinBean;
import tech.shooting.ipsc.bean.CombatNoteBean;
import tech.shooting.ipsc.bean.Stat;
import tech.shooting.ipsc.enums.TypeOfInterval;
import tech.shooting.ipsc.enums.TypeOfPresence;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.*;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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

	@Autowired
	private CombatNoteRepository combatNoteRepository;

	public List<CheckIn> createCheck (TokenUser byToken, List<CheckinBean> beans) throws BadRequestException {
		List<CheckIn> res = new ArrayList<>();
		for(CheckinBean bean : beans) {
			CheckIn check = new CheckIn();
			Person person = checkPerson(bean);
			check.setPerson(person).setStatus(bean.getStatus()).setOfficer(checkUser(byToken));
			check.setDivisionId(person.getDivision().getId());
			check = checkinRepository.save(check);
			res.add(check);
		}
		return res;
	}

	private User checkUser (TokenUser byToken) throws BadRequestException {
		return userRepository.findById(byToken.getId()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect  user Id %s", byToken.getId())));
	}

	private Person checkPerson (CheckinBean bean) throws BadRequestException {
		return personRepository.findById(bean.getPerson()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect  person Id %s", bean.getPerson())));
	}

	private Person checkPerson (Long bean) throws BadRequestException {
		return personRepository.findById(bean).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect  person Id %s", bean)));
	}

	public List<Person> getList (Long id) throws BadRequestException {
		return personRepository.findByDivision(checkDivision(id));
	}

	private Division checkDivision (Long id) throws BadRequestException {
		return divisionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect division bean %s", id)));
	}

	public List<AggBean> getChecksByDivisionStatusDateInterval (Long divisionId, TypeOfPresence status, OffsetDateTime date, TypeOfInterval interval) throws BadRequestException {
		return checkinRepository.findAllByDivisionStatusDateInterval(checkDivision(divisionId), status, date, interval);
	}

	public CombatNote createCombatNote (Long divisionId, CombatNoteBean note) throws BadRequestException {
		Division division = checkDivision(divisionId);
		Person person = checkPerson(note.getCombatId());
		OffsetDateTime date = note.getDate();
		LocalTime localTime = date.toLocalTime();
		TypeOfInterval type;
		if(localTime.isBefore(TypeOfInterval.MIDDLE) || localTime.equals(TypeOfInterval.MIDDLE)) {
			type = TypeOfInterval.MORNING;
		} else {
			type = TypeOfInterval.EVENING;
		}
		checkStatData(division, TypeOfPresence.ALL, note.getDate(), type);
		List<Stat> combatNoteByDivisionFromPeriod = checkinRepository.getCombatNoteByDivisionFromPeriod(division, note.getDate(), type);
		CombatNote result = new CombatNote();
		result.setStatList(combatNoteByDivisionFromPeriod).setCombat(person).setDate(note.getDate()).setDivision(division);
		return combatNoteRepository.save(result);
	}

	void checkStatData (Division division, TypeOfPresence status, OffsetDateTime date, TypeOfInterval interval) throws BadRequestException {
		if(checkinRepository.findAllByDivisionStatusDateInterval(division, status, date, interval).size() == 0) {
			throw new BadRequestException(new ErrorMessage("Statistics for this division %s by that period start %s and end %s is not exist", division.getId(), interval.getStart(), interval.getEnd()));
		}
	}
}
