select min(hes."nID_Service") AS nID, hes."nID_Service" AS nID_Service, min(s."sName") AS ServiceName,
       hes."sID_UA" AS SID_UA, min(p."sName") AS placeName, count(*) AS nCountTotal,
       avg(hes."nRate") AS averageRate, avg(hes."nTimeMinutes")/60 AS averageTime
from "HistoryEvent_Service" AS hes, "Service" AS s, "Place" AS p
where s."nID" = hes."nID_Service"
      and p."sID_UA" = hes."sID_UA"
      and hes."sDate" >= to_timestamp( :dateFrom , 'YYYY-MM-DD hh24:mi:ss')
      and hes."sDate" < to_timestamp( :dateTo  , 'YYYY-MM-DD hh24:mi:ss')
group by hes."nID_Service", hes."sID_UA"