package com.elemica.tms.controller;

import javax.validation.Valid;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.model.dto.ShipmentDTO;
import com.elemica.tms.model.resourceobject.ShipmentRequest;
import com.elemica.tms.model.resourceobject.ShipmentResponse;
import com.elemica.tms.service.contract.ShipmentService;
import com.elemica.tms.util.ShipmentUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConstants.SHIPMENT)
public class ShipmentController {

    @Autowired
    ShipmentService shipmentService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> getAllShipments(){
        return ResponseEntity.status(HttpStatus.OK).body(ShipmentUtils.prepareResponse(shipmentService.getAll()));
    }

    @GetMapping(value = CommonConstants.PATH_PARAM_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> getShipmentByName(@PathVariable(CommonConstants.NAME) final String name){
        return ResponseEntity.status(HttpStatus.OK).body(ShipmentUtils.prepareResponse(shipmentService.getByName(name)));
    }

    @PutMapping(value = CommonConstants.ADD, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addShipment(@Valid @RequestBody ShipmentRequest shipmentRequest){
        shipmentService.saveShipment(new ShipmentDTO(shipmentRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonConstants.CREATED_SHIPMENT);
    }

    @PostMapping(value = CommonConstants.DELETE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeShipment(@RequestParam(CommonConstants.NAME) final String name){
        shipmentService.removeShipment(name);
        return ResponseEntity.status(HttpStatus.OK).body(CommonConstants.DELETED_SHIPMENT);
    }

    @PostMapping(value = CommonConstants.PATH_VEHICLE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> assignVehicleToShipment(@RequestParam(CommonConstants.SHIPMENT_NAME) final String shipmentName,
                                                                    @RequestParam(CommonConstants.VEHICLE_NAME) final String vehicleName){

        return ResponseEntity.status(HttpStatus.OK).body(ShipmentUtils.prepareResponse(shipmentService.assignVehicleToShipment(shipmentName,vehicleName)));
    }

    @PostMapping(value = CommonConstants.PATH_TARIFF, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> assignTariffToShipment(@RequestParam(CommonConstants.SHIPMENT_NAME) final String shipmentName,
                                                                   @RequestParam(CommonConstants.TARIFF_NAME) final String tariffName){
        return ResponseEntity.status(HttpStatus.OK).body(ShipmentUtils.prepareResponse(shipmentService.assignTariffAndCalculateShipmentCost(shipmentName, tariffName)));
    }

    @PostMapping(value = CommonConstants.PATH_CALCULATE_SHIPMENT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> assignVehicleTariffWithCheapestCost(@RequestParam(CommonConstants.SHIPMENT_NAME) final String shipmentName){
        return ResponseEntity.status(HttpStatus.OK).body(ShipmentUtils.prepareResponse(shipmentService.calculateBestShipmentCost(shipmentName)));
    }

    @GetMapping(value = CommonConstants.PATH_EXPENSIVE_SHIPMENT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> getMostExpensiveShipment(){
        return ResponseEntity.status(HttpStatus.OK).body(ShipmentUtils.prepareResponse(shipmentService.getMostExpensiveShipment()));
    }

}
