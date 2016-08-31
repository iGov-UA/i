#!/bin/bash
sSource="scripts/config/app.lst"

get_change() {

 sApp=$1
 USER=$2
 TOKEN=$3
 sJob=$4
 sHost=test_alpha
 new=$(find ./$sApp -type f  -printf '%TY-%Tm-%Td %TT %p\n' | sort -r | head -n 1 | awk '{print $1, $2}') >> last_change
 echo $new >> last_change_new_$sApp
 DIFF=$(diff -lq last_change_$sApp last_change_new_$sApp)
  if [[ "$DIFF" != "" ]]; then
     curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_"$sJob"/buildWithParameters?delay=0sec" 
     echo 'Start Job' $sHost'_'$sJob 
         if [[ "$sApp" == "wf-base" ]] || [[ "$sApp" == "storage-static" ]] || [[ "$sApp" == "storage-temp" ]]; then
            touch 'no'
            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_"$sJob"/buildWithParameters?delay=0sec" 
            echo 'Start Job' $sHost'_'$sJob
                if [[ "$sApp" == "wf-central" ]] || [[ "$sApp" == "wf-region" ]]  && [[ ! -f 'no' ]]; then
                    curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_"$sJob"/buildWithParameters?delay=0sec" 
                    echo 'Start Job' $sHost'_'$sJob 
                fi
          fi
   
  fi

rm ./no
mv last_change_new_$sApp last_change_$sApp
}

 while read sLine; do
 if [[ -z $sLine ]] || [[ "$sLine" == "" ]] || [[ "$sLine" =~ ^#.* ]]; then continue; fi
    get_change $sLine
 done < $sSource 
exit 0
