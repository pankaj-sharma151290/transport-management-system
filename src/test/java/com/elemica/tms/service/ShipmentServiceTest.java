package com.elemica.tms.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.entity.Shipment;
import com.elemica.tms.exception.TMSException;
import com.elemica.tms.model.dto.ShipmentDTO;
import com.elemica.tms.model.dto.TariffDTO;
import com.elemica.tms.model.dto.VehicleDTO;
import com.elemica.tms.repository.ShipmentRepository;
import com.elemica.tms.service.impl.ShipmentServiceImpl;
import com.elemica.tms.service.impl.TariffServiceImpl;
import com.elemica.tms.service.impl.VehicleServiceImpl;
import com.elemica.tms.util.EntityMapper;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
public class ShipmentServiceTest {

    @InjectMocks
    ShipmentServiceImpl shipmentService;

    @Mock
    private VehicleServiceImpl vehicleService;

    @Mock
    private TariffServiceImpl tariffService;

    @Mock
    private ShipmentRepository shipmentRepository;

    private EntityMapper<Shipment, ShipmentDTO> shipmentDTOEntityMapper = new EntityMapper<>(
            Shipment.class, ShipmentDTO.class);

    @Test
    @DisplayName(value = "Test get shipment by name with not available object")
    public void testGetByNameNoFound() {

        TMSException exception = Assertions.assertThrows(TMSException.class, () -> shipmentService.getByName("NotAvailableObject"));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_SHIPMENT_NOT_FOUND));
    }

    @Test
    @DisplayName(value = "Test save shipment when some unexpected error pops up")
    public void testSaveShipmentException() {

        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1").build();
        doThrow(new TMSException("")).when(shipmentRepository).save(any(Shipment.class));
        Exception exception = Assertions.assertThrows(Exception.class, () -> shipmentService.saveShipment(shipmentDTO));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_CREATE_OBJECT));
        verify(shipmentRepository).save(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));
    }

    @Test
    @DisplayName(value = "Test save shipment with invalid object")
    public void testSaveShipmentInvalidObject() {

        ShipmentDTO        shipmentWithoutWeight = ShipmentDTO.builder().name("SH-1").build();
        Optional<Shipment> shipmentFromDB        = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentWithoutWeight));
        when(shipmentRepository.save(shipmentFromDB.get())).thenThrow(new TransactionSystemException(""));
        TMSException exception = Assertions.assertThrows(TMSException.class, () -> shipmentService.saveShipment(shipmentWithoutWeight));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_CREATE_OBJECT_VALIDATION));
        verify(shipmentRepository).save(shipmentDTOEntityMapper.mapToDBObject(shipmentWithoutWeight));
    }

    @Test
    @DisplayName(value = "Test remove shipment")
    public void testRemoveShipment() {

        doNothing().when(shipmentRepository).deleteById("S-1");
        shipmentService.removeShipment("S-1");
        verify(shipmentRepository).deleteById("S-1");
    }

    @Test
    @DisplayName(value = "Test remove shipment with null as name")
    public void testRemoveShipmentNull() {

        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> shipmentService.removeShipment(null));
        assertThat(exception.getMessage(), is("shipmentName is marked non-null but is null"));
    }

    @Test
    @DisplayName(value = "Test remove shipment with name that does not exist in system")
    public void testRemoveShipmentNotFound() {

        doThrow(new EmptyResultDataAccessException(1)).when(shipmentRepository).deleteById("S-1");
        TMSException exception = Assertions.assertThrows(TMSException.class, () -> shipmentService.removeShipment("S-1"));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_SHIPMENT_NOT_FOUND));
        verify(shipmentRepository).deleteById("S-1");
    }

    @Test
    @DisplayName(value = "Test remove shipment when some unexpected error pops up")
    public void testRemoveShipmentException() {

        doThrow(new TMSException("")).when(shipmentRepository).deleteById("S-1");
        Exception exception = Assertions.assertThrows(Exception.class, () -> shipmentService.removeShipment("S-1"));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_DELETE_OBJECT));
        verify(shipmentRepository).deleteById("S-1");
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test assign vehicle to shipment when there is no tariff is assigned to shipment")
    public void testAssignVehicleToShipmentWhenNoTariffAssigned() {

        VehicleDTO vehicleDTO = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1")
                                             .weight(new BigDecimal(2))
                                             .build();

        Optional<Shipment> shipmentDBObject = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));

        when(shipmentRepository.findById(shipmentDTO.getName())).thenReturn(shipmentDBObject);
        when(vehicleService.getByName(vehicleDTO.getName())).thenReturn(vehicleDTO);

        ShipmentDTO shipmentDTOActual = shipmentService.assignVehicleToShipment(shipmentDTO.getName(), vehicleDTO.getName());

        assertThat(shipmentDTOActual.getVehicle().getName(), is(vehicleDTO.getName()));
        verify(shipmentRepository).save(shipmentDTOEntityMapper.mapToDBObject(shipmentDTOActual));
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test assign vehicle to shipment when tariff is already assigned to shipment")
    public void testAssignVehicleToShipmentWhenTariffAssigned() {

        VehicleDTO vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        VehicleDTO vehicleDTOTwo = VehicleDTO.builder().name("V-2").capacity(new BigDecimal(15)).build();

        List<VehicleDTO> applicableVehicleDTOList = new ArrayList<>(2);
        applicableVehicleDTOList.add(vehicleDTOOne);

        TariffDTO tariffDTO = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).applicableVehicles(applicableVehicleDTOList).build();

        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1")
                                             .weight(new BigDecimal(2))
                                             .cost(new BigDecimal(50))
                                             .tariff(tariffDTO)
                                             .vehicle(vehicleDTOOne)
                                             .build();
        Optional<Shipment> shipmentDBObject = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));

        when(shipmentRepository.findById(shipmentDTO.getName())).thenReturn(shipmentDBObject);
        when(vehicleService.getByName(vehicleDTOTwo.getName())).thenReturn(vehicleDTOTwo);

        ShipmentDTO shipmentDTOActual = shipmentService.assignVehicleToShipment(shipmentDTO.getName(), vehicleDTOTwo.getName());

        assertThat(shipmentDTOActual.getVehicle().getName(), is(vehicleDTOTwo.getName()));
        assertThat(shipmentDTOActual.getCost(), Matchers.comparesEqualTo(BigDecimal.ZERO));
        Assert.assertNull(shipmentDTOActual.getTariff());
        verify(shipmentRepository).save(shipmentDTOEntityMapper.mapToDBObject(shipmentDTOActual));
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test assign vehicle to shipment when there is not capacity in vehicle")
    public void testAssignVehicleToShipmentNoTEnoughCapacity() {

        VehicleDTO vehicleDTO = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(10)).build();
        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1")
                                             .weight(new BigDecimal(20))
                                             .build();

        Optional<Shipment> shipmentDBObject = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));

        when(shipmentRepository.findById(shipmentDTO.getName())).thenReturn(shipmentDBObject);
        when(vehicleService.getByName(vehicleDTO.getName())).thenReturn(vehicleDTO);

        TMSException exception = Assertions.assertThrows(TMSException.class, () -> shipmentService.assignVehicleToShipment(shipmentDTO.getName(), vehicleDTO.getName()));

        assertThat(exception.getMessage(), is(CommonConstants.ERROR_SHIPMENT_ASSIGN_VEHICLE));
        verify(shipmentRepository).findById(shipmentDTO.getName());
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test assign tariff to shipment when No vehicle is assigned to shipment")
    public void testAssignTariffAndCalculateShipmentCostWhenVehicleNotAssigned() {

        VehicleDTO vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        VehicleDTO vehicleDTOTwo = VehicleDTO.builder().name("V-2").capacity(new BigDecimal(15)).build();

        List<VehicleDTO> applicableVehicleDTOList = new ArrayList<>(2);
        applicableVehicleDTOList.add(vehicleDTOOne);

        TariffDTO tariffDTO = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).applicableVehicles(applicableVehicleDTOList).build();

        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1")
                                             .weight(new BigDecimal(2))
                                             .build();
        Optional<Shipment> shipmentDBObject = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));

        when(shipmentRepository.findById(shipmentDTO.getName())).thenReturn(shipmentDBObject);
        when(tariffService.getByName(tariffDTO.getName())).thenReturn(tariffDTO);
        when(tariffService.getApplicableVehicleWithMinimumCapacity(tariffDTO, shipmentDTO.getWeight())).thenReturn(vehicleDTOTwo);

        ShipmentDTO shipmentDTOActual = shipmentService.assignTariffAndCalculateShipmentCost(shipmentDTO.getName(), tariffDTO.getName());

        assertThat(shipmentDTOActual.getVehicle().getName(), is(vehicleDTOTwo.getName()));
        assertThat(shipmentDTOActual.getCost(), Matchers.comparesEqualTo(new BigDecimal(18)));
        assertThat(shipmentDTOActual.getTariff().getName(), is(tariffDTO.getName()));
        verify(shipmentRepository).save(shipmentDTOEntityMapper.mapToDBObject(shipmentDTOActual));
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test assign tariff to shipment when there is not enough capacity in applicable vehicles of given tariff")
    public void testAssignTariffAndCalculateShipmentCostNotEnoughCapacityVehicle() {

        TariffDTO tariffDTO = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).build();

        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1")
                                             .weight(new BigDecimal(70))
                                             .build();
        Optional<Shipment> shipmentDBObject = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));

        when(shipmentRepository.findById(shipmentDTO.getName())).thenReturn(shipmentDBObject);
        when(tariffService.getByName(tariffDTO.getName())).thenReturn(tariffDTO);
        when(tariffService.getApplicableVehicleWithMinimumCapacity(tariffDTO, shipmentDTO.getWeight())).thenReturn(null);

        TMSException exception = Assertions.assertThrows(TMSException.class, () -> shipmentService.assignTariffAndCalculateShipmentCost(shipmentDTO.getName(), tariffDTO.getName()));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_CALCULATION_SHIPMENT_VEHICLE));
        verify(shipmentRepository).findById(shipmentDTO.getName());
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test assign tariff to shipment when tariff has invalid discount value")
    public void testAssignTariffAndCalculateShipmentCostNegativeShipmentWeight() {

        VehicleDTO vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();

        List<VehicleDTO> applicableVehicleDTOList = new ArrayList<>(2);
        applicableVehicleDTOList.add(vehicleDTOOne);

        TariffDTO tariffDTO = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(150)).applicableVehicles(applicableVehicleDTOList).build();

        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1")
                                             .weight(new BigDecimal(10))
                                             .build();
        Optional<Shipment> shipmentDBObject = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));

        when(shipmentRepository.findById(shipmentDTO.getName())).thenReturn(shipmentDBObject);
        when(tariffService.getByName(tariffDTO.getName())).thenReturn(tariffDTO);
        when(tariffService.getApplicableVehicleWithMinimumCapacity(tariffDTO, shipmentDTO.getWeight())).thenReturn(vehicleDTOOne);

        TMSException exception = Assertions.assertThrows(TMSException.class, () -> shipmentService.assignTariffAndCalculateShipmentCost(shipmentDTO.getName(), tariffDTO.getName()));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_CALCULATION_SHIPMENT_COST));
        verify(shipmentRepository).findById(shipmentDTO.getName());
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test assign tariff to shipment when there is not enough capacity in applicable vehicles of given tariff")
    public void testAssignTariffAndCalculateShipmentCostInvalidDiscountValue() {

        VehicleDTO vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();

        List<VehicleDTO> applicableVehicleDTOList = new ArrayList<>(2);
        applicableVehicleDTOList.add(vehicleDTOOne);

        TariffDTO tariffDTO = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).applicableVehicles(applicableVehicleDTOList).build();

        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1")
                                             .weight(new BigDecimal(-10))
                                             .build();
        Optional<Shipment> shipmentDBObject = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));

        when(shipmentRepository.findById(shipmentDTO.getName())).thenReturn(shipmentDBObject);
        when(tariffService.getByName(tariffDTO.getName())).thenReturn(tariffDTO);
        when(tariffService.getApplicableVehicleWithMinimumCapacity(tariffDTO, shipmentDTO.getWeight())).thenReturn(vehicleDTOOne);

        TMSException exception = Assertions.assertThrows(TMSException.class, () -> shipmentService.assignTariffAndCalculateShipmentCost(shipmentDTO.getName(), tariffDTO.getName()));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_CALCULATION_TARIFF_RATE_NEGATIVE));
        verify(shipmentRepository).findById(shipmentDTO.getName());
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test assign tariff to shipment when other vehicle from same tariff is assigned to shipment")
    public void testAssignTariffAndCalculateShipmentCostWhenVehicleAssigned() {

        VehicleDTO vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        VehicleDTO vehicleDTOTwo = VehicleDTO.builder().name("V-2").capacity(new BigDecimal(15)).build();

        List<VehicleDTO> applicableVehicleDTOList = new ArrayList<>(2);
        applicableVehicleDTOList.add(vehicleDTOOne);
        applicableVehicleDTOList.add(vehicleDTOTwo);

        TariffDTO tariffDTO = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).applicableVehicles(applicableVehicleDTOList).build();

        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1")
                                             .weight(new BigDecimal(2))
                                             .vehicle(vehicleDTOTwo)
                                             .build();
        Optional<Shipment> shipmentDBObject = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));

        when(shipmentRepository.findById(shipmentDTO.getName())).thenReturn(shipmentDBObject);
        when(tariffService.getByName(tariffDTO.getName())).thenReturn(tariffDTO);

        ShipmentDTO shipmentDTOActual = shipmentService.assignTariffAndCalculateShipmentCost(shipmentDTO.getName(), tariffDTO.getName());

        assertThat(shipmentDTOActual.getVehicle().getName(), is(vehicleDTOTwo.getName()));
        assertThat(shipmentDTOActual.getCost(), Matchers.comparesEqualTo(new BigDecimal(18)));
        assertThat(shipmentDTOActual.getTariff().getName(), is(tariffDTO.getName()));
        verify(shipmentRepository).save(shipmentDTOEntityMapper.mapToDBObject(shipmentDTOActual));
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test assign tariff to shipment when vehicle from other tariff is already assigned to shipment")
    public void testAssignTariffAndCalculateShipmentCostWhenVehicleAssignedFromOtherTariff() {

        VehicleDTO vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        VehicleDTO vehicleDTOTwo = VehicleDTO.builder().name("V-2").capacity(new BigDecimal(15)).build();

        List<VehicleDTO> applicableVehicleDTOList = new ArrayList<>(2);
        applicableVehicleDTOList.add(vehicleDTOTwo);

        TariffDTO tariffDTO = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).applicableVehicles(applicableVehicleDTOList).build();

        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1")
                                             .weight(new BigDecimal(2))
                                             .vehicle(vehicleDTOOne)
                                             .build();
        Optional<Shipment> shipmentDBObject = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));

        when(shipmentRepository.findById(shipmentDTO.getName())).thenReturn(shipmentDBObject);
        when(tariffService.getByName(tariffDTO.getName())).thenReturn(tariffDTO);

        TMSException exception = Assertions.assertThrows(TMSException.class, () -> shipmentService.assignTariffAndCalculateShipmentCost(shipmentDTO.getName(), tariffDTO.getName()));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_SHIPMENT_ASSIGN_TARIFF));
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test calculate best and cheapest shipment cost")
    public void testCalculateBestShipmentCost() {

        VehicleDTO vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        VehicleDTO vehicleDTOTwo = VehicleDTO.builder().name("V-2").capacity(new BigDecimal(15)).build();

        List<VehicleDTO> applicableVehicleDTOListOne = new ArrayList<>(1);
        applicableVehicleDTOListOne.add(vehicleDTOOne);

        List<VehicleDTO> applicableVehicleDTOListTwo = new ArrayList<>(1);
        applicableVehicleDTOListTwo.add(vehicleDTOTwo);

        TariffDTO tariffDTOOne = TariffDTO.builder()
                                          .name("T-1")
                                          .rate(new BigDecimal(10))
                                          .discount(new BigDecimal(10))
                                          .applicableVehicles(applicableVehicleDTOListOne)
                                          .build();
        TariffDTO       tariffDTOTwo  = TariffDTO.builder().name("T-1").rate(new BigDecimal(5)).discount(new BigDecimal(5)).applicableVehicles(applicableVehicleDTOListTwo).build();
        List<TariffDTO> tariffDTOList = new ArrayList<>(2);
        tariffDTOList.add(tariffDTOOne);
        tariffDTOList.add(tariffDTOTwo);

        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1")
                                             .weight(new BigDecimal(2))
                                             .build();
        Optional<Shipment> shipmentDBObject = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));

        when(shipmentRepository.findById(shipmentDTO.getName())).thenReturn(shipmentDBObject);
        when(tariffService.getAll()).thenReturn(tariffDTOList);
        when(tariffService.getApplicableVehicleWithMinimumCapacity(tariffDTOTwo, shipmentDTO.getWeight())).thenReturn(vehicleDTOTwo);

        ShipmentDTO shipmentDTOActual = shipmentService.calculateBestShipmentCost(shipmentDTO.getName());

        assertThat(shipmentDTOActual.getVehicle().getName(), is(vehicleDTOTwo.getName()));
        assertThat(shipmentDTOActual.getCost(), Matchers.comparesEqualTo(new BigDecimal("9.5")));
        assertThat(shipmentDTOActual.getTariff().getName(), is(tariffDTOTwo.getName()));
        verify(shipmentRepository).save(shipmentDTOEntityMapper.mapToDBObject(shipmentDTOActual));
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test calculate best and cheapest shipment cost when There is not suitable tariff available")
    public void testCalculateBestShipmentCostWithNoTariffAvailable() {

        VehicleDTO vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        VehicleDTO vehicleDTOTwo = VehicleDTO.builder().name("V-2").capacity(new BigDecimal(15)).build();

        List<VehicleDTO> applicableVehicleDTOListOne = new ArrayList<>(1);
        applicableVehicleDTOListOne.add(vehicleDTOOne);

        List<VehicleDTO> applicableVehicleDTOListTwo = new ArrayList<>(1);
        applicableVehicleDTOListTwo.add(vehicleDTOTwo);

        TariffDTO tariffDTOOne = TariffDTO.builder()
                                          .name("T-1")
                                          .rate(new BigDecimal(10))
                                          .discount(new BigDecimal(10))
                                          .applicableVehicles(applicableVehicleDTOListOne)
                                          .build();
        TariffDTO       tariffDTOTwo  = TariffDTO.builder().name("T-1").rate(new BigDecimal(5)).discount(new BigDecimal(5)).applicableVehicles(applicableVehicleDTOListTwo).build();
        List<TariffDTO> tariffDTOList = new ArrayList<>(2);
        tariffDTOList.add(tariffDTOOne);
        tariffDTOList.add(tariffDTOTwo);

        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1")
                                             .weight(new BigDecimal(100))
                                             .build();
        Optional<Shipment> shipmentDBObject = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));

        when(shipmentRepository.findById(shipmentDTO.getName())).thenReturn(shipmentDBObject);
        when(tariffService.getAll()).thenReturn(tariffDTOList);
        when(tariffService.getApplicableVehicleWithMinimumCapacity(tariffDTOTwo, shipmentDTO.getWeight())).thenReturn(vehicleDTOTwo);


        TMSException exception = Assertions.assertThrows(TMSException.class, () -> shipmentService.calculateBestShipmentCost(shipmentDTO.getName()));

        assertThat(exception.getMessage(), is(CommonConstants.ERROR_SHIPMENT_ASSIGN_NO_TARIFF));
        verify(shipmentRepository).findById(shipmentDTO.getName());
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test get most expensive shipment")
    public void testGetMostExpensiveShipment() {

        ShipmentDTO shipmentDTOOne = ShipmentDTO.builder().name("SH-1")
                                                .weight(new BigDecimal(2))
                                                .cost(new BigDecimal(15))
                                                .build();
        ShipmentDTO shipmentDTOTwo = ShipmentDTO.builder().name("SH-2")
                                                .weight(new BigDecimal(2))
                                                .cost(new BigDecimal(25))
                                                .build();

        List<Shipment> shipmentDBObjectlist = new ArrayList<>(2);
        shipmentDBObjectlist.add(shipmentDTOEntityMapper.mapToDBObject(shipmentDTOOne));
        shipmentDBObjectlist.add(shipmentDTOEntityMapper.mapToDBObject(shipmentDTOTwo));

        when(shipmentRepository.findAll()).thenReturn(( shipmentDBObjectlist ));

        ShipmentDTO shipmentDTOActual = shipmentService.getMostExpensiveShipment();
        assertThat(shipmentDTOActual.getName(), is(shipmentDTOTwo.getName()));
        verify(shipmentRepository).findAll();
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test get most expensive shipment when there is no shipment with cost calculated")
    public void testGetMostExpensiveShipmentWithNoShipmentsAvailable() {

        ShipmentDTO shipmentDTOOne = ShipmentDTO.builder().name("SH-1")
                                                .weight(new BigDecimal(2))
                                                .build();
        ShipmentDTO shipmentDTOTwo = ShipmentDTO.builder().name("SH-2")
                                                .weight(new BigDecimal(2))
                                                .build();

        List<Shipment> shipmentDBObjectlist = new ArrayList<>(2);
        shipmentDBObjectlist.add(shipmentDTOEntityMapper.mapToDBObject(shipmentDTOOne));
        shipmentDBObjectlist.add(shipmentDTOEntityMapper.mapToDBObject(shipmentDTOTwo));

        when(shipmentRepository.findAll()).thenReturn(( shipmentDBObjectlist ));

        TMSException exception = Assertions.assertThrows(TMSException.class, () -> shipmentService.getMostExpensiveShipment());

        assertThat(exception.getMessage(), is(CommonConstants.ERROR_MAX_SHIPMENT_NOT_FOUND));
        verify(shipmentRepository).findAll();
    }
}
