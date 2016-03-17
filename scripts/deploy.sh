#!/bin/bash

#Setting-up variables
export LANG=en_US.UTF-8
sVersion=$1 #версия sVersion (alpha, beta, ...)
sProject=$2 #название проекта ### wf-central ###
bExcludeTest=$3 #Нужно ли выполнять тесты после сборки
saCompile=($4) #массив с компонентами для сборки saCompile=storage-static,storage-temp,wf-base
sDate=`date "+%Y.%m.%d-%H.%M.%S"`

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
if [[ $sVersion == "alpha" && $sProject == "central-js" ]] || [[ $sVersion == "alpha" && $sProject == "wf-central" ]]; then
		sHost="test.igov.org.ua"
		sJenkinsWorkspace="/sybase/jenkins/data/jobs/central_alpha/workspace"
fi
#if [[ $sVersion == "beta" && $sProject == "central-js" ]] || [[ $sVersion == "alpha" && $sProject == "wf-central" ]]; then
#		sHost="test-version.igov.org.ua"
#fi
#if [[ $sVersion == "prod" && $sProject == "central-js" ]] || [[ $sVersion == "alpha" && $sProject == "wf-central" ]]; then
#		sHost="igov.org.ua"
#fi

if [[ $sVersion == "alpha" && $sProject == "dashboard-js" ]] || [[ $sVersion == "alpha" && $sProject == "wf-region" ]]; then
		sHost="test.igov.org.ua"
		sJenkinsWorkspace="/sybase/jenkins/data/jobs/regional-alpha/workspace"
fi
#if [[ $sVersion == "beta" && $sProject == "dashboard-js" ]] || [[ $sVersion == "alpha" && $sProject == "wf-region" ]]; then
#		sHost="test-version.region.igov.org.ua"
#fi
#if [[ $sVersion == "prod" && $sProject == "dashboard-js" ]] || [[ $sVersion == "alpha" && $sProject == "wf-region" ]]; then
#		sHost="region.igov.org.ua"
#fi

if [ -z $sHost ]; then
    echo "Cloud not select host for deploy. Wrong version or project."
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
	rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' /sybase/jenkins/data/jobs/central_alpha/workspace/central-js/dist/ sybase@$sHost:/sybase/.upload/central-js.$sDate/
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
	rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' /sybase/jenkins/data/jobs/regional-alpha/workspace/dashboard-js/dist/ sybase@$sHost:/sybase/.upload/dashboard-js.$sDate/
}

build_base ()
{
	if [ -z "$saCompile" ]; then
		saCompile=(storage-static storage-temp wf-base)
	fi
	if [ "$bExcludeTest" ==  "true" ]; then
		local sBuildArg="-DskipTests=true"
	fi
	for sArrComponent in "${saCompile[@]}"
	do
		case "$sArrComponent" in
		storage-static)
			echo  "will build $sArrComponent"
			cd $sJenkinsWorkspace/storage-static
			mvn -P $sVersion clean install $sBuildArg
			;;
		storage-temp)
			echo  "will build $sArrComponent"
			cd $sJenkinsWorkspace/storage-temp
			mvn -P $sVersion clean install $sBuildArg
			;;
		wf-base)
			echo  "will build $sArrComponent"
			cd $sJenkinsWorkspace/wf-base
			mvn -P $sVersion clean install site $sBuildArg -Ddependency.locations.enabled=false
			;;
		*)
			echo "Bad project name $sArrComponent"
			exit 1
		   ;;
		esac
	done
}

build_central ()
{
	if [ "$bExcludeTest" ==  "true" ]; then
		local sBuildArg="-DskipTests=true"
	fi
	build_base $saCompile
	cd /sybase/jenkins/data/jobs/central_alpha/workspace/wf-central
    mvn -P $sVersion clean install site $sBuildArg -Ddependency.locations.enabled=false
	rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' target/wf-central.war sybase@$sHost:/sybase/.upload/
}

build_region ()
{
	if [ "$bExcludeTest" ==  "true" ]; then
		local sBuildArg="-DskipTests=true"
	fi
	build_base $saCompile
	cd /sybase/jenkins/data/jobs/regional-alpha/workspace/wf-region
    mvn -P $sVersion clean install site $sBuildArg -Ddependency.locations.enabled=false
	rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' target/wf-region.war sybase@$sHost:/sybase/.upload/
}

if [ $sProject == "wf-central" ]; then
	build_central
