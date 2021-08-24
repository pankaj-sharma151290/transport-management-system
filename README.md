# Pankaj Sharma

# Transport Management System (TMS)
This software is used by companies to manage their shipments in most effective way in terms of cost.

## Getting Started
The project is dockerized. 

There is no UI in the application therefore the application has detailed JSON response that helps users to uderstand about add details for vehicles, tariffs and shipments and managing shipments with differents tasks like calculating shipment with minimum cost automatically, etc...

## The application is built on below stack.

* Spring Boot
* Docker
* H2 Database (in memory)
* Swagger


### Prerequisites

Things required to run the application.

```
Docker
Maven
```

### How to run


```
Checkout the code from github : <https://github.com/pankaj-sharma151290/transport-management-system.git>

cd transport-management-system

mvn clean package

docker build --no-cache -t transport-management-system:1.0.0 .

docker run --name tms -p 8081:8081 -e "SERVICE_PORT=8081" transport-management-system:1.0.0
```

## Access application and database

### Swagger API
URL: http://localhost:8081/swagger-ui.html#/status-controller
### H2 Database
URL:http://localhost:8081/h2-console/
##### DB configurations:

- Driver Class: org.h2.Driver
- JDBC URL: jdbc:h2:mem:testdb
- username: sa
- Password: <Leave Blank>

## Test cases

The application has Unit Test as well as Integration Test written.

### Unit Test (coverage ~ 100%)

```
ShipmentControllerTest.java
TariffControllerTest.java
VehicleControllerTest.java
ShipmentServiceTest.java
TariffServiceTest.java
VehicleServiceTest.java
others...
```

### Integration Test

```
TransportManagementSystemApplicationTests.java
```
### Test Data
Initial test data will be available on application startup. Data.sql is maintained for the same.  

# API Details:
## There are 4 controllers in the application
* #### status-controller
* #### vehicle-controller
* #### tariff-controller
* #### shipment-controller

### 1. status-controller
This controller has API that will provide the status of the application.

- Controller URL : http://localhost:8081/swagger-ui.html#/status-controller
- API:  
    - GET : http://localhost:8081/status

###### RESPONSE
```
Transport Management System Application is running.
```


### 2. vehicle-controller
This controller has APIs that will allow user to manage the vehicle details like add, remove, update, list all vehicles etc...

Controller URL : http://localhost:8081/swagger-ui.html#/vehicle-controller

#### 2.1. List all vehicles available in the system

GET : http://localhost:8081/tms/vehicle

###### RESPONSE

```
{
  "vehicles": [
    {
      "name": "V-1",
      "capacity": 100
    },
    {
      "name": "V-2",
      "capacity": 50
    },
    {
      "name": "V-3",
      "capacity": 10
    },
    {
      "name": "V-4",
      "capacity": 150
    },
    {
      "name": "V-5",
      "capacity": 200
    }
  ],
  "count": 5
}
```
#### 2.2. Get vehicle details by its name

The API needs need vehicle name passed as path variable to the API. It gives details of the Vehicle. This can be used when user knows vehicle name and want to get the complete details of it.

GET : http://localhost:8081/tms/vehicle/{vehicle-name}

###### RESPONSE
```
{
  "vehicles": [
    {
      "name": "V-1",
      "capacity": 100
    }
  ],
  "count": 1
}
```

#### 2.3. Add or update vehicle
This API will add a new vehicle or will update the existing one.

PUT : http://localhost:8081/tms/vehicle/add

###### REQUEST
```
{
  "capacity": 0, // Required, It must be positive number.
  "name": "string" // Required, It can not be null or empty.
}
```
###### RESPONSE
```
Vehicle created
```


#### 2.4. Remove vehicle
This API will delete the existing vehicle if it is not linked with tariff or shipmnet. Vehicle name needs to be provided and query paraameter.

POST : http://localhost:8081/tms/vehicle/delete?name={vehicle-name}


###### RESPONSE
```
Vehicle deleted
```


