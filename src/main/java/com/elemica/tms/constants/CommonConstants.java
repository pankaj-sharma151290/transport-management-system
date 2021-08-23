package com.elemica.tms.constants;

public interface CommonConstants {

    String EMPTY_STRING     = "";
    String SHIPMENT_SERVICE = "ShipmentService";
    String TARIFF_SERVICE   = "TariffService";
    String VEHICLE_SERVICE  = "VehicleService";

    String API_PATH_STATUS         = "/status";
    String API_PATH_SHIPMENT       = "/tms/shipment";
    String API_PATH_VEHICLE        = "/tms/vehicle";
    String API_PATH_TARIFF         = "/tms/tariff";
    String API_PATH_DELETE         = "/delete";
    String API_PATH_ADD            = "/add";
    String PATH_VEHICLE            = "/vehicle";
    String PATH_TARIFF             = "/tariff";
    String PATH_CALCULATE_SHIPMENT = "/autoCalculate";
    String PATH_EXPENSIVE_SHIPMENT = "/expensive";

    String PARAM_VEHICLE_NAME  = "vehicle_name";
    String PARAM_SHIPMENT_NAME = "shipment_name";
    String PARAM_TARIFF_NAME   = "tariff_name";
    String PARAM_NAME          = "name";
    String PATH_PARAM_NAME     = "/{name}";

    String APPLICATION_STATUS_MSG                 = "Transport Management System Application is running.";
    String CREATED_SHIPMENT                       = "Shipment is created.";
    String CREATED_VEHICLE                        = "Vehicle is created.";
    String CREATED_TARIFF                         = "Tariff is created.";
    String DELETED_SHIPMENT                       = "Shipment is Deleted.";
    String DELETED_VEHICLE                        = "Vehicle is Deleted.";
    String DELETED_TARIFF                         = "Tariff is Deleted.";
    String ERROR_DELETE_OBJECT                    = "Error while deleting object.";
    String ERROR_CREATE_OBJECT                    = "Error while creating object, please check input JSON.";
    String ERROR_CREATE_OBJECT_VALIDATION         = "Object can not be created Validation failed, Please check the input.";
    String ERROR_TARIFF_NOT_FOUND                 = "No tariff found with given name.";
    String ERROR_VEHICLE_NOT_FOUND                = "No vehicle found with given name.";
    String ERROR_VEHICLE_IN_USE                   = "Given vehicle can not be de deleted since it is assigned in one of tariffs.";
    String ERROR_TARIFF_IN_USE                    = "Given tariff can not be de deleted since it is assigned to active shipment.";
    String ERROR_SHIPMENT_NOT_FOUND               = "No shipment found with given name.";
    String ERROR_MAX_SHIPMENT_NOT_FOUND           = "There is no shipment found with shipment cost.";
    String ERROR_SHIPMENT_ASSIGN_TARIFF           = "Tariff can not be assigned to shipment as assigned vehicle to shipment is not available for given tariff.";
    String ERROR_SHIPMENT_ASSIGN_NO_TARIFF        = "No tariff available to assign for the shipment.";
    String ERROR_SHIPMENT_ASSIGN_VEHICLE          = "Given vehicle can not assign to shipment due to not enough capacity.";
    String ERROR_CALCULATION_TARIFF_RATE_NEGATIVE = "Calculations can not be done, Invalid tariff rate or shipment weight. Please check the shipment details.";
    String ERROR_CALCULATION_SHIPMENT_VEHICLE     = "Shipment calculations can not be done as no vehicle available with enough space with assigned tariff.";
    String ERROR_CALCULATION_SHIPMENT_COST        = "Cost can not be calculated, Please check assigned Tariff.";

}
