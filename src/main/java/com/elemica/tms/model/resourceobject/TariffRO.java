package com.elemica.tms.model.resourceobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.elemica.tms.model.dto.TariffDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data
class TariffRO {

    @NotNull(message = "Tariff name cannot be null")
    @NotBlank(message = "Tariff name cannot be Blank")
    private String name;

    @NotNull(message = "Tariff rate cannot be null")
    @Min(0)
    private BigDecimal rate;

    @Min(0)
    @Max(100)
    private BigDecimal discount;

    private List<VehicleRO> applicableVehicles;

    public TariffRO(@NonNull TariffDTO tariffDTO) {

        this.name = tariffDTO.getName();
        this.rate = tariffDTO.getRate();
        this.discount = tariffDTO.getDiscount();
        if(Objects.nonNull(tariffDTO.getApplicableVehicles())) {
            this.applicableVehicles = new ArrayList<>(tariffDTO.getApplicableVehicles().size());
            tariffDTO.getApplicableVehicles().forEach(vehicleDTO -> this.applicableVehicles.add(new VehicleRO(vehicleDTO)));
        }
    }

}
