package com.elemica.tms.controller;

import javax.validation.Valid;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.model.dto.TariffDTO;
import com.elemica.tms.model.resourceobject.TariffRO;
import com.elemica.tms.model.resourceobject.TariffResponse;
import com.elemica.tms.service.contract.TariffService;
import com.elemica.tms.util.TariffUtils;

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
@RequestMapping(path = CommonConstants.API_PATH_TARIFF, produces = MediaType.APPLICATION_JSON_VALUE)
public class TariffController {

    @Autowired
    private TariffService tariffService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TariffResponse> getAllTariffs() {

        return ResponseEntity.status(HttpStatus.OK).body(TariffUtils.prepareResponse(tariffService.getAll()));
    }

    @GetMapping(path = CommonConstants.PATH_PARAM_NAME)
    public ResponseEntity<TariffResponse> getTariffByName(@PathVariable(CommonConstants.PARAM_NAME) final String name) {

        return ResponseEntity.status(HttpStatus.OK).body(TariffUtils.prepareResponse(tariffService.getByName(name)));
    }

    @PutMapping(path = CommonConstants.API_PATH_ADD, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addTariff(@Valid @RequestBody TariffRO tariffRO) {

        tariffService.saveTariff(new TariffDTO(tariffRO));
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonConstants.CREATED_TARIFF);
    }

    @PostMapping(path = CommonConstants.API_PATH_DELETE)
    public ResponseEntity<String> removeTariff(@RequestParam(CommonConstants.PARAM_NAME) final String name) {

        tariffService.removeTariff(name);
        return ResponseEntity.status(HttpStatus.OK).body(CommonConstants.DELETED_TARIFF);
    }
}
