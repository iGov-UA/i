#!/bin/bash

#Setting-up variables
export LANG=en_US.UTF-8
sVersion=$1 #версия sVersion (alpha, beta, ...)
sProject=$2 #название проекта ### wf-central ### Нужна ли вообще эта переменная?
sData=`date "+%Y.%m.%d-%H.%M.%S"`

TMP=TEMP=TMPDIR=/tmp/c_alpha && export TMPDIR TMP TEMP
mkdir -p $TMP

#Проверяем, все ли параметры переданы
if [ $# -ne 2 ]; then
	echo "Can't start. You must specify all arguments!"
    #echo "Here is an example: ./deploy.sh true true \"storage-static storage-temp wf-base\""
    #echo "Parameter description: ./deploy.sh \$sVersion \$sProject \$saCompile"
    exit 1
fi

#Определяем сервер для установки
if [ "sVersion" == "alpha" ]; then
		sHost="test.igov.org.ua"
	else
		echo "Can't find suitable server for version $sVersion"
		exit 1
fi

build_central-js ()
{
	cd central-js
	npm cache clean
	npm install
	bower install
	npm install grunt-contrib-imagemin
	grunt build
	cd dist
	npm install --production
	rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' /sybase/jenkins/data/jobs/central_alpha/workspace/central-js/dist/ sybase@$sHost:/sybase/.upload/central-js.$sData/
}

build_dashboard-js ()
{
	cd dashboard-js    
	npm install
    npm list grunt
    npm list grunt-google-cdn
	bower install
	npm install grunt-contrib-imagemin
	grunt build
	cd dist
	npm install --production
	rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' ../dist/ sybase@$sHost:/sybase/.upload/dashboard-js.$sData/
}

build_base ()
{
	sSuffixTest=""
	if [ "$bExcludeTest" ==  "true" ]; then
		local sBuildArg="-DskipTests=true"
	fi

	cd storage-static
	mvn -P $sVersion clean install $sBuildArg
    cd ..
    cd storage-temp
    mvn -P $sVersion clean install $sBuildArg
    cd ..
    cd wf-base
    mvn -P $sVersion clean install site $sBuildArg -Ddependency.locations.enabled=false
    cd ..
}

build_central ()
{
	sSuffixTest=""
	if [ "$bExcludeTest" ==  "true" ]; then
		local sBuildArg="-DskipTests=true"
	fi
	cd /sybase/jenkins/data/jobs/central_alpha/workspace
	build_base
	cd wf-central
    mvn -P $sVersion clean install site $sBuildArg -Ddependency.locations.enabled=false
    cd /sybase/jenkins/data/jobs/central_alpha/workspace/wf-central
	rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' target/wf-central.war sybase@$sHost:/sybase/.upload/
}

build_region ()
{
	sSuffixTest=""
	if [ "$bExcludeTest" ==  "true" ]; then
		local sBuildArg="-DskipTests=true"
	fi
	cd /sybase/jenkins/data/jobs/regional-alpha/workspace
	build_base
	cd wf-region
    mvn -P $sVersion clean install site $sBuildArg -Ddependency.locations.enabled=false
	cd /sybase/jenkins/data/jobs/regional-alpha/workspace/wf-region
	rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' target/wf-region.war sybase@$sHost:/sybase/.upload/
}

if [ $sProject == "wf-central" ]; then
	build_central
fi
	

#Connecting to remote host (Project deploy)
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $sHost << EOF

fallback ()
{
	echo "Fatal error! Executing fallback task..."
	#Убиваем процесс. Нет смысла ждать его корректной остановки.
	cd /sybase/tomcat_$sProject-$1/bin/ && ./_shutdown_force.sh
	#Удаляем новые конфиги
	rm -rf /sybase/tomcat_$sProject-$1/conf
	#Копируем старые конфиги обратно
	cp -rp /sybase/.backup/configs/$sProject/tomcat_$sProject-$1/conf /sybase/tomcat_$sProject-$1/
	#Очищаем папку с приложениями
	rm -f /sybase/tomcat_$sProject-$1/webapps/*
	#Копируем обратно старое приложение
	cp -p /sybase/.backup/war/$sProject/tomcat_$sProject-$1/wf.war /sybase/tomcat_$sProject-$1/webapps/
	#Запускаем службу
	cd /sybase/tomcat_$sProject-$1/bin/ && ./_startup.sh
	sleep 15
	#Проверяем статус службы. Если нашлась ошибка в логе - завершаем скрипт с критической ошибкой.
	if grep ERROR /sybase/tomcat_$sProject-$1/logs/catalina.out | grep -v log4j | grep -v stopServer
		echo "Fatal error found in tomcat_$sProject-$1/logs/catalina.out! Can't start previous configuration."
		exit 1
	fi
	#Возвращаем на место основной конфиг прокси для Nginx.
	rm -f /sybase/nginx/conf/sites/upstream.conf
	cp -p /sybase/.configs/nginx/only_primary_upstream.conf /sybase/nginx/conf/sites/upstream.conf
	sudo /sybase/nginx/sbin/nginx -s reload
	sleep 5
	sResponseCode=$(curl -o /dev/null --connect-timeout 5 --silent --head --write-out '%{http_code}\n' https://$sHost/)
	if [ $sResponseCode -ne 200 ]; then
		echo "Error. Unexpected server response code. Can't start previous configuration."
		exit 1
	fi
	echo "Deployment failed. Previous configuration returned successfully."
	exit 1
}
	
#Создадим функцию для бекапа, т.к. для основного и вторичного инстанса действия идентичны
backup ()
{
	#Удаляем старые бекапы. Нужно написать функцию по ротации бекапов.
	#rm -rf /sybase/.backup/configs/$sProject/tomcat_$sProject-secondary/conf
	#rm -f /sybase/.backup/war/$sProject/tomcat_$sProject-secondary/wf.war
	#Делаем бекап конфигов
	if [ ! -d /sybase/.backup/configs/$sProject/tomcat_$sProject-$1/$sData ]; then
		mkdir -p /sybase/.backup/configs/$sProject/tomcat_$sProject-$1/$sData
	fi
	cp -rp /sybase/tomcat_$sProject-$1/conf /sybase/.backup/configs/$sProject/tomcat_$sProject-$1/$sData/
	#Делаем бекап приложения
	if [ ! -d /sybase/.backup/war/$sProject/tomcat_$sProject-$1/$sData ]; then
		mkdir -p /sybase/.backup/war/$sProject/tomcat_$sProject-$1/$sData
	fi
	cp -p /sybase/tomcat_$sProject-$1/webapps/wf.war /sybase/.backup/war/$sProject/tomcat_$sProject-$1/$sData/
}
	
#Функция по деплою томката. Для первичного и вторичного инстанса действия идентичны
deploy-tomcat ()
{
	#Выключаем томкат. Ротируется ли лог при выключении или старте?
	cd /sybase/tomcat_$sProject-$1/bin/ && ./_shutdown_force.sh
	sleep 5
	#Разворачиваем новые конфиги
	cp -rf /sybase/.configs/$sProject-$1/* /sybase/tomcat_$sProject-$1/conf/
	#Устанавливаем новую версию приложения
	rm -r /sybase/tomcat_$sProject-$1/webapps/*
	cp -p /sybase/.upload/$sProject.war /sybase/tomcat_$sProject-$1/webapps/wf.war
	#Запускаем томкат
	cd /sybase/tomcat_$sProject-$1/bin/ && ./_startup.sh
	sleep 15
}

if [ $sProject == "central-js" ]; then
	cd /sybase && pm2 stop central-js && pm2 delete central-js
	#Делаем бекап старой версии
	if [ ! -d /sybase/.backup/$sProject ]; then
		mkdir -p /sybase/.backup/$sProject
	fi
	cp -p /sybase/central-js /sybase/.backup/central-js/$sData
	#Удаляем старую версию
	rm -rf /sybase/central-js
	#Перемещаем новую версию на место старой
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

if [ $sProject == "dashboard-js" ]; then
	cd /sybase
	pm2 stop dashboard-js
	if [ ! -d /sybase/.backup/$sProject ]; then
		mkdir -p /sybase/.backup/$sProject
	fi
	cp -p /sybase/dashboard-js /sybase/.backup/dashboard-js/$sData
	pm2 delete dashboard-js
	rm -rf /sybase/dashboard-js
	mv /sybase/.upload/dashboard-js.$sData /sybase/dashboard-js
    cp -f /sybase/.configs/dashboard-js/process.json /sybase/dashboard-js/process.json
	cd /sybase/dashboard-js
	pm2 start process.json --name dashboard-js
	pm2 info dashboard-js
fi

if [ $sProject == "wf-central"  ] || [ $sProject == "wf-region" ]; then
	#Сразу создадим бекапы
	backup secondary

	#Развернем новое приложение на вторичном инстансе
	deploy-tomcat secondary

	#Проверяем на наличие ошибок вторичный инстанс
	if grep ERROR /sybase/tomcat_$sProject-secondary/logs/catalina.out | grep -v log4j | grep -v stopServer
	then
		#Откатываемся назад
		fallback secondary
	else
		echo "Everything is OK. Continuing deployment ..."
		rm -f /sybase/nginx/conf/sites/upstream.conf
		cp -p /sybase/.configs/nginx/only_secondary_upstream.conf /sybase/nginx/conf/sites/upstream.conf
		sudo /sybase/nginx/sbin/nginx -s reload
		sResponseCode=$(curl -o /dev/null --connect-timeout 5 --silent --head --write-out '%{http_code}\n' https://$sHost/)
		if [ $sResponseCode -ne 200 ]; then
			echo "Error. Unexpected server response code. Returning to previous Tomcat configuration."
			fallback secondary
		fi
		
		#Разворачиваем приложение в основной инстанс
		#Сразу создадим бекапы
		backup primary
			
		#Развернем новое приложение на вторичном инстансе
		deploy-tomcat primary
			
		#Проверяем на наличие ошибок вторичный инстанс
		if grep ERROR /sybase/tomcat_$sProject-primary/logs/catalina.out | grep -v log4j | grep -v stopServer
		then
			#Откатываемся назад
			fallback primary
		else
			echo "Everything is OK. Continuing deployment ..."
			rm -f /sybase/nginx/conf/sites/upstream.conf
			cp -p /sybase/.configs/nginx/only_primary_upstream.conf /sybase/nginx/conf/sites/upstream.conf
			sudo /sybase/nginx/sbin/nginx -s reload
			sResponseCode=$(curl -o /dev/null --connect-timeout 5 --silent --head --write-out '%{http_code}\n' https://$sHost/)
			if [ $sResponseCode -ne 200 ]; then
				echo "Error. Unexpected server response code. Returning to previous Tomcat configuration."
				fallback primary
			fi
		fi
	fi
fi

EOF
