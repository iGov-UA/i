@echo off
set serv=TWAIN
@echo Останавливаем сервис %serv%
@echo ===============================
@echo.

sc stop %serv%
@echo.
@pause