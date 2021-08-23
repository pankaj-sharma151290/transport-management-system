package com.elemica.tms.model.resourceobject;

import java.util.List;

import com.elemica.tms.model.dto.TariffDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TariffResponse {

    private List<TariffRO> tariffs;
    private long           count;
}
