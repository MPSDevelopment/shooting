package tech.shooting.ipsc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.bean.CompetitionBean;
import tech.shooting.ipsc.bean.CompetitorMark;
import tech.shooting.ipsc.bean.CompetitorMarks;
import tech.shooting.ipsc.bean.RatingBean;
import tech.shooting.ipsc.bean.ScoreBean;
import tech.shooting.ipsc.controller.Pageable;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.ClassifierIPSC;
import tech.shooting.ipsc.enums.DisqualificationEnum;
import tech.shooting.ipsc.enums.TypeMarkEnum;
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.mqtt.event.CompetitionUpdatedEvent;
import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Competitor;
import tech.shooting.ipsc.pojo.LevelBean;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Score;
import tech.shooting.ipsc.pojo.Stage;
import tech.shooting.ipsc.repository.CompetitionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.RankRepository;
import tech.shooting.ipsc.repository.ScoreRepository;
import tech.shooting.ipsc.repository.UserRepository;

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

	@Autowired
	private RankRepository rankRepository;

	private Competition useBeanUtilsWithOutJudges(CompetitionBean competitionBean, Competition competition) throws BadRequestException {
		BeanUtils.copyProperties(competitionBean, competition, Competition.MATCH_DIRECTOR_FIELD, Competition.RANGE_MASTER_FIELD, Competition.STATS_OFFICER_FIELD, Competition.COMPETITORS_FIELD, Competition.STAGES_FIELD);
		if (competitionBean.getRangeMaster() != null) {
			competition.setRangeMaster(userRepository.findById(competitionBean.getRangeMaster()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Range Master id %s", competitionBean.getRangeMaster()))));
		}
		if (competitionBean.getMatchDirector() != null) {
			competition.setMatchDirector(userRepository.findById(competitionBean.getMatchDirector()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Match Director id %s", competitionBean.getMatchDirector()))));
		}
		if (competitionBean.getStatsOfficer() != null) {
			competition.setStatsOfficer(userRepository.findById(competitionBean.getStatsOfficer()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Stats officer id %s", competitionBean.getStatsOfficer()))));
		}
		competition.getStages().forEach(stage -> stage.setAllTargets(stage.getTargets() + stage.getPopper() + stage.getNoShoots()));
		if (competitionBean.getClazz() != null) {
			competition.setClazz(competitionBean.getClazz());
		}
		return competition;
	}

	private Competition createCompetition(Competition competition) {
		log.info("Create competition with name %s ", competition.getName());
		if (competitionRepository.findByName(competition.getName()) != null) {
			throw new ValidationException(Competition.NAME_FIELD, "Competition with name %s is already exist", competition.getName());
		}
		competition.setActive(true);
		if (competition.getStages() == null) {
			competition.setStages(new ArrayList<>());
		}
		return competitionRepository.save(competition);
	}

	public Competition updateCompetition(Long id, CompetitionBean competition) throws BadRequestException {
		Competition competitionFromDb = checkCompetition(id);
		checkCompetitionActive(competitionFromDb);
		Competition existCompetition = useBeanUtilsWithOutJudges(competition, competitionFromDb);
//		// Do not change competitors and stages
//		BeanUtils.copyProperties(competition, existCompetition, Competition.COMPETITORS_FIELD);

		EventBus.publishEvent(new CompetitionUpdatedEvent(id, "Competition %s updated", competitionFromDb.getName()));
		log.info("Competition %s updated", competitionFromDb.getName());

		return competitionRepository.save(existCompetition);
	}

	public Competition createCompetition(CompetitionBean competitionBean) throws BadRequestException {
		Competition competition = useBeanUtilsWithOutJudges(competitionBean, new Competition());
		var competiton = createCompetition(competition);
		EventBus.publishEvent(new CompetitionUpdatedEvent(competiton.getId(), "Competition %s created", competiton.getName()));
		log.info("Competition %s created", competiton.getName());
		return competition;
	}

	public Competition checkCompetition(Long id) throws BadRequestException {
		return competitionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitionId %s", id)));
	}

	public void removeCompetition(Long id) throws BadRequestException {
		Competition competition = checkCompetition(id);
		competitionRepository.delete(competition);
	}

	public int getCount() {
		return competitionRepository.findAll().size();
	}

	public List<Competition> getAll() {
		return competitionRepository.findAll();
	}

	public ResponseEntity getCompetitionsByPage(Integer page, Integer size) {
		return Pageable.getPage(page, size, competitionRepository);
	}

	public List<Stage> getStages(Long id) throws BadRequestException {
		Competition competition = checkCompetition(id);
		competition.getStages().forEach(stage -> stage.setAllTargets(stage.getTargets() + stage.getPopper() + stage.getNoShoots()));
		return competition.getStages();
	}

	public List<Stage> addedAllStages(Long id, List<Stage> toAdded) throws BadRequestException {
		Competition competition = checkCompetition(id);
		checkCompetitionActive(competition);
		List<Stage> stages = competition.getStages();
		stages.forEach(stage -> stage.setAllTargets(stage.getTargets() + stage.getPopper() + stage.getNoShoots()));
		stages.addAll(toAdded);
		competition.setStages(stages);
		return competitionRepository.save(competition).getStages();
	}

	public Stage addStage(Long id, Stage stageToAdd) throws BadRequestException {
		Competition competition = checkCompetition(id);
		checkCompetitionActive(competition);
		if (StringUtils.isBlank(stageToAdd.getName())) {
			stageToAdd.setName(Integer.valueOf(competition.getStages().size() + 1).toString());
		}
		stageToAdd.setAllTargets(stageToAdd.getTargets() + stageToAdd.getPopper() + stageToAdd.getNoShoots());
		competition.getStages().add(stageToAdd);
		List<Stage> stages = competitionRepository.save(competition).getStages();
		int index = 0;
		for (int i = 0; i < stages.size(); i++) {
			if (stages.get(i).getName().equals(stageToAdd.getName()) && stages.get(i).getMaximumPoints().equals(stageToAdd.getMaximumPoints()) && stages.get(i).getTargets().equals(stageToAdd.getTargets())) {
				index = i;
			}
		}

		EventBus.publishEvent(new CompetitionUpdatedEvent(id, "Competition %s stage added", competition.getName()));

		return stages.get(index);
	}

	public Stage getStage(Long competitionId, Long stageId) throws BadRequestException {
		var stage = checkStage(checkCompetition(competitionId), stageId);
		stage.setAllTargets(stage.getTargets() + stage.getPopper() + stage.getNoShoots());
		return stage;
	}

	private Stage checkStage(Competition competition, Long stageId) throws BadRequestException {
		var stage = competition.getStages().stream().filter((i) -> i.getId().equals(stageId)).findFirst().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect stageId %s", stageId)));
		stage.setAllTargets(stage.getTargets() + stage.getPopper() + stage.getNoShoots());
		return stage;
	}

	public void deleteStage(Long competitionId, Long stageId) throws BadRequestException {
		checkCompetition(competitionId);
		competitionRepository.pullStageFromCompetition(competitionId, stageId);

		EventBus.publishEvent(new CompetitionUpdatedEvent(competitionId, "Competition %s stage deleted", competitionId));
	}

	public Stage updateStage(Long competitionId, Long stageId, Stage stage) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		checkCompetitionActive(competition);
		Stage stageFromDB = checkStage(competition, stageId);
		stage.setAllTargets(stage.getTargets() + stage.getPopper() + stage.getNoShoots());
		BeanUtils.copyProperties(stage, stageFromDB);
		List<Stage> stages = competition.getStages();
		stages.remove(stageFromDB);
		stages.add(stageFromDB);
		competitionRepository.save(competition.setStages(stages));

		EventBus.publishEvent(new CompetitionUpdatedEvent(competitionId, "Competition %s stage updated", competitionId));

		return stageFromDB;
	}

	public Competitor addedCompetitor(Long id, Competitor competitor) throws BadRequestException {
		Competition competition = checkCompetition(id);
		checkCompetitionActive(competition);
		checkPerson(competitor.getPerson().getId());
		Competitor competitorToDB = new Competitor();
		BeanUtils.copyProperties(competitor.setActive(false), competitorToDB);
		Competitor saveAndReturn = saveAndReturn(competition, competitorToDB, true);

		EventBus.publishEvent(new CompetitionUpdatedEvent(id, "Competition %s competitor added", competition.getName()));

		return saveAndReturn;
	}

	private Person checkPerson(Long id) throws BadRequestException {
		return personRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitorId %s", id)));
	}

	public void deleteCompetitor(Long id, Long competitorId) throws BadRequestException {
		var competition = checkCompetition(id);
		competitionRepository.pullCompetitorFromCompetition(id, competitorId);

		EventBus.publishEvent(new CompetitionUpdatedEvent(id, "Competition %s competitor deleted", competition.getId()));
	}

	public Competitor getCompetitor(Long id, Long competitorId) throws BadRequestException {
		return checkCompetitor(checkCompetition(id).getCompetitors(), competitorId);
	}

	public Competitor getCompetitor(Long id, String mark) throws BadRequestException {
		return checkCompetitor(checkCompetition(id).getCompetitors(), mark);
	}

	public Competitor updateCompetitor(Long id, Long competitorId, Competitor competitor) throws BadRequestException {
		Competition competition = checkCompetition(id);
		checkCompetitionActive(competition);
		Competitor competitorFromDB = checkCompetitor(competition.getCompetitors(), competitorId);
		BeanUtils.copyProperties(competitor, competitorFromDB);
		Competitor saveAndReturn = saveAndReturn(competition, competitorFromDB, false);

		EventBus.publishEvent(new CompetitionUpdatedEvent(id, "Competition %s competitor updated", competition.getId()));

		return saveAndReturn;
	}

	public List<Competitor> addedAllCompetitors(Long id, List<Long> competitorsIdList) throws BadRequestException {
		Competition competition = checkCompetition(id);
		checkCompetitionActive(competition);
		List<Competitor> competitors = competition.getCompetitors();
		competitors.clear();
		for (Long idPerson : competitorsIdList) {
			Person person = checkPerson(idPerson);
			Competitor competitor = new Competitor();
			competitor.setPerson(person).setName(person.getName());
			competitors.add(competitor);
		}
		competition.setCompetitors(competitors);
		competition = competitionRepository.save(competition);

		EventBus.publishEvent(new CompetitionUpdatedEvent(id, "Competition %s competitors updated", competition.getId()));

		return competition.getCompetitors();
	}

	private Competitor checkCompetitor(List<Competitor> competitors, Long competitorId) throws BadRequestException {
		return competitors.stream().filter(competitor -> competitor.getId().equals(competitorId)).findFirst().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitor id $s", competitorId)));
	}

	private Competitor checkCompetitor(List<Competitor> competitors, String mark) throws BadRequestException {
		return competitors.stream().filter(competitor -> competitor.getRfidCode().equals(mark)).findFirst().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitor id $s", mark)));
	}

	private Competitor saveAndReturn(Competition competition, Competitor competitorToDB, boolean flag) throws BadRequestException {
		checkCompetitionActive(competition);
		List<Competitor> competitors = competition.getCompetitors();
		if (flag) {
			competitors.add(competitorToDB);
		} else {
			int indexF = 0;
			for (int i = 0; i < competitors.size(); i++) {
				if (competitors.get(i).getId().equals(competitorToDB.getId())) {
					indexF = i;
					break;
				}
			}
			competitors.set(indexF, competitorToDB);
		}
		competition.setCompetitors(competitors);
		competitors = competitionRepository.save(competition).getCompetitors();
		int index = 0;
		for (int i = 0; i < competitors.size(); i++) {
			if (competitors.get(i).getName().equals(competitorToDB.getName()) && competitors.get(i).getPerson().equals(competitorToDB.getPerson())
					&& ((competitors.get(i).getRfidCode() != null && competitors.get(i).getRfidCode().equals(competitorToDB.getRfidCode()))
							|| (competitors.get(i).getNumber() != null && competitors.get(i).getNumber().equals(competitorToDB.getNumber())))) {
				index = i;
			}
		}
		return competitors.get(index);
	}

	public CompetitorMark checkMarkToCompetitor(Long competitionId, Long competitorId, CompetitorMark competitorMark) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		checkCompetitionActive(competition);

		checkIfMarkOccupied(competition, competitorId, competitorMark);

		return competitorMark;
	}

	public Competitor addedMarkToCompetitor(Long competitionId, Long competitorId, CompetitorMark competitorMark) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		checkCompetitionActive(competition);

		checkIfMarkOccupied(competition, competitorId, competitorMark);

		Competitor competitor = checkCompetitor(competition.getCompetitors(), competitorId);
		if (competitorMark.getType().equals(TypeMarkEnum.RFID)) {
			competitor.setRfidCode(competitorMark.getMark());
		} else {
			competitor.setNumber(competitorMark.getMark());
		}
		competitor.setActive(competitorMark.isActive()).setName(competitorMark.getName());
		return saveAndReturn(competition, competitor, false);
	}

	private void checkIfMarkOccupied(Competition competition, Long competitorId, CompetitorMark competitorMark) throws BadRequestException {
		for (var competitor : competition.getCompetitors()) {
			if (!competitor.getId().equals(competitorId)) {
				if (competitorMark.getType().equals(TypeMarkEnum.RFID)) {
					if (competitor.getRfidCode() != null && competitor.getRfidCode().equalsIgnoreCase(competitorMark.getMark())) {
						throw new BadRequestException(new ErrorMessage("Rfid mark already exists %s", competitorMark.getMark()));
					}
				} else {
					if (competitor.getNumber() != null && competitor.getNumber().equalsIgnoreCase(competitorMark.getMark())) {
						throw new BadRequestException(new ErrorMessage("Number already exists %s", competitorMark.getMark()));
					}
				}
			}
		}
	}

	public Competitor addedMarkToCompetitor(Long competitionId, Long competitorId, CompetitorMarks competitorMark) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		checkCompetitionActive(competition);
		Competitor competitor = checkCompetitor(competition.getCompetitors(), competitorId);
		if (StringUtils.isNotEmpty(competitorMark.getRfid())) {
			competitor.setRfidCode(competitorMark.getRfid());
		}
		if (StringUtils.isNotEmpty(competitorMark.getNumber())) {
			competitor.setNumber(competitorMark.getNumber());
		}
		competitor.setActive(competitorMark.isActive()).setName(competitorMark.getName());
		return saveAndReturn(competition, competitor, false);
	}

	public List<LevelBean> getLevelEnum() {
		return ClassificationBreaks.getList();
	}

	public List<Stage> getEnum() {
		return ClassifierIPSC.getListStage();
	}

	public WeaponTypeEnum[] getListTypeWeapon() {
		return WeaponTypeEnum.values();
	}

	public Score addedScoreRow(Long competitionId, Long stageId, ScoreBean scoreBean) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		checkCompetitionActive(competition);
		checkStage(competition, stageId);
		return addedScoreWithoutCheck(competition, stageId, scoreBean);
	}

	private void checkCompetitionActive(Competition competition) throws BadRequestException {
		if (!competition.isActive()) {
			throw new BadRequestException(new ErrorMessage("Cannot modify an archived competition, active = %s", competition.isActive()));
		}
	}

	public Competitor checkCompetitorByRfidCode(Competition competition, String mark) throws BadRequestException {
		return competition.getCompetitors().stream().filter(member -> mark.equals(member.getRfidCode())).findFirst().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitor rfid %s", mark)));
	}

	private Competitor checkCompetitorByNumberCode(Competition competition, String mark) throws BadRequestException {
		return competition.getCompetitors().stream().filter(member -> mark.equals(member.getNumber())).findFirst().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitor number %s", mark)));
	}

	public List<Score> addedBulk(Long competitionId, Long stageId, List<ScoreBean> scoreBean) throws BadRequestException {

		log.info("Adding score for competition %s, stage %s, json is %s", competitionId, stageId, JacksonUtils.getPrettyJson(scoreBean));

		List<Score> result = new ArrayList<>();
		Competition competition = checkCompetition(competitionId);
		checkCompetitionActive(competition);
		checkStage(competition, stageId);
		for (ScoreBean score : scoreBean) {
			Score newScore = addedScoreWithoutCheck(competition, stageId, score);
			if (newScore != null) {
				if (result.contains(newScore)) {
					log.error("Duplicate score %s", newScore);
				} else {
					result.add(newScore);
				}
			}
		}
		return result;
	}

	private Score addedScoreWithoutCheck(Competition competition, Long stageId, ScoreBean score) throws BadRequestException {
		Competitor competitor;
		if (score.getType().equals(TypeMarkEnum.RFID)) {
			competitor = checkCompetitorByRfidCode(competition, score.getMark());
		} else {
			competitor = checkCompetitorByNumberCode(competition, score.getMark());
		}

		Score scoreResult = new Score().setStageId(stageId).setPersonId(competitor.getPerson().getId());

		log.info("Adding score %s to competitor id %s", score.getScore(), competitor.getId());

		if (scoreRepository.findByPersonIdAndStageId(competitor.getId(), stageId) != null) {
			return null;
		}
		if (StringUtils.isNotBlank(score.getDisqualificationReason())) {
			switch (score.getDisqualificationReason()) {
			case "DISQUALIFICATION":
				scoreResult.setDisqualificationReason(DisqualificationEnum.DISQUALIFICATION.getType());
				break;
			case "ABSENT":
				scoreResult.setDisqualificationReason(DisqualificationEnum.ABSENT.getType());
				break;
			case "INJURED":
				scoreResult.setDisqualificationReason(DisqualificationEnum.INJURED.getType());
				break;
			case "BROKEN_RULE":
				scoreResult.setDisqualificationReason(DisqualificationEnum.BROKEN_RULE.getType());
				break;
			default:
				scoreResult.setDisqualificationReason(score.getDisqualificationReason());
			}

			scoreResult.setScore(0).setTimeOfExercise(0);

		} else {
			scoreResult.setScore(score.getScore()).setTimeOfExercise(score.getTimeOfExercise());
		}
		scoreResult = scoreRepository.save(scoreResult);
		List<Score> result = competitor.getResult();
		result.add(scoreResult);
		competitor.setResult(result);
		saveAndReturn(competition, competitor, false);
		return scoreResult;
	}

	public List<String> getListTypeMark() {
		return TypeMarkEnum.getList();
	}

	public List<Score> getScoreList(Long competitionId, Long stageId) throws BadRequestException {
		checkStage(checkCompetition(competitionId), stageId);
		// my fault score save stageId not DBref, because don't save to DB
		return scoreRepository.findAllByStageId(stageId);
	}

	public List<Score> getScoreList(Long competitionId) throws BadRequestException {
		var competition = checkCompetition(competitionId);
		// my fault score save stageId not DBref, because don't save to DB
		return scoreRepository.findByStageIdIn(competition.getStages().stream().map(item -> item.getId()).collect(Collectors.toList()));
	}

	public List<RatingBean> getRating(Long competitionId) throws BadRequestException {
		var competition = checkCompetition(competitionId);
		// my fault score save stageId not DBref, because don't save to DB
		var scores = scoreRepository.findByStageIdIn(competition.getStages().stream().map(item -> item.getId()).collect(Collectors.toList()));

		return convertScoresToRating(competition, filterScores(scores));
	}

	public List<Score> filterScores(List<Score> scores) {
		// need to replace this code
		var result = new ArrayList<Score>();
		for (var score : scores) {
			if (!result.contains(score)) {
				result.add(score);
			}
		}
		return result;
	}

	public List<RatingBean> convertScoresToRating(Competition competition, List<Score> scores) {
		var result = new ArrayList<RatingBean>();

		Map<Long, List<Score>> map = scores.stream().collect(Collectors.groupingBy(Score::getPersonId, Collectors.toList()));

		double maxRating = 0;

		for (var personId : map.keySet()) {
			
			var personScores = map.get(personId);
			
			RatingBean personalRating = new RatingBean();
			personalRating.setPersonId(personId);
			personalRating.setScores(personScores);

			personalRating.setStages(personalRating.getScores().size());
			personalRating.setScore(personalRating.getScores().stream().mapToLong(Score::getScore).sum());
			personalRating.setTimeOfExercise(personalRating.getScores().stream().mapToDouble(Score::getTimeOfExercise).sum());

			if (personalRating.getTimeOfExercise() != 0) {
				personalRating.setHitFactor((float) personalRating.getScore() / personalRating.getTimeOfExercise());
			}

			maxRating = Math.max(personalRating.getHitFactor(), maxRating);

			// var disqualifications = scores.stream().collect(Collectors.groupingBy(Score::getDisqualificationReason, Collectors.counting()));

			personScores.removeIf(item -> StringUtils.isBlank(item.getDisqualificationReason()));

			var disqualifications = personScores.stream().map(item -> item.getDisqualificationReason()).collect(Collectors.toList());

			if (CollectionUtils.isNotEmpty(disqualifications)) {
				personalRating.setDisqualification(String.valueOf(disqualifications.size()) + "(" + StringUtils.join(disqualifications, ",") + ")");
			}

			result.add(personalRating);
		}

		// add empty scores for rating

		for (var competitor : competition.getCompetitors()) {
			if (!map.keySet().contains(competitor.getPerson().getId())) {
				RatingBean personalRating = new RatingBean();
				personalRating.setPersonId(competitor.getPerson().getId());
				personalRating.setScore(0L);
				personalRating.setTimeOfExercise(0);

				result.add(personalRating);
			}
		}

		for (var item : result) {
			item.setPercentage(maxRating == 0 ? 0 : 100 * item.getHitFactor() / maxRating);
		}

		// sort rating by percentage

		return result.stream().sorted(Comparator.comparing(RatingBean::getPercentage).reversed()).collect(Collectors.toList());
	}

	public void deleteAll() {
		competitionRepository.deleteAll();
	}

	public Competition startCompetition(Long id) throws BadRequestException {
		var competiton = checkCompetition(id);
		competiton.setStarted(true);
		competitionRepository.save(competiton);
		EventBus.publishEvent(new CompetitionUpdatedEvent(id, "Competition %s started", competiton.getName()));
		log.info("Competition %s started", competiton.getName());
		return competiton;
	}

	public Competition stopCompetition(Long id) throws BadRequestException {
		var competiton = checkCompetition(id);
		competiton.setStarted(false);
		competitionRepository.save(competiton);
		EventBus.publishEvent(new CompetitionUpdatedEvent(id, "Competition %s stopped", competiton.getName()));
		log.info("Competition %s stopped", competiton.getName());
		return competiton;
	}
}
