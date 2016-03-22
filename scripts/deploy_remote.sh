#!/bin/bash

sProject=$1
sDate=$2
nSecondsWait=$3

fallback ()
{
	echo "Fatal error! Executing fallback task..."
	#Убиваем процесс. Нет смысла ждать его корректной остановки.
	cd /sybase/tomcat_${sProject}/bin/ && ./_shutdown_force.sh
	#Удаляем новые конфиги
	rm -rf /sybase/tomcat_${sProject}/conf
	#Копируем старые конфиги обратно
	cp -rp /sybase/.backup/configs/$sProject/tomcat_${sProject}/$sDate/conf /sybase/tomcat_${sProject}/
	#Очищаем папку с приложениями
	rm -rf /sybase/tomcat_${sProject}/webapps/*
	#Копируем обратно старое приложение
	cp -p /sybase/.backup/war/$sProject/tomcat_${sProject}/$sDate/wf.war /sybase/tomcat_${sProject}/webapps/
	#Запускаем службу
	cd /sybase/tomcat_${sProject}/bin/ && ./_startup.sh
	sleep 15
	#Проверяем статус службы. Если нашлась ошибка в логе - завершаем скрипт с критической ошибкой.
	if grep ERROR /sybase/tomcat_${sProject}/logs/catalina.out | grep -v log4j | grep -v stopServer; then
		echo "Fatal error found in tomcat_${sProject}/logs/catalina.out! Can't start previous configuration."
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
	echo "Backuping tomcat_${sProject}"
	#Удаляем старые бекапы. Нужно написать функцию по ротации бекапов.
	#rm -rf /sybase/.backup/configs/$sProject/tomcat_$sProject-secondary/conf
	#rm -f /sybase/.backup/war/$sProject/tomcat_$sProject-secondary/wf.war
	#Делаем бекап конфигов
	if [ ! -d /sybase/.backup/configs/$sProject/tomcat_${sProject}/$sDate ]; then
		mkdir -p /sybase/.backup/configs/$sProject/tomcat_${sProject}/$sDate
	fi
	cp -rp /sybase/tomcat_${sProject}/conf /sybase/.backup/configs/$sProject/tomcat_${sProject}/$sDate/
	#Делаем бекап приложения
	if [ ! -d /sybase/.backup/war/$sProject/tomcat_${sProject}/$sDate ]; then
		mkdir -p /sybase/.backup/war/$sProject/tomcat_${sProject}/$sDate
	fi
	cp -p /sybase/tomcat_${sProject}/webapps/wf.war /sybase/.backup/war/$sProject/tomcat_${sProject}/$sDate/
}
	
#Функция по деплою томката. Для первичного и вторичного инстанса действия идентичны
deploy-tomcat ()
{
	#Выключаем томкат. Ротируется ли лог при выключении или старте?
	cd /sybase/tomcat_${sProject}/bin/ && ./_shutdown_force.sh
	sleep 5
	#Разворачиваем новые конфиги
	cp -rf /sybase/.configs/${sProject}/* /sybase/tomcat_${sProject}/conf/
	#Устанавливаем новую версию приложения
	rm -rf /sybase/tomcat_${sProject}/webapps/*
	cp -p /sybase/.upload/$sProject.war /sybase/tomcat_${sProject}/webapps/wf.war
	#Запускаем томкат
	cd /sybase/tomcat_${sProject}/bin/ && ./_startup.sh
	sleep 15
}

if [ $sProject == "central-js" ]; then
	echo "Deploying project $sProject"
	cd /sybase && pm2 stop central-js && pm2 delete central-js
	#Делаем бекап старой версии
	if [ ! -d /sybase/.backup/$sProject ]; then
		mkdir -p /sybase/.backup/$sProject
	fi
	mv -f /sybase/central-js /sybase/.backup/$sProject/$sDate
	#Перемещаем новую версию на место старой
	mv -f /sybase/.upload/central-js /sybase/central-js
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
	pm2 delete dashboard-js
	if [ ! -d /sybase/.backup/$sProject ]; then
		mkdir -p /sybase/.backup/$sProject
	fi
	mv -f /sybase/dashboard-js /sybase/.backup/$sProject/$sDate
	mv /sybase/.upload/dashboard-js /sybase/dashboard-js
    cp -f /sybase/.configs/dashboard-js/process.json /sybase/dashboard-js/process.json
	cd /sybase/dashboard-js
	pm2 start process.json --name dashboard-js
	pm2 info dashboard-js
fi

if [ $sProject == "wf-central"  ] || [ $sProject == "wf-region" ]; then
	#Сразу создадим бекапы
	echo "Starting backup of DOUBLE"
	#Удаляем старые бекапы. Нужно написать функцию по ротации бекапов.
	#rm -rf /sybase/.backup/configs/$sProject/tomcat_$sProject-secondary/conf
	#rm -f /sybase/.backup/war/$sProject/tomcat_$sProject-secondary/wf.war
	#Делаем бекап конфигов
	if [ ! -d /sybase/.backup/configs/$sProject/tomcat_${sProject}_double/$sDate ]; then
		mkdir -p /sybase/.backup/configs/$sProject/tomcat_${sProject}_double/$sDate
	fi
	cp -rp /sybase/tomcat_${sProject}_double/conf /sybase/.backup/configs/$sProject/tomcat_${sProject}_double/$sDate/
	#Делаем бекап приложения
	if [ ! -d /sybase/.backup/war/$sProject/tomcat_${sProject}_double/$sDate ]; then
		mkdir -p /sybase/.backup/war/$sProject/tomcat_${sProject}_double/$sDate
	fi
	cp -p /sybase/tomcat_${sProject}_double/webapps/wf.war /sybase/.backup/war/$sProject/tomcat_${sProject}_double/$sDate/

	#Развернем новое приложение на вторичном инстансе
	echo "Starting deploy of DOUBLE"
	#Выключаем томкат.
	cd /sybase/tomcat_${sProject}_double/bin/ && ./_shutdown_force.sh
	sleep 5
	#Разворачиваем новые конфиги
	cp -rf /sybase/.configs/${sProject}/* /sybase/tomcat_${sProject}_double/conf/
	#Устанавливаем новую версию приложения
	rm -rf /sybase/tomcat_${sProject}_double/webapps/*
	cp -p /sybase/.upload/$sProject.war /sybase/tomcat_${sProject}_double/webapps/wf.war
	#Запускаем томкат
	cd /sybase/tomcat_${sProject}_double/bin/ && ./_startup.sh
	sleep 15

	nTimeout=0
	until grep -q "FrameworkServlet 'dispatcher': initialization completed in" /sybase/tomcat_${sProject}_double/logs/catalina.out || [ $nTimeout -eq $nSecondsWait ]; do
		((nTimeout++))
		sleep 1
		echo "waiting for server startup $nTimeout"
		if [ $nTimeout -ge $nSecondsWait ]; then
			echo "timeout reached"
			grep -B 3 -A 2 ERROR /sybase/tomcat_${sProject}_double/logs/catalina.out
			#Откатываемся назад
			echo "Fatal error! Executing fallback task..."
			#Убиваем процесс. Нет смысла ждать его корректной остановки.
			cd /sybase/tomcat_${sProject}_double/bin/ && ./_shutdown_force.sh
			#Удаляем новые конфиги
			rm -rf /sybase/tomcat_${sProject}_double/conf
			#Копируем старые конфиги обратно
			cp -rp /sybase/.backup/configs/$sProject/tomcat_${sProject}_double/$sDate/conf /sybase/tomcat_${sProject}_double/
			#Очищаем папку с приложениями
			rm -rf /sybase/tomcat_${sProject}_double/webapps/*
			#Копируем обратно старое приложение
			cp -p /sybase/.backup/war/$sProject/tomcat_${sProject}_double/$sDate/wf.war /sybase/tomcat_${sProject}_double/webapps/
			echo "Deploy failed previous configuration returned"
			exit 1
		fi
	done
	
	#Проверяем на наличие ошибок вторичный инстанс
	if grep ERROR /sybase/tomcat_${sProject}_double/logs/catalina.out | grep -v log4j | grep -v stopServer; then
		grep -B 3 -A 2 ERROR /sybase/tomcat_${sProject}_double/logs/catalina.out
		#Откатываемся назад
		echo "Fatal error! Executing fallback task..."
		#Убиваем процесс. Нет смысла ждать его корректной остановки.
		cd /sybase/tomcat_${sProject}_double/bin/ && ./_shutdown_force.sh
		#Удаляем новые конфиги
		rm -rf /sybase/tomcat_${sProject}_double/conf
		#Копируем старые конфиги обратно
		cp -rp /sybase/.backup/configs/$sProject/tomcat_${sProject}_double/$sDate/conf /sybase/tomcat_${sProject}_double/
		#Очищаем папку с приложениями
		rm -rf /sybase/tomcat_${sProject}_double/webapps/*
		#Копируем обратно старое приложение
		cp -p /sybase/.backup/war/$sProject/tomcat_${sProject}_double/$sDate/wf.war /sybase/tomcat_${sProject}_double/webapps/
		#Запускаем службу
		cd /sybase/tomcat_${sProject}_double/bin/ && ./_startup.sh
		sleep 15
		#Проверяем статус службы. Если нашлась ошибка в логе - завершаем скрипт с критической ошибкой.
		if grep ERROR /sybase/tomcat_${sProject}_double/logs/catalina.out | grep -v log4j | grep -v stopServer; then
			echo "Fatal error found in tomcat_${sProject}_double/logs/catalina.out! Can't start previous configuration."
			exit 1
		fi
		#Возвращаем на место основной конфиг прокси для Nginx.
		cat /sybase/.configs/nginx/only_primary_upstream.conf > /sybase/nginx/conf/sites/upstream.conf
		sudo /sybase/nginx/sbin/nginx -s reload
		sleep 5
#		sResponseCode=$(curl -o /dev/null --connect-timeout 5 --silent --head --write-out '%{http_code}\n' https://$sHost/)
#		if [ $sResponseCode -ne 200 ]; then
#			echo "Error. Unexpected server response code. Can't start previous configuration."
#			exit 1
#		fi
		echo "Deployment failed. Previous configuration returned successfully."
		exit 1
	else
		echo "Everything is OK. Continuing deployment ..."
		cat /sybase/.configs/nginx/${sProject}_secondary_upstream.conf > /sybase/nginx/conf/sites/${sProject}_upstream.conf
		sudo /sybase/nginx/sbin/nginx -s reload
#		sResponseCode=$(curl -o /dev/null --connect-timeout 5 --silent --head --write-out '%{http_code}\n' https://$sHost/)
#		if [ $sResponseCode -ne 200 ]; then
#			echo "Error. Unexpected server response code. Returning to previous Tomcat configuration."
#			fallback _double
#		fi
		
		#Разворачиваем приложение в основной инстанс
		#Сразу создадим бекапы
		backup
			
		#Развернем новое приложение на вторичном инстансе
		deploy-tomcat
			
		#Проверяем на наличие ошибок вторичный инстанс
		if grep ERROR /sybase/tomcat_${sProject}/logs/catalina.out | grep -v log4j | grep -v stopServer; then
			grep -B 3 -A 2 ERROR /sybase/tomcat_${sProject}/logs/catalina.out
			#Откатываемся назад
			fallback
		else
			echo "Everything is OK. Continuing deployment ..."
			cat /sybase/.configs/nginx/${sProject}_primary_upstream.conf > /sybase/nginx/conf/sites/${sProject}_upstream.conf
			sudo /sybase/nginx/sbin/nginx -s reload
#			sResponseCode=$(curl -o /dev/null --connect-timeout 5 --silent --head --write-out '%{http_code}\n' https://$sHost/)
#			if [ $sResponseCode -ne 200 ]; then
#				echo "Error. Unexpected server response code. Returning to previous Tomcat configuration."
#				fallback
#			fi
			cd /sybase/tomcat_${sProject}_double/bin/ && ./_shutdown_force.sh
		fi
	fi
fi
