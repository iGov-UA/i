@echo %~dp0
cd %~dp0
cd..\..
cd bin\Debug
@echo.
@echo -------------------
sc stop TWAIN@Web
sc delete TWAIN@Web
sc create TWAIN@Web binPath= "%CD%\TwainWeb.Standalone.exe" DisplayName= "my TWAIN@Web" type= own  start= auto
sc start TWAIN@Web

@pause