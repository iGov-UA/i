#!/bin/bash
sSource="scripts/config/app.lst"


get_change() {

 sApp=$1
 USER=$3
 TOKEN=$4
 sHost=test_delta
 new=$(find ./$sApp -type f  -printf '%TY-%Tm-%Td %TT %p\n' | sort -r | head -n 1 | awk '{print $1, $2}') >> last_change
 echo $new >> last_change_new_$sApp
 DIFF=$(diff -lq last_change_$sApp last_change_new_$sApp)
 if [[ "$DIFF" != "" ]]; then
   if [[ "$sApp" == "central-js"  ]]; then
      echo 'Have change in' $sHost'_Front_Central'
         echo 'Start Job in' $sHost'_Front_Central_CI'
         curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_Front_Central/buildWithParameters?delay=0sec" 


        elif [[ "$sApp" == "dashboard-js"  ]]; then
           echo 'Have change in' $sHost'_Front_Region'

            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_Front_Region/buildWithParameters?delay=0sec" 
            echo 'Start Job' $sHost'_Front_Region_CI' 

        elif [[ "$sApp" == "wf-base" ]] || [[ "$sApp" == "storage-static" ]] || [[ "$sApp" == "storage-temp" ]]; then
           touch 'no'
           echo 'Have change in' $sHost'_Back'
            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_Back/buildWithParameters?delay=0sec" 
            echo 'Start Job' $sHost'_Back_CI'

        elif [[ "$sApp" == "wf-central" ]] && [[ ! -f 'no' ]]; then
           echo 'Have change in' $sHost'_Back_Central'

            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_Back_Central/buildWithParameters?delay=0sec" 
            echo 'Start Job' $sHost'_Back_Central_CI'


        elif [[ "$sApp" == "wf-region" ]] && [[ ! -f 'no' ]]; then
           echo 'Have change in' $sHost'_Back_Region'
            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_Back_Region/buildWithParameters?delay=0sec"
            echo 'Start Job' $sHost'_Back_Region_CI' 
        fi
           else
           echo "no change"

 fi
rm no
mv last_change_new_$sApp last_change_$sApp
}


while read sLine; do
  get_change $sLine
done < $sSource 
exit 0
