#!/bin/bash

#Setting-up variables
export LANG=en_US.UTF-8
#This will cause the shell to exit immediately if a simple command exits with a nonzero exit value.
set -e

while [[ $# > 1 ]]
do
	sKey="$1"
	case $sKey in
		--version)
			sVersion="$2"
			shift
			;;
		--project)
			sProject="$2"
			shift
			;;
		--skip-deploy)
			bSkipDeploy="$2"
			shift
			;;
		--skip-build)
			bSkipBuild="$2"
			shift
			;;
		--skip-test)
			bSkipTest="$2"
			shift
			;;
		--skip-doc)
			bSkipDoc="$2"
			shift
			;;
		--deploy-timeout)
			nSecondsWait="$2"
			shift
			;;
		--compile)
			IFS=',' read -r -a saCompile <<< "$2"
			shift
			;;
		--jenkins-user)
			sJenkinsUser="$2"
			shift
			;;
		--jenkins-api)
			sJenkinsAPI="$2"
			shift
			;;
		--docker)
			bDocker="$2"
			shift
			;;
		--dockerOnly)
			bDockerOnly="$2"
			shift
			;;
		--gitCommit)
			sGitCommit="$2"
			shift
			;;
		--dockerOnly)
			bDockerOnly="$2"
			shift
			;;
		--gitCommit)
			sGitCommit="$2"
			shift
			;;
		*)
			echo "bad option"
			exit 1
			;;
	esac
shift
done

unset IFS
sDate=`date "+%Y.%m.%d-%H.%M.%S"`

