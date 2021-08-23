package com.elemica.tms.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.elemica.tms.model.resourceobject.ShipmentRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public @Data
class ShipmentDTO {

    private String name;

    private BigDecimal weight;

    private VehicleDTO vehicle;

    private TariffDTO tariff;

    private BigDecimal cost;

    private LocalDateTime createdDate;

    private LocalDateTime updateDate;

    public ShipmentDTO(@NonNull ShipmentRequest shipmentRequest) {

        this.name = shipmentRequest.getName();
        this.weight = shipmentRequest.getWeight();
    }

}
