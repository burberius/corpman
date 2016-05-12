# CorpMan, the EVE Online Corporation Manager Web Application

## Features
- POS Overview: Shows all your POSes, their modules and it's content.   
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
