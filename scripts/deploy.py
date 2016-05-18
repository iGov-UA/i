#!/usr/bin/env python

import sys
import time
import argparse
import subprocess

parser = argparse.ArgumentParser()
parser.add_argument('-v', '--version', help='Project version', required=True)
parser.add_argument('-p', '--project', help='Project name', default='base')
parser.add_argument('-sd', '--skip-deploy', help='Skip deploy', default='false')
parser.add_argument('-sb', '--skip-build', help='Skip build', default='false')
parser.add_argument('-st', '--skip-test', help='Skip tests', default='false')
parser.add_argument('-sdoc', '--skip-doc', help='Skip doc', default='false')
parser.add_argument('-dt', '--deploy-timeout', help='Deploy timeout', default=200)
parser.add_argument('-c', '--compile', help='Compile', default='*')
parser.add_argument('-ju', '--jenkins-user', help='Jenkins username', required=True)
parser.add_argument('-ja', '--jenkins-api', help='jenkins api key', required=True)
parser.add_argument('-d', '--docker', help='Build with docker', default='false')
parser.add_argument('-do', '--dockerOnly', help='Only build with docker', default='false')
parser.add_argument('-gc', '--gitCommit', help='Git commit', default='none')
a = parser.parse_args()

print a

subprocess.call("ssh-agent bash -c 'ssh-add /sybase/.secret/id_rsa_iSystem; git clone git@github.com:e-government-ua/iSystem.git'", shell=True)
subprocess.call("rsync -rt iSystem/config/$sVersion/$sProject/ ./$sProject/", shell=True)
subprocess.call("rsync -rt iSystem/scripts/ scripts/", shell=True)
subprocess.call("rm -rf iSystem", shell=True)
subprocess.call("chmod +x scripts/*", shell=True)

subprocess.call(["sh","scripts/main.sh","-v",a.version,"-p",a.project,"-ju",a.jenkins_user,"-ja",a.jenkins_api,"-st",a.skip_test,"-sb",a.skip_build,"-sdoc",a.skip_doc,"-sd",a.skip_deploy,"-d",a.docker,"-gc",a.gitCommit])

