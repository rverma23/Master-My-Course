FROM        glassfish:4.1-jdk8

WORKDIR     /var/app

ADD         glassfish-start.sh /

ADD         . /var/app/

CMD         []
ENTRYPOINT  ["bash", "/glassfish-start.sh"]

EXPOSE      8080 4848 8181
