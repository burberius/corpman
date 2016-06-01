# CorpMan, the EVE Online Corporation Manager Web Application

## Features
- POS Overview: Shows all your POSes, their modules, their content and 
the time left till they are empty or full.   
- Ratting Stats: Statistics over the killed rats, in which systems, which 
  types, by whom, income of the corp...

## Building
You need Node.js, JDK 8 and Maven to build. Steps:

```
npm install
./buildfrontend.sh
mvn clean install
```

## Installation
You just need a JRE 8 to run the JAR file:

```
java -jar corpman-VERSION.jar
```
As data storage you need a MySQL database, the settings are explained in the next step.

## Setting
Default settings in the package:
```
server.address=127.0.0.1
server.port=8080
spring.datasource.url=jdbc:mysql://localhost/corpman
spring.datasource.username=corpman
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.testOnBorrow=true
spring.datasource.validationQuery=SELECT 1
```
These can be overwritten in an extra *application.properties* 
file next to the jar on startup.

## Enhancements
If you want a special feature and are willing to pay for it, just contact me. Or file an
issue on GitHub and I will implement it, when I have time for it.
My current priority are general features and stuff I need!

## Contact
Bug reports or feature wishes can be filed as GitHub Issues:
https://github.com/burberius/corpman/issues

You can contact me in Eve Online by mail to the character *Burboom Prodac*.