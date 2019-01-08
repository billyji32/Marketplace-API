# backend-tech-assessment

Skeleton project for Backend Technical Assessment.

Includes
--------
- Maven - [pom.xml](pom.xml)
- Application properties - [application.yml](src/main/resources/application.yml)
- Runnable Spring Boot Application - [BackendTechAssessmentApplication](src/main/java/com/intuit/cg/backendtechassessment/BackendTechAssessmentApplication.java)
- REST endpoints - [RequestMappings.java](src/main/java/com/intuit/cg/backendtechassessment/controller/requestmappings/RequestMappings.java)

How to create entities
------------
Entities that aren't tied to other entities upon creation time, i.e. buyers and sellers, can simply be posted like so:  
//New buyer  
```curl -X POST http://localhost:8080/buyers -H 'Content-type:application/json' -d '{"firstName":"bill", "lastName":"ji", "email":"billji@gmail.com"}'```  

Entities that are tied to other entities upon creation time, i.e. bids and projects, require the href link in the request body like so:  
//New bid  
```curl -X POST http://localhost:8080/projects/1/bids -H 'Content-type:application/json' -d '{"amount":"100", "buyer":"http://localhost:8080/buyers/1"}'```  
//New project  
```curl -X POST http://localhost:8080/projects -H 'Content-type:application/json' -d '{"name":"dummyProj","description":"test project", "seller":"http://localhost:8080/sellers/1", "budget":"10000"}'```  

Traversal through entities is done through links. For example, this is the response from calling GET on a project:  
```{  
    "_links": {  
        "bids": {  
            "href": "http://localhost:8080/projects/3/bids"  
        },
        "lowestBid": {
            "href": "http://localhost:8080/projects/3/bids/lowest"
        },
        "self": {
            "href": "http://localhost:8080/projects/3"
        },
        "seller": {
            "href": "http://localhost:8080/sellers/1"
        }
    },
    "budget": 5000.0,
    "deadline": "2019-01-19T17:43:21",
    "description": "test project",
    "name": "dummyProj"
}```

