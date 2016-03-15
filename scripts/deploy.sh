#!/bin/bash

#Setting-up variables
export LANG=en_US.UTF-8
bIncludeUI=$1 #Собираем UI?
bIncludeBack=$2 #Собираем backend?
sVersion=$3 #версия sVersion (alpha, beta, ...)
sProject=$4 #название проекта ### wf-central ### Нужна ли вообще эта переменная?
sHost=$5 #сервер на котором будет развернут проект
sData=`date "+%Y.%m.%d-%H.%M.%S"`
TMP=TEMP=TMPDIR=/tmp/c_alpha && export TMPDIR TMP TEMP

#Checking if all parameters are specified
if [ $# -ne 5 ]; then
	echo "Can't start. You must specify all arguments!"
    echo "Here is an example: ./deploy.sh true true alpha wf-central test.igov.org.ua"
    echo "Parameter description: ./deploy.sh \$bIncludeUI \$bIncludeBack \$sVersion \$sProject \$sHost"
    exit 1
fi

#Creating temporary directories
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
	#Cleaning-up after all process finished
	rm -rf $TMP/*
fi

#Compiling Backend
if [ "$bIncludeBack" == "true" ]; then
	cd /sybase/jenkins/data/jobs/central_alpha/workspace
 
#	if [ "$bExcludeTest" ==  "true" ]; then
#		mvn -P $sVersion clean install site -U -DskipTests=true 
#	else
#		mvn -P $sVersion clean install site -U
#	fi
  
#	if [ "$bExcludeTest" ==  "true" ]; then
#		mvn -P $sVersion clean install site -U -DskipTests=true -Ddependency.locations.enabled=false
#	else
#		mvn -P $sVersion clean install site -U -Ddependency.locations.enabled=false
#	fi
  
	sSuffixTest=""
	if [ "$bExcludeTest" ==  "true" ]; then
#		mvn -P alpha clean test
		sSuffixTest="-DskipTests=true"
#		mvn -P $sVersion clean install -DskipTests=true    
#	else
#		mvn -P alpha clean test
#		mvn -P $sVersion clean install -DskipTests=true    
#		mvn -P $sVersion clean install
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
	#Cleaning-up after all process finished
	rm -rf $TMP/*
fi

#Проверим, есть ли что компилировать и деплоить
if [[ "$bIncludeBack" == "false" && "$bIncludeUI" == "false" ]]; then
    echo "Nothing to compile and deploy. Exiting..."
    exit 0
fi

#Connecting to remote host (Project deploy)
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $sHost << EOF

#Creating temporary directories
TMP=TEMP=TMPDIR=/tmp/c_$sVersion && export TMPDIR TMP TEMP
mkdir -p $TMP

#Deploy UI
if [ "$bIncludeUI" == "true" ]; then
	cd /sybase && pm2 stop central-js && pm2 delete central-js
	#Делаем бекап старой версии
	if [ ! -d /sybase/.backup/central-js ]; then
		mkdir -p /sybase/.backup/central-js
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

#Deploy backend
if [ "$bIncludeBack" == "true" ]; then
	#Создадим функцию для отката изменений , т.к. это будет использоваться в нескольких местах
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
		rm -f /sybase/tomcat_$sProject-$1/conf/*
		cp -rf /sybase/.configs/$sProject-$1/* /sybase/tomcat_$sProject-$1/conf/
		#Устанавливаем новую версию приложения
		rm -r /sybase/tomcat_$sProject-$1/webapps/*
		cp -p /sybase/.upload/$sProject.war /sybase/tomcat_$sProject-$1/webapps/wf.war
		#Запускаем томкат
		cd /sybase/tomcat_$sProject-$1/bin/ && ./_startup.sh
		sleep 15
	}
	
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

#Cleaning-up
rm -rf $TMP/*
EOF
