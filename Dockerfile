FROM registry.access.redhat.com/jboss-eap-6/eap64-openshift

MAINTAINER Vitor Silva Lima <vlima@redhat.com>

ADD standalone.xml /opt/eap/standalone/configuration/standalone.xml

ADD postgresql-9.4.1212.jar /opt/eap/standalone/deployments/postgresql-9.4.1212.jar
RUN touch /opt/eap/standalone/deployments/postgresql-9.4.1212.jar.dodeploy

ADD partner_timekeeper.war /opt/eap/standalone/deployments/partner_timekeeper.war
RUN touch /opt/eap/standalone/deployments/partner_timekeeper.war.dodeploy


RUN /opt/eap/bin/add-user.sh admin redhat@123 --silent

ENV JBOSS_HOME /opt/eap

EXPOSE 8080 9990 9999

ENTRYPOINT $JBOSS_HOME/bin/standalone.sh