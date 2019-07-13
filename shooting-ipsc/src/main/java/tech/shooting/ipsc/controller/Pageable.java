package tech.shooting.ipsc.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.commons.utils.HeaderUtils;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;

@Slf4j
public class Pageable {
	
	public static ResponseEntity getPage(int page, int size, MongoRepository<? extends BaseDocument, Long> personRepository) {
		page = Math.max(1, page);
		page--;
		Page<?> pageOfUsers = Pageable.countPage(page, size, personRepository);
		return new ResponseEntity(pageOfUsers.getContent(), setHeaders(page, pageOfUsers.getTotalElements(), pageOfUsers.getTotalPages()), HttpStatus.OK);
	}

	public static Page<? extends BaseDocument> countPage(Integer page, Integer size, MongoRepository<? extends BaseDocument, Long> personRepository) {
		size = Math.min(Math.max(10, size), 20);
		PageRequest pageable = PageRequest.of(page, size, Sort.Direction.ASC, User.ID_FIELD);
		if (personRepository instanceof UserRepository) {
			return ((UserRepository) personRepository).findAllByRoleName(RoleName.USER.toString(), pageable);
		}

		long t1 = System.currentTimeMillis();
		var list = personRepository.findAll(pageable);
		long t2 = System.currentTimeMillis();

		log.info("Page list took %d ms", t2 - t1);

		return list;
	}

	public static MultiValueMap<String, String> setHeaders(Integer page, Long totalElements, Integer totalPages) {
		MultiValueMap<String, String> headers = new HttpHeaders();
		page++;
		headers.add(HeaderUtils.PAGE_HEADER, page.toString());
		headers.add(HeaderUtils.TOTAL_HEADER, totalElements.toString());
		headers.add(HeaderUtils.PAGES_HEADER, totalPages.toString());
		return headers;
	}
}
