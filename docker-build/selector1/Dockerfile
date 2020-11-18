FROM java:8-jdk-alpine
COPY lib /usr/app/lib
COPY src /usr/app/src
COPY send.me.xml /usr/app/
WORKDIR /usr/app
RUN mkdir /usr/app/bin
RUN  javac -cp lib/activemq-all-5.15.3.jar -d ./bin src/*.java
ENTRYPOINT ["java", "-cp", "./lib/activemq-all-5.15.3.jar:./bin", "AMQConsumer", "tag='selector1'"]
