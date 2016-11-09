select CONCAT(CAST(hes.nID_Service AS VARCHAR), CAST(hes.sID_UA AS VARCHAR)) AS nID, hes.nID_Service AS nID_Service, s.sName AS ServiceName,
       hes.sID_UA AS SID_UA, p.sName AS placeName, count(*) AS nCountTotal,
       count(hes.nID_Proccess_Feedback) AS nCountFeedback, count(hes.nID_Proccess_Escalation) AS nCountEscalation,
       avg(hes.nRate) AS averageRate, avg(hes.nTimeMinutes) AS averageTime
from HistoryEvent_Service AS hes
       LEFT OUTER JOIN Service AS s
              ON s.nID = hes.nID_Service
       LEFT OUTER JOIN Place AS p
              ON p.sID_UA = hes.sID_UA
where hes.sDate >= :dateFrom
      and hes.sDate < :dateTo
group by hes.nID_Service, hes.sID_UA
order by hes.nID_Service, hes.sID_UA
