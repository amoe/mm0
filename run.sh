#! /bin/sh


get_main_class() {
    pom_namespace_uri="http://maven.apache.org/POM/4.0.0"
    xmlstarlet sel -N "n=${pom_namespace_uri}" \
      -t -v /n:project/n:properties/n:mainClass -n pom.xml
}


main_class=$(get_main_class)
yml_configuration=micromacro.yml
dropwizard_args="server ${yml_configuration}"

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

mvn compile exec:java \
  -Dexec.mainClass="$main_class" -Dexec.args="$dropwizard_args" -o
