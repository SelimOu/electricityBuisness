FROM openjdk:11-jre-slim
WORKDIR /app
# Install mysql client so we can wait-for-mysql at container start
RUN apt-get update && apt-get install -y default-mysql-client bash && rm -rf /var/lib/apt/lists/*
# Copy the pre-built jar (built locally with `mvn -DskipTests package`)
COPY target/*.jar app.jar
COPY wait-for-mysql.sh /wait-for-mysql.sh
RUN chmod +x /wait-for-mysql.sh
EXPOSE 8080
ENTRYPOINT ["/wait-for-mysql.sh","mysql","3306","java","-jar","/app/app.jar"]