build_docker ()
{
	if ! [ -x "$(command -v docker)" ]; then
	    echo "Docker is not installed."
	    exit 1
	fi

	sCurrDir=`echo "$PWD" | sed 's!.*/!!'`
	if ! [[ $sCurrDir == $sProject ]]; then
	    cd $sProject
	fi

	git clone git@github.com:e-government-ua/iSystem.git
	rsync -rtv iSystem/config/$sVersion/$sProject/ ./
	cp iSystem/scripts/deploy_container.py ./
	chmod +x deploy_container.py
	rm -rf iSystem

	if ! [ -f Dockerfile ]; then
		echo "Error. Dockerfile not found."
		exit 1
	fi

	if ! [ -d /tmp/$sProject ]; then
		mkdir /tmp/$sProject
	fi

	python deploy_container.py --project $sProject --version $sVersion --gitCommit $sGitCommit
	exit 0
}
build_central-js ()
{
	if [ "$bSkipBuild" == "true" ]; then
		echo "Deploy to host: $sHost"
		cd central-js
		rsync -az --delete -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' dist/ sybase@$sHost:/sybase/.upload/central-js/
		return
	fi
	if [ "$bSkipDeploy" == "true" ]; then
		cd central-js
		npm cache clean
		npm install
		bower install
		npm install grunt-contrib-imagemin
		grunt build
		cd dist
		npm install --production
		cd ..
		rm -rf /tmp/$sProject
		return
	else
		cd central-js
		npm cache clean
		npm install
		bower install
		npm install grunt-contrib-imagemin
		grunt build
		cd dist
		npm install --production
		cd ..
		rm -rf /tmp/$sProject
		rsync -az --delete -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' dist/ sybase@$sHost:/sybase/.upload/central-js/
		cd ..
	fi
}
build_dashboard-js ()
{
	if [ "$bSkipBuild" == "true" ]; then
		echo "Deploy to host: $sHost"
		cd dashboard-js
		rsync -az --delete -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' dist/ sybase@$sHost:/sybase/.upload/dashboard-js/
		return
	fi
	if [ "$bSkipDeploy" == "true" ]; then
		cd dashboard-js
		npm install
		npm list grunt
		npm list grunt-google-cdn
		bower install
		npm install grunt-contrib-imagemin
		grunt build
		cd dist
		npm install --production
		cd ..
		rm -rf /tmp/$sProject
		return
	else
		cd dashboard-js
		npm install
		npm list grunt
		npm list grunt-google-cdn
		bower install
		npm install grunt-contrib-imagemin
		grunt build
		cd dist
		npm install --production
		cd ..
		rm -rf /tmp/$sProject
		rsync -az --delete -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' dist/ sybase@$sHost:/sybase/.upload/dashboard-js/
		cd ..
	fi
}
build_base ()
{
	if [ "$bSkipTest" ==  "true" ]; then
		local sBuildArg="-DskipTests=true"
	fi
	for sArrComponent in "${saCompile[@]}"
	do
		case "$sArrComponent" in
		storage-static)
			echo  "will build $sArrComponent"
			cd storage-static
			mvn -P $sVersion clean install $sBuildArg
			cd ..
			;;
		storage-temp)
			echo  "will build $sArrComponent"
			cd storage-temp
			mvn -P $sVersion clean install $sBuildArg
			cd ..
			;;
		wf-base)
			echo  "will build $sArrComponent"
			cd wf-base
			mvn -P $sVersion clean install $sBuildDoc $sBuildArg -Ddependency.locations.enabled=false
			cd ..
			;;
		"*")
			echo "Build all base modules"
			cd storage-static
			mvn -P $sVersion clean install $sBuildArg
			cd ..
			cd storage-temp
			mvn -P $sVersion clean install $sBuildArg
			cd ..
			cd wf-base
			mvn -P $sVersion clean install $sBuildDoc $sBuildArg -Ddependency.locations.enabled=false
			cd ..
		   ;;
		esac
	done
}
build_central ()
{
	if [ "$bSkipBuild" == "true" ]; then
		cd wf-central
		if [ ! -f target/wf-central.war ]; then
			echo "File not found! Need to rebuild application..."
			if [ "$bSkipTest" ==  "true" ]; then
				local sBuildArg="-DskipTests=true"
			fi
			build_base $saCompile
			cd wf-central
			mvn -P $sVersion clean install $sBuildDoc $sBuildArg -Ddependency.locations.enabled=false
		fi
		rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' target/wf-central.war sybase@$sHost:/sybase/.upload/
		return
	fi
	if [ "$bSkipDeploy" == "true" ]; then
		if [ "$bSkipTest" ==  "true" ]; then
			local sBuildArg="-DskipTests=true"
		fi
		build_base $saCompile
		cd wf-central
		mvn -P $sVersion clean install $sBuildDoc $sBuildArg -Ddependency.locations.enabled=false
		rm -rf /tmp/$sProject
		return
	else
		if [ "$bSkipTest" ==  "true" ]; then
			local sBuildArg="-DskipTests=true"
		fi
		build_base $saCompile
		cd wf-central
		mvn -P $sVersion clean install $sBuildDoc $sBuildArg -Ddependency.locations.enabled=false
		rm -rf /tmp/$sProject
		rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' target/wf-central.war sybase@$sHost:/sybase/.upload/
	fi
}
build_region ()
{
	if [ "$bSkipBuild" == "true" ]; then
		cd wf-region
		if [ ! -f target/wf-region.war ]; then
			echo "File not found! Need to rebuild application..."
			if [ "$bSkipTest" ==  "true" ]; then
				local sBuildArg="-DskipTests=true"
			fi
			build_base $saCompile
			cd wf-region
			mvn -P $sVersion clean install $sBuildDoc $sBuildArg -Ddependency.locations.enabled=false
		fi
		rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' target/wf-region.war sybase@$sHost:/sybase/.upload/
		return
	fi
	if [ "$bSkipDeploy" == "true" ]; then
		if [ "$bSkipTest" ==  "true" ]; then
			local sBuildArg="-DskipTests=true"
		fi
		build_base $saCompile
		cd wf-region
		mvn -P $sVersion clean install $sBuildDoc $sBuildArg -Ddependency.locations.enabled=false
		rm -rf /tmp/$sProject
		return
	else
		if [ "$bSkipTest" ==  "true" ]; then
			local sBuildArg="-DskipTests=true"
		fi
		build_base $saCompile
		cd wf-region
		mvn -P $sVersion clean install $sBuildDoc $sBuildArg -Ddependency.locations.enabled=false
		rm -rf /tmp/$sProject
		rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' target/wf-region.war sybase@$sHost:/sybase/.upload/
	fi
}

if [ -z $nSecondsWait ]; then
	nSecondsWait=185
fi
if [ -z $sJenkinsUser ]; then
	echo "Please provide Jenkins access credentials!"
	exit 1
fi
if [ -z $sJenkinsAPI ]; then
	echo "Please provide Jenkins access credentials!"
	exit 1
fi
if curl --silent --show-error http://$sJenkinsUser:$sJenkinsAPI@localhost:8080/ | grep "HTTP ERROR"; then
	echo "Failed to connect to Jenkins with current credentials!"
	exit 1
fi
if [ "$bSkipDoc" == "true" ]; then
	sBuildDoc="site"
fi

#Определяем сервер для установки
if [[ $sVersion == "alpha" && $sProject == "central-js" ]] || [[ $sVersion == "alpha" && $sProject == "wf-central" ]]; then
		sHost="test.igov.org.ua"
		export PATH=/usr/local/bin:$PATH
fi
if [[ $sVersion == "beta" && $sProject == "central-js" ]] || [[ $sVersion == "beta" && $sProject == "wf-central" ]]; then
		sHost="test-version.igov.org.ua"
		export PATH=/usr/local/bin:$PATH
fi
#if [[ $sVersion == "prod" && $sProject == "central-js" ]] || [[ $sVersion == "alpha" && $sProject == "wf-central" ]]; then
#		sHost="igov.org.ua"
#fi

