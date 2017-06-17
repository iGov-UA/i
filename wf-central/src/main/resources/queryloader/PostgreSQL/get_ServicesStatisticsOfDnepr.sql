select distinct CASE
                WHEN concat(hes.nID_Service, hes.sID_UA) = ''
                  THEN 0
                ELSE CAST(concat(hes.nID_Service, hes.sID_UA) AS bigint)
                END AS nID,
                hes.nID_Service AS nID_Service,
                s."sName" AS ServiceName,
                hes.sID_UA AS SID_UA,
                p."sName" AS placeName,
                hes.nCountTotal AS nCountTotal,
                hes.nCountFeedback AS nCountFeedback,
                hes.nCountEscalation AS nCountEscalation,
                hes.averageRate AS averageRate,
                hes.averageTime AS averageTime
from ( select hes."nID_Service" AS nID_Service, hes."sID_UA" AS SID_UA, count(*) AS nCountTotal,
         count(hes."nID_Proccess_Feedback") AS nCountFeedback, count(hes."nID_Proccess_Escalation") AS nCountEscalation,
              avg(hes."nRate") AS averageRate, avg(hes."nTimeMinutes") AS averageTime
       from "HistoryEvent_Service" AS hes
      where hes."sDate" >= to_timestamp( :dateFrom , 'YYYY-MM-DD hh24:mi:ss')
            and hes."sDate" < to_timestamp( :dateTo , 'YYYY-MM-DD hh24:mi:ss')
           and hes."sID_Public_SubjectOrganJoin" in('lev', 'prav')
            
       group by hes."nID_Service", hes."sID_UA"
     ) AS hes
  LEFT OUTER JOIN "Service" AS s
    ON s."nID" = hes.nID_Service
  LEFT OUTER JOIN "Place" AS p
    ON p."sID_UA" = hes.sID_UA
order by hes.nID_Service, hes.sID_UA