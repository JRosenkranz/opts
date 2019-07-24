#!/usr/bin/env bash

read -p "Really deploy to maven cetral repository  (yes/no)? "

if ( [ "$REPLY" == "yes" ] ) then
  ssh-add ~/.ssh/jrosenkranz
  ssh-add -l
  mvn release:clean release:prepare release:perform -B -e | tee maven-central-deploy.log
  ssh-add -D
else
  echo 'Exit without deploy'
fi
