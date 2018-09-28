# -----------------------------------------------------------------------------
# Build Image
# -----------------------------------------------------------------------------
FROM hseeberger/scala-sbt:8u181_2.12.6_1.2.3

WORKDIR /opt

# Run sbt update and sbt assembly separately so that Docker will cache
# the downloaded dependencies.

ADD project project/
RUN sbt -no-colors update

ADD build.sbt .
RUN sbt -no-colors update

ADD . .
RUN sbt -no-colors assembly

# -----------------------------------------------------------------------------
# Final Image
# -----------------------------------------------------------------------------
FROM openjdk:8u181-jre
COPY --from=0 /opt/target/meetup-034.jar /opt/meetup-034.jar
ENTRYPOINT ["java" , "-jar", "/opt/meetup-034.jar"]
