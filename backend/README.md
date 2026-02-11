# backend (Spring Boot + Maven)

Java backend service built with **Spring Boot**, managed by
**Maven**, targeting **Java 21**.

------------------------------------------------------------------------

## Requirements

-   **Java 21 (JDK 21)**
-   **Maven 3.9+**
-   (Optional) Git

### Verify Installation

``` bash
java -version
mvn -version
```

Ensure Java version shows **21**.

------------------------------------------------------------------------

##  Build the Project

From the root directory (where `pom.xml` is located):

``` bash
mvn clean install
```

This will: - Compile the project - Run tests - Package the application
into a JAR file inside the `target/` directory

------------------------------------------------------------------------

## Run the Application

### Option 1: Run with Maven

``` bash
mvn spring-boot:run
```

### Option 2: Run the Packaged JAR

First build:

``` bash
mvn clean package
```

Then run:

``` bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

------------------------------------------------------------------------

## Access the Application

By default, Spring Boot runs on:

http://localhost:8080

To change the port, edit:

    src/main/resources/application.properties

Example:

``` properties
server.port=8081
```

------------------------------------------------------------------------

## 🧪 Run Tests

``` bash
mvn test
```

------------------------------------------------------------------------

##  Stop the Application

Press:

CTRL + C

in the terminal where the app is running.

------------------------------------------------------------------------

##  Lombok Configuration

This project uses Lombok for annotation processing.

If using an IDE: - Enable Annotation Processing - Install the Lombok
plugin (if required)

------------------------------------------------------------------------

##  Troubleshooting

### Port 8080 already in use

Change the port in `application.properties`.

### Wrong Java Version

Make sure `JAVA_HOME` points to JDK 21.
