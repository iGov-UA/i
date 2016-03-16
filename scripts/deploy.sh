#!/bin/bash

export LANG=en_US.UTF-8
echo "Информация по скрипту: для корректной работы скрипну нужно передать все необходимые параметры"
echo "Пример запуска скрипта в ручном режиме (аналогично запуску из Jenkins):"
echo "./deploy.sh true true 2016.03.14-13.26.22 alpha wf-central"
echo "В этом случае будет собран UI и backend. Первый параметр отвечает за сборку UI, второй за backend."

#Setting-up variables
bIncludeUI=$1 #Собираем UI?
bIncludeBack=$2 #Собираем backend?
sData=$3 #Дата переданная из Jenkins
sVersion=$4 #версия sVersion (alpha, beta, ...)
sProject=$5 #название проекта ### wf-central ### Нужна ли вообще эта переменная?
sHost=$6 #сервер на котором будет развернут проект
data=`date "+%Y.%m.%d-%H.%M.%S"`

TMP=TEMP=TMPDIR=/tmp/c_alpha && export TMPDIR TMP TEMP

#Compiling project
mkdir -p $TMP

#Compiling UI
if [ "$bIncludeUI" == "true" ]; then
	cd central-js
	npm cache clean
	npm install
	bower install
	npm install grunt-contrib-imagemin
	grunt build
	cd dist
	npm install --production
	#Uploading to the target server
	rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' /sybase/jenkins/data/jobs/central_alpha/workspace/central-js/dist/ sybase@$sHost:/sybase/.upload/central-js.$data/
fi

#Compiling Backend
if [ "$bIncludeBack" == "true" ]; then
	cd /sybase/jenkins/data/jobs/central_alpha/workspace
 
#	if [ "$bExcludeTest" ==  "true" ]; then
#	mvn -P $sVersion clean install site -U -DskipTests=true 
#else
#	mvn -P $sVersion clean install site -U
#fi
  
#if [ "$bExcludeTest" ==  "true" ]; then
#	mvn -P $sVersion clean install site -U -DskipTests=true -Ddependency.locations.enabled=false
#else
#	mvn -P $sVersion clean install site -U -Ddependency.locations.enabled=false
#fi
  
	sSuffixTest=""
	if [ "$bExcludeTest" ==  "true" ]; then
		#mvn -P alpha clean test
		sSuffixTest="-DskipTests=true"
		#mvn -P $sVersion clean install -DskipTests=true    
	#else
		#mvn -P alpha clean test
		#mvn -P $sVersion clean install -DskipTests=true    
		#mvn -P $sVersion clean install
	fi
	cd storage-static
	mvn -P $sVersion clean install $sSuffixTest
    cd ..
    cd storage-temp
    mvn -P $sVersion clean install $sSuffixTest
    cd ..
    cd wf-base
    mvn -P $sVersion clean install site $sSuffixTest -Ddependency.locations.enabled=false
    cd ..
    cd wf-central
	mvn -P $sVersion clean install site $sSuffixTest -Ddependency.locations.enabled=false
	
	#Uploading to the target server
	cd /sybase/jenkins/data/jobs/central_alpha/workspace/wf-central
	rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' target/wf-central.war sybase@$sHost:/sybase/.upload/
fi

#Connecting to remote host (Project deploy)
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $sHost << EOF

#Creating temporary directories
TMP=TEMP=TMPDIR=/tmp/c_$sVersion && export TMPDIR TMP TEMP
mkdir -p $TMP

