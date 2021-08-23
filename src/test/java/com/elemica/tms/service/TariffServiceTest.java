package com.elemica.tms.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.entity.Tariff;
import com.elemica.tms.exception.TMSException;
import com.elemica.tms.model.dto.TariffDTO;
import com.elemica.tms.model.dto.VehicleDTO;
import com.elemica.tms.repository.TariffRepository;
import com.elemica.tms.service.impl.TariffServiceImpl;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class TariffServiceTest {

    @InjectMocks
    TariffServiceImpl tariffService;

    @Mock
    TariffRepository tariffRepository;

    private EntityMapper<Tariff, TariffDTO> mapper = new EntityMapper<>(
            Tariff.class, TariffDTO.class);

    @Test
    @DisplayName(value = "Test get all tariffs")
    public void testGetAll() {

        TariffDTO tariffDTOOne = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).build();
        TariffDTO tariffDTOTwo = TariffDTO.builder().name("T-2").rate(new BigDecimal(50)).discount(new BigDecimal(20)).build();

        List<Tariff> TariffListFromDB = new ArrayList<>(2);
        TariffListFromDB.add(mapper.mapToDBObject(tariffDTOOne));
        TariffListFromDB.add(mapper.mapToDBObject(tariffDTOTwo));

        when(tariffRepository.findAll()).thenReturn(TariffListFromDB);
        List<TariffDTO> TariffDTOListActual = tariffService.getAll();
        assertThat(TariffDTOListActual.size(), is(TariffListFromDB.size()));
        verify(tariffRepository).findAll();
    }

    @Test
    @DisplayName(value = "Test get tariff by name")
    public void testGetByName() {

        TariffDTO        tariffDTOOne  = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).build();
        Optional<Tariff> vehicleFromDB = Optional.ofNullable(mapper.mapToDBObject(tariffDTOOne));
        when(tariffRepository.findById(tariffDTOOne.getName())).thenReturn(vehicleFromDB);
        TariffDTO TariffDTOActual = tariffService.getByName(tariffDTOOne.getName());
        assertThat(TariffDTOActual.getName(), is(tariffDTOOne.getName()));
        verify(tariffRepository).findById(tariffDTOOne.getName());
    }

    @Test
    @DisplayName(value = "Test get tariff by name with not available object")
    public void testGetByNameNoFound() {

        TMSException exception = Assertions.assertThrows(TMSException.class, () -> tariffService.getByName("NotAvailableObject"));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_TARIFF_NOT_FOUND));
    }

    @Test
    @DisplayName(value = "Test get tariff with null as name")
    public void testGetByNameNull() {

        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> tariffService.getByName(null));
        assertThat(exception.getMessage(), is("tariffName is marked non-null but is null"));
    }

    @Test
    @DirtiesContext
    @DisplayName(value = "Test save tariff")
    public void testSaveTariff() {

        TariffDTO        tariffDTOOne = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).build();
        Optional<Tariff> tariffFromDB = Optional.ofNullable(mapper.mapToDBObject(tariffDTOOne));
        when(tariffRepository.save(tariffFromDB.get())).thenReturn(tariffFromDB.get());
        tariffService.saveTariff(tariffDTOOne);
        verify(tariffRepository).save(mapper.mapToDBObject(tariffDTOOne));
    }

    @Test
    @DisplayName(value = "Test save tariff with null object")
    public void testSaveTariffNull() {

        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> tariffService.saveTariff(null));
        assertThat(exception.getMessage(), is("tariffDTO is marked non-null but is null"));
    }

    @Test
    @DisplayName(value = "Test save tariff when some unexpected error pops up")
    public void testSaveTariffException() {

        TariffDTO tariffDTOOne = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).build();
        Tariff    tariffFromDB = mapper.mapToDBObject(tariffDTOOne);
        doThrow(new TMSException("")).when(tariffRepository).save(tariffFromDB);
        Exception exception = Assertions.assertThrows(Exception.class, () -> tariffService.saveTariff(tariffDTOOne));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_CREATE_OBJECT));
        verify(tariffRepository).save(tariffFromDB);
    }


    @Test
    @DisplayName(value = "Test save tariff with invalid object")
    public void testSaveTariffInvalidObject() {

        Tariff tariffFromDB = new Tariff();
        tariffFromDB.setName("T-1");
        when(tariffRepository.save(tariffFromDB)).thenThrow(new TransactionSystemException(""));
        TMSException exception = Assertions.assertThrows(TMSException.class, () -> tariffService.saveTariff(mapper.convertToDTO(tariffFromDB)));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_CREATE_OBJECT_VALIDATION));
        verify(tariffRepository).save(tariffFromDB);
    }

    @Test
    @DisplayName(value = "Test remove tariff")
    public void testRemoveTariff() {

        doNothing().when(tariffRepository).deleteById("T-1");
        tariffService.removeTariff("T-1");
        verify(tariffRepository).deleteById("T-1");
    }

    @Test
    @DisplayName(value = "Test remove tariff with null as name")
    public void testRemoveTariffNull() {

        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> tariffService.removeTariff(null));
        assertThat(exception.getMessage(), is("tariffName is marked non-null but is null"));
    }

    @Test
    @DisplayName(value = "Test remove tariff when it is referenced with shipment")
    public void testRemoveReferencedTariff() {

        doThrow(new DataIntegrityViolationException("")).when(tariffRepository).deleteById("T-1");
        TMSException exception = Assertions.assertThrows(TMSException.class, () -> tariffService.removeTariff("T-1"));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_TARIFF_IN_USE));
        verify(tariffRepository).deleteById("T-1");
    }

    @Test
    @DisplayName(value = "Test remove tariff with name that does not exist in system")
    public void testRemoveTariffNotFound() {

        doThrow(new EmptyResultDataAccessException(1)).when(tariffRepository).deleteById("T-1");
        TMSException exception = Assertions.assertThrows(TMSException.class, () -> tariffService.removeTariff("T-1"));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_TARIFF_NOT_FOUND));
        verify(tariffRepository).deleteById("T-1");
    }

    @Test
    @DisplayName(value = "Test remove tariff when some unexpected error pops up")
    public void testRemoveTariffException() {

        doThrow(new TMSException("")).when(tariffRepository).deleteById("T-1");
        Exception exception = Assertions.assertThrows(Exception.class, () -> tariffService.removeTariff("T-1"));
        assertThat(exception.getMessage(), is(CommonConstants.ERROR_DELETE_OBJECT));
        verify(tariffRepository).deleteById("T-1");
    }

    @Test
    @DisplayName(value = "Test get applicable vehicle with minimum capacity")
    public void testGetApplicableVehicleWithMinimumCapacity() {

        VehicleDTO vehicleDTOOne = VehicleDTO.builder().name("V-1").capacity(new BigDecimal(50)).build();
        VehicleDTO vehicleDTOTwo = VehicleDTO.builder().name("V-2").capacity(new BigDecimal(15)).build();

        List<VehicleDTO> applicableVehicleDTOList = new ArrayList<>(2);
        applicableVehicleDTOList.add(vehicleDTOOne);
        applicableVehicleDTOList.add(vehicleDTOTwo);

        BigDecimal requiredCapacity = new BigDecimal(10);
        TariffDTO  tariffDTO        = TariffDTO.builder().name("T-1").rate(new BigDecimal(10)).discount(new BigDecimal(10)).applicableVehicles(applicableVehicleDTOList).build();

        VehicleDTO vehicleDTOActual = tariffService.getApplicableVehicleWithMinimumCapacity(tariffDTO, requiredCapacity);

        assertThat(vehicleDTOActual.getName(), is(vehicleDTOTwo.getName()));
        assertThat(vehicleDTOActual.getCapacity().compareTo(requiredCapacity), is(1));
    }

}
