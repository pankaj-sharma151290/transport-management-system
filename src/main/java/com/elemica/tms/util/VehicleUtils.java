package com.elemica.tms.util;

import java.util.ArrayList;
import java.util.List;

import com.elemica.tms.model.dto.VehicleDTO;
import com.elemica.tms.model.resourceobject.VehicleRO;
import com.elemica.tms.model.resourceobject.VehicleResponse;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VehicleUtils {

    public VehicleResponse prepareResponse(@NonNull List<VehicleDTO> vehicleDTOList) {

        List<VehicleRO> vehicleROList = new ArrayList<>(vehicleDTOList.size());
        vehicleDTOList.forEach(vehicleDTO -> vehicleROList.add(new VehicleRO(vehicleDTO)));
        return new VehicleResponse(vehicleROList, vehicleROList.size());
    }

    public VehicleResponse prepareResponse(@NonNull VehicleDTO vehicleDTO) {

        List<VehicleRO> vehicleROList = new ArrayList<>(1);
        vehicleROList.add(new VehicleRO(vehicleDTO));
        return new VehicleResponse(vehicleROList, 1);
    }
}
