UPDATE "HistoryEvent_Service" SET "nID_StatusType" = 0 WHERE "sID_Status" = 'Заявка подана';
UPDATE "HistoryEvent_Service" SET "nID_StatusType" = 11 WHERE "sID_Status" = 'Заявка виконана';
UPDATE "HistoryEvent_Service" SET "nID_StatusType" = 2 WHERE "sID_Status" LIKE '%(в роботі)';
UPDATE "HistoryEvent_Service" SET "nID_StatusType" = 3 WHERE "sID_Status" = 'Запит на уточнення даних';
UPDATE "HistoryEvent_Service" SET "sID_Status" = regexp_replace("sID_Status", '(в роботі)$', '') WHERE "sID_Status" LIKE '%(в роботі)';
UPDATE "HistoryEvent_Service" SET "sID_Status" = '' WHERE "sID_Status" LIKE '%Заявка подана%' OR "sID_Status" LIKE '%Заявка виконана%' OR "sID_Status" LIKE '%Запит на уточнення даних%';
UPDATE "HistoryEvent_Service" SET "sID_Status" = '' WHERE "sID_Status" is null;
UPDATE "HistoryEvent_Service" SET "sUserTaskName" = "sID_Status"; 
