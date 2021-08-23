package com.elemica.tms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.elemica.tms.constants.CommonConstants;
import com.elemica.tms.model.resourceobject.ShipmentRO;
import com.elemica.tms.model.resourceobject.ShipmentResponse;
import com.elemica.tms.model.resourceobject.TariffRO;
import com.elemica.tms.model.resourceobject.VehicleRO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransportManagementSystemApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName(value = "Application Status Test")
    void checkLoginStatus() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(CommonConstants.API_PATH_STATUS).accept(MediaType.TEXT_HTML_VALUE))
                                     .andExpect(status().isOk()).andReturn();
        assertEquals(CommonConstants.APPLICATION_STATUS_MSG, mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName(value = "Application Test : Add Vehicle|Tariff|Shipment|Calculate Shipment")
    @DirtiesContext
    void applicationIntegrationTest() throws Exception {

        VehicleRO vehicleRO1 = new VehicleRO("V-111", new BigDecimal(100));
        VehicleRO vehicleRO2 = new VehicleRO("V-222", new BigDecimal(50));

        TariffRO tariffRO1 = new TariffRO();
        tariffRO1.setName("T-111");
        tariffRO1.setDiscount(new BigDecimal(5));
        tariffRO1.setRate(new BigDecimal(10));
        List<VehicleRO> vehicleROList = new ArrayList<>(2);
        vehicleROList.add(vehicleRO1);
        vehicleROList.add(vehicleRO2);
        tariffRO1.setApplicableVehicles(vehicleROList);

        ShipmentRO shipmentRO = new ShipmentRO();
        shipmentRO.setName("SH-111");
        shipmentRO.setWeight(new BigDecimal(75));

        ResultActions addTariffResult = mockMvc
                .perform(MockMvcRequestBuilders.put(CommonConstants.API_PATH_TARIFF + CommonConstants.API_PATH_ADD).content(asJsonString(tariffRO1))
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        ResultActions addShipmentResult = mockMvc
                .perform(MockMvcRequestBuilders.put(CommonConstants.API_PATH_SHIPMENT + CommonConstants.API_PATH_ADD).content(asJsonString(shipmentRO))
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        ResultActions assignVehicleToShipmentResult = mockMvc
                .perform(MockMvcRequestBuilders.post(CommonConstants.API_PATH_SHIPMENT + CommonConstants.PATH_VEHICLE)
                                               .queryParam(CommonConstants.PARAM_SHIPMENT_NAME, shipmentRO.getName())
                                               .queryParam(CommonConstants.PARAM_VEHICLE_NAME, vehicleRO1.getName())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult assignTariffToShipmentResult = mockMvc
                .perform(MockMvcRequestBuilders.post(CommonConstants.API_PATH_SHIPMENT + CommonConstants.PATH_TARIFF)
                                               .queryParam(CommonConstants.PARAM_SHIPMENT_NAME, shipmentRO.getName())
                                               .queryParam(CommonConstants.PARAM_TARIFF_NAME, tariffRO1.getName())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ShipmentResponse shipmentResponse = objectMapper.readValue(assignTariffToShipmentResult.getResponse().getContentAsString(), ShipmentResponse.class);
        assertThat(shipmentResponse.getCount(), is(1L));
        assertThat(shipmentResponse.getShipments().size(), is(1));
        assertThat(shipmentResponse.getShipments().get(0).getName(), is(shipmentRO.getName()));
        assertThat(shipmentResponse.getShipments().get(0).getWeight(), Matchers.comparesEqualTo(shipmentRO.getWeight()));
        assertThat(shipmentResponse.getShipments().get(0).getCost(), Matchers.comparesEqualTo(new BigDecimal("712.50")));
        assertThat(shipmentResponse.getShipments().get(0).getTariff().getName(), is(tariffRO1.getName()));
        assertThat(shipmentResponse.getShipments().get(0).getVehicle().getName(), is(vehicleRO1.getName()));
    }

    public static String asJsonString(final Object obj) {

        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
