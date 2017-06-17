@echo off
set serv=TWAIN@Web
@echo Пересоздаем сервис %serv%
@echo ============================
@echo.
sc stop %serv%
sc start %serv%

@echo.
@pause