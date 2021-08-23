package com.elemica.tms.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.model.dto.TariffDTO;
import com.elemica.tms.model.resourceobject.TariffRO;
import com.elemica.tms.model.resourceobject.TariffResponse;
import com.elemica.tms.model.resourceobject.VehicleResponse;
import com.elemica.tms.service.impl.TariffServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.elemica.tms.constants.CommonConstants.EMPTY_STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TariffControllerTest {

    @InjectMocks
    TariffController tariffController;

    @Mock
    TariffServiceImpl tariffService;

    @Test
    @DisplayName(value = "Test get all tariffs controller")
    public void TestGetAllTariffs() {

        List<TariffDTO> tariffDTOs      = new ArrayList<>();
        VehicleResponse vehicleResponse = new VehicleResponse();
        when(tariffService.getAll()).thenReturn(tariffDTOs);
        ResponseEntity<TariffResponse> responseEntity = tariffController.getAllTariffs();
        verify(tariffService).getAll();
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getCount(), is(vehicleResponse.getCount()));
    }

    @Test
    @DisplayName(value = "Test get tariff by name controller")
    public void TestGetTariffByName() {

        TariffDTO tariffDTOOne = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).build();
        when(tariffService.getByName(tariffDTOOne.getName())).thenReturn(tariffDTOOne);
        ResponseEntity<TariffResponse> responseEntity = tariffController.getTariffByName(tariffDTOOne.getName());
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getCount(), is(1L));
        assertThat(responseEntity.getBody().getTariffs().get(0).getName(), is(tariffDTOOne.getName()));
        verify(tariffService).getByName(tariffDTOOne.getName());
    }

    @Test
    @DisplayName(value = "Test add tariff controller")
    public void TestAddVehicle() {

        TariffRO               tariffRO       = new TariffRO();
        TariffDTO              tariffDTO      = new TariffDTO();
        ResponseEntity<String> responseEntity = tariffController.addTariff(tariffRO);
        verify(tariffService).saveTariff(tariffDTO);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(responseEntity.getBody(), is(CommonConstants.CREATED_TARIFF));
        verify(tariffService).saveTariff(tariffDTO);
    }

    @Test
    @DisplayName(value = "Test delete tariff by name controller")
    public void TestRemoveVehicle() {

        ResponseEntity<String> responseEntity = tariffController.removeTariff(EMPTY_STRING);
        verify(tariffService).removeTariff(EMPTY_STRING);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), is(CommonConstants.DELETED_TARIFF));
        verify(tariffService).removeTariff(EMPTY_STRING);

    }
}
