#!/usr/bin/env bash

ssh-add ~/.ssh/jrosenkranz
ssh-add -l
mvn versions:set -DnewVersion=0.0.1
mvn clean deploy -P sonatype-oss-release
ssh-add -D
# Then issue mvn versions:set -DnewVersion=<the new snapshot version>