### 3. tariff-controller
This controller has APIs that will allow used to manage the tariff details like add, remove, update, list all tariifs etc...

Controller URL : http://localhost:8081/swagger-ui.html#/tariff-controller

#### 3.1. List all tariffs available in the system

GET : http://localhost:8081/tms/tariff

###### RESPONSE

```
{
  "tariffs": [
    {
      "name": "T-1",
      "rate": 5,
      "discount": 5,
      "applicableVehicles": [
        {
          "name": "V-1",
          "capacity": 100
        },
        {
          "name": "V-2",
          "capacity": 50
        }
      ]
    },
    {
      "name": "T-2",
      "rate": 3,
      "discount": 0,
      "applicableVehicles": [
        {
          "name": "V-3",
          "capacity": 10
        },
        {
          "name": "V-4",
          "capacity": 150
        },
        {
          "name": "V-5",
          "capacity": 200
        }
      ]
    }
  ],
  "count": 2
}
```
#### 3.2. Get tariff details by its name
The API needs need tariff name passed as path variable to the API. It gives details of the tariff. This can be used when user knows tariff name and want to get the complete details of it.

GET : http://localhost:8081/tms/tariff/{tariff-name}

###### RESPONSE
```
{
  "tariffs": [
    {
      "name": "T-1",
      "rate": 5,
      "discount": 5,
      "applicableVehicles": [
        {
          "name": "V-1",
          "capacity": 100
        },
        {
          "name": "V-2",
          "capacity": 50
        }
      ]
    }
  ],
  "count": 1
}
```

#### 3.3. Add or update tariff
This API will add a new tariff or will update the existing one.

PUT : http://localhost:8081/tms/tariff/add

###### REQUEST
```
{
  "applicableVehicles": [
    {
      "capacity": 0, // Required, It must be positive number.
      "name": "string"// Required, It can not be null or empty
    }
  ],
  "discount": 0,  // Optional, value can be between 0-100
  "name": "string", // Required, It can not be null or empty
  "rate": 0 //Required, value can be  >= 0
}
```
###### RESPONSE
```
Tariff created
```

#### 3.4. Remove tariff
This API will delete the existing tariff if it is not linked with shipmnet. Tariff name needs to be provided and query parameter.
POST : http://localhost:8081/tms/tariff/delete?name=string

###### RESPONSE
```
Vehicle deleted
```

### 4. shipment-controller

This controller has APIs that will allow user to manage the shipment details like add, remove, update, list all shipments, calculate shipment etc...
Controller URL :

#### 4.1. List all shipment available in the system

GET : http://localhost:8081/tms/shipment

###### RESPONSE

```
{
  "shipments": [
    {
      "name": "SH-1",
      "weight": 5
    },
    {
      "name": "SH-2",
      "weight": 15
    },
    {
      "name": "SH-3",
      "weight": 25
    },
    {
      "name": "SH-4",
      "weight": 20
    },
    {
      "name": "SH-5",
      "weight": 10
    }
  ],
  "count": 5
}
```
#### 4.2. Get shipment details by its name

The API needs need shipment name passed as path variable to the API. It gives details of the shipment. This can be used when user knows shipment name and want to get the complete details of it.

GET : http://localhost:8081/tms/shipment/{shipment-name}

###### RESPONSE
```
{
  "shipments": [
    {
      "name": "SH-1",
      "weight": 5,
      "vehicle": {
        "name": "V-9",
        "capacity": 80
      },
      "tariff": {
        "name": "T-4",
        "rate": 2,
        "discount": 1,
        "applicableVehicles": [
          {
            "name": "V-8",
            "capacity": 110
          },
          {
            "name": "V-9",
            "capacity": 80
          }
        ]
      },
      "cost": 9.9
    }
  ],
  "count": 1
}
```

#### 4.3. Add or update shipment
This API will add a new shipment or will update the existing one.

PUT : http://localhost:8081/tms/shipment/add

