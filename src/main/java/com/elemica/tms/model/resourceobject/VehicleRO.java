package com.elemica.tms.model.resourceobject;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.elemica.tms.model.dto.VehicleDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data
class VehicleRO {

    @NotNull(message = "Vehicle name cannot be null or Blank")
    @NotBlank
    private String name;

    @NotNull(message = "Vehicle capacity cannot be null")
    @Positive(message = "Vehicle Weight must be Positive.")
    private BigDecimal capacity;

    public VehicleRO(@NonNull VehicleDTO vehicleDTO) {

        this.name = vehicleDTO.getName();
        this.capacity = vehicleDTO.getCapacity();
    }
}
