#!/bin/bash
source="scripts/config/app.lst"

array=$(cat scripts/config/app.lst | awk '{print $2}')
echo $array
get_change() {
 app=$1
 USER=$3
 TOKEN=$4
 new=$(find ./$app -type f  -printf '%TY-%Tm-%Td %TT %p\n' | sort -r | head -n 1 | awk '{print $1, $2}') >> last_change
 echo $new >> last_change_new_$app
 DIFF=$(diff -lq last_change_$app last_change_new_$app)
for x in ${array[@]}
  do
if [[ "$DIFF" != "" ]]; then
   if [[ "$app" == "central-js" ]]; then
    echo 'change in' $x'_Central-front'
    curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$x"_Front_Central/buildWithParameters?delay=0sec" 
     if [[ "$x" == "test_alpha-old" ]]; then
      TOKEN=$5
      curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins-backup.tech.igov.org.ua/job/"$x"_Front_Central/buildWithParameters?delay=0sec"
      echo 'change in' $x'_Central-front'
     
     
   elif [[ "$app" == "dashboard-js" ]]; then
      echo 'change in' $x'_Region-front'
      curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$x"_Front_Region/buildWithParameters?delay=0sec" 
 #            if [[ "$x" == "test_alpha-old" ]]; then
 #              TOKEN=$5
 #              curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins-backup.tech.igov.org.ua/job/"$x"_Front_Region/buildWithParameters?delay=0sec"
 #              echo 'change in' $x'_Region-front'
 #            fi

        elif [[ "$app" == "wf-base" ||  "$app" == "storage-static" ||  "$app" == "storage-temp" || "$app" == "analytic" ]]; then
           echo 'change in' $x'_Back'
           touch no
           curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$x"_Back/buildWithParameters?delay=0sec"
  #                if [[ "$x" == "test_alpha-old" ]]; then
  #                   TOKEN=$5
  #                   curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins-backup.tech.igov.org.ua/job/"$x"_Back/buildWithParameters?delay=0sec"
  #                   echo 'change in' $x'_Back'
  #                fi

           elif [[ "$app" == "wf-central" || ! -f no ]]; then
              echo 'change in' $x'_Central-Back'
             curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$x"_Back_Central/buildWithParameters?delay=0sec" 
   #                         if [[ "$x" == "test_alpha-old" ]]; then
   #                           TOKEN=$5
   #                           curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins-backup.tech.igov.org.ua/job/"$x"_Back_Central/buildWithParameters?delay=0sec"
   #                           echo 'change in' $x'_Central-Back'
   #                         fi

                    elif [[ "$app" == "wf-region" || ! -f no ]]; then
                        echo 'change in' $x'_Region-Back'
                        curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins.tech.igov.org.ua/job/"$x"_Back_Region/buildWithParameters?delay=0sec"
    #                          if [[ "$x" == "test_alpha-old" ]]; then
    #                            TOKEN=$5
    #                            curl -k -XPOST --user $USER":"$TOKEN "https://ci-jenkins-backup.tech.igov.org.ua/job/"$x"_Back_Region/buildWithParameters?delay=0sec"
    #                            echo 'change in' $x'_Region-Back'
    #
    fi
    fi
   fi  
   else
   echo "no change"
fi
done

mv last_change_new_$app last_change_$app
}

while read sLine; do
# if [[ -z $sLine ]] || [[ "$sLine" == "" ]] || [[ "$sLine" =~ ^#.* ]]; then continue; fi
#   echo $sLine 
   get_change $sLine
 done < $source 
exit 0
