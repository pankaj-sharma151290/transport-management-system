package com.elemica.tms.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.elemica.tms.model.resourceobject.ShipmentRO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public @Data
class ShipmentDTO {

    private String name;

    private BigDecimal weight;

    private VehicleDTO vehicle;

    private TariffDTO tariff;

    private BigDecimal cost;

    private LocalDateTime createdDate;

    private LocalDateTime updateDate;

    public ShipmentDTO(@NonNull ShipmentRO shipmentRO) {

        this.name = shipmentRO.getName();
        this.weight = shipmentRO.getWeight();
        this.cost = shipmentRO.getCost();
        if(Objects.nonNull(shipmentRO.getVehicle())) {
            this.vehicle = new VehicleDTO(shipmentRO.getVehicle());
        }
        if(Objects.nonNull(shipmentRO.getTariff())) {
            this.tariff = new TariffDTO(shipmentRO.getTariff());
        }
    }

}