###### REQUEST
```
{
  "name": "string", // Required, It can not be null or empty
  "weight": 0 //Required, must be positive number
}
```
###### RESPONSE
```
Shipment created
```

#### 4.4. Remove shipment
This API will delete the existing shipment. Shipment name needs to be provided and query parameter.

POST : http://localhost:8081/tms/shipment/delete?name={shipment-name}

###### RESPONSE
```
Shipment deleted
```

#### 4.5. Calculate cheapest shipment cost automaticaly
This API will calculate the cheapest shipment cost for the existing shipment and vehicle and tariff will be assigned to shipment accoirdingly. Shipment name needs to be provided and query parameter.

PUT :http://localhost:8081/tms/shipment/autoCalculate?shipment_name={shipment-name}

###### RESPONSE
```
{
  "shipments": [
    {
      "name": "SH-1",
      "weight": 5,
      "vehicle": {
        "name": "V-9",
        "capacity": 80
      },
      "tariff": {
        "name": "T-4",
        "rate": 2,
        "discount": 1,
        "applicableVehicles": [
          {
            "name": "V-8",
            "capacity": 110
          },
          {
            "name": "V-9",
            "capacity": 80
          }
        ]
      },
      "cost": 9.9
    }
  ],
  "count": 1
}
```

#### 4.6. Get most expensive shipment
This API will provide the most expensive shipment available in system based on its cost.

GET : http://localhost:8081/tms/shipment/expensive

###### RESPONSE
```
{
  "shipments": [
    {
      "name": "SH-1",
      "weight": 5,
      "vehicle": {
        "name": "V-9",
        "capacity": 80
      },
      "tariff": {
        "name": "T-4",
        "rate": 2,
        "discount": 1,
        "applicableVehicles": [
          {
            "name": "V-8",
            "capacity": 110
          },
          {
            "name": "V-9",
            "capacity": 80
          }
        ]
      },
      "cost": 9.9
    }
  ],
  "count": 1
}
```

#### 4.7. Assign tariff to shipment
This API will assign the tariff to shipment, calculate and save the shipment cost.
- if assigned vehicle of shipment is applicable for given tariff.
- if no vehicle assigned to shipment then this method will assign the given tariff and also assign the vehicle with minimum capacity from given tariff that fulfils the shipment weight.
This API needs shipment name and tariff name as query parameter.
     
PUT : http://localhost:8081/tms/shipment/tariff?shipment_name={shipment-name}&tariff_name={tariff-name}

###### RESPONSE
```
{
  "shipments": [
    {
      "name": "SH-1",
      "weight": 5,
      "vehicle": {
        "name": "V-9",
        "capacity": 80
      },
      "tariff": {
        "name": "T-4",
        "rate": 2,
        "discount": 1,
        "applicableVehicles": [
          {
            "name": "V-8",
            "capacity": 110
          },
          {
            "name": "V-9",
            "capacity": 80
          }
        ]
      },
      "cost": 9.9
    }
  ],
  "count": 1
}
```

#### 4.8. Assign vehicle to shipment
This API will assign the vehicle to shipment,
- if vehicle's capacity is enough for shipment's weight. 
- And if given vehicle is not belongs to applicable vehicles of assigned tariff then tariff will be removed from shipment and cost of shipment will become zero.
This API needs shipment name and vehicle name as query parameter.
     
PUT : http://localhost:8081/tms/shipment/vehicle?shipment_name={shipment-name}&vehicle_name={vehicle-name}

###### RESPONSE
```
{
  "shipments": [
    {
      "name": "SH-1",
      "weight": 5,
      "vehicle": {
        "name": "V-9",
        "capacity": 80
      },
      "tariff": {
        "name": "T-4",
        "rate": 2,
        "discount": 1,
        "applicableVehicles": [
          {
            "name": "V-8",
            "capacity": 110
          },
          {
            "name": "V-9",
            "capacity": 80
          }
        ]
      },
      "cost": 9.9
    }
  ],
  "count": 1
}
```