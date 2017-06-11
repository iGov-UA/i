@echo off
@echo Удаляем сервис
@echo ====================
@echo.
sc query | findstr /i "TWAIN@Web"

IF ERRORLEVEL 1 (GOTO NOTEXIST) ELSE GOTO EXIST
:NOTEXIST
@echo Сервис не существует
GOTO ENDGOTO

:EXIST

::Планировщик задач можно написать SC QUERY schedule, и это будет работать везде. 
::Проверить, запущена ли служба, можно по наличию строки RUNNING или STOPPED:
sc query schedule | find "RUNNING"

sc stop TWAIN@Web
sc delete TWAIN@Web
@echo Успех :) сервис был удален

:ENDGOTO
@echo.


@pause