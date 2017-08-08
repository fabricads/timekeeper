FROM registry.access.redhat.com/jboss-eap-6/eap64-openshift

MAINTAINER Vitor Silva Lima <vlima@redhat.com>

RUN /opt/eap/bin/add-user.sh admin redhat@123 --silent

ENV JBOSS_HOME /opt/eap

EXPOSE 8080 8443 8778

ENTRYPOINT $JBOSS_HOME/bin/standalone.sh