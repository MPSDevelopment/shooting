package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Subject;

import java.util.List;

public interface CustomSubjectRepository {
	
	List<Subject> createIfNotExists (List<Subject> subjects);
}
