package com.elemica.tms.controller;

import java.util.ArrayList;
import java.util.List;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.model.dto.ShipmentDTO;
import com.elemica.tms.model.resourceobject.ShipmentRequest;
import com.elemica.tms.model.resourceobject.ShipmentResponse;
import com.elemica.tms.service.impl.ShipmentServiceImpl;

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
public class ShipmentControllerTest {

    @InjectMocks
    ShipmentController shipmentController;

    @Mock
    ShipmentServiceImpl shipmentService;

    @Test
    @DisplayName(value = "Test Get all shipments controller")
    public void TestGetAllShipments() {

        List<ShipmentDTO> shipmentDTOs     = new ArrayList<>();
        ShipmentResponse  shipmentResponse = new ShipmentResponse();
        when(shipmentService.getAll()).thenReturn(shipmentDTOs);
        ResponseEntity<ShipmentResponse> responseEntity = shipmentController.getAllShipments();
        verify(shipmentService).getAll();
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getCount(), is(shipmentResponse.getCount()));
    }

    @Test
    @DisplayName(value = "Test get shipment by name controller")
    public void TestGetShipmentByName() {

        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1").build();
        when(shipmentService.getByName(shipmentDTO.getName())).thenReturn(shipmentDTO);
        ResponseEntity<ShipmentResponse> responseEntity = shipmentController.getShipmentByName(shipmentDTO.getName());
        verify(shipmentService).getByName(shipmentDTO.getName());
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getCount(), is(1L));
        assertThat(responseEntity.getBody().getShipments().get(0).getName(), is(shipmentDTO.getName()));
    }

    @Test
    @DisplayName(value = "Test add shipment controller")
    public void TestAddShipment() {

        ShipmentRequest        shipmentRequest = new ShipmentRequest();
        ResponseEntity<String> responseEntity  = shipmentController.addShipment(shipmentRequest);
        verify(shipmentService).saveShipment(new ShipmentDTO());
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(responseEntity.getBody(), is(CommonConstants.CREATED_SHIPMENT));
    }

    @Test
    @DisplayName(value = "Test remove shipment controller")
    public void TestRemoveShipment() {

        ShipmentDTO            shipmentDTO    = ShipmentDTO.builder().name("SH-1").build();
        ResponseEntity<String> responseEntity = shipmentController.removeShipment(shipmentDTO.getName());
        verify(shipmentService).removeShipment(shipmentDTO.getName());
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), is(CommonConstants.DELETED_SHIPMENT));
    }

    @Test
    @DisplayName(value = "Test assign vehicle to shipment controller")
    public void testAssignVehicleToShipment() {

        ShipmentDTO shipmentDTO = new ShipmentDTO();
        when(shipmentService.assignVehicleToShipment(EMPTY_STRING, EMPTY_STRING)).thenReturn(shipmentDTO);
        ResponseEntity<ShipmentResponse> responseEntity = shipmentController.assignVehicleToShipment(EMPTY_STRING, EMPTY_STRING);
        verify(shipmentService).assignVehicleToShipment(EMPTY_STRING, EMPTY_STRING);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getCount(), is(1L));
    }

    @Test
    @DisplayName(value = "Test assign tariff to shipment controller")
    public void testAssignTariffToShipment() {

        ShipmentDTO shipmentDTO = new ShipmentDTO();
        when(shipmentService.assignTariffAndCalculateShipmentCost(EMPTY_STRING, EMPTY_STRING)).thenReturn(shipmentDTO);
        ResponseEntity<ShipmentResponse> responseEntity = shipmentController.assignTariffToShipment(EMPTY_STRING, EMPTY_STRING);
        verify(shipmentService).assignTariffAndCalculateShipmentCost(EMPTY_STRING, EMPTY_STRING);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getCount(), is(1L));
    }

    @Test
    @DisplayName(value = "Test calculate best (cheapest) shipping cost controller")
    public void testAssignVehicleTariffWithCheapestCost() {

        ShipmentDTO shipmentDTO = new ShipmentDTO();
        when(shipmentService.calculateBestShipmentCost(EMPTY_STRING)).thenReturn(shipmentDTO);
        ResponseEntity<ShipmentResponse> responseEntity = shipmentController.assignVehicleTariffWithCheapestCost(EMPTY_STRING);
        verify(shipmentService).calculateBestShipmentCost(EMPTY_STRING);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getCount(), is(1L));
    }

    @Test
    @DisplayName(value = "Test get most expensive shipment controller")
    public void testGetMostExpensiveShipment() {

        ShipmentDTO shipmentDTO = new ShipmentDTO();
        when(shipmentService.getMostExpensiveShipment()).thenReturn(shipmentDTO);
        ResponseEntity<ShipmentResponse> responseEntity = shipmentController.getMostExpensiveShipment();
        verify(shipmentService).getMostExpensiveShipment();
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getCount(), is(1L));
    }

}
