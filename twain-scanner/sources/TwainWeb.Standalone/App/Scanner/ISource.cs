using System.Collections.Generic;
using System.Drawing;
using TwainWeb.Standalone.App.Models.Response;
using TwainWeb.Standalone.App.Twain;

namespace TwainWeb.Standalone.App.Scanner
{
	public interface ISource
	{
        //Его номер
		int Index { get; }
        //Имя файла
		string Name { get; }
        //настройки сканера
		ScannerSettings GetScannerSettings();        
        //метод сканирования
		List<Image> Scan(SettingsAcquire settings);
	}
}
