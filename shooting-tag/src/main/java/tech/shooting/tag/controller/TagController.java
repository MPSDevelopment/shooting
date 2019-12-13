package tech.shooting.tag.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.tag.pojo.SuccessfulMessage;
import tech.shooting.tag.pojo.TagEpc;
import tech.shooting.tag.service.TagService;

import javax.validation.Valid;
import java.net.SocketException;

@Controller
@RequestMapping(ControllerAPI.TAG_CONTROLLER)
@Slf4j
public class TagController {

    @Autowired
    private TagService service;

    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_GET_STATUS)
    public ResponseEntity<SuccessfulMessage> getStatus() throws SocketException {
        var status = service.getStatus();
        if (status) {
            return new ResponseEntity<>(new SuccessfulMessage("Status connected Tag Ip %s Local ip %s", service.getTagIp(), service.getFirstNonLoopbackAddress()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new SuccessfulMessage("Status disconnected  Tag Ip %s Local ip %s", service.getTagIp(), service.getFirstNonLoopbackAddress()), HttpStatus.OK);
    }


    @PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_POST_NEW_EPC, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SuccessfulMessage> writeNewEPCCode(@RequestBody TagEpc tagEpc) throws SocketException {
		service.rewriteEPCRequest(tagEpc);
    	return new ResponseEntity<>(new SuccessfulMessage("EPC was rewrite", service.getTagIp(), service.getFirstNonLoopbackAddress()), HttpStatus.OK);
    }

}
