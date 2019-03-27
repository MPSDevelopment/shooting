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
import tech.shooting.ipsc.bean.CompetitorMark;
import tech.shooting.ipsc.bean.ScoreBean;
import tech.shooting.ipsc.controller.PageAble;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.ClassifierIPSC;
import tech.shooting.ipsc.enums.TypeMarkEnum;
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.CompetitionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.ScoreRepository;
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

	@Autowired
	private ScoreRepository scoreRepository;

	private Competition useBeanUtilsWithOutJudges (CompetitionBean competitionBean, Competition competition) throws BadRequestException {
		BeanUtils.copyProperties(competitionBean, competition, Competition.MATCH_DIRECTOR_FIELD, Competition.RANGE_MASTER_FIELD, Competition.STATS_OFFICER_FIELD);
		if(competitionBean.getRangeMaster() != null) {
			competition.setRangeMaster(userRepository.findById(competitionBean.getRangeMaster())
			                                         .orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Range Master id %s", competitionBean.getRangeMaster()))));
		}
		if(competitionBean.getMatchDirector() != null) {
			competition.setMatchDirector(userRepository.findById(competitionBean.getMatchDirector())
			                                           .orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Match Director id %s", competitionBean.getMatchDirector()))));
		}
		if(competitionBean.getStatsOfficer() != null) {
			competition.setStatsOfficer(userRepository.findById(competitionBean.getStatsOfficer())
			                                          .orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Stats officer id %s", competitionBean.getStatsOfficer()))));
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

	public List<Stage> getStages (Long id) throws BadRequestException {
		Competition competition = checkCompetition(id);
		return competition.getStages();
	}

	public List<Stage> addedAllStages (Long id, List<Stage> toAdded) throws BadRequestException {
		Competition competition = checkCompetition(id);
		List<Stage> stages = competition.getStages();
		stages.addAll(toAdded);
		competition.setStages(stages);
		return competitionRepository.save(competition).getStages();
	}

	public Stage addStage (Long id, Stage toAdded) throws BadRequestException {
		Competition competition = checkCompetition(id);
		competition.getStages().add(toAdded);
		List<Stage> stages = competitionRepository.save(competition).getStages();
		int index = 0;
		for(int i = 0; i < stages.size(); i++) {
			if(stages.get(i).getName().equals(toAdded.getName()) && stages.get(i).getMaximumPoints().equals(toAdded.getMaximumPoints()) && stages.get(i).getTargets().equals(toAdded.getTargets())) {
				index = i;
			}
		}
		return stages.get(index);
	}

	public Stage getStage (Long competitionId, Long stageId) throws BadRequestException {
		return checkStage(checkCompetition(competitionId), stageId);
	}

	private Stage checkStage (Competition competition, Long stageId) throws BadRequestException {
		return competition.getStages().stream().filter((i) -> i.getId().equals(stageId)).findAny().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect stageId %s", stageId)));
	}

	public void deleteStage (Long competitionId, Long stageId) throws BadRequestException {
		checkCompetition(competitionId);
		competitionRepository.pullStageFromCompetition(competitionId, stageId);
	}

	public Stage updateStage (Long competitionId, Long stageId, Stage stage) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		Stage stageFromDB = checkStage(competition, stageId);
		BeanUtils.copyProperties(stage, stageFromDB);
		List<Stage> stages = competition.getStages();
		stages.remove(stageFromDB);
		stages.add(stageFromDB);
		competitionRepository.save(competition.setStages(stages));
		return stageFromDB;
	}

	public Competitor addedCompetitor (Long id, Competitor competitor) throws BadRequestException {
		Competition competition = checkCompetition(id);
		checkPerson(competitor.getPerson().getId());
		Competitor competitorToDB = new Competitor();
		BeanUtils.copyProperties(competitor.setActive(false), competitorToDB);
		return saveAndReturn(competition, competitorToDB, true);
	}

	private Person checkPerson (Long id) throws BadRequestException {
		return personRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitorId %s", id)));
	}

	public void deleteCompetitor (Long id, Long competitorId) throws BadRequestException {
		checkCompetition(id);
		competitionRepository.pullCompetitorFromCompetition(id, competitorId);
	}

	public Competitor getCompetitor (Long id, Long competitorId) throws BadRequestException {
		return checkCompetitor(checkCompetition(id).getCompetitors(), competitorId);
	}

	public Competitor updateCompetitor (Long id, Long competitorId, Competitor competitor) throws BadRequestException {
		Competition competition = checkCompetition(id);
		Competitor competitorFromDB = checkCompetitor(competition.getCompetitors(), competitorId);
		BeanUtils.copyProperties(competitor, competitorFromDB);
		return saveAndReturn(competition, competitorFromDB, false);
	}

	public List<Competitor> addedAllCompetitors (Long id, List<Long> competitorsIdList) throws BadRequestException {
		Competition competition = checkCompetition(id);
		List<Competitor> competitors = competition.getCompetitors();
		competitors.clear();
		for(Long idPerson : competitorsIdList) {
			Person person = checkPerson(idPerson);
			Competitor competitor = new Competitor();
			competitor.setPerson(person).setName(person.getName());
			competitors.add(competitor);
		}
		competition.setCompetitors(competitors);
		return competitionRepository.save(competition).getCompetitors();
	}

	private Competitor checkCompetitor (List<Competitor> competitors, Long competitorId) throws BadRequestException {
		return competitors.stream().filter(competitor -> competitor.getId().equals(competitorId)).findFirst().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitor id $s", competitorId)));
	}

	private Competitor saveAndReturn (Competition competition, Competitor competitorToDB, boolean flag) {
		List<Competitor> competitors = competition.getCompetitors();
		if(flag) {
			competitors.add(competitorToDB);
		} else {
			int indexF = 0;
			for(int i = 0; i < competitors.size(); i++) {
				if(competitors.get(i).getId().equals(competitorToDB.getId())) {
					indexF = i;
					break;
				}
			}
			competitors.set(indexF, competitorToDB);
		}
		competition.setCompetitors(competitors);
		competitors = competitionRepository.save(competition).getCompetitors();
		int index = 0;
		for(int i = 0; i < competitors.size(); i++) {
			if(competitors.get(i).getName().equals(competitorToDB.getName()) && competitors.get(i).getPerson().equals(competitorToDB.getPerson()) &&
			   (competitors.get(i).getRfidCode().equals(competitorToDB.getRfidCode()) || competitors.get(i).getNumber().equals(competitorToDB.getNumber()))) {
				index = i;
			}
		}
		return competitors.get(index);
	}

	public Competitor addedMarkToCompetitor (Long competitionId, Long competitorId, CompetitorMark competitorMark) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		Competitor competitor = checkCompetitor(competition.getCompetitors(), competitorId);
		if(competitorMark.getType().equals(TypeMarkEnum.RFID)) {
			competitor.setRfidCode(competitorMark.getMark());
		} else {
			competitor.setNumber(competitorMark.getMark());
		}
		competitor.setActive(competitorMark.isActive()).setName(competitorMark.getName());
		return saveAndReturn(competition, competitor, false);
	}

	public List<LevelBean> getLevelEnum () {
		return ClassificationBreaks.getList();
	}

	public List<Stage> getEnum () {
		return ClassifierIPSC.getListStage();
	}

	public List<TypeWeapon> getListTypeWeapon () {
		return WeaponTypeEnum.getList();
	}

	public Score addedScoreRow (Long competitionId, Long stageId, ScoreBean scoreBean) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		checkStage(competition, stageId);
		return addedScoreWithoutCheck(competition, stageId, scoreBean);
	}

	private Competitor checkCompetitorByRfidCode (Competition competition, String mark) throws BadRequestException {
		return competition.getCompetitors().stream().filter(member -> member.getRfidCode().equals(mark)).findFirst().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitor rfid $s", mark)));
	}

	private Competitor checkCompetitorByNumberCode (Competition competition, String mark) throws BadRequestException {
		return competition.getCompetitors().stream().filter(member -> member.getNumber().equals(mark)).findFirst().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitor number $s", mark)));
	}

	public List<Score> addedBulk (Long competitionId, Long stageId, List<ScoreBean> scoreBean) throws BadRequestException {
		List<Score> result = new ArrayList<>();
		Competition competition = checkCompetition(competitionId);
		checkStage(competition, stageId);
		for(ScoreBean score : scoreBean) {
			result.add(addedScoreWithoutCheck(competition, stageId, score));
		}
		return result;
	}

	private Score addedScoreWithoutCheck (Competition competition, Long stageId, ScoreBean score) throws BadRequestException {
		Competitor competitor;
		if(score.getType().equals(TypeMarkEnum.RFID)) {
			competitor = checkCompetitorByRfidCode(competition, score.getMark());
		} else {
			competitor = checkCompetitorByNumberCode(competition, score.getMark());
		}
		Score scoreResult = new Score().setStageId(stageId)
		                               .setPersonId(competitor.getPerson().getId())
		                               .setScore(score.getScore())
		                               .setTimeOfExercise(score.getTimeOfExercise())
		                               .setDisqualificationReason(score.getDisqualificationReason());
		scoreResult = scoreRepository.save(scoreResult);
		List<Score> result = competitor.getResult();
		result.add(scoreResult);
		competitor.setResult(result);
		saveAndReturn(competition, competitor, false);
		return scoreResult;
	}
}
