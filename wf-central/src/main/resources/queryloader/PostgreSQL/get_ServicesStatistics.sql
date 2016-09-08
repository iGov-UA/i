select distinct (hes.nID_Service || hes.sID_UA) AS nID, hes.nID_Service AS nID_Service, s."sName" AS ServiceName,
                hes.sID_UA AS SID_UA, p."sName" AS placeName, hes.nCountTotal AS nCountTotal,
                hes.averageRate AS averageRate, hes.averageTime AS averageTime
from ( select hes."nID_Service" AS nID_Service, hes."sID_UA" AS SID_UA, count(*) AS nCountTotal,
              avg(hes."nRate") AS averageRate, avg(hes."nTimeMinutes") AS averageTime
       from "HistoryEvent_Service" AS hes
       where hes."sDate" >= to_timestamp( :dateFrom , 'YYYY-MM-DD hh24:mi:ss')
             and hes."sDate" < to_timestamp( :dateTo , 'YYYY-MM-DD hh24:mi:ss')
       group by hes."nID_Service", hes."sID_UA"
     ) AS hes, "Service" AS s, "Place" AS p
where s."nID" = hes.nID_Service
      and p."sID_UA" = hes.sID_UA
order by hes.nID_Service, hes.sID_UA