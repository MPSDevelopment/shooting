package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.ipsc.pojo.Stage;
import tech.shooting.ipsc.repository.StageRepository;

import javax.validation.Valid;

@Controller
@RequestMapping(ControllerAPI.STAGE_CONTROLLER)
@Api(value = ControllerAPI.STAGE_CONTROLLER)
@Slf4j
public class StageController {

	@Autowired
	private StageRepository stageRepository;

	public ResponseEntity<Stage> createStage (@PathVariable(value = "competitionId") Long id, @RequestBody @Valid Stage stage) {
		return new ResponseEntity<>(stage, HttpStatus.OK);
	}
}
