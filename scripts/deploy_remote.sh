#!/bin/bash
sProject=$1 #
sDate=$2 #
nSecondsWait=$3 #
sVersion=$4 #

if [ -z $sProject ]; then
        echo "Empty Project variable"
        exit 1
fi
if [ -z $sVersion ]; then
        echo "Empty Version variable"
        exit 1
fi
if [ -z $sDate ]; then
        echo "Empty Date variable"
        exit 1
fi

if [ ! -d /sybase/.backup/configs/$sDate ]; then
        mkdir -p /sybase/.backup/configs/$sDate
fi
cp -rp /sybase/.configs /sybase/.backup/configs/$sDate/

sudo ssh-agent bash -c 'ssh-add /sybase/secret/sshKey; git clone git@github.com:e-government-ua/iSystem.git /sybase/iSystem'
rsync -rtv /sybase/iSystem/config/$sVersion/.configs/ /sybase/.configs
sudo rm -rf /sybase/iSystem

fallback ()
{
        echo "Fatal error! Executing fallback task..."
        #Убиваем процесс. Нет смысла ждать его корректной остановки.
        cd /sybase/tomcat_${sProject}$1/bin/ && ./_shutdown.sh
        #Удаляем новые конфиги
        rm -rf /sybase/tomcat_${sProject}$1/conf
        #Копируем старые конфиги обратно
        cp -rp /sybase/.backup/configs/${sProject}/tomcat_${sProject}$1/$sDate/conf /sybase/tomcat_${sProject}$1/
        #Очищаем папку с приложениями
        rm -rf /sybase/tomcat_${sProject}$1/webapps/*
        #Копируем обратно старое приложение
        cp -p /sybase/.backup/war/${sProject}/tomcat_${sProject}$1/$sDate/wf.war /sybase/tomcat_${sProject}$1/webapps/
        #Запускаем службу
        cd /sybase/tomcat_${sProject}$1/bin/ && ./_startup.sh
        sleep 15
        #Проверяем статус службы. Если нашлась ошибка в логе - завершаем скрипт с критической ошибкой.
        if grep ERROR /sybase/tomcat_${sProject}$1/logs/catalina.out | grep -v "but failOnError was false" | grep -v log4j | grep -v stopServer; then
                echo "Fatal error found in tomcat_${sProject}$1/logs/catalina.out! Can't start previous configuration."
                exit 1
        fi
        cat /sybase/.configs/nginx/${sProject}_upstream.conf > /sybase/nginx/conf/sites/${sProject}_upstream.conf
        sudo /sybase/nginx/sbin/nginx -s reload
        echo "Deployment failed. Previous configuration returned successfully."
        exit 1
}

#Создадим функцию для бекапа, т.к. для основного и вторичного инстанса действия идентичны
backup ()
{
        echo "Backuping tomcat_${sProject}"
        #Удаляем старые бекапы.
        IFS=$'\n' sBackupArray=( $(ls -1rt /sybase/.backup/configs/${sProject}/tomcat_${sProject}$1 | head -$[$(ls -l /sybase/.backup/configs/${sProject}/tomcat_${sProject}$1 | wc -l)-4]) )
        for sBackup in ${sBackupArray[@]}; do
                echo "deleting backup $sBackup"
                rm -rf "/sybase/.backup/configs/$sProject/tomcat_${sProject}$1/$sBackup"
        done
        unset IFS
        IFS=$'\n' sBackupArray=( $(ls -1rt /sybase/.backup/war/${sProject}/tomcat_${sProject}$1 | head -$[$(ls -l /sybase/.backup/war/${sProject}/tomcat_${sProject}$1 | wc -l)-4]) )
        for sBackup in ${sBackupArray[@]}; do
                echo "deleting backup $sBackup"
                rm -rf "/sybase/.backup/war/${sProject}/tomcat_${sProject}$1/$sBackup"
        done
        unset IFS
        #Делаем бекап конфигов
        if [ ! -d /sybase/.backup/configs/$sProject/tomcat_${sProject}$1/$sDate ]; then
                mkdir -p /sybase/.backup/configs/$sProject/tomcat_${sProject}$1/$sDate
        fi
        cp -rp /sybase/tomcat_${sProject}$1/conf /sybase/.backup/configs/$sProject/tomcat_${sProject}$1/$sDate/
        #Делаем бекап приложения
        if [ ! -d /sybase/.backup/war/$sProject/tomcat_${sProject}$1/$sDate ]; then
                mkdir -p /sybase/.backup/war/$sProject/tomcat_${sProject}$1/$sDate
        fi
        cp -p /sybase/tomcat_${sProject}$1/webapps/wf.war /sybase/.backup/war/$sProject/tomcat_${sProject}$1/$sDate/
}

#Функция по деплою томката. Для первичного и вторичного инстанса действия идентичны
deploy-tomcat ()
{
        if ps ax | grep ${sProject}$1 | grep -v grep; then
                cd /sybase/tomcat_${sProject}$1/bin/
                ./_shutdown.sh > /dev/null 2>&1
                sleep 5
        fi
        #Разворачиваем новые конфиги
        cp -rf /sybase/.configs/${sProject}/* /sybase/tomcat_${sProject}$1/conf/
        #Устанавливаем новую версию приложения
        rm -rf /sybase/tomcat_${sProject}$1/webapps/*
        cp -p /sybase/.upload/$sProject.war /sybase/tomcat_${sProject}$1/webapps/wf.war
        #Запускаем томкат
        cd /sybase/tomcat_${sProject}$1/bin/ && ./_startup.sh > /dev/null 2>&1
        sleep 15
}

if [ $sProject == "central-js" ]; then
        echo "Deploying project $sProject"
        cd /sybase
        pm2 stop central-js
        pm2 delete central-js
        #Делаем бекап старой версии
        if [ ! -d /sybase/.backup/$sProject ]; then
                mkdir -p /sybase/.backup/$sProject
        fi
        IFS=$'\n' sBackupArray=( $(ls -1rt /sybase/.backup/$sProject | head -$[$(ls -l /sybase/.backup/$sProject | wc -l)-4]) )
        for sBackup in ${sBackupArray[@]}; do
                echo "deleting backup $sBackup"
                rm -rf "/sybase/.backup/$sProject/$sBackup"
        done
        unset IFS
        mv -f /sybase/central-js /sybase/.backup/$sProject/$sDate
        cp -r /sybase/.upload/central-js /sybase/central-js
        cd /sybase/central-js
        cp -f -R /sybase/.configs/central-js/* /sybase/central-js/
        pm2 start process.json --name central-js --log /sybase/logs/central_front
        pm2 info central-js
fi

if [ $sProject == "dashboard-js" ]; then
        cd /sybase
        pm2 stop dashboard-js
        pm2 delete dashboard-js
        if [ ! -d /sybase/.backup/$sProject ]; then
                mkdir -p /sybase/.backup/$sProject
        fi
        IFS=$'\n' sBackupArray=( $(ls -1rt /sybase/.backup/$sProject | head -$[$(ls -l /sybase/.backup/$sProject | wc -l)-4]) )
        for sBackup in ${sBackupArray[@]}; do
                echo "deleting backup $sBackup"
                rm -rf "/sybase/.backup/$sProject/$sBackup"
        done
        unset IFS
        mv -f /sybase/dashboard-js /sybase/.backup/$sProject/$sDate
        mv /sybase/.upload/dashboard-js /sybase/dashboard-js
    #cp -f /sybase/.configs/dashboard-js/process.json /sybase/dashboard-js/process.json
    cp -f -R /sybase/.configs/dashboard-js/* /sybase/dashboard-js/
        cd /sybase/dashboard-js
        pm2 start process.json --name dashboard-js
        pm2 info dashboard-js
fi

if [ $sProject == "wf-central"  ] || [ $sProject == "wf-region" ]; then
        echo "Starting backup of DOUBLE"
        backup _double
        echo "Starting deploy of DOUBLE"
        deploy-tomcat _double

        nTimeout=0
        until grep -q "FrameworkServlet 'dispatcher': initialization completed in" /sybase/tomcat_${sProject}_double/logs/catalina.out || [ $nTimeout -eq $nSecondsWait ]; do
                ((nTimeout++))
                sleep 1
                echo "waiting for server startup $nTimeout"
                if [ $nTimeout -ge $nSecondsWait ]; then
                        echo "timeout reached"
                        cat /sybase/tomcat_${sProject}_double/logs/catalina.out | grep -v log4j | sed -n -e '/ERROR/,$p'
#			fallback _double
                        exit 1
                fi
                if grep "Destroying Web application" /sybase/tomcat_${sProject}_double/logs/catalina.out; then
                        echo "Tomcat started but application failed to start!"
                        echo "Restarting Tomcat..."
                        echo "======================================================="
                        /sybase/tomcat_${sProject}_double/bin/_shutdown.sh > /dev/null 2>&1
                        /sybase/tomcat_${sProject}_double/bin/_startup.sh > /dev/null 2>&1
                        break
                fi
        done

        nTimeout=0
        until grep -q "FrameworkServlet 'dispatcher': initialization completed in" /sybase/tomcat_${sProject}_double/logs/catalina.out || [ $nTimeout -eq $nSecondsWait ]; do
                ((nTimeout++))
                sleep 1
                echo "waiting for server startup $nTimeout"
                if grep ERROR /sybase/tomcat_${sProject}_double/logs/catalina.out | grep -v "but failOnError was false" | grep -v log4j | grep -v stopServer; then
                        cat /sybase/tomcat_${sProject}_double/logs/catalina.out | sed -n -e '/ERROR/,$p'
                        /sybase/tomcat_${sProject}_double/bin/_shutdown.sh > /dev/null 2>&1
                        exit 1
                fi
                if [ $nTimeout -ge $nSecondsWait ]; then
                        echo "timeout reached"
                        exit 1
                fi
        done

        echo "Everything is OK. Continuing deployment ..."
        cat /sybase/.configs/nginx/${sProject}_double_upstream.conf > /sybase/nginx/conf/sites/${sProject}_upstream.conf
        sudo /sybase/nginx/sbin/nginx -s reload
        backup
        deploy-tomcat
        if grep ERROR /sybase/tomcat_${sProject}/logs/catalina.out | grep -v "but failOnError was false" | grep -v log4j | grep -v stopServer; then
                grep -B 3 -A 2 ERROR /sybase/tomcat_${sProject}/logs/catalina.out
                fallback
        else
                echo "Everything is OK. Continuing deployment ..."
                cat /sybase/.configs/nginx/${sProject}_upstream.conf > /sybase/nginx/conf/sites/${sProject}_upstream.conf
                sudo /sybase/nginx/sbin/nginx -s reload

                #Разворачиваем приложение в основной инстанс
                #Сразу создадим бекапы
                backup

                #Развернем новое приложение на вторичном инстансе
                deploy-tomcat

                #Проверяем на наличие ошибок вторичный инстанс //
                if grep ERROR /sybase/tomcat_${sProject}/logs/catalina.out | grep -v "but failOnError was false" | grep -v log4j | grep -v stopServer; then
                        grep -B 3 -A 2 ERROR /sybase/tomcat_${sProject}/logs/catalina.out
                        #Откатываемся назад
                        fallback
                else
                        echo "Everything is OK. Continuing deployment ..."
                        cat /sybase/.configs/nginx/${sProject}_upstream.conf > /sybase/nginx/conf/sites/${sProject}_upstream.conf
                        sudo /sybase/nginx/sbin/nginx -s reload
                        cd /sybase/tomcat_${sProject}_double/bin/ && ./_shutdown.sh
                fi
                cd /sybase/tomcat_${sProject}_double/bin/ && ./_shutdown_force.sh
        fi
fi
