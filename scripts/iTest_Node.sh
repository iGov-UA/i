#!/bin/sh
	cd central-js
		npm cache clean
		npm install
		bower install
		npm install grunt-contrib-imagemin
		grunt test server
		cd dist
		npm install --production
		cd ..
	return
