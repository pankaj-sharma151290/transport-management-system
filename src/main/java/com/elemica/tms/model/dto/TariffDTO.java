package com.elemica.tms.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

import com.elemica.tms.model.resourceobject.TariffRO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public @Data
class TariffDTO {

    @NotBlank
    private String name;

    private BigDecimal rate;

    private BigDecimal discount;

    private List<VehicleDTO> applicableVehicles;

    private LocalDateTime createdDate;

    private LocalDateTime updateDate;

    public TariffDTO(@NonNull TariffRO tariffRO) {

        this.name = tariffRO.getName();
        this.rate = tariffRO.getRate();
        this.discount = tariffRO.getDiscount();
        if(Objects.nonNull(tariffRO.getApplicableVehicles())) {
            this.applicableVehicles = new ArrayList<>(tariffRO.getApplicableVehicles().size());
            tariffRO.getApplicableVehicles().forEach(vehicleRO -> this.applicableVehicles.add(new VehicleDTO(vehicleRO)));
        }
    }

}
