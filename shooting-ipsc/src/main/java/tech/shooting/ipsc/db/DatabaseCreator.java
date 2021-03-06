package tech.shooting.ipsc.db;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.pojo.AnimalType;
import tech.shooting.ipsc.pojo.Settings;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Rank;
import tech.shooting.ipsc.pojo.Subject;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.AnimalTypeRepository;
import tech.shooting.ipsc.repository.SettingsRepository;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.RankRepository;
import tech.shooting.ipsc.repository.SubjectRepository;

@Component
@Slf4j
public class DatabaseCreator {

	public static final String DEFAULT_SETTINGS_NAME = "Default";

	public static final String PRIVATE = "Рядовой";

	public static final String ADMIN_PASSWORD = "test";

	public static final String GUEST_PASSWORD = "guest";

	public static final String JUDGE_PASSWORD = "judgeTest";

	public static final String GUEST_LOGIN = "guest";

	public static final String ADMIN_LOGIN = "admin";

	public static final String JUDGE_LOGIN = "judge";

	@Autowired
	private DatabaseCreator databaseCreator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private RankRepository rankRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private AnimalTypeRepository animalTypeRepository;

	@Autowired
	private SettingsRepository appSettingsRepository;

	public DatabaseCreator() {
	}

	@PostConstruct
	public void init() {
		databaseCreator.createDatabase();
	}

	private void createDatabase () {
		userDao.createIfNotExists(new User().setLogin(ADMIN_LOGIN).setPassword(ADMIN_PASSWORD).setRoleName(RoleName.ADMIN).setActive(true).setName("Admin"));
		userDao.createIfNotExists(new User().setLogin(JUDGE_LOGIN).setPassword(JUDGE_PASSWORD).setRoleName(RoleName.JUDGE).setActive(true).setName("Judge"));
		userDao.createIfNotExists(new User().setLogin(GUEST_LOGIN).setPassword(GUEST_PASSWORD).setRoleName(RoleName.GUEST).setActive(true).setName("Guest"));

		//create subject
		subjectRepository.createIfNotExists(List.of(
			new Subject().setRus("Огневая подготовка").setKz("Fire training"),
			new Subject().setRus("Физическая подготовка").setKz("PHYSICAL"),
			new Subject().setRus("Военно-медицинская подготовка").setKz("MILITARY_MEDICIAL"),
			new Subject().setRus("Тактико-специальная подготовка").setKz("SPECIAL_TACTICAL"),
			new Subject().setRus("Тактико-служебно боевого применения").setKz("TACTICAL_AND_SERVICE_COMBAT_USE"),
			new Subject().setRus("Высотная подготовка").setKz("ALTITUDE"),
			new Subject().setRus("Общевоинские уставы").setKz("GENERAL_MILITARY_REGULATIONS"),
			new Subject().setRus("Военно-инженерная подготовка").setKz("MILITARY_ENGINEERING"),
			new Subject().setRus("Радиационная, химическая и биологическая защита").setKz("RADIATION_CHEMICAL_AND_BIOLOGICAL_PROTECTION"),
			new Subject().setRus("Подготовка по связи").setKz("COMMUNICATION"),
			new Subject().setRus("Военная топография").setKz("MILITARY_TOPOGRAPHY"),
			new Subject().setRus("Специальная подготовка по категориям специалистов").setKz("SPECIAL_TRAINING_IN_PROFESSIONAL_CATEGORIES")));
		
		
		rankRepository.createIfNotExists(List.of(
				new Rank().setRus(PRIVATE).setKz("Қатардағы жауынгер"),
				new Rank().setRus("Ефрейтор").setKz("Ефрейтор"),
				
				new Rank().setRus("Младший сержант").setKz("Кiшi сержант"),
				new Rank().setRus("Сержант").setKz("Сержант"),
				new Rank().setRus("Cтарший сержант").setKz("Аға сержант"),
				
				new Rank().setRus("Сержант третьего класса").setKz("Үшiншi сыныпты сержант"),
				new Rank().setRus("Сержант второго класса").setKz("Екiншi сыныпты сержант"),
				new Rank().setRus("Сержант первого класса").setKz("Бiрiншi сыныпты сержант"),
				
				new Rank().setRus("Штаб-сержант").setKz("Штаб-сержант"),
				new Rank().setRus("Мастер-сержант").setKz("Шебер-сержант"),
				
				new Rank().setRus("Лейтенант").setKz("Лейтенант").setOfficer(true),
				new Rank().setRus("Старший лейтенант").setKz("Аға лейтенант").setOfficer(true),
				new Rank().setRus("Капитан").setKz("Капитан").setOfficer(true),
				
				new Rank().setRus("Майор").setKz("Майор").setOfficer(true),
				new Rank().setRus("Подполковник").setKz("Подполковник").setOfficer(true),
				new Rank().setRus("Полковник").setKz("Полковник").setOfficer(true), 
		
				new Rank().setRus("Генерал-майор").setKz("Генерал-майор").setOfficer(true),
				new Rank().setRus("Генерал-лейтенант").setKz("Генерал-лейтенант").setOfficer(true),
				new Rank().setRus("Генерал-полковник").setKz("Генерал-полковник").setOfficer(true)));
		
		long count = divisionRepository.count();
		divisionRepository.createIfNotExists(new Division().setName("Все").setActive(true));
		log.info("Division count was %s, after root save if not exist, it is %s", count, divisionRepository.count());
		log.info("Divisions are %s", JacksonUtils.getJson(divisionRepository.findAll()));
		
		
		animalTypeRepository.createIfNotExists(new AnimalType().setName("Собака"));

		if (appSettingsRepository.findByName(DEFAULT_SETTINGS_NAME) == null) {
			appSettingsRepository.save(new Settings().setName(DEFAULT_SETTINGS_NAME).setTagServiceIp("127.0.0.1"));
		}
		
	}
}
