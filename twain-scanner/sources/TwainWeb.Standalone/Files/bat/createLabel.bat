@set @x=0; /*
@cscript /e:jscript "%~f0"
@exit */
oShell = new ActiveXObject("WScript.Shell");
myDir = oShell.CurrentDirectory;
uShortcut = oShell.CreateShortcut(oShell.SpecialFolders("Desktop") + "\\TWAIN@Web1.lnk");
uShortcut.TargetPath = myDir + "\\TwainWeb.Standalone.exe";
uShortcut.Arguments = "run";
uShortcut.WorkingDirectory = myDir;
//uShortcut.HotKey = "CTRL+ALT+SHIFT+C";
// uShortcut.Description = "";
// uShortcut.IconLocation = "";
// uShortcut.WindowStyle = 1;       // Стиль окна: 1-Обычное; 3-Развёрнутое; 7-Свёрнутое
uShortcut.Save();
 