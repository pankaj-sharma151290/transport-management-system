package com.elemica.tms.service.contract;

import java.util.List;

import com.elemica.tms.model.dto.VehicleDTO;

import lombok.NonNull;

public interface VehicleService {

    /**
     * This method will provide list of all available Vehicles in the DataBase.
     *
     * @return List of VehicleDTO
     */
    public List<VehicleDTO> getAll();

    /**
     * This method will get the Vehicle object from DB based on given name.
     *
     * @param vehicleName
     * @return VehicleDTO
     */
    public VehicleDTO getByName(@NonNull String vehicleName);

    /**
     *This method will save or update the vehicle object in DB.
     *
     * @param vehicleDTO
     */
    public void saveVehicle(@NonNull VehicleDTO vehicleDTO);

    /**
     * This method will remove the vehicle from the DB based on vehicle name.
     *
     * @param vehicleName
     */
    public void removeVehicle(@NonNull String vehicleName);
}
