FROM dengyiping/scala-sbt-ubuntu:latest

RUN apt-get install -y git && git clone https://github.com/DengYiping/shuoshuo_scala.git
WORKDIR /shuoshuo_scala
RUN sbt compile

EXPOSE 8080
ENTRYPOINT ["sbt"]
CMD ["run"]