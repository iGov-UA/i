#!/bin/bash

ServersIGOV=(
alpha-old.test.igov.org.ua
alpha-old.test.region.igov.org.ua
alpha.test.igov.org.ua
alpha.test.region.igov.org.ua
beta-old.test.igov.org.ua
beta-old.test.region.igov.org.ua
beta.test.igov.org.ua
beta.test.region.igov.org.ua
delta.test.igov.org.ua
delta.test.region.igov.org.ua
omega.test.igov.org.ua
omega.test.region.igov.org.ua
igov.org.ua
region.igov.org.ua
prod-double-central.tech.igov.org.ua
prod-double-region.tech.igov.org.ua
visor-zabbix.tech.igov.org.ua
)


for i in "${ServersIGOV[@]}"
do
    nslookup $i | awk '/^Address: / { print $2 ; exit }' >> tmp.f
done

sort -u tmp.f > ip.txt
rm -f tmp.f

