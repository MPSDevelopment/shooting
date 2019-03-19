package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.CompetitionBean;
import tech.shooting.ipsc.controller.PageAble;
import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.repository.CompetitionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CompetitionService {
	@Autowired
	private CompetitionRepository competitionRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private UserRepository userRepository;

	private Competition useBeanUtilsWithOutJudges (CompetitionBean competitionBean, Competition competition) throws BadRequestException {
		BeanUtils.copyProperties(competitionBean, competition, Competition.MATCH_DIRECTOR_FIELD, Competition.RANGE_MASTER_FIELD, Competition.STATS_OFFICER_FIELD);
		if(competitionBean.getRangeMaster() != null) {
			competition.setRangeMaster(userRepository.findById(competitionBean.getRangeMaster()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Range Master id %s", competitionBean.getRangeMaster()))));
		}
		if(competitionBean.getMatchDirector() != null) {
			competition.setMatchDirector(userRepository.findById(competitionBean.getMatchDirector()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Match Director id %s", competitionBean.getMatchDirector()))));
		}
		if(competitionBean.getStatsOfficer() != null) {
			competition.setStatsOfficer(userRepository.findById(competitionBean.getStatsOfficer()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Stats officer id %s", competitionBean.getStatsOfficer()))));
		}
		return competition;
	}

	private void createCompetition (Competition competition) {
		log.info("Create competition with name %s ", competition.getName());
		if(competitionRepository.findByName(competition.getName()) != null) {
			throw new ValidationException(Competition.NAME_FIELD, "Competition with name %s is already exist", competition.getName());
		}
		competition.setActive(true);
		if(competition.getStages() == null) {
			competition.setStages(new ArrayList<>());
		}
		competitionRepository.save(competition);
	}

	public Competition updateCompetition (Long id, CompetitionBean competition) throws BadRequestException {
		Competition existCompetition = useBeanUtilsWithOutJudges(competition, checkCompetition(id));
		BeanUtils.copyProperties(competition, existCompetition);
		return competitionRepository.save(existCompetition);
	}

	public Competition createCompetition (CompetitionBean competitionBean) throws BadRequestException {
		Competition competition = useBeanUtilsWithOutJudges(competitionBean, new Competition());
		createCompetition(competition);
		return competition;
	}

	public Competition checkCompetition (Long id) throws BadRequestException {
		return competitionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitionId %s", id)));
	}

	public void removeCompetition (Long id) throws BadRequestException {
		Competition competition = checkCompetition(id);
		competitionRepository.delete(competition);
	}

	public int getCount () {
		return competitionRepository.findAll().size();
	}

	public List<Competition> getAll () {
		return competitionRepository.findAll();
	}

	public ResponseEntity getCompetitionsByPage (Integer page, Integer size) {
		return PageAble.getPage(page, size, competitionRepository);
	}
}
