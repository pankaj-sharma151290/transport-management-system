package com.elemica.tms.controller;

import javax.validation.Valid;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.model.dto.VehicleDTO;
import com.elemica.tms.model.resourceobject.VehicleRO;
import com.elemica.tms.model.resourceobject.VehicleResponse;
import com.elemica.tms.service.contract.VehicleService;
import com.elemica.tms.util.VehicleUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = CommonConstants.API_PATH_VEHICLE, produces = MediaType.APPLICATION_JSON_VALUE)
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping()
    public ResponseEntity<VehicleResponse> getAllVehicles() {

        return ResponseEntity.status(HttpStatus.OK).body(VehicleUtils.prepareResponse(vehicleService.getAll()));
    }

    @GetMapping(path = CommonConstants.PATH_PARAM_NAME)
    public ResponseEntity<VehicleResponse> getVehicleByName(@PathVariable(CommonConstants.PARAM_NAME) final String name) {

        return ResponseEntity.status(HttpStatus.OK).body(VehicleUtils.prepareResponse(vehicleService.getByName(name)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @PutMapping(path = CommonConstants.API_PATH_ADD, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addVehicle(@Valid @RequestBody VehicleRO vehicleRO) {

        vehicleService.saveVehicle(new VehicleDTO(vehicleRO));
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonConstants.CREATED_VEHICLE);
    }

    @PostMapping(path = CommonConstants.API_PATH_DELETE)
    public ResponseEntity<String> removeVehicle(@RequestParam(CommonConstants.PARAM_NAME) final String name) {

        vehicleService.removeVehicle(name);
        return ResponseEntity.status(HttpStatus.OK).body(CommonConstants.DELETED_VEHICLE);
    }

}
