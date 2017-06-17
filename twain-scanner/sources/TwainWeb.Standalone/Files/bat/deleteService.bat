@echo off
set serv=TWAIN
@echo Удаляем сервис
@echo ====================
@echo.

sc stop %serv%
sc delete %serv%


sc query | findstr /i "%serv%"

IF ERRORLEVEL 1 (GOTO NOTEXIST) ELSE GOTO EXIST
:NOTEXIST
@echo Сервис %serv% не существует
GOTO ENDGOTO

:EXIST

::Планировщик задач можно написать SC QUERY schedule, и это будет работать везде. 
::Проверить, запущена ли служба, можно по наличию строки RUNNING или STOPPED:
sc query schedule | find "RUNNING"

sc stop %serv%
sc delete %serv%
@echo Успех :) сервис %serv% был удален

:ENDGOTO
@echo.


@pause