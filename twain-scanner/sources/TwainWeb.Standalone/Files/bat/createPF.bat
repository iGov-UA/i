@echo off
set serv=TWAIN@Web
@echo Пересоздаем сервис %serv%
@echo ============================
@echo.

cd %~dp0
cd..\..

sc stop %serv%
sc delete %serv%
sc create %serv% binPath= "%CD%\TwainWeb.Standalone.exe" DisplayName= "My %serv%" type= own  start= auto
sc start %serv%

@echo.
@pause