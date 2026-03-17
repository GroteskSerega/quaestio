# Layers
# Stage 1
FROM maven:4.0.0-rc-5-eclipse-temurin-25-alpine AS builder
WORKDIR /app

# Copy settings.xml
# COPY settings.xml /root/.m2/settings.xml

# Copy pom.xml and cache layers
COPY pom.xml .
COPY local-repo ./local-repo

RUN mvn dependency:go-offline -B

# Copy src
COPY src ./src
RUN mkdir -p .mvn
RUN mvn clean package
# RUN mvn clean package -DskipTests

RUN java -Djarmode=layertools -jar target/*.jar extract

# Stage 2
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy layers for cache
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# Start app
ENTRYPOINT ["java", \
            "-XX:MaxRAMPercentage=75.0", \
            "-XX:+UseG1GC", \
            "-XX:+UnlockExperimentalVMOptions", \
            "-Dspring.threads.virtual.enabled=true", \
            "org.springframework.boot.loader.launch.JarLauncher"]