package com.elemica.tms.util;

import java.util.ArrayList;
import java.util.List;

import com.elemica.tms.model.dto.TariffDTO;
import com.elemica.tms.model.resourceobject.TariffRO;
import com.elemica.tms.model.resourceobject.TariffResponse;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TariffUtils {

    public TariffResponse prepareResponse(@NonNull List<TariffDTO> tariffDTOList) {

        List<TariffRO> tariffROList = new ArrayList<>(tariffDTOList.size());
        tariffDTOList.forEach(tariffDTO -> tariffROList.add(new TariffRO(tariffDTO)));
        return new TariffResponse(tariffROList, tariffROList.size());
    }

    public TariffResponse prepareResponse(@NonNull TariffDTO tariffDTO) {

        List<TariffRO> tariffROList = new ArrayList<>(1);
        tariffROList.add(new TariffRO(tariffDTO));
        return new TariffResponse(tariffROList, 1);
    }

}
