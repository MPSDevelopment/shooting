package tech.shooting.ipsc.db;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Rank;
import tech.shooting.ipsc.pojo.Subject;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.RankRepository;
import tech.shooting.ipsc.repository.SubjectRepository;

@Component
public class DatabaseCreator {
	
	public static final String PRIVATE = "Рядовой";

	public static final String ADMIN_PASSWORD = "test";

	public static final String GUEST_PASSWORD = "";

	public static final String JUDGE_PASSWORD = "judgeTest";

	public static final String GUEST_LOGIN = "";

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

	public DatabaseCreator () {
	}

	@PostConstruct
	public void init () {
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
				new Rank().setRus(PRIVATE).setKz("Рядовой"),
				new Rank().setRus("Ефрейтор").setKz("Ефрейтор"),
				new Rank().setRus("Младший сержант").setKz("Младший сержант"),
				new Rank().setRus("Сержант").setKz("Сержант"),
				new Rank().setRus("старший сержант").setKz("Старший сержант"),
				new Rank().setRus("Старшина").setKz("Старшина"),
				new Rank().setRus("Прапорщик").setKz("Прапорщик"),
				new Rank().setRus("Старший прапорщик").setKz("Старший прапорщик"),
				new Rank().setRus("Младший лейтенант").setKz("Младший лейтенант").setOfficer(true),
				new Rank().setRus("Лейтенант").setKz("Лейтенант").setOfficer(true),
				new Rank().setRus("Старший лейтенант").setKz("Старший лейтенант").setOfficer(true),
				new Rank().setRus("Капитан").setKz("Капитан").setOfficer(true),
				new Rank().setRus("Майор").setKz("Майор").setOfficer(true),
				new Rank().setRus("Подполковник").setKz("Подполковник").setOfficer(true),
				new Rank().setRus("Полковник").setKz("Полковник").setOfficer(true)));
		
		divisionRepository.createIfNotExists(new Division().setName("Все").setActive(true));
		
	}
}
