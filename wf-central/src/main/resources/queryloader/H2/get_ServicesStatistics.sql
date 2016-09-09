select hes.nID_Service AS nID, hes.nID_Service AS nID_Service, s.sName AS ServiceName,
       hes.sID_UA AS SID_UA, p.sName AS placeName, count(*) AS nCountTotal,
       count(hes.nID_Proccess_Feedback) AS nCountFeedback, count(hes.nID_Proccess_Escalation) AS nCountEscalation,
       avg(hes.nRate) AS averageRate, avg(hes.nTimeMinutes) AS averageTime
from HistoryEvent_Service AS hes, Service AS s, Place AS p
where s.nID = hes.nID_Service
      and p.sID_UA = hes.sID_UA
      and hes.sDate >= :dateFrom
      and hes.sDate < :dateTo
group by hes.nID_Service, hes.sID_UA