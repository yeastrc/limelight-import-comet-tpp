FROM amazoncorretto:11.0.17

ADD build/libs/cometTPP2LimelightXML.jar  /usr/local/bin/cometTPP2LimelightXML.jar
ADD entrypoint.sh /usr/local/bin/entrypoint.sh
ADD cometTPP2LimelightXML /usr/local/bin/cometTPP2LimelightXML

RUN chmod 755 /usr/local/bin/entrypoint.sh && chmod 755 /usr/local/bin/cometTPP2LimelightXML
RUN yum update -y && yum install -y procps

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD []
