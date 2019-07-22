package tech.shooting.ipsc.service;

import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.WorkSpaceBean;
import tech.shooting.ipsc.mqtt.MqttService;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Quiz;
import tech.shooting.ipsc.pojo.WorkSpace;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.QuizRepository;
import tech.shooting.ipsc.repository.WorkSpaceRepository;

@Service
public class WorkSpaceService {

    @Autowired
    private WorkSpaceRepository workSpaceRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private MqttService mqttService;

    private static final String MQTT_URL = "tcp://127.0.0.1:1883";
    public static final String TOPIC_CREATED = "create/topic";

    public void createWorkSpace(String remoteIp) throws MqttException {
        WorkSpace save = workSpaceRepository.save(new WorkSpace().setIp(remoteIp));
        MqttClient publisher = mqttService.createPublisher(MQTT_URL, "guest", "guest");
        MqttMessage message = mqttService.createMessage(remoteIp);
        publisher.getTopic(TOPIC_CREATED).publish(message);

    }

    public void createTopicForAdmin() {
        //mqttService.createPublisher() //needed explain parameters
    }

    public void updateWorkSpaceDataAndStartTest(List<WorkSpaceBean> beans) throws BadRequestException {
        for (WorkSpaceBean bean : beans) {
            WorkSpace save = workSpaceRepository.save(checkAllDependOn(bean));
        }
    }

    private WorkSpace checkAllDependOn(WorkSpaceBean bean) throws BadRequestException {
        WorkSpace workSpace = checkWorkspace(bean.getId());
        Person person = checkPerson(bean.getPerson());
        Quiz quiz = checkQuiz(bean.getTest());
        return workSpace.setPerson(person).setTest(quiz);
    }

    private Person checkPerson(long person) throws BadRequestException {
        return personRepository.findById(person)
                               .orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect PersonId id, check id is %s", person)));
    }

    private Quiz checkQuiz(long quiz) throws BadRequestException {
        return quizRepository.findById(quiz)
                             .orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Quiz id, check id is %s", quiz)));
    }

    private WorkSpace checkWorkspace(long worksSpaceId) throws BadRequestException {
        return workSpaceRepository.findById(worksSpaceId)
                                  .orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect work space id, check id is %s", worksSpaceId)));
    }
}
