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

build_in_container ()
{
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
					grunt test server && \
					cd dist && \
					npm install --production"
		
}
