# Imagem a partir do jar já buildado.
# O build não acontece dentro do Docker porque o service depende da interface pma:product,
# que só existe no repositório Maven local (instalada com ./mvnw install no repositório product).
#
#   ./mvnw clean package
#   docker build -t product .
#
# No compose do pma26 a imagem é buildada a partir do código-fonte pelo docker/java-service.Dockerfile.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/product-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
