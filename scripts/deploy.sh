#!/bin/sh

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no sybase@test.igov.org.ua << EOF

TMP=TEMP=TMPDIR=/tmp/c_alpha && export TMPDIR TMP TEMP
mkdir -p $TMP

central() {
        if [ "$bIncludeBack" == "true" ]
                then
                cp -p /sybase/nginx/conf/sites/upstream1.conf.bak /sybase/nginx/conf/sites/upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload
                sleep 3
                cd /sybase/tomcat_wf-central/bin/ && ./_shutdown.sh
                sleep 3
                rm -rf /sybase/.config-backup/wf-central/conf.
                rm -rf /sybase/.war-backup/wf-central/wf.war
                cp -rp /sybase/tomcat_wf-central/conf/  /sybase/.config-backup/wf-central
                cp -rf /sybase/.configs/wf-central/* /sybase/tomcat_wf-central/conf/
                cp -p /sybase/tomcat_wf-central/webapps/wf.war /sybase/.war-backup/wf-central
                rm -r /sybase/tomcat_wf-central/webapps/**
                mv -f /sybase/.upload/wf-central.war /sybase/tomcat_wf-central/webapps/wf.war
                cd /sybase/tomcat_wf-central/bin/ && ./_startup.sh
                sleep 15
        fi

        if grep ERROR /sybase/tomcat_wf-central/logs/catalina.out | grep -v log4j | grep -v stopServer
                then
                echo "найдена ошибка"
                cd /sybase/tomcat_wf-central/bin/ && ./_shutdown.sh
                cp -rp /sybase/.config-backup/wf-central/conf/ /sybase/tomcat_wf-central/
                cp -p /sybase/.war-backup/wf-central/wf.war /sybase/tomcat_wf-central/webapps/
                cd /sybase/tomcat_wf-central/bin/ && ./_startup.sh
                sleep 15
                cp -p /sybase/nginx/conf/sites/upstream.conf.bak /sybase/nginx/conf/sites/upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload
        else
                echo "выставляю на следующий"
                cp -p /sybase/nginx/conf/sites/upstream2.conf.bak /sybase/nginx/conf/sites/upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload
                cd /sybase/tomcat_wf-central_duble/bin/ && ./_shutdown.sh
                sleep 3
                cp -rf /sybase/.configs/wf-central/* /sybase/tomcat_wf-central_duble/conf/
                rm -r /sybase/tomcat_wf-central_duble/webapps/**
                cp -p /sybase/tomcat_wf-central/webapps/wf.war /sybase/tomcat_wf-central_duble/webapps/wf.war
                cd /sybase/tomcat_wf-central_duble/bin/ && ./_startup.sh
                sleep 15
                cp -p /sybase/nginx/conf/sites/upstream.conf.bak /sybase/nginx/conf/sites/upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload
        fi
}

region() {
        if [ "$bIncludeBack" == "true" ]
                then
                cp -p /sybase/nginx/conf/sites/upstream1.conf.bak /sybase/nginx/conf/sites/upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload
                sleep 3
                cd /sybase/tomcat_wf-region/bin/ && ./_shutdown.sh
                sleep 3
                rm -rf /sybase/.config-backup/wf-region/conf.
                rm -rf /sybase/.war-backup/wf-region/wf.war
                cp -rp /sybase/tomcat_wf-region/conf/  /sybase/.config-backup/wf-region
                cp -rf /sybase/.configs/wf-region/* /sybase/tomcat_wf-region/conf/
                cp -p /sybase/tomcat_wf-region/webapps/wf.war /sybase/.war-backup/wf-region
                rm -r /sybase/tomcat_wf-region/webapps/**
                mv -f /sybase/.upload/wf-region.war /sybase/tomcat_wf-region/webapps/wf.war
                cd /sybase/tomcat_wf-region/bin/ && ./_startup.sh
                sleep 15
        fi

        if grep ERROR /sybase/tomcat_wf-region/logs/catalina.out | grep -v log4j | grep -v stopServer
                then
                echo "найдена ошибка"
                cd /sybase/tomcat_wf-region/bin/ && ./_shutdown.sh
                cp -rp /sybase/.config-backup/wf-region/conf/ /sybase/tomcat_wf-region/
                cp -p /sybase/.war-backup/wf-region/wf.war /sybase/tomcat_wf-region/webapps/
                cd /sybase/tomcat_wf-region/bin/ && ./_startup.sh
                sleep 15
                cp -p /sybase/nginx/conf/sites/upstream.conf.bak /sybase/nginx/conf/sites/upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload
        else
                echo "выставляю на следующий"
                cp -p /sybase/nginx/conf/sites/upstream2.conf.bak /sybase/nginx/conf/sites/upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload
                cd /sybase/tomcat_wf-region_duble/bin/ && ./_shutdown.sh
                sleep 3
                cp -rf /sybase/.configs/wf-region/* /sybase/tomcat_wf-region_duble/conf/
                rm -r /sybase/tomcat_wf-region_duble/webapps/**
                cp -p /sybase/tomcat_wf-region/webapps/wf.war /sybase/tomcat_wf-region_duble/webapps/wf.war
                cd /sybase/tomcat_wf-region_duble/bin/ && ./_startup.sh
                sleep 15
                cp -p /sybase/nginx/conf/sites/upstream.conf.bak /sybase/nginx/conf/sites/upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload
        fi
}


if [ "$bIncludeUI" == "true" ]; then
 cd /sybase && pm2 stop central-js && pm2 delete central-js
 rm -rf /sybase/central-js
 mv -f /sybase/.upload/central-js.$data /sybase/central-js
 ##mv -f /sybase/.upload/central-js.$data/dist /sybase/central-js
 cd /sybase/central-js
 #cp -f /sybase/.configs/central-js/index.js /sybase/central-js/server/config/index.js
 ##cp -f /sybase/.configs/central-js/config/index.js /sybase/central-js/server/config/index.js
 ##cp -f /sybase/.configs/central-js/config.js /sybase/central-js/server/config.js
 cp -f -R /sybase/.configs/central-js/* /sybase/central-js/
 pm2 start process.json --name central-js
 pm2 info central-js
fi

rm -rf $TMP/*
EOF
