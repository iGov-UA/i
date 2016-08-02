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
		--type)
                        sType="$2"
                        shift
                        ;;
		--tier)
                        sTier="$2"
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
		--bBuildInContainer)
			bBuildInContainer="$2"
			shift
			;;
		--gitCommit)
			sGitCommit="$2"
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

sDate=`date "+%Y.%m.%d-%H.%M.%S"`
versionsList="versions.lst"
serversList="servers.lst"
sHost="none"
sProfile="none"
sConfigStorePath="/var/jenkins_home/configs_generated/"$sVersion"_"$sType"_"$sTier"_"$sDate

sTmpPath="/tmp/sandbox_"$sVersion"_"$sType"_"$sTier
if [ -d $sTmpPath ]; then
	rm -rf $sTmpPath
fi

git clone git@iSystem.github.com:e-government-ua/iSystem.git -b test --single-branch  $sTmpPath"/iSystem_tmp"
cp $sTmpPath"/iSystem_tmp/config/_default/"$versionsList ./
cp $sTmpPath"/iSystem_tmp/config/_default/"$serversList ./

IFS="="
while read -r ver value
do
     if [ $sVersion == $ver ]; then
        sProfile=${value//\"/}
     fi
done < $versionsList

if [ $sProfile == "none" ]; then
    echo "Error! Wrong version!"
    exit 1
else
    echo "Version = "$sVersion", Maven version = "$sProfile
fi

while read -r srv domain
do
     if [ $sVersion"_"$sType == $srv ]; then
        sHost=${domain//\"/}
     fi
done < $serversList

if [ $sHost == "none" ]; then
    echo "Deploy new!"
else
    echo "Version = "$sVersion"_"$sType", Host = "$sHost
fi
unset IFS

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
	
        git clone git@iSystem.github.com:e-government-ua/iSystem.git -b test --single-branch  $sTmpPath"/iSystem"
        git clone git@iSystemTop.github.com:e-government-ua/iSystemTop.git $sTmpPath"/iSystemTop"
	cp $sTmpPath/iSystem/scripts/*.py ./ && chmod +x *.py
	rsync -rtv $sTmpPath/iSystem/config/_default/ ./_default/
	rsync -rtv $sTmpPath/iSystemTop/config/_default/ ./_defaultTop/
	if [ -d  $sTmpPath/iSystem/config/$sVersion/$sProject/conf/ ]; then
		rsync -rtv $sTmpPath/iSystem/config/$sVersion/$sProject/conf/ ./_custom/
	fi
	if [ -d  $sTmpPath/iSystemTop/config/$sVersion/$sProject/conf/ ]; then
		rsync -rtv $sTmpPath/iSystemTop/config/$sVersion/$sProject/conf/ ./_custom/
	fi
	#rm -rf /tmp/iSystem
	python render_configs.py --version $sVersion --type $sType --tier $sTier
	rsync -rtv _default/$sType"_"$sTier/files/ conf/

        if ! [ -f Dockerfile ]; then
                echo "Error. Dockerfile not found."
                exit 1
        fi

        if ! [ -d $sTmpPath ]; then
                mkdir $sTmpPath
        fi

        python deploy_container.py --project $sProject --version $sVersion --gitCommit $sGitCommit
        #rm -rf ./_default ./_custom ./conf ./kube ./Dockerfile*
        mkdir -p $sConfigStorePath
        if [ -d ./conf ]; then
                mv ./conf $sConfigStorePath/
        fi
        if [ -d ./kube ]; then
                mv ./kube $sConfigStorePath/
        fi
        if [ -d ./_default ]; then
                mv ./_default $sConfigStorePath/
        fi
		if [ -d ./_defaultTop ]; then
                mv ./_defaultTop $sConfigStorePath/
        fi
        if [ -d ./_custom ]; then
                 mv ./_custom $sConfigStorePath/
        fi
        mv ./Dockerfile* $sConfigStorePath/
        exit 0
}

build_in_container ()
{
	sBuildContainerFront="264876730186.dkr.ecr.eu-west-1.amazonaws.com/npm-builder:latest"
	sBuildContainerFrontOld="264876730186.dkr.ecr.eu-west-1.amazonaws.com/npm-builder_for-old:latest"
	sBuildContainerBack="264876730186.dkr.ecr.eu-west-1.amazonaws.com/maven-builder:latest"
	sBuildContainerBackOld="264876730186.dkr.ecr.eu-west-1.amazonaws.com/maven-builder_for-old:latest"
	sJenkinsHomeHost="/sybase/apps/jenkins"
	sJenkinsHomeContainer="/var/jenkins_home"
	sCmdTmp="docker run --rm -v $PWD:/data -w /data"
	
	if [ "$bDocker" == "false" ]; then
		sCmdTmp="sudo docker run --rm -v $PWD:/data -w /data"
		sJenkinsHomeHost="/sybase/jenkins"
	fi
	#sCmdTmp="docker run --rm -v $PWD:/data -w /data"
	sCmd="${sCmdTmp/$sJenkinsHomeContainer/$sJenkinsHomeHost}"
	
	if [ "$sTier" == "front" ]; then
		if [ "$bDocker" == "false" ]; then
			sCmd="$sCmd $sBuildContainerFrontOld "
		else
			sCmd="$sCmd $sBuildContainerFront "
		fi
		echo $sCmd
		$sCmd bash -c " \
					cd $sProject && \
					npm cache clean && \
					npm install && \
					npm list grunt && \
					npm list grunt-google-cdn && \
					bower install && \
					npm install grunt-contrib-imagemin && \
					grunt build && \
					cd dist && \
					npm install --production"
		
		if [ "$bSkipDeploy" == "false" ]; then
			rsync -az --delete -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' $sProject/dist/ $sHost:/sybase/.upload/$sProject/
		fi
	elif [ "$sTier" == "back" ]; then
		if [ "$bDocker" == "false" ]; then
			sCmd="$sCmd $sBuildContainerBackOld "
		else
			sCmd="$sCmd $sBuildContainerBack "
		fi
		echo $sCmd
		
		if [ "$bSkipTest" ==  "true" ]; then
			local sBuildArg="-DskipTests=true"
		fi

		$sCmd bash -c " \
					cd $sProject && \
					mvn -P $sProfile clean install $sBuildDoc $sBuildArg -Ddependency.locations.enabled=false
		"
		
		if [ "$bSkipDeploy" == "false" ]; then
			rsync -az -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' $sProject/target/$sProject.war $sHost:/sybase/.upload/
		fi
	else
		echo "No functions for build "$sTier
		exit 1
	fi
}

build_central-js ()
{
	if [ "$bSkipBuild" == "true" ]; then
		echo "Deploy to host: $sHost"
		cd central-js
		rsync -az --delete -e 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no' dist/ $sHost:/sybase/.upload/central-js/
		return
	fi
	if [ "$bSkipDeploy" == "true" ]; then
		cd central-js
		npm cache clean
		npm install
		bower install
		npm install grunt-contrib-imagemin
		grunt test server
		cd dist
		npm install --production
		cd ..
		rm -rf $sTmpPath
		return
Ї}
