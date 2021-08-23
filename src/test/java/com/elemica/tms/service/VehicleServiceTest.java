package com.elemica.tms.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.entity.Vehicle;
import com.elemica.tms.exception.TMSException;
import com.elemica.tms.model.dto.VehicleDTO;
import com.elemica.tms.repository.VehicleRepository;
import com.elemica.tms.service.impl.VehicleServiceImpl;
import com.elemica.tms.util.EntityMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class VehicleServiceTest {

    @InjectMocks
    VehicleServiceImpl vehicleService;

    @Mock
    VehicleRepository vehicleRepository;

    private EntityMapper<Vehicle, VehicleDTO> mapper = new EntityMapper<>(
            Vehicle.class, VehicleDTO.class);

    @Test
    @DisplayName(value = "Test get all vehicles")
    public void testGetAll() {

        VehicleDTO    vehicleDTOOne     = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        VehicleDTO    vehicleDTOTwo     = VehicleDTO.builder().name("V-2").capacity(new BigDecimal(15)).build();
        List<Vehicle> vehicleListFromDB = new ArrayList<>(2);
        vehicleListFromDB.add(mapper.mapToDBObject(vehicleDTOOne));
        vehicleListFromDB.add(mapper.mapToDBObject(vehicleDTOTwo));

        when(vehicleRepository.findAll()).thenReturn(vehicleListFromDB);
        List<VehicleDTO> vehicleDTOListActual = vehicleService.getAll();
        assertThat(vehicleDTOListActual.size(), is(vehicleListFromDB.size()));
        verify(vehicleRepository).findAll();
    }

    @Test
    @DisplayName(value = "Test get vehicle by name")
    public void testGetByName() {

        VehicleDTO        vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        Optional<Vehicle> vehicleFromDB = Optional.ofNullable(mapper.mapToDBObject(vehicleDTOOne));
        when(vehicleRepository.findById(vehicleDTOOne.getName())).thenReturn(vehicleFromDB);
        VehicleDTO vehicleDTOActual = vehicleService.getByName(vehicleDTOOne.getName());
        assertThat(vehicleDTOActual.getName(), is(vehicleDTOOne.getName()));
        verify(vehicleRepository).findById(vehicleDTOOne.getName());
    }

    @Test
    @DisplayName(value = "Test get vehicle by name with not available object")
    public void testGetByNameNoFound() {

        TMSException exception = Assertions.assertThrows(TMSException.class, () -> vehicleService.getByName("NotAvailableObject"));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_VEHICLE_NOT_FOUND));
    }

    @Test
    @DisplayName(value = "Test get vehicle with null as name")
    public void testGetByNameNull() {

        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> vehicleService.getByName(null));
        assertThat(exception.getMessage(), is("vehicleName is marked non-null but is null"));
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test save vehicle")
    public void testSaveVehicle() {

        VehicleDTO        vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        Optional<Vehicle> vehicleFromDB = Optional.ofNullable(mapper.mapToDBObject(vehicleDTOOne));
        when(vehicleRepository.save(vehicleFromDB.get())).thenReturn(vehicleFromDB.get());
        vehicleService.saveVehicle(vehicleDTOOne);
        verify(vehicleRepository).save(mapper.mapToDBObject(vehicleDTOOne));
    }

    @Test
    @DisplayName(value = "Test save vehicle with null object")
    public void testSaveVehicleNull() {

        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> vehicleService.saveVehicle(null));
        assertThat(exception.getMessage(), is("vehicleDTO is marked non-null but is null"));
    }

    @Test
    @DisplayName(value = "Test save vehicle when some unexpected error pops up")
    public void testSaveVehicleException() {

        VehicleDTO vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        doThrow(new TMSException("")).when(vehicleRepository).save(any(Vehicle.class));
        Exception exception = Assertions.assertThrows(Exception.class, () -> vehicleService.saveVehicle(vehicleDTOOne));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_CREATE_OBJECT));
        verify(vehicleRepository).save(mapper.mapToDBObject(vehicleDTOOne));
    }

    @Test
    @DisplayName(value = "Test save vehicle with invalid object")
    public void testSaveVehicleInvalidObject() {

        VehicleDTO        vehicleDTOWithoutCapacity = VehicleDTO.builder().name("V-1").build();
        Optional<Vehicle> vehicleFromDB             = Optional.ofNullable(mapper.mapToDBObject(vehicleDTOWithoutCapacity));
        when(vehicleRepository.save(vehicleFromDB.get())).thenThrow(new TransactionSystemException(""));
        TMSException exception = Assertions.assertThrows(TMSException.class, () -> vehicleService.saveVehicle(vehicleDTOWithoutCapacity));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_CREATE_OBJECT_VALIDATION));
        verify(vehicleRepository).save(mapper.mapToDBObject(vehicleDTOWithoutCapacity));
    }

    @Test
    @DisplayName(value = "Test remove vehicle")
    public void testRemoveVehicle() {

        doNothing().when(vehicleRepository).deleteById("V-1");
        vehicleService.removeVehicle("V-1");
        verify(vehicleRepository).deleteById("V-1");
    }

    @Test
    @DisplayName(value = "Test remove vehicle with null as name")
    public void testRemoveVehicleNull() {

        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> vehicleService.removeVehicle(null));
        assertThat(exception.getMessage(), is("vehicleName is marked non-null but is null"));
    }

    @Test
    @DisplayName(value = "Test remove vehicle when it is referenced with tariff or shipment")
    public void testRemoveReferencedVehicle() {

        doThrow(new DataIntegrityViolationException("")).when(vehicleRepository).deleteById(any());
        TMSException exception = Assertions.assertThrows(TMSException.class, () -> vehicleService.removeVehicle("V-1"));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_VEHICLE_IN_USE));
        verify(vehicleRepository).deleteById("V-1");
    }

    @Test
    @DisplayName(value = "Test remove vehicle with name that does not exist in system")
    public void testRemoveVehicleNotFound() {

        doThrow(new EmptyResultDataAccessException(1)).when(vehicleRepository).deleteById(any());
        TMSException exception = Assertions.assertThrows(TMSException.class, () -> vehicleService.removeVehicle("V-1"));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_VEHICLE_NOT_FOUND));
        verify(vehicleRepository).deleteById("V-1");
    }

    @Test
    @DisplayName(value = "Test remove vehicle when some unexpected error pops up")
    public void testRemoveVehicleException() {

        doThrow(new TMSException("")).when(vehicleRepository).deleteById(any());
        Exception exception = Assertions.assertThrows(Exception.class, () -> vehicleService.removeVehicle("V-1"));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_DELETE_OBJECT));
        verify(vehicleRepository).deleteById("V-1");
    }

}
