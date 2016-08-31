#!/bin/bash
sSource="scripts/config/app.lst"
#sHost_ARRAY=$(cat scripts/config/app.lst | awk '{print $2}')

get_change() {

 sApp=$1
 USER=$3
 TOKEN=$4
 sHost=test_beta
 new=$(find ./$sApp -type f  -printf '%TY-%Tm-%Td %TT %p\n' | sort -r | head -n 1 | awk '{print $1, $2}') >> last_change
 echo $new >> last_change_new_$sApp
 DIFF=$(diff -lq last_change_$sApp last_change_new_$sApp)
#for sHost in $sHost_ARRAY; do
 if [[ "$DIFF" != "" ]]; then
   if [[ "$sApp" == "central-js"  ]]; then
      echo 'Have change in' $sHost'_Front_Central'
#         if [[ "$sHost" == "test_alpha-old" ]] || [[ "$sHost" == "test_beta-old" ]] || [[ "$sHost" == "PROD-Double" ]]; then
#         TOKEN=$5
#         echo 'Start Job in' $sHost'_Front_Central_Jenkins'
#         curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins-backup.tech.igov.org.ua/job/"$sHost"_Front_Central/buildWithParameters?delay=0sec"
#      elif [[ "$sHost" == "test_alpha" ]] || [[ "$sHost" == "test_beta" ]] || [[ "$sHost" == "test_delta" ]] || [[ "$sHost" == "test_omega" ]]; then
         echo 'Start Job in' $sHost'_Front_Central_CI'
         curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_Front_Central/buildWithParameters?delay=0sec" 
#      fi

        elif [[ "$sApp" == "dashboard-js"  ]]; then
           echo 'Have change in' $sHost'_Front_Region'
#         if [[ "$sHost" == "test_alpha-old" ]] || [[ "$sHost" == "test_beta-old" ]] || [[ "$sHost" == "PROD-Double" ]]; then
#            echo 'Start Job' $sHost'_Front_Region_Jenkins' 
#            TOKEN=$5
#            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins-backup.tech.igov.org.ua/job/"$sHost"_Front_Region/buildWithParameters?delay=0sec"
#         elif [[ "$sHost" == "test_alpha" ]] || [[ "$sHost" == "test_beta" ]] || [[ "$sHost" == "test_delta" ]] || [[ "$sHost" == "test_omega" ]]; then
            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_Front_Region/buildWithParameters?delay=0sec" 
            echo 'Start Job' $sHost'_Front_Region_CI' 
#        fi

        elif [[ "$sApp" == "wf-base" ]] || [[ "$sApp" == "storage-static" ]] || [[ "$sApp" == "storage-temp" ]]; then
           touch 'no'
           echo 'Have change in' $sHost'_Back'
#         if [[ "$sHost" == "test_alpha-old" ]] || [[ "$sHost" == "test_beta-old" ]] || [[ "$sHost" == "PROD-Double" ]]; then
#            echo 'Start Job' $sHost'_Back_Jenkins'
#            TOKEN=$5
#            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins-backup.tech.igov.org.ua/job/"$sHost"_Back/buildWithParameters?delay=0sec"
#         elif [[ "$sHost" == "test_alpha" ]] || [[ "$sHost" == "test_beta" ]] || [[ "$sHost" == "test_delta" ]] || [[ "$sHost" == "test_omega" ]]; then
            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_Back/buildWithParameters?delay=0sec" 
            echo 'Start Job' $sHost'_Back_CI'
#        fi

        elif [[ "$sApp" == "wf-central" ]] && [[ ! -f 'no' ]]; then
           echo 'Have change in' $sHost'_Back_Central'
#         if [[ "$sHost" == "test_alpha-old" ]] || [[ "$sHost" == "test_beta-old" ]] || [[ "$sHost" == "PROD-Double" ]]; then
#            TOKEN=$5
#            echo 'Start Job' $sHost'_Back_Central_Jenkins'
#            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins-backup.tech.igov.org.ua/job/"$sHost"_Back_Central/buildWithParameters?delay=0sec"
#         elif [[ "$sHost" == "test_alpha" ]] || [[ "$sHost" == "test_beta" ]] || [[ "$sHost" == "test_delta" ]] || [[ "$sHost" == "test_omega" ]]; then
            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_Back_Central/buildWithParameters?delay=0sec" 
            echo 'Start Job' $sHost'_Back_Central_CI'
 #       fi

        elif [[ "$sApp" == "wf-region" ]] && [[ ! -f 'no' ]]; then
           echo 'Have change in' $sHost'_Back_Region'
#         if [[ "$sHost" == "test_alpha-old" ]] || [[ "$sHost" == "test_beta-old" ]] || [[ "$sHost" == "PROD-Double" ]]; then
#            TOKEN=$5
#            echo 'Start Job' $sHost'_Back_Region_Jenkins'
#            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins-backup.tech.igov.org.ua/job/"$sHost"_Back_Region/buildWithParameters?delay=0sec"
#         elif [[ "$sHost" == "test_alpha" ]] || [[ "$sHost" == "test_beta" ]] || [[ "$sHost" == "test_delta" ]] || [[ "$sHost" == "test_omega" ]]; then
            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$sHost"_Back_Region/buildWithParameters?delay=0sec"
            echo 'Start Job' $sHost'_Back_Region_CI' 
        fi
           else
           echo "no change"
#   fi
 fi

#done
rm no
mv last_change_new_$sApp last_change_$sApp
}


while read sLine; do
  get_change $sLine
done < $sSource 
exit 0
