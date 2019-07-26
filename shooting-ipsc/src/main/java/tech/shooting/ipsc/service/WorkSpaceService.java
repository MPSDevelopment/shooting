package tech.shooting.ipsc.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.moquette.broker.ClientDescriptor;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.WorkSpaceBean;
import tech.shooting.ipsc.enums.WorkspaceStatusEnum;
import tech.shooting.ipsc.mqtt.MqttService;
import tech.shooting.ipsc.mqtt.event.MqttOnConnectEvent;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Quiz;
import tech.shooting.ipsc.pojo.Workspace;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.QuizRepository;
import tech.shooting.ipsc.repository.WorkSpaceRepository;

@Service
public class WorkSpaceService {

	private Map<String, Workspace> map = new HashMap<>();

	@Autowired
	private WorkSpaceRepository workSpaceRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private MqttService mqttService;

	public Workspace createWorkspace(String clientId, String ip) {
		var workspace = new Workspace();
		workspace.setStatus(WorkspaceStatusEnum.CONNECTED);
		workspace.setClientId(clientId);
		workspace.setIp(ip);
		
		map.put(clientId, workspace);
		
		return workspace;
	}

	public Workspace getWorkspaceByClientId(String clientId) {
		return map.get(clientId);
	}

	public Workspace removeWorkspace(String clientId) {
		Workspace workspace = getWorkspaceByClientId(clientId);
		if (workspace != null) {
			map.remove(clientId);
			workspace.setStatus(WorkspaceStatusEnum.DISCONNECTED);
		}
		return workspace;
	}

	public Workspace updateWorkspace(WorkSpaceBean bean) {
		Workspace workspace = getWorkspaceByClientId(bean.getClientId());
		if (workspace != null) {
			BeanUtils.copyProperties(workspace, bean);
		}
		return workspace;
	}

	public void createTopicForAdmin() {
		// mqttService.createPublisher() //needed explain parameters
	}

//    public void updateWorkSpaceDataAndStartTest(List<WorkSpaceBean> beans) throws BadRequestException {
//        for (WorkSpaceBean bean : beans) {
//            WorkSpace save = workSpaceRepository.save(checkAllDependOn(bean));
//        }
//    }

//    private WorkSpace checkAllDependOn(WorkSpaceBean bean) throws BadRequestException {
//        WorkSpace workSpace = checkWorkspace(bean.getId());
//        Person person = checkPerson(bean.getPerson());
//        Quiz quiz = checkQuiz(bean.getTest());
//        return workSpace.setPerson(person).setTest(quiz);
//    }

	private Person checkPerson(long person) throws BadRequestException {
		return personRepository.findById(person).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect PersonId id, check id is %s", person)));
	}

	private Quiz checkQuiz(long quiz) throws BadRequestException {
		return quizRepository.findById(quiz).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Quiz id, check id is %s", quiz)));
	}

	private Workspace checkWorkspace(long worksSpaceId) throws BadRequestException {
		return workSpaceRepository.findById(worksSpaceId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect work space id, check id is %s", worksSpaceId)));
	}

	public Collection<Workspace> getAllWorkspaces() {
		return map.values();
	}
}
