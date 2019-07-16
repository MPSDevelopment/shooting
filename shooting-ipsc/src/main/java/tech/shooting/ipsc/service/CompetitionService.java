package tech.shooting.ipsc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.CompetitionBean;
import tech.shooting.ipsc.bean.CompetitorMark;
import tech.shooting.ipsc.bean.CompetitorMarks;
import tech.shooting.ipsc.bean.ScoreBean;
import tech.shooting.ipsc.controller.Pageable;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.ClassifierIPSC;
import tech.shooting.ipsc.enums.DisqualificationEnum;
import tech.shooting.ipsc.enums.TypeMarkEnum;
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Competitor;
import tech.shooting.ipsc.pojo.LevelBean;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Score;
import tech.shooting.ipsc.pojo.Stage;
import tech.shooting.ipsc.pojo.TypeWeapon;
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
//		if (CollectionUtils.isNotEmpty(competitionBean.getCompetitors())) {
//			var list = new ArrayList<Competitor>();
//			for (var item : competitionBean.getCompetitors()) {
//				Competitor competitor = new Competitor();
//				BeanUtils.copyProperties(competitionBean, competitor, Competitor.PERSON);
//				Person person = personRepository.findById(item.getPerson()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person with id %s for competitor %s", item.getPerson(), item)));
//				competitor.setPerson(person);
//				competitor.setName(person.getName());
//				list.add(competitor);
//			}
//			competition.setCompetitors(list);
//		}
		if (competitionBean.getClazz() != null) {
			competition.setClazz(competitionBean.getClazz());
		}
		return competition;
	}

	private void createCompetition(Competition competition) {
		log.info("Create competition with name %s ", competition.getName());
		if (competitionRepository.findByName(competition.getName()) != null) {
			throw new ValidationException(Competition.NAME_FIELD, "Competition with name %s is already exist", competition.getName());
		}
		competition.setActive(true);
		if (competition.getStages() == null) {
			competition.setStages(new ArrayList<>());
		}
		competitionRepository.save(competition);
	}

	public Competition updateCompetition(Long id, CompetitionBean competition) throws BadRequestException {
		Competition competitionFromDb = checkCompetition(id);
		checkCompetitionActive(competitionFromDb);
		Competition existCompetition = useBeanUtilsWithOutJudges(competition, competitionFromDb);
//		// Do not change competitors and stages
//		BeanUtils.copyProperties(competition, existCompetition, Competition.COMPETITORS_FIELD);
		return competitionRepository.save(existCompetition);
	}

	public Competition createCompetition(CompetitionBean competitionBean) throws BadRequestException {
		Competition competition = useBeanUtilsWithOutJudges(competitionBean, new Competition());
		createCompetition(competition);
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
		return competition.getStages();
	}

	public List<Stage> addedAllStages(Long id, List<Stage> toAdded) throws BadRequestException {
		Competition competition = checkCompetition(id);
		checkCompetitionActive(competition);
		List<Stage> stages = competition.getStages();
		stages.addAll(toAdded);
		competition.setStages(stages);
		return competitionRepository.save(competition).getStages();
	}

	public Stage addStage(Long id, Stage toAdded) throws BadRequestException {
		Competition competition = checkCompetition(id);
		checkCompetitionActive(competition);
		competition.getStages().add(toAdded);
		List<Stage> stages = competitionRepository.save(competition).getStages();
		int index = 0;
		for (int i = 0; i < stages.size(); i++) {
			if (stages.get(i).getName().equals(toAdded.getName()) && stages.get(i).getMaximumPoints().equals(toAdded.getMaximumPoints()) && stages.get(i).getTargets().equals(toAdded.getTargets())) {
				index = i;
			}
		}
		return stages.get(index);
	}

	public Stage getStage(Long competitionId, Long stageId) throws BadRequestException {
		return checkStage(checkCompetition(competitionId), stageId);
	}

	private Stage checkStage(Competition competition, Long stageId) throws BadRequestException {
		return competition.getStages().stream().filter((i) -> i.getId().equals(stageId)).findAny().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect stageId %s", stageId)));
	}

	public void deleteStage(Long competitionId, Long stageId) throws BadRequestException {
		checkCompetition(competitionId);
		competitionRepository.pullStageFromCompetition(competitionId, stageId);
	}

	public Stage updateStage(Long competitionId, Long stageId, Stage stage) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		checkCompetitionActive(competition);
		Stage stageFromDB = checkStage(competition, stageId);
		BeanUtils.copyProperties(stage, stageFromDB);
		List<Stage> stages = competition.getStages();
		stages.remove(stageFromDB);
		stages.add(stageFromDB);
		competitionRepository.save(competition.setStages(stages));
		return stageFromDB;
	}

	public Competitor addedCompetitor(Long id, Competitor competitor) throws BadRequestException {
		Competition competition = checkCompetition(id);
		checkCompetitionActive(competition);
		checkPerson(competitor.getPerson().getId());
		Competitor competitorToDB = new Competitor();
		BeanUtils.copyProperties(competitor.setActive(false), competitorToDB);
		return saveAndReturn(competition, competitorToDB, true);
	}

	private Person checkPerson(Long id) throws BadRequestException {
		return personRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitorId %s", id)));
	}

	public void deleteCompetitor(Long id, Long competitorId) throws BadRequestException {
		checkCompetition(id);
		competitionRepository.pullCompetitorFromCompetition(id, competitorId);
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
		return saveAndReturn(competition, competitorFromDB, false);
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
		return competitionRepository.save(competition).getCompetitors();
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

	public Competitor addedMarkToCompetitor(Long competitionId, Long competitorId, CompetitorMark competitorMark) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		checkCompetitionActive(competition);

		for (var competitor : competition.getCompetitors()) {
			if (competitorMark.getType().equals(TypeMarkEnum.RFID)) {
				if (competitor.getRfidCode() != null && competitor.getRfidCode().equalsIgnoreCase(competitorMark.getMark())) {
					new BadRequestException(new ErrorMessage("Rfid mark already exists %s", competitorMark.getMark()));
				}
			} else {
				if (competitor.getNumber() != null && competitor.getNumber().equalsIgnoreCase(competitorMark.getMark())) {
					new BadRequestException(new ErrorMessage("Number already exists %s", competitorMark.getMark()));
				}
			}
		}

		Competitor competitor = checkCompetitor(competition.getCompetitors(), competitorId);
		if (competitorMark.getType().equals(TypeMarkEnum.RFID)) {
			competitor.setRfidCode(competitorMark.getMark());
		} else {
			competitor.setNumber(competitorMark.getMark());
		}
		competitor.setActive(competitorMark.isActive()).setName(competitorMark.getName());
		return saveAndReturn(competition, competitor, false);
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

	public List<TypeWeapon> getListTypeWeapon() {
		return WeaponTypeEnum.getList();
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
		List<Score> result = new ArrayList<>();
		Competition competition = checkCompetition(competitionId);
		checkCompetitionActive(competition);
		checkStage(competition, stageId);
		for (ScoreBean score : scoreBean) {
			Score score1 = addedScoreWithoutCheck(competition, stageId, score);
			if (score1 != null) {
				result.add(score1);
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

			scoreResult.setScore(0).setTimeOfExercise(0L);

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

	public void deleteAll() {
		competitionRepository.deleteAll();
	}
}
