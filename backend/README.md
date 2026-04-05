# backend (Spring Boot + Maven)

Java backend service built with **Spring Boot**, managed by
**Maven**, targeting **Java 21**.

------------------------------------------------------------------------

## Requirements

- **Java 21 (JDK 21)**
- **Maven 3.9+**
- (Optional) Git

### Verify Installation

``` bash
java -version
mvn -version
```

Ensure Java version shows **21**.

------------------------------------------------------------------------

## Build the Project

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

Spring Boot runs on: http://localhost:8080

------------------------------------------------------------------------

## 🧪 Run Tests

``` bash
mvn test
```

------------------------------------------------------------------------

## Stop the Application

Press:

CTRL + C

in the terminal where the app is running.

------------------------------------------------------------------------

## Lombok Configuration

This project uses Lombok for annotation processing.

If using an IDE: - Enable Annotation Processing - Install the Lombok
plugin (if required)

------------------------------------------------------------------------

## Troubleshooting

### Port 8080 already in use

Kill the process you have running on 8080 and try again.

### Wrong Java Version

Make sure `JAVA_HOME` points to JDK 21.

------------------------------------------------------------------------

### Format-on-save

To toggle format-on-save, go to
`Settings → Tools → Actions on Save → Reformat code`

