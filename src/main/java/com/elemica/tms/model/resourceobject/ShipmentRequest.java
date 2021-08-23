package com.elemica.tms.model.resourceobject;

import java.math.BigDecimal;

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
class ShipmentRequest {

    @NotNull(message = "Shipment name cannot be null.")
    @NotBlank(message = "Shipment name cannot be Blank.")
    private String name;

    @NotNull(message = "Shipment Weight cannot be null.")
    @Positive(message = "Shipment Weight must be Positive.")
    private BigDecimal weight;

    public ShipmentRequest(@NonNull ShipmentDTO shipmentDTO) {

        this.name = shipmentDTO.getName();
        this.weight = shipmentDTO.getWeight();
    }

}
