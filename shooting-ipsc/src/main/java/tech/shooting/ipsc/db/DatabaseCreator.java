package tech.shooting.ipsc.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.ipsc.pojo.Subject;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.SubjectRepository;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DatabaseCreator {
	public static final String ADMIN_PASSWORD = "test";

	public static final String JUDGE_PASSWORD = "judgeTest";

	public static final String ADMIN_LOGIN = "admin";

	public static final String JUDGE_LOGIN = "judge";

	@Autowired
	private DatabaseCreator databaseCreator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private SubjectRepository subjectRepository;

	public DatabaseCreator () {
	}

	@PostConstruct
	public void init () {
		databaseCreator.createDatabase();
	}

	private void createDatabase () {
		userDao.createIfNotExists(new User().setLogin(ADMIN_LOGIN).setPassword(ADMIN_PASSWORD).setRoleName(RoleName.ADMIN).setActive(true).setName("Admin"));
		userDao.createIfNotExists(new User().setLogin(JUDGE_LOGIN).setPassword(JUDGE_PASSWORD).setRoleName(RoleName.JUDGE).setActive(true).setName("Judge"));
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
	}
}
