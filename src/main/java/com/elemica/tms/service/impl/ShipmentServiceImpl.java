package com.elemica.tms.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.entity.Shipment;
import com.elemica.tms.exception.TMSException;
import com.elemica.tms.model.dto.ShipmentDTO;
import com.elemica.tms.model.dto.TariffDTO;
import com.elemica.tms.model.dto.VehicleDTO;
import com.elemica.tms.repository.ShipmentRepository;
import com.elemica.tms.service.contract.ShipmentService;
import com.elemica.tms.service.contract.TariffService;
import com.elemica.tms.service.contract.VehicleService;
import com.elemica.tms.util.EntityMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import lombok.NonNull;

@Primary
@Service(value = CommonConstants.SHIPMENT_SERVICE)
public class ShipmentServiceImpl implements ShipmentService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private TariffService tariffService;

    private EntityMapper<Shipment, ShipmentDTO> mapper = new EntityMapper<>(
            Shipment.class, ShipmentDTO.class);

    @Override
    public List<ShipmentDTO> getAll() {

        List<ShipmentDTO> shipmentDTOs = new ArrayList<>();
        shipmentRepository.findAll().forEach(shipment -> shipmentDTOs.add(mapper.convertToDTO(shipment)));
        return shipmentDTOs;
    }

    @Override
    public ShipmentDTO getByName(@NonNull final String shipmentName) {

        Optional<Shipment> shipment = shipmentRepository.findById(shipmentName);
        if(shipment.isPresent()) {
            return mapper.convertToDTO(shipment.get());
        } else {
            logger.error("No Shipment object found with given name : {}", shipmentName);
            throw new TMSException(CommonConstants.ERROR_SHIPMENT_NOT_FOUND);
        }
    }

    @Override
    public void saveShipment(@NonNull final ShipmentDTO shipmentDTO) {

        try {
            shipmentRepository.save(mapper.mapToDBObject(shipmentDTO));
        } catch(TransactionSystemException e) {
            logger.error("Shipment can not be created Validation failed, Please check the input : {}", e);
            throw new TMSException(CommonConstants.ERROR_CREATE_OBJECT_VALIDATION);
        } catch(Exception e) {
            logger.error("Error while creating object : ", e);
            throw new TMSException(CommonConstants.ERROR_CREATE_OBJECT);
        }
    }

    @Override
    public void removeShipment(@NonNull final String shipmentName) {

        try {
            shipmentRepository.deleteById(shipmentName);
        } catch(EmptyResultDataAccessException e) {
            logger.error("No Shipment object found with given name : {}, {}", shipmentName, e);
            throw new TMSException(CommonConstants.ERROR_SHIPMENT_NOT_FOUND);
        } catch(Exception e) {
            logger.error("Error while deleting object : ", e);
            throw new TMSException(CommonConstants.ERROR_DELETE_OBJECT);
        }
    }

    @Override
    public ShipmentDTO assignVehicleToShipment(final String shipmentName, final String vehicleName) {

        ShipmentDTO shipmentDTO = this.getByName(shipmentName);
        VehicleDTO  vehicleDTO  = vehicleService.getByName(vehicleName);
        TariffDTO   tariffDTO   = shipmentDTO.getTariff();
        if(vehicleDTO.getCapacity().compareTo(shipmentDTO.getWeight()) > 0) {
            shipmentDTO.setVehicle(vehicleDTO);
            if(Objects.nonNull(tariffDTO)) {
                Optional<VehicleDTO> vehicleFromTariff = tariffDTO.getApplicableVehicles().stream().filter(vehicleDTO1 -> vehicleDTO1.getName().equals(vehicleName)).findAny();
                if(!vehicleFromTariff.isPresent()) {
                    shipmentDTO.setTariff(null);
                    shipmentDTO.setCost(BigDecimal.ZERO);
                }
            }
            this.saveShipment(shipmentDTO);
        } else {
            logger.error("Given vehicle can not assign to shipment due to not enough capacity: {}", shipmentDTO.getName());
            throw new TMSException(CommonConstants.ERROR_SHIPMENT_ASSIGN_VEHICLE);
        }
        return shipmentDTO;
    }

    @Override
    public ShipmentDTO assignTariffAndCalculateShipmentCost(final String shipmentName, final String tariffName) {

        ShipmentDTO shipmentDTO = this.getByName(shipmentName);
        TariffDTO   tariffDTO   = tariffService.getByName(tariffName);

        this.assignTariff(shipmentDTO, tariffDTO);
        shipmentDTO.setCost(this.calculateShipmentCost(shipmentDTO));
        this.saveShipment(shipmentDTO);
        return shipmentDTO;
    }

    @Override
    public ShipmentDTO calculateBestShipmentCost(final String shipmentName) {

        ShipmentDTO shipmentDTO = this.getByName(shipmentName);
        Optional<TariffDTO> cheapestTariffOptional = tariffService.getAll().stream()
                                                                  .sorted(Comparator.comparing(t -> calculateCostAfterDiscountPercentage(t.getRate(), t.getDiscount())))
                                                                  .filter(
                                                                          tariffDTO -> tariffDTO.getApplicableVehicles()
                                                                                                .stream()
                                                                                                .anyMatch(vehicleDTO -> vehicleDTO.getCapacity()
                                                                                                                                  .compareTo(shipmentDTO.getWeight()) >= 0)
                                                                         )
                                                                  .findFirst();
        if(cheapestTariffOptional.isPresent()) {
            TariffDTO cheapestTariffDTO = cheapestTariffOptional.get();
            this.assignTariff(shipmentDTO, cheapestTariffDTO);
            shipmentDTO.setCost(this.calculateShipmentCost(shipmentDTO));
            this.saveShipment(shipmentDTO);
        } else {
            logger.error("No Tariff available to assign for the shipment. {}", shipmentDTO.getName());
            throw new TMSException(CommonConstants.ERROR_SHIPMENT_ASSIGN_NO_TARIFF);
        }
        return shipmentDTO;
    }

    @Override
    public ShipmentDTO getMostExpensiveShipment() {

        Optional<ShipmentDTO> shipmentDTO = getAll().stream()
                                                    .filter(shipment -> Objects.nonNull(shipment.getCost()))
                                                    .collect(Collectors.toList())
                                                    .stream().sorted((s1, s2) -> s2.getCost().compareTo(s1.getCost()))
                                                    .findFirst();

        if(shipmentDTO.isPresent()) {
            return shipmentDTO.get();
        } else {
            logger.error("No max shipment object found.");
            throw new TMSException(CommonConstants.ERROR_MAX_SHIPMENT_NOT_FOUND);
        }
    }

    /**
     * This method will assign the tariff to shipment, - if assigned vehicle of shipment is applicable for given tariff. - if no vehicle assigned to shipment - then this method
     * will assign the given tariff and also assign the vehicle with minimum capacity from given tariff that fulfils the shipment weight.
     *
     * @param shipmentDTO
     * @param tariffDTO
     * @return ShipmentDTO
     */
    private ShipmentDTO assignTariff(ShipmentDTO shipmentDTO, TariffDTO tariffDTO) {

        if(Objects.isNull(shipmentDTO.getVehicle())) {
            VehicleDTO vehicleDTO = tariffService.getApplicableVehicleWithMinimumCapacity(tariffDTO, shipmentDTO.getWeight());
            if(Objects.nonNull(vehicleDTO)) {
                shipmentDTO.setVehicle(vehicleDTO);
                shipmentDTO.setTariff(tariffDTO);
            } else {
                logger.error("Shipment calculations can not be done as no vehicle available with enough space with assigned tariff : {}", shipmentDTO.getName());
                throw new TMSException(CommonConstants.ERROR_CALCULATION_SHIPMENT_VEHICLE);
            }
        } else {
            VehicleDTO vehicleDTO = shipmentDTO.getVehicle();
            if(tariffDTO.getApplicableVehicles().stream().anyMatch(applicableVehicle -> vehicleDTO.getName().equals(applicableVehicle.getName()))) {
                shipmentDTO.setTariff(tariffDTO);
            } else {
                logger.error("Tariff can not be assigned to shipment as assigned vehicle to Shipment is not available for given tariff: {}", shipmentDTO.getName());
                throw new TMSException(CommonConstants.ERROR_SHIPMENT_ASSIGN_TARIFF);
            }
        }
        return shipmentDTO;
    }

    /**
     * This method will calculate the shipment cost for given shipment.
     *
     * @param shipmentDTO
     */
    private BigDecimal calculateShipmentCost(ShipmentDTO shipmentDTO) {

        BigDecimal cost;
        TariffDTO  tariffDTO      = shipmentDTO.getTariff();
        BigDecimal tariffRate     = tariffDTO.getRate();
        BigDecimal shipmentWeight = shipmentDTO.getWeight();
        if(tariffRate.signum() >= 0 && shipmentWeight.signum() > 0) {
            cost = tariffDTO.getRate().multiply(shipmentDTO.getWeight());
            BigDecimal discountPercentage = tariffDTO.getDiscount();
            if(cost.signum() > 0 && discountPercentage.signum() > 0) {
                cost = calculateCostAfterDiscountPercentage(cost, discountPercentage);
            }
            if(cost.signum() >= 0) {
                return cost;
            } else {
                logger.error("Shipment cost can not be negative, Please check: {}", shipmentDTO.getName());
                throw new TMSException(CommonConstants.ERROR_CALCULATION_SHIPMENT_COST);
            }
        } else {
            logger.error("Calculations can not be done, Invalid tariff rate or shipment weight. Please check the shipment details: {}", shipmentDTO.getName());
            throw new TMSException(CommonConstants.ERROR_CALCULATION_TARIFF_RATE_NEGATIVE);
        }
    }

    /**
     * This method will calculate the cost after applying the discount
     *
     * @param cost
     * @param discountPercentage
     * @return BigDecimal
     */
    private BigDecimal calculateCostAfterDiscountPercentage(@NonNull BigDecimal cost, @NonNull BigDecimal discountPercentage) {

        BigDecimal value = cost.multiply(discountPercentage).divide(new BigDecimal("100"));
        value = cost.subtract(value);
        return value;
    }

}