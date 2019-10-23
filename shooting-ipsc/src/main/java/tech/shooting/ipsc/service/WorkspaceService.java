package tech.shooting.ipsc.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.NotFoundException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.WorkspaceBean;
import tech.shooting.ipsc.enums.WorkspaceStatusEnum;
import tech.shooting.ipsc.mqtt.MqttConstants;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Quiz;
import tech.shooting.ipsc.pojo.Subject;
import tech.shooting.ipsc.pojo.Workspace;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.QuizRepository;
import tech.shooting.ipsc.repository.SubjectRepository;
import tech.shooting.ipsc.repository.WorkSpaceRepository;

@Service
@Slf4j
public class WorkspaceService {

	private Map<String, Workspace> map = new HashMap<>();

	@Autowired
	private WorkSpaceRepository workSpaceRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private MqttService mqttService;

	public Workspace createWorkspace(String clientId, String ip) throws MqttException {

		Workspace workspace;
		if ((workspace = getWorkspaceByClientId(clientId)) != null) {
			log.error("Workspace with clientid %s already exists", clientId);
			return workspace;
		}
		try {
			workspace = getWorkspaceByIp(ip);
//			if (workspace != null && mqttService.isPublisherExists() && !mqttService.getPublisher().getClientId().equals(clientId)) {
			if (workspace != null) {
				log.error("Workspace with ip %s already exists", ip);
				return workspace;
			}
		} catch (NotFoundException e) {
		}

		workspace = new Workspace();
		workspace.setStatus(WorkspaceStatusEnum.CONNECTED);
		workspace.setClientId(clientId);
		workspace.setIp(ip);

		log.info("Create workspace %s in the map", workspace);

		putWorkspace(workspace);

		return workspace;
	}

	public Workspace getWorkspaceByClientId(String clientId) {
		return map.get(clientId);
	}

	public Workspace getWorkspaceByIp(String ip) throws NotFoundException {
		for (Workspace workspace : map.values()) {
			if (workspace.getIp() != null && workspace.getIp().equals(ip)) {
				return workspace;
			}
		}
		throw new NotFoundException(new ErrorMessage("There is no workspace for ip %s", ip));
	}

	public Workspace removeWorkspace(String clientId) {
		Workspace workspace = getWorkspaceByClientId(clientId);
		if (workspace != null) {
			map.remove(clientId);
			workspace.setStatus(WorkspaceStatusEnum.DISCONNECTED);
		} else {
			log.error("Cannot remove a workspace with cliendId %s. It does not exist (%s)", clientId, StringUtils.join(map.keySet(), ","));
		}
		return workspace;
	}

	public List<Workspace> checkWorkspaces(List<WorkspaceBean> list) throws BadRequestException, MqttPersistenceException, MqttException {
		// return list.stream().map(item -> startWorkspace(item)).collect(Collectors.toList());

		List<Workspace> result = new ArrayList<Workspace>();
		for (WorkspaceBean bean : list) {
			result.add(startWorkspace(bean.setCheck(true)));
		}
		return result;
	}

	public List<Workspace> startWorkspaces(List<WorkspaceBean> list) throws BadRequestException, MqttPersistenceException, MqttException {
		// return list.stream().map(item -> startWorkspace(item)).collect(Collectors.toList());

		List<Workspace> result = new ArrayList<Workspace>();
		for (WorkspaceBean bean : list) {
			result.add(startWorkspace(bean.setCheck(false)));
		}
		return result;
	}

	public Workspace startWorkspace(WorkspaceBean bean) throws BadRequestException, MqttPersistenceException, MqttException {
		Workspace workspace = updateWorkspace(bean);

		if (workspace == null) {
			log.error("Cannot start workspace unknown workspace %s, existing list:", bean);
			getAllWorkspaces().forEach(item -> log.info("Workspace %s", item));
			return workspace;
		} 
			
		log.error("Starting the workspace %s", bean);

		String topic = MqttConstants.TEST_TOPIC + "/" + workspace.getIp();
		log.info("Sending test start to the topic %s", topic);
		mqttService.getPublisher().publish(topic, mqttService.createJsonMessage(workspace));
		return workspace;
	}

	public Workspace updateWorkspace(WorkspaceBean bean) throws BadRequestException {

		Workspace workspace = getWorkspaceByClientId(bean.getClientId());
		if (workspace != null) {
			checkPerson(bean.getPersonId());
			checkQuiz(bean.getQuizId());
			BeanUtils.copyProperties(bean, workspace);
			putWorkspace(workspace);
		}
		return workspace;
	}

	public void createTopicForAdmin() {
		// mqttService.createPublisher() //needed explain parameters
	}

	private Person checkPerson(long person) throws BadRequestException {
		return personRepository.findById(person).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect PersonId id, check id is %s", person)));
	}

	private Quiz checkQuiz(long quiz) throws BadRequestException {
		return quizRepository.findById(quiz).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Quiz id, check id is %s", quiz)));
	}

	private Subject checkSubject(long subject) throws BadRequestException {
		return subjectRepository.findById(subject).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect subject id, check id is %s", subject)));
	}

	private Workspace checkWorkspace(long worksSpaceId) throws BadRequestException {
		return workSpaceRepository.findById(worksSpaceId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect work space id, check id is %s", worksSpaceId)));
	}

	public Collection<Workspace> getAllWorkspaces() {
		return map.values();
	}

	public Collection<Workspace> getAllWorkspacesForTest() {
		
		log.info("Trying to get all workspaces for test: ");
		logWorkspaces();
		
		return map.values().stream().filter(item -> item.isUseInTest()).collect(Collectors.toList());
	}

	public void putWorkspace(WorkspaceBean bean) {
		Workspace workspace = new Workspace();
		BeanUtils.copyProperties(bean, workspace);
		putWorkspace(workspace);
	}

	public void putWorkspace(Workspace workspace) {
		log.info("Put workspace %s to the map, size is %s", workspace, map.size());
		map.put(workspace.getClientId(), workspace);
		logWorkspaces();
	}

	private void logWorkspaces() {
		map.values().forEach(item -> {
			log.info("Workspace is %s %s %s %s", item.isUseInTest(), item.getId(), item.getIp(), item.getPersonId());
		});
	}

	public void clear() {
		map.clear();
	}
}
