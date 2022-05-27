#docker-compose up

FROM tomcat:8.5.79-jre8-openjdk-slim-buster

RUN mkdir /var/batman/ && \
	mkdir /var/batman/config/ && \
	mkdir /var/batman/log/

RUN mv webapps webapps2 && \
	mv webapps.dist/ webapps

COPY /batman-sample-impl/src/main/resources/* /var/batman/config/
COPY /batman-sample-war/target/batman-sample-war-0.0.2.war /usr/local/tomcat/webapps/batman.war

COPY /batman-sample-war/src/main/resources/tomcat/tomcat-users.xml /usr/local/tomcat/conf/
COPY /batman-sample-war/src/main/resources/tomcat/context.xml /usr/local/tomcat/webapps/manager/META-INF/
