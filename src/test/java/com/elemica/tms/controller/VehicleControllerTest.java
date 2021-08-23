package com.elemica.tms.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.model.dto.VehicleDTO;
import com.elemica.tms.model.resourceobject.VehicleRO;
import com.elemica.tms.model.resourceobject.VehicleResponse;
import com.elemica.tms.service.impl.VehicleServiceImpl;

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
public class VehicleControllerTest {

    @InjectMocks
    VehicleController vehicleController;

    @Mock
    VehicleServiceImpl vehicleService;

    @Test
    @DisplayName(value = "Test Get all vehicles controller")
    public void TestGetAllShipments() {

        List<VehicleDTO> vehicleDTOs     = new ArrayList<>();
        VehicleResponse  vehicleResponse = new VehicleResponse();
        when(vehicleService.getAll()).thenReturn(vehicleDTOs);
        ResponseEntity<VehicleResponse> responseEntity = vehicleController.getAllVehicles();
        verify(vehicleService).getAll();
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getCount(), is(vehicleResponse.getCount()));
    }

    @Test
    @DisplayName(value = "Test get vehicle by name controller")
    public void TestGetVehicleByName() {

        VehicleDTO vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        when(vehicleService.getByName(vehicleDTOOne.getName())).thenReturn(vehicleDTOOne);
        ResponseEntity<VehicleResponse> responseEntity = vehicleController.getVehicleByName(vehicleDTOOne.getName());
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getCount(), is(1L));
        assertThat(responseEntity.getBody().getVehicles().get(0).getName(), is(vehicleDTOOne.getName()));
        verify(vehicleService).getByName(vehicleDTOOne.getName());
    }

    @Test
    @DisplayName(value = "Test add vehicle controller")
    public void TestAddVehicle() {

        VehicleRO              vehicleRO      = new VehicleRO();
        VehicleDTO             vehicleDTO     = new VehicleDTO();
        ResponseEntity<String> responseEntity = vehicleController.addVehicle(vehicleRO);
        verify(vehicleService).saveVehicle(vehicleDTO);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(responseEntity.getBody(), is(CommonConstants.CREATED_VEHICLE));
        verify(vehicleService).saveVehicle(vehicleDTO);
    }

    @Test
    @DisplayName(value = "Test remove vehicle by name controller")
    public void TestRemoveVehicle() {

        ResponseEntity<String> responseEntity = vehicleController.removeVehicle(EMPTY_STRING);
        verify(vehicleService).removeVehicle(EMPTY_STRING);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), is(CommonConstants.DELETED_VEHICLE));
        verify(vehicleService).removeVehicle(EMPTY_STRING);
    }
}
