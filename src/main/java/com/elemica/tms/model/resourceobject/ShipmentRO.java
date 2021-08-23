package com.elemica.tms.model.resourceobject;

import java.math.BigDecimal;
import java.util.Objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.elemica.tms.model.dto.ShipmentDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data
class ShipmentRO {

    @NotNull(message = "Shipment name cannot be null.")
    @NotBlank(message = "Shipment name cannot be Blank.")
    private String name;

    @NotNull(message = "Shipment Weight cannot be null.")
    @Positive(message = "Shipment Weight must be Positive.")
    private BigDecimal weight;

    private VehicleRO vehicle;

    private TariffRO tariff;

    @Min(0)
    private BigDecimal cost;

    public ShipmentRO(@NonNull ShipmentDTO shipmentDTO) {

        this.name = shipmentDTO.getName();
        this.weight = shipmentDTO.getWeight();
        this.cost = shipmentDTO.getCost();
        if(Objects.nonNull(shipmentDTO.getVehicle())) {
            this.vehicle = new VehicleRO(shipmentDTO.getVehicle());
        }
        if(Objects.nonNull(shipmentDTO.getTariff())) {
            this.tariff = new TariffRO(shipmentDTO.getTariff());
        }
    }

}
