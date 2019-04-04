package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Subject;

import java.util.List;

public interface CustomSubjectRepository {
	void createIfNotExists (List<Subject> subjects);
}
