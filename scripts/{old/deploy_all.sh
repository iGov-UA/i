#!/bin/bash

export LANG=en_US.UTF-8
sVersion="$1" #версия sVersion (alpha,beta,release)
saProject="$2" #название проекта saProject (wf-base,wf-region,wf-central) #dashboard-js,central-js
addrSer="$3" #адрес сервера
#$POS2() {
#}

if [ "$saProject" == "wf_central" ]; then

  #Сборка дашборда NodeJS
  if [ "$bIncludeUI_Dashboard" == "true" ]; then
      cd dashboard-js....
      npm install
      npm list grunt
      npm list grunt-google-cdn
      bower install
      npm install grunt-contrib-imagemin
      grunt build
      cd dist
      npm install --production

      rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' ../dist/ sybase@test.igov.org.ua:/sybase/.upload/dashboard-js.$data/
  fi

  #Сборка бека Maven
  if [ "$bIncludeBack" ==  "true" ]; then
      cd /sybase/jenkins/data/jobs/regional-$sVersion/workspace

    sSuffixTest=""
    if [ "$bExcludeTest" ==  "true" ]; then
      sSuffixTest="-DskipTests=true"
    fi

    ##########
      cd storage-static
          mvn -P $sVersion clean install $sSuffixTest
      cd ..
      cd storage-temp
          mvn -P $sVersion clean install $sSuffixTest
      cd ..
      cd wf-base
          mvn -P $sVersion clean install site $sSuffixTest -Ddependency.locations.enabled=false
      cd ..
      cd wf-region
          mvn -P $sVersion clean install site $sSuffixTest -Ddependency.locations.enabled=false
      cd ..
    ##########

      cd /sybase/jenkins/data/jobs/regional-$sVersion/workspace/wf-region
      rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' target/wf-region.war sybase@test.igov.org.ua:/sybase/.upload/
  fi


    if [ "$bIncludeBack" == "true" ]
    #ssh sybase@test.igov.org.ua << EOF
        cd /sybase/jenkins/data/jobs/central_"$sVersion"_it080992mna/workspace/scripts/
        sh deploy.sh wf-central
    fi

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no sybase@$addrSer << EOF

TMP=TEMP=TMPDIR=/tmp/r_$sVersion && export TMPDIR TMP TEMP
mkdir -p $TMP

  #Выставление дашборда на Node
  if [ "$bIncludeUI_Dashboard" == "true" ]; then
      cd /sybase
      pm2 stop dashboard-js
      pm2 delete dashboard-js
      rm -rf /sybase/dashboard-js
      mv /sybase/.upload/dashboard-js.$data /sybase/dashboard-js
      cp -f /sybase/.configs/dashboard-js/process.json /sybase/dashboard-js/process.json
      cd /sybase/dashboard-js
      pm2 start process.json --name dashboard-js
      pm2 info dashboard-js
  fi

  rm -rf $TMP/*
  EOF
  rm -rf $TMP/*
fi
