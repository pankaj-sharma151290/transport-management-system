package com.elemica.tms.service.contract;

import java.util.List;

import com.elemica.tms.model.dto.ShipmentDTO;

import lombok.NonNull;

public interface ShipmentService {

    /**
     * This method will provide list of all available shipments in the DataBase.
     *
     * @return List of ShipmentDTO
     */
    List<ShipmentDTO> getAll();

    /**
     * This method will get the shipment object from DB based on given name.
     *
     * @param shipmentName
     * @return
     */
    ShipmentDTO getByName(@NonNull String shipmentName);

    /**
     * This method will save or update the shipment object in DB.
     *
     * @param shipmentDTO
     */
    void saveShipment(@NonNull ShipmentDTO shipmentDTO);

    /**
     * This method will remove the vehicle from the DB based on shipment name.
     *
     * @param shipmentName
     */
    void removeShipment(@NonNull String shipmentName);

    /**
     * This method will assign the vehicle to shipment,
     * - if vehicle's weight capacity is enough for shipment weight.
     * - And if given vehicle is not belongs to applicable vehicles of assigned tariff then tariff will be removed from shipment
     *   and cost of shipment will become zero.
     *
     * @param shipmentName
     * @param vehicleName
     * @return ShipmentDTO
     */
    ShipmentDTO assignVehicleToShipment(final String shipmentName, final String vehicleName);

    /**
     * This method will assign the tariff to shipment, calculate and save the shipment cost.
     * - if assigned vehicle of shipment is applicable for given tariff.
     * - if no vehicle assigned to shipment
     *      - then this method will assign the given tariff and also assign the vehicle with minimum capacity from given tariff that fulfils the shipment weight.
     *
     * @param shipmentName
     * @param tariffName
     * @return ShipmentDTO
     */
    ShipmentDTO assignTariffAndCalculateShipmentCost(final String shipmentName, final String tariffName);

    /**
     * This method will calculate the best(cheapest) possible shipment cost and assign the tariff and vehicle to shipment
     *
     * @param shipmentName
     * @return ShipmentDTO
     */
    ShipmentDTO calculateBestShipmentCost(final String shipmentName);

    /**
     * This method will find the most expensive shipment available in the database.
     *
     * @return ShipmentDTO
     */
    ShipmentDTO getMostExpensiveShipment();


}
