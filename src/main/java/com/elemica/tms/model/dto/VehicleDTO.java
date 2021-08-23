package com.elemica.tms.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.elemica.tms.model.resourceobject.VehicleRO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public @Data
class VehicleDTO {

    private String name;

    private BigDecimal capacity;

    private LocalDateTime createdDate;

    private LocalDateTime updateDate;

    public VehicleDTO(@NonNull VehicleRO vehicleRO) {

        this.name = vehicleRO.getName();
        this.capacity = vehicleRO.getCapacity();
    }
}
