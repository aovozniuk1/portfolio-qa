FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

# Copy pom.xml first to leverage Docker layer caching for dependencies
COPY pom.xml .
RUN mvn dependency:resolve dependency:resolve-plugins -q

# Copy source code
COPY src/ src/

# Default: run all tests in headless mode against Selenium Grid
ENTRYPOINT ["mvn", "clean", "test", "-Dheadless=true"]
