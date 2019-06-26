# bank-application

A sample bank application demonstrating the transfer of funds between accounts

## Pre-requisites
1) This application is written using Java 10. It is tested on openjdk 10.

2) Maven version (3.3.x) should be installed.

## Build and launch.
1) Clone the repository to your working directory.
2) Open the command promt and navigate to the cloned project folder.
3) Execute the below command to build the project.
   
   `mvn clean install`
4) After the project is successfully build, the target folder is created inside the project folder. Navigate to the target folder.
5) Execute the below command to launch the project

    `java -jar bank-application-0.0.1.jar`
    
    The above command will launch the application in the port 8090. Once the server is up, the REST API's could be fired using curl,           POSTMAN etc
    
## REST APIs
  The REST APIs are provided to simulate the transfer of funds functionality. Please refer the swagger file for more details.
  
## Assumptions and Improvements
1) The application starts on the port 8090, therefore any other application must not be running on this port.

2) Although the application demonstrate the createion/transfer of funds for any banking application. For the sake of simplicity, account      creation is kept to minimal.

3) No authentication/authorization is added.

4) In memory store is used to store accounts based on their accountNumber. This could be improved further by using any database for          persitence.

5) This could be further improved to simulate the banking application by adding more features such as Customer creation, mapping accounts to customers, maintaining the statuses of accounts and its type etc.
