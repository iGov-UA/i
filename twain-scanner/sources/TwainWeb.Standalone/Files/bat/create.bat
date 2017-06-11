@echo dp0 = %~dp0
sc stop TWAIN@Web
sc delete TWAIN@Web
sc create TWAIN@Web binPath= "%~dp0TwainWeb.Standalone.exe" DisplayName= "my TWAIN@Web" type= own  start= auto
sc start TWAIN@Web

@pause