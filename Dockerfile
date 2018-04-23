FROM openjdk:8-jre-alpine

ADD target/nyct-rt-proxy-1.0.7-SNAPSHOT-withAllDependencies.jar .
ADD config .
ADD start.sh .


RUN chmod +x start.sh
CMD ["./start.sh"]

EXPOSE 8001