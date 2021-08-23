package com.elemica.tms.util;

import java.util.ArrayList;
import java.util.List;

import com.elemica.tms.model.dto.ShipmentDTO;
import com.elemica.tms.model.resourceobject.ShipmentRO;
import com.elemica.tms.model.resourceobject.ShipmentResponse;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ShipmentUtils {

    public ShipmentResponse prepareResponse(@NonNull List<ShipmentDTO> shipmentDTOList) {

        List<ShipmentRO> shipmentROList = new ArrayList<>(shipmentDTOList.size());
        shipmentDTOList.forEach(shipmentDTO -> shipmentROList.add(new ShipmentRO(shipmentDTO)));
        return new ShipmentResponse(shipmentROList, shipmentROList.size());
    }

    public ShipmentResponse prepareResponse(@NonNull ShipmentDTO shipmentDTO) {

        List<ShipmentRO> shipmentROList = new ArrayList<>(1);
        shipmentROList.add(new ShipmentRO(shipmentDTO));
        return new ShipmentResponse(shipmentROList, 1);
    }
}