#Deploy UI
if [ "$bIncludeUI" == "true" ]; then
	cd /sybase && pm2 stop central-js && pm2 delete central-js
	#Нужен ли бекап или тут 100% все будет работать с новой версией?
	rm -rf /sybase/central-js
	mv -f /sybase/.upload/central-js.$sData /sybase/central-js
	#mv -f /sybase/.upload/central-js.$data/dist /sybase/central-js
	cd /sybase/central-js
	#cp -f /sybase/.configs/central-js/index.js /sybase/central-js/server/config/index.js
	#cp -f /sybase/.configs/central-js/config/index.js /sybase/central-js/server/config/index.js
	#cp -f /sybase/.configs/central-js/config.js /sybase/central-js/server/config.js
	cp -f -R /sybase/.configs/central-js/* /sybase/central-js/
	pm2 start process.json --name central-js
	pm2 info central-js
fi

#Deploy backend
if [ "$bIncludeBack" == "true" ]; then
	#Копируем конфиг с одним хостом для проксирования
	cp -p /sybase/nginx/conf/sites/upstream1.conf.bak /sybase/nginx/conf/sites/upstream.conf
	sudo /sybase/nginx/sbin/nginx -s reload
	sleep 3
	#Выключаем томкат
	cd /sybase/tomcat_$sProject/bin/ && ./_shutdown_force.sh
	sleep 3
	#Удаляем старые бекапы
	rm -rf /sybase/.config-backup/$sProject/conf 
	rm -rf /sybase/.war-backup/$sProject/wf.war
	#Делаем новые бекапы
	cp -rp /sybase/tomcat_$sProject/conf/  /sybase/.config-backup/$sProject
	#Разворачиваем новые конфиги
	cp -rf /sybase/.configs/$sProject/* /sybase/tomcat_$sProject/conf/
	#Делаем бекап приложения
	cp -p /sybase/tomcat_$sProject/webapps/wf.war /sybase/.war-backup/$sProject
	#Очищаем папку с приложениями
	rm -r /sybase/tomcat_$sProject/webapps/**
	#Копируем новую версию приложения
	mv -f /sybase/.upload/$sProject.war /sybase/tomcat_$sProject/webapps/wf.war
	#Запускаем  томкат
	cd /sybase/tomcat_$sProject/bin/ && ./_startup.sh
	sleep 15

	#Проверяем на наличие ошибок
	if grep ERROR /sybase/tomcat_$sProject/logs/catalina.out | grep -v log4j | grep -v stopServer
		then
		echo "найдена ошибка возвращаемя к старой конфигурации"
		cd /sybase/tomcat_$sProject/bin/ && ./_shutdown.sh
		cp -rp /sybase/.config-backup/$sProject/conf/ /sybase/tomcat_$sProject/
		cp -p /sybase/.war-backup/$sProject/wf.war /sybase/tomcat_$sProject/webapps/
		cd /sybase/tomcat_$sProject/bin/ && ./_startup.sh
		sleep 15
		cp -p /sybase/nginx/conf/sites/upstream.conf.bak /sybase/nginx/conf/sites/upstream.conf
		sudo /sybase/nginx/sbin/nginx -s reload
	else
		echo "выставляю на следующий"
		#Копируем конфиг с одним хостом для проксирования? 
		cp -p /sybase/nginx/conf/sites/upstream2.conf.bak /sybase/nginx/conf/sites/upstream.conf
		sudo /sybase/nginx/sbin/nginx -s reload
		cd /sybase/tomcat_$sProject_duble/bin/ && ./_shutdown.sh
		sleep 3
		#Копируем конфиги и не меняем порт? Нужно сделать отдельный каталог с конфигами для дубля
		cp -rf /sybase/.configs/$sProject/* /sybase/tomcat_$sProject_duble/conf/
		rm -r /sybase/tomcat_$sProject_duble/webapps/**
		cp -p /sybase/tomcat_$sProject/webapps/wf.war /sybase/tomcat_$sProject_duble/webapps/wf.war
		cd /sybase/tomcat_$sProject_duble/bin/ && ./_startup.sh
		sleep 15
		#Зачем то еще раз копируем конфиг с одним хостом для проксирования?
		cp -p /sybase/nginx/conf/sites/upstream.conf.bak /sybase/nginx/conf/sites/upstream.conf
		sudo /sybase/nginx/sbin/nginx -s reload
	fi
fi

#Cleaning-up
rm -rf $TMP/*
EOF
rm -rf $TMP/*
