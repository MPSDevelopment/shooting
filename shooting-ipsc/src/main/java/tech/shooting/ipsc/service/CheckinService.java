package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.commons.pojo.TokenUser;
import tech.shooting.ipsc.bean.*;
import tech.shooting.ipsc.enums.TypeOfInterval;
import tech.shooting.ipsc.enums.TypeOfPresence;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.*;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	public List<SearchResult> getChecksByDivisionStatusDateInterval (Long divisionId, TypeOfPresence status, OffsetDateTime date, TypeOfInterval interval) throws BadRequestException {
		List<AggBean> result = checkinRepository.findAllByDivisionStatusDateInterval(checkDivision(divisionId), status, date, interval);
		List<SearchResult> toFront = new ArrayList<>();
		for(int i = 0; i < result.size(); i++) {
			Map<String, Long> collect = result.get(i).getStat().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			SearchResult searchResult = new SearchResult();
			searchResult.setPerson(result.get(i).getPerson()).setStatus(collect);
			toFront.add(searchResult);
		}
		return toFront;
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
		result.setStatList(combatNoteByDivisionFromPeriod).setCombat(person).setDate(note.getDate().toLocalDate()).setDivision(division);
		return combatNoteRepository.save(result);
	}

	void checkStatData (Division division, TypeOfPresence status, OffsetDateTime date, TypeOfInterval interval) throws BadRequestException {
		if(checkinRepository.findAllByDivisionStatusDateInterval(division, status, date, interval).size() == 0) {
			throw new BadRequestException(new ErrorMessage("Statistics for this division %s by that period start %s and end %s is not exist", division.getId(), interval.getStart(), interval.getEnd()));
		}
	}

	public List<CombatNote> getCombatNote (Long divisionId) throws BadRequestException {
		Division division = checkDivision(divisionId);
		return combatNoteRepository.findAllByDivision(division);
	}

	public List<String> getInterval () {
		List<String> res = new ArrayList<>();
		TypeOfInterval[] values = TypeOfInterval.values();
		for (int i = 0; i < values.length; i++) {
			res.add(values[i].getState());
		}
		return res;
	}

	public List<SearchResult> getSearch (Long divisionId, String status, String interval, String date) throws BadRequestException {
		TypeOfPresence typeOfPresence = TypeOfPresence.getByState(status);
		TypeOfInterval typeOfInterval = TypeOfInterval.valueOf(interval);
		OffsetDateTime dates = OffsetDateTime.parse(date);
		return getChecksByDivisionStatusDateInterval(divisionId, typeOfPresence, dates, typeOfInterval);
	}
}
