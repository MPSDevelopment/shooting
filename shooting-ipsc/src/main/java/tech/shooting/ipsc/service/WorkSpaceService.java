package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public void createWorkSpace(String remoteIp) {
        workSpaceRepository.save(new WorkSpace().setIp(remoteIp));
    }
}
