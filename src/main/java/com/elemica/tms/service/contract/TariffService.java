package com.elemica.tms.service.contract;

import java.math.BigDecimal;
import java.util.List;

import com.elemica.tms.model.dto.TariffDTO;
import com.elemica.tms.model.dto.VehicleDTO;

import lombok.NonNull;

public interface TariffService {

    /**
     * This method will provide list of all available Tariffs in the DataBase.
     *
     * @return List of TariffDTO
     */
    public List<TariffDTO> getAll();

    /**
     *This method will get the Tariff object from DB based on given name.
     *
     * @param tariffName
     * @return TariffDTO
     */
    public TariffDTO getByName(@NonNull String tariffName);

    /**
     * This method will save or update the tariff object in DB.
     *
     * @param tariffDTO
     */
    public void saveTariff(@NonNull TariffDTO tariffDTO);

    /**
     * This method will remove the tariff from the DB based on tariff name.
     *
     * @param tariffName
     */
    public void removeTariff(@NonNull String tariffName);

    /**
     * This method will get the tariff's applicable vehicle with minimum capacity that fulfils the required capacity
     *
     * @param tariffDTO
     * @param requiredCapacity
     * @return VehicleDTO
     */
    public VehicleDTO getApplicableVehicleWithMinimumCapacity(@NonNull TariffDTO tariffDTO, @NonNull BigDecimal requiredCapacity);
}
