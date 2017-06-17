@echo off
set serv=TWAIN
@echo Стартуем сервис %serv%
@echo ====================
@echo.

sc start %serv%

@echo.
@pause