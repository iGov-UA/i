#!/bin/bash

export LANG=en_US.UTF-8
POS1="$1" #версия sVersion
POS2="$2" #название проекта saProject
POS3="$3" #адрес сервера

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no sybase@$POS3

TMP=TEMP=TMPDIR=/tmp/c_$POS1 && export TMPDIR TMP TEMP
mkdir -p $TMP

        if [ "$bIncludeBack" == "true" ]
                then
                cp -p /sybase/nginx/conf/sites/upstream1.conf.bak /sybase/nginx/conf/sites/upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload
                sleep 3
                cd /sybase/tomcat_$POS2/bin/ && ./_shutdown.sh
                sleep 3
                rm -rf /sybase/.config-backup/$POS2/conf.
                rm -rf /sybase/.war-backup/$POS2/wf.war
                cp -rp /sybase/tomcat_$POS2/conf/  /sybase/.config-backup/$POS2
                cp -rf /sybase/.configs/$POS2/* /sybase/tomcat_$POS2/conf/
                cp -p /sybase/tomcat_$POS2/webapps/wf.war /sybase/.war-backup/$POS2
                rm -r /sybase/tomcat_$POS2/webapps/**
                mv -f /sybase/.upload/$POS2.war /sybase/tomcat_$POS2/webapps/wf.war
                cd /sybase/tomcat_$POS2/bin/ && ./_startup.sh
                sleep 15
        fi

        if grep ERROR /sybase/tomcat_$POS2/logs/catalina.out | grep -v log4j | grep -v stopServer
                then
                echo "найдена ошибка"
                cd /sybase/tomcat_$POS2/bin/ && ./_shutdown.sh
                cp -rp /sybase/.config-backup/$POS2/conf/ /sybase/tomcat_$POS2/
                cp -p /sybase/.war-backup/$POS2/wf.war /sybase/tomcat_$POS2/webapps/
                cd /sybase/tomcat_$POS2/bin/ && ./_startup.sh
                sleep 15
                cp -p /sybase/nginx/conf/sites/upstream.conf.bak /sybase/nginx/conf/sites/upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload
        else
                echo "выставляю на следующий"
                cp -p /sybase/nginx/conf/sites/upstream2.conf.bak /sybase/nginx/conf/sites/upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload
                cd /sybase/tomcat_"$POS2"_duble/bin/ && ./_shutdown.sh
                sleep 3
                cp -rf /sybase/.configs/$POS2/* /sybase/tomcat_"$POS2"_duble/conf/
                rm -r /sybase/tomcat_"$POS2"_duble/webapps/**
                cp -p /sybase/tomcat_$POS2/webapps/wf.war /sybase/tomcat_"$POS2"_duble/webapps/wf.war
                cd /sybase/tomcat_"$POS2"_duble/bin/ && ./_startup.sh
                sleep 15
                cp -p /sybase/nginx/conf/sites/upstream.conf.bak /sybase/nginx/conf/sites/upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload
        fi
}
