package com.elemica.tms.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.entity.Tariff;
import com.elemica.tms.exception.TMSException;
import com.elemica.tms.model.dto.TariffDTO;
import com.elemica.tms.model.dto.VehicleDTO;
import com.elemica.tms.repository.TariffRepository;
import com.elemica.tms.service.contract.TariffService;
import com.elemica.tms.util.EntityMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import lombok.NonNull;

@Primary
@Service(value = CommonConstants.TARIFF_SERVICE)
public class TariffServiceImpl implements TariffService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TariffRepository tariffRepository;

    private EntityMapper<Tariff, TariffDTO> mapper = new EntityMapper<>(
            Tariff.class, TariffDTO.class);

    @Override
    public List<TariffDTO> getAll() {

        List<TariffDTO> tariffDTOS = new ArrayList<>();
        tariffRepository.findAll().forEach(tariff -> tariffDTOS.add(mapper.convertToDTO(tariff)));
        return tariffDTOS;
    }

    @Override
    public TariffDTO getByName(@NonNull final String tariffName) {

        Optional<Tariff> tariff = tariffRepository.findById(tariffName);
        if(tariff.isPresent()) {
            return mapper.convertToDTO(tariff.get());
        } else {
            logger.error("No Tariff Object found with given name : {}", tariffName);
            throw new TMSException(CommonConstants.ERROR_TARIFF_NOT_FOUND);
        }
    }

    @Override
    public void saveTariff(@NonNull final TariffDTO tariffDTO) {

        try {
            tariffRepository.save(mapper.mapToDBObject(tariffDTO));
        } catch(TransactionSystemException e) {
            logger.error("Tariff can not be created Validation failed, Please check the input : {}", e);
            throw new TMSException(CommonConstants.ERROR_CREATE_OBJECT_VALIDATION);
        } catch(Exception e) {
            logger.error("Error while creating object", e);
            throw new TMSException(CommonConstants.ERROR_CREATE_OBJECT);
        }
    }

    @Override
    public void removeTariff(@NonNull final String tariffName) {

        try {
            tariffRepository.deleteById(tariffName);
        } catch(DataIntegrityViolationException e) {
            logger.error("Tariff can not be de deleted since it is assigned to active Shipment : {}", e);
            throw new TMSException(CommonConstants.ERROR_TARIFF_IN_USE);
        } catch(EmptyResultDataAccessException e) {
            logger.error("No tariff object found with given name : {}, {}", tariffName, e);
            throw new TMSException(CommonConstants.ERROR_TARIFF_NOT_FOUND);
        } catch(Exception e) {
            logger.error("Error while deleting object", e);
            throw new TMSException(CommonConstants.ERROR_DELETE_OBJECT);
        }
    }

    @Override
    public VehicleDTO getApplicableVehicleWithMinimumCapacity(@NonNull final TariffDTO tariffDTO, @NonNull final BigDecimal requiredCapacity) {

        return tariffDTO.getApplicableVehicles().stream()
                        .sorted(Comparator.comparing(VehicleDTO::getCapacity))
                        .filter(applicableVehicle -> applicableVehicle.getCapacity().compareTo(requiredCapacity) >= 0)
                        .findFirst().orElse(null);
    }
}