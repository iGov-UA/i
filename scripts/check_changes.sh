#!/bin/bash
source="scripts/config/app.lst"

get_change()
{
#cp scripts/check_changes.sh ..
app=$1
new=$(find ./$app -type f  -printf '%TY-%Tm-%Td %TT %p\n' | sort -r | head -n 1 | awk '{print $1, $2}') >> last_change
echo $new >> last_change_$app


while read sLine; do
# if [[ -z $sLine ]] || [[ "$sLine" == "" ]] || [[ "$sLine" =~ ^#.* ]]; then continue; fi
   echo $sLine 
   get_change $sLine
 done < $source 
exit 0