if [[ $sVersion == "alpha" && $sProject == "dashboard-js" ]] || [[ $sVersion == "alpha" && $sProject == "wf-region" ]]; then
		sHost="test.region.igov.org.ua"
		export PATH=/usr/local/bin:$PATH
fi
if [[ $sVersion == "beta" && $sProject == "dashboard-js" ]] || [[ $sVersion == "beta" && $sProject == "wf-region" ]]; then
		sHost="test-version.region.igov.org.ua"
		export PATH=/usr/local/bin:$PATH
fi
if [ $sVersion == "delta" ]; then
	sHost="none"
fi
if [ $sVersion == "omega" ]; then
	sHost="none"
fi
#if [[ $sVersion == "prod" && $sProject == "dashboard-js" ]] || [[ $sVersion == "alpha" && $sProject == "wf-region" ]]; then
#		sHost="region.igov.org.ua"
#fi
if [ $sVersion == "delta" ]; then
	sHost="none"
fi
if [ $sVersion == "omega" ]; then
	sHost="none"
fi

if [ -z $bDockerOnly ]; then
	bDockerOnly="false"
fi
if [ -z $sProject ]; then
	build_base $saCompile
	exit 0
else
	if [ $bDockerOnly == "true" ]; then
		build_docker
		exit 0
	fi
	if [ -z $sHost ]; then
		echo "Cloud not select host for deploy. Wrong version or project."
		exit 1
	fi
	echo "Host $sHost will be a target server for deploy...."
	if [[ $sProject ]]; then
		if [ -d /tmp/$sProject ]; then
			rm -rf /tmp/$sProject
		fi
		mkdir /tmp/$sProject
		export TMPDIR=/tmp/$sProject
		export TEMP=/tmp/$sProject
		export TMP=/tmp/$sProject
	fi
	if [ $sProject == "wf-central" ]; then
		sleep 15
		if curl --silent --show-error http://$sJenkinsUser:$sJenkinsAPI@localhost:8080/job/alpha_Back/lastBuild/api/json | grep -q result\":null; then
			echo "Building of alpha_Back project is running. Compilation of wf-central will start automatically."
			exit 0
		else
			echo "Building of alpha_Back project is not running."
			build_central
		fi
	fi
	if [ $sProject == "wf-region" ]; then
		sleep 15
		if curl --silent --show-error http://$sJenkinsUser:$sJenkinsAPI@localhost:8080/job/alpha_Back/lastBuild/api/json | grep -q result\":null; then
			echo "Building of alpha_Back project is running. Compilation of wf-region will start automatically."
			exit 0
		else
			echo "Building of alpha_Back project is not running."
			build_region
		fi
	fi
	if [ $sProject == "central-js" ]; then
		touch /tmp/$sProject/build.lock
		if [ -f /tmp/dashboard-js/build.lock ]; then
			if ps ax | grep -v grep | grep -q dashboard-js; then
				while [ -f /tmp/dashboard-js/build.lock ]; do
					if ps ax | grep -v grep | grep -q dashboard-js; then
						sleep 10
						echo "dashboard-js compilation is still running. we will wait until it finish."
					else
						break
					fi
				done
			else
				echo "dashboard-js compilation script is not running but lock file exist. removing lock file and starting compilation"
				rm -f /tmp/dashboard-js/build.lock
			fi
		fi
		build_central-js
	fi
	if [ $sProject == "dashboard-js" ]; then
		sleep 10
		touch /tmp/$sProject/build.lock
		if [ -f /tmp/central-js/build.lock ]; then
			if ps ax | grep -v grep | grep -q central-js; then
				while [ -f /tmp/central-js/build.lock ]; do
					if ps ax | grep -v grep | grep -q central-js; then
						sleep 10
						echo "central-js compilation is still running. we will wait until it finish."
					else
						break
					fi
				done
			else
				echo "central-js compilation script is not running but lock file exist. removing lock file and starting compilation"
				rm -f /tmp/central-js/build.lock
			fi
		fi
		build_dashboard-js
	fi

	echo "Compilation finished removing lock file"
	rm -f /tmp/$sProject/build.lock
	
	if [ "$bDocker" == "true" ]; then
		build_docker
	fi
	
	if [ "$bSkipDeploy" == "true" ]; then
		echo "Deploy dsiabled"
		exit 0
	fi
fi

echo "Compilation finished removing lock file"
rm -f /tmp/$sProject/build.lock

echo "Connecting to remote host $sHost"
cd $WORKSPACE
rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' scripts/deploy_remote.sh sybase@$sHost:/sybase/
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $sHost << EOF
chmod +x /sybase/deploy_remote.sh
/sybase/deploy_remote.sh $sProject $sDate $nSecondsWait $sVersion
EOF
