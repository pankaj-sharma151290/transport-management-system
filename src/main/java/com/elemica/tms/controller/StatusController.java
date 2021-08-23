package com.elemica.tms.controller;

import com.elemica.tms.constants.CommonConstants;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping(CommonConstants.API_PATH_STATUS)
    public ResponseEntity<String> checkStatus() {

        return ResponseEntity.status(HttpStatus.OK).body(CommonConstants.APPLICATION_STATUS_MSG);
    }
}