fi
if [ $sProject == "wf-region" ]; then
	build_region
fi
if [ $sProject == "central-js" ]; then
	build_central-js
fi
if [ $sProject == "dashboard-js" ]; then
	build_dashboard-js
fi

#Connecting to remote host (Project deploy)
#ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $sHost << EOF

fallback ()
{
	echo "Fatal error! Executing fallback task..."
	#Убиваем процесс. Нет смысла ждать его корректной остановки.
	cd /sybase/tomcat_${sProject}_$1/bin/ && ./_shutdown_force.sh
	#Удаляем новые конфиги
	rm -rf /sybase/tomcat_${sProject}_$1/conf
	#Копируем старые конфиги обратно
	cp -rp /sybase/.backup/configs/$sProject/tomcat_${sProject}_$1/$sDate/conf /sybase/tomcat_${sProject}_$1/
	#Очищаем папку с приложениями
	rm -f /sybase/tomcat_${sProject}_$1/webapps/*
	#Копируем обратно старое приложение
	cp -p /sybase/.backup/war/$sProject/tomcat_${sProject}_$1/$sDate/wf.war /sybase/tomcat_${sProject}_$1/webapps/
	#Запускаем службу
	cd /sybase/tomcat_${sProject}_$1/bin/ && ./_startup.sh
	sleep 15
	#Проверяем статус службы. Если нашлась ошибка в логе - завершаем скрипт с критической ошибкой.
	if grep ERROR /sybase/tomcat_${sProject}_$1/logs/catalina.out | grep -v log4j | grep -v stopServer
		echo "Fatal error found in tomcat_${sProject}_$1/logs/catalina.out! Can't start previous configuration."
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
	if [ ! -d /sybase/.backup/configs/$sProject/tomcat_${sProject}_$1/$sDate ]; then
		mkdir -p /sybase/.backup/configs/$sProject/tomcat_${sProject}_$1/$sDate
	fi
	cp -rp /sybase/tomcat_${sProject}_$1/conf /sybase/.backup/configs/$sProject/tomcat_${sProject}_$1/$sDate/
	#Делаем бекап приложения
	if [ ! -d /sybase/.backup/war/$sProject/tomcat_${sProject}_$1/$sDate ]; then
		mkdir -p /sybase/.backup/war/$sProject/tomcat_${sProject}_$1/$sDate
	fi
	cp -p /sybase/tomcat_${sProject}_$1/webapps/wf.war /sybase/.backup/war/$sProject/tomcat_${sProject}_$1/$sDate/
}
	
#Функция по деплою томката. Для первичного и вторичного инстанса действия идентичны
deploy-tomcat ()
{
	#Выключаем томкат. Ротируется ли лог при выключении или старте?
	cd /sybase/tomcat_${sProject}_$1/bin/ && ./_shutdown_force.sh
	sleep 5
	#Разворачиваем новые конфиги
	cp -rf /sybase/.configs/${sProject}_$1/* /sybase/tomcat_${sProject}_$1/conf/
	#Устанавливаем новую версию приложения
	rm -f /sybase/tomcat_${sProject}_$1/webapps/*
	cp -p /sybase/.upload/$sProject.war /sybase/tomcat_${sProject}_$1/webapps/wf.war
	#Запускаем томкат
	cd /sybase/tomcat_${sProject}_$1/bin/ && ./_startup.sh
	sleep 15
}

if [ $sProject == "central-js" ]; then
	cd /sybase && pm2 stop central-js && pm2 delete central-js
	#Делаем бекап старой версии
	if [ ! -d /sybase/.backup/$sProject ]; then
		mkdir -p /sybase/.backup/$sProject
	fi
	cp -p /sybase/central-js /sybase/.backup/$sProject/$sDate
	#Удаляем старую версию
	rm -rf /sybase/central-js
	#Перемещаем новую версию на место старой
	mv -f /sybase/.upload/central-js.$sDate /sybase/central-js
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
	cp -p /sybase/dashboard-js /sybase/.backup/$sProject/$sDate
	pm2 delete dashboard-js
	rm -rf /sybase/dashboard-js
	mv /sybase/.upload/dashboard-js.$sDate /sybase/dashboard-js
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
	if grep ERROR /sybase/tomcat_${sProject}_secondary/logs/catalina.out | grep -v log4j | grep -v stopServer
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
		if grep ERROR /sybase/tomcat_${sProject}_primary/logs/catalina.out | grep -v log4j | grep -v stopServer
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
