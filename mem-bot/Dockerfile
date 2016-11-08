FROM java:latest
MAINTAINER volyx <volyihin@gmail.com>

RUN mkdir bot
ADD target/mem-bot-0.0.1-SNAPSHOT.jar /bot

WORKDIR /bot

EXPOSE 8080

CMD ["java", "-jar", "mem-bot-0.0.1-SNAPSHOT.jar"]
