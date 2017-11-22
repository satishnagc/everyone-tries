# everyone-tries
This is spring boot application which can be executed by running the below command.
 <project main directory, this has the pom.xml> mvn spring-boot:run
 
 Prerequisite:
 1) Local instance of cassandra running on 127.0.0.1 default port.
 2) Execute the file everyone-tries/src/main/cassandra-schema.cql on the local cassandra database.
 
API Documentation 

1) Get Products API Call:
 
  curl -X GET -H "Cache-Control: no-cache" "http://localhost:8080/v1/products/13860428"

  Expected Response:
  {
    "current_price": {
      "value": 9.59,
      "currency_code": "USD"
    },
    "id": "13860428",
    "name": "The Big Lebowski (Blu-ray)"
  }
  
2) PUT API Call:

  curl -X PUT -H "Content-Type: application/json" -H "Cache-Control: no-cache" -d '{
  "current_price": {
    "value": 9.59,
    "currency_code": "USD"
  },
  "id": "13860428",
  "name": "The Big Lebowski (Blu-ray)"
}' "http://localhost:8080/v1/products/13860428"




