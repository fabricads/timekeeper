FROM registry.access.redhat.com/jboss-eap-6/eap64-openshift

MAINTAINER Vitor Silva Lima <vlima@redhat.com>

#ADD postgresql-9.4.1212.jar /opt/eap/standalone/deployments/postgresql-9.4.1212.jar
#RUN touch /opt/eap/standalone/deployments/postgresql-9.4.1212.jar.dodeploy

ADD partner_timekeeper.war $JBOSS_HOME/standalone/deployments/ROOT.war
RUN touch $JBOSS_HOME/standalone/deployments/ROOT.war.dodeploy


RUN /opt/eap/bin/add-user.sh admin redhat@123 --silent

ENV JBOSS_HOME /opt/eap

EXPOSE 8080 9990 9999

ENTRYPOINT $JBOSS_HOME/bin/standalone.sh