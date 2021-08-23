package com.elemica.tms.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.elemica.tms.model.dto.ShipmentDTO;
import com.elemica.tms.model.resourceobject.ShipmentRO;
import com.elemica.tms.model.resourceobject.ShipmentResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

@ExtendWith(SpringExtension.class)
public class ShipmentUtilsTest {

    private ShipmentUtils shipmentUtils;

    @Test
    @DisplayName("Test prepare response for shipmentDTO List")
    public void testPrepareResponse() {

        List<ShipmentDTO> shipmentDTOList = new ArrayList<>(2);
        shipmentDTOList.add(ShipmentDTO.builder().name("SH-1").updateDate(LocalDateTime.now()).build());
        shipmentDTOList.add(ShipmentDTO.builder().name("SH-2").updateDate(LocalDateTime.now()).build());
        ShipmentResponse shipmentResponse = shipmentUtils.prepareResponse(shipmentDTOList);
        assertThat(shipmentResponse.getCount(), is(2l));
        assertTrue( shipmentResponse.getShipments().get(0) instanceof ShipmentRO);
    }


}
