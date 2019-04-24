package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import tech.shooting.ipsc.pojo.Rank;
import tech.shooting.ipsc.service.RankService;
import java.util.List;

@Controller
@RequestMapping(ControllerAPI.RANK_CONTROLLER)
@Api(value = ControllerAPI.RANK_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class RankController {

	@Autowired
	private RankService rankService;

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.RANK_CONTROLLER_GET_ALL)
	@ApiOperation(value = "Get  list ranks")
	public ResponseEntity<List<Rank>> getAll() {
		return new ResponseEntity<>(rankService.getAll(), HttpStatus.OK);
	}
}
