package com.elemica.tms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.entity.Vehicle;
import com.elemica.tms.exception.TMSException;
import com.elemica.tms.model.dto.VehicleDTO;
import com.elemica.tms.repository.VehicleRepository;
import com.elemica.tms.service.contract.VehicleService;
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
@Service(value = CommonConstants.VEHICLE_SERVICE)
public class VehicleServiceImpl implements VehicleService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private VehicleRepository vehicleRepository;

    private EntityMapper<Vehicle, VehicleDTO> mapper = new EntityMapper<Vehicle, VehicleDTO>(
            Vehicle.class, VehicleDTO.class);

    @Override
    public List<VehicleDTO> getAll() {

        List<VehicleDTO> vehicleDTOs = new ArrayList<>();
        vehicleRepository.findAll().forEach(vehicle -> vehicleDTOs.add(mapper.convertToDTO(vehicle)));
        return vehicleDTOs;
    }

    @Override
    public VehicleDTO getByName(@NonNull final String vehicleName) {

        Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleName);
        if(vehicle.isPresent()) {
            return mapper.convertToDTO(vehicle.get());
        } else {
            logger.error("No Vehicle object found with given name : {}", vehicleName);
            throw new TMSException(CommonConstants.ERROR_VEHICLE_NOT_FOUND);
        }
    }

    @Override
    public void saveVehicle(@NonNull final VehicleDTO vehicleDTO) {

        try {
            vehicleRepository.save(mapper.mapToDBObject(vehicleDTO));
        } catch(TransactionSystemException e) {
            logger.error("Vehicle can not be created Validation failed, Please check the input : {}", e);
            throw new TMSException(CommonConstants.ERROR_CREATE_OBJECT_VALIDATION);
        } catch(Exception e) {
            logger.error("Error while creating object", e);
            throw new TMSException(CommonConstants.ERROR_CREATE_OBJECT);
        }
    }

    @Override
    public void removeVehicle(@NonNull final String vehicleName) {

        try {
            vehicleRepository.deleteById(vehicleName);
        } catch(DataIntegrityViolationException e) {
            logger.error("Vehicle can not be de deleted since it is assigned to one of tariffs or shipments: {}", e);
            throw new TMSException(CommonConstants.ERROR_VEHICLE_IN_USE);
        } catch(EmptyResultDataAccessException e) {
            logger.error("No Vehicle object found with given name : {}, {}", vehicleName, e);
            throw new TMSException(CommonConstants.ERROR_VEHICLE_NOT_FOUND);
        } catch(Exception e) {
            logger.error("Error while deleting object", e);
            throw new TMSException(CommonConstants.ERROR_DELETE_OBJECT);
        }
    }
}