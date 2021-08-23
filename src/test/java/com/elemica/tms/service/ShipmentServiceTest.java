package com.elemica.tms.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.entity.Shipment;
import com.elemica.tms.entity.Tariff;
import com.elemica.tms.entity.Vehicle;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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

    private EntityMapper<Shipment, ShipmentDTO> shipmentDTOEntityMapper= new EntityMapper<>(
            Shipment.class, ShipmentDTO.class);

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
    public void testAssignVehicleToShipmentWhenTariffAssigned(){

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
    @DisplayName(value = "Test assign tariff to shipment when No vehicle is assigned to shipment")
    public void testAssignTariffAndCalculateShipmentCostWhenVehicleNotAssigned(){

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
        when(tariffService.getApplicableVehicleWithMinimumCapacity(tariffDTO,shipmentDTO.getWeight())).thenReturn(vehicleDTOTwo);

        ShipmentDTO shipmentDTOActual = shipmentService.assignTariffAndCalculateShipmentCost(shipmentDTO.getName(),tariffDTO.getName());

        assertThat(shipmentDTOActual.getVehicle().getName(), is(vehicleDTOTwo.getName()));
        assertThat(shipmentDTOActual.getCost(), Matchers.comparesEqualTo(new BigDecimal(18)));
        assertThat(shipmentDTOActual.getTariff().getName(), is(tariffDTO.getName()));
        verify(shipmentRepository).save(shipmentDTOEntityMapper.mapToDBObject(shipmentDTOActual));
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test assign tariff to shipment when other vehicle from same tariff is assigned to shipment")
    public void testAssignTariffAndCalculateShipmentCostWhenVehicleAssigned(){

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

        ShipmentDTO shipmentDTOActual = shipmentService.assignTariffAndCalculateShipmentCost(shipmentDTO.getName(),tariffDTO.getName());

        assertThat(shipmentDTOActual.getVehicle().getName(), is(vehicleDTOTwo.getName()));
        assertThat(shipmentDTOActual.getCost(), Matchers.comparesEqualTo(new BigDecimal(18)));
        assertThat(shipmentDTOActual.getTariff().getName(), is(tariffDTO.getName()));
        verify(shipmentRepository).save(shipmentDTOEntityMapper.mapToDBObject(shipmentDTOActual));
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test assign tariff to shipment when vehicle from other tariff is already assigned to shipment")
    public void testAssignTariffAndCalculateShipmentCostWhenVehicleAssignedFromOtherTariff(){

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

        TMSException exception = Assertions.assertThrows(TMSException.class, () -> {
            shipmentService.assignTariffAndCalculateShipmentCost(shipmentDTO.getName(), tariffDTO.getName());
        });
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_SHIPMENT_ASSIGN_TARIFF));
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test calculate best and cheapest shipment cost")
    public void testCalculateBestShipmentCost(){

        VehicleDTO vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        VehicleDTO vehicleDTOTwo = VehicleDTO.builder().name("V-2").capacity(new BigDecimal(15)).build();

        List<VehicleDTO> applicableVehicleDTOListOne = new ArrayList<>(1);
        applicableVehicleDTOListOne.add(vehicleDTOOne);

        List<VehicleDTO> applicableVehicleDTOListTwo = new ArrayList<>(1);
        applicableVehicleDTOListTwo.add(vehicleDTOTwo);

        TariffDTO tariffDTOOne = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).applicableVehicles(applicableVehicleDTOListOne).build();
        TariffDTO tariffDTOTwo = TariffDTO.builder().name("T-1").rate(new BigDecimal(5)).discount(new BigDecimal(5)).applicableVehicles(applicableVehicleDTOListTwo).build();
        List<TariffDTO> tariffDTOList = new ArrayList<>(2);
        tariffDTOList.add(tariffDTOOne);
        tariffDTOList.add(tariffDTOTwo);

        ShipmentDTO shipmentDTO = ShipmentDTO.builder().name("SH-1")
                                             .weight(new BigDecimal(2))
                                             .build();
        Optional<Shipment> shipmentDBObject = Optional.ofNullable(shipmentDTOEntityMapper.mapToDBObject(shipmentDTO));

        when(shipmentRepository.findById(shipmentDTO.getName())).thenReturn(shipmentDBObject);
        when(tariffService.getAll()).thenReturn(tariffDTOList);
        when(tariffService.getApplicableVehicleWithMinimumCapacity(tariffDTOTwo,shipmentDTO.getWeight())).thenReturn(vehicleDTOTwo);

        ShipmentDTO shipmentDTOActual = shipmentService.calculateBestShipmentCost(shipmentDTO.getName());

        assertThat(shipmentDTOActual.getVehicle().getName(), is(vehicleDTOTwo.getName()));
        assertThat(shipmentDTOActual.getCost(), Matchers.comparesEqualTo(new BigDecimal(9.5)));
        assertThat(shipmentDTOActual.getTariff().getName(), is(tariffDTOTwo.getName()));
        verify(shipmentRepository).save(shipmentDTOEntityMapper.mapToDBObject(shipmentDTOActual));
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test calculate best and cheapest shipment cost")
    public void testGetMostExpensiveShipment(){
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

        when(shipmentRepository.findAll()).thenReturn((shipmentDBObjectlist));

        ShipmentDTO shipmentDTOActual = shipmentService.getMostExpensiveShipment();

        assertThat(shipmentDTOActual.getName(), is(shipmentDTOTwo.getName()));
    }
}
