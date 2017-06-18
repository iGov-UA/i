using log4net;
using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.ServiceProcess;
using System.Text;
using System.Threading;
using TwainWeb.Standalone.App.Binders;
using TwainWeb.Standalone.App.Cache;
using TwainWeb.Standalone.App.Controllers;
using TwainWeb.Standalone.App.Models.Response;
using TwainWeb.Standalone.App.Scanner;

namespace TwainWeb.Standalone.Host
{
    public class ScanService : ServiceBase
    {
        private readonly ILog _logger = LogManager.GetLogger(typeof(ScanService));
        private WindowsMessageLoopThread _messageLoop;

        private readonly object _markerAsynchrone = new object();
        private readonly int _port;
        private readonly string _procesName;

        public ScanService(int port, string serviceName)
        {
            _port = port;
            ServiceName = serviceName;
            _procesName = "/" + serviceName + "/";
        }


        public HttpServerError CheckServer()
        {
            var startResult = StartServer();
            if (startResult != null)
                return startResult;
            for (var i = 0; i < 100; i++)
            {
                if (StopServer() == null)
                    break;
            }
            return null;
        }

        private HttpServerError StartServer()
        {
            try
            {
                _httpServer = new HttpServer(10);
                _httpServer.ProcessRequest += httpServer_ProcessRequest;
                _cacheSettings = new CacheSettings();
                _httpServer.Start("http://+:" + _port + _procesName);
            }
            catch (Exception ex)
            {
                _logger.ErrorFormat("Code: {0}, Text: {1}", ((System.Net.HttpListenerException)ex).ErrorCode, ex);
                return new HttpServerError { Code = ((System.Net.HttpListenerException)ex).ErrorCode, Text = ex.ToString() };
            }

            try
            {
                var fwHelper = new FierwallHelper();
                fwHelper.AddRuleForPort(_port);
            }
            catch (Exception e)
            {
                return new HttpServerError { Code = 0, Text = e.ToString() };
            }
            return null;
        }

        private string StopServer()
        {
            try
            {
                _httpServer.Stop();
            }
            catch (Exception ex)
            {
                _logger.Error(ex);
                return ex.Message;
            }
            return null;
        }

        private IScannerManager _scannerManager;



        private HttpServer _httpServer;
        private CacheSettings _cacheSettings;

        protected override void OnStart(string[] args)
        {
            _logger.InfoFormat("Start service on port: {0}", _port);
            _messageLoop = new WindowsMessageLoopThread();
            var smFactory = new ScannerManagerFactory();
            try
            {
                _scannerManager = smFactory.GetScannerManager(_messageLoop);
            }
            catch (Exception e)
            {
                _logger.ErrorFormat(e.ToString());
            }
            StartServer();
            var sdf = new Thread(() => _logger.InfoFormat("Http server started"));
            sdf.Start();
        }

        public void Start()
        {
            _messageLoop = new WindowsMessageLoopThread();
            var smFactory = new ScannerManagerFactory();
            try
            {
                _scannerManager = smFactory.GetScannerManager(_messageLoop);
            }
            catch (Exception e)
            {
                _logger.ErrorFormat(e.ToString());
            }
            StartServer();
        }


        private void httpServer_ProcessRequest(System.Net.HttpListenerContext ctx)
        {
            ActionResult actionResult;
            ctx.Response.AppendHeader("Access-Control-Allow-Origin", "*");
            ctx.Response.AppendHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
            if (ctx.Request.HttpMethod == "POST")
            {
                var segments = new Uri(ctx.Request.Url.AbsoluteUri).Segments;
                if (segments.Length > 1 && segments[segments.Length - 2] == (ServiceName + "/") && segments[segments.Length - 1] == "ajax")
                {
                    var scanFormModelBinder = new ModelBinder(GetPostData(ctx.Request));
                    var method = scanFormModelBinder.BindAjaxMethod();
                    var scanController = new ScanController(_markerAsynchrone);
                    switch (method)
                    {
                        case "GetScannerParameters":
                            actionResult = scanController.GetScannerParameters(_scannerManager, _cacheSettings, scanFormModelBinder.BindSourceIndex());
                            break;

                        case "Scan":
                            actionResult = scanController.Scan(scanFormModelBinder.BindScanForm(), _scannerManager);
                            break;

                        case "RestartWia":
                            actionResult = scanController.RestartWia();
                            break;

                        case "Restart":
                            actionResult = scanController.Restart();
                            break;

                        default:
                            actionResult = DefaultResponce(ctx);
                            break;
                    }
                }
                else
                {
                    actionResult = DefaultResponce(ctx);
                }
            }
            else if (ctx.Request.HttpMethod == "GET")
            {
                if (ctx.Request.Url.AbsolutePath.Length >= _procesName.Length)
                {
                    var homeCtrl = new HomeController();
                    var method = ctx.Request.Url.AbsolutePath.Substring(_procesName.Length);

                    switch (method)
                    {
                        case "download":
                            ModelBinder MB = new ModelBinder(GetGetData(ctx.Request));
                            var fileParam = MB.BindDownloadFile();

                            if (MB.IsBase64)
                                actionResult = homeCtrl.ConvertToBase64File(fileParam);
                            else
                                actionResult = homeCtrl.DownloadFile(fileParam);
                            break;

                        case "version":
                            actionResult = homeCtrl.GetVersionProgram(
                                Assembly.GetEntryAssembly().GetName().Version.ToString());
                            break;

                        default:
                            if (method == "")
                                method = "index.html";
                            actionResult = homeCtrl.StaticFile(method);
                            break;
                    }
                }
                else
                {
                    actionResult = DefaultResponce(ctx);
                }
            }
            else
            {
                actionResult = DefaultResponce(ctx);
            }


            if (actionResult.FileNameToDownload != null)
                ctx.Response.AddHeader("Content-Disposition", "attachment; filename*=UTF-8''" + Uri.EscapeDataString(Uri.UnescapeDataString(actionResult.FileNameToDownload)));

            if (actionResult.ContentType != null)
                ctx.Response.ContentType = actionResult.ContentType;

            try
            {
                ctx.Response.OutputStream.Write(actionResult.Content, 0, actionResult.Content.Length);
            }
            catch (Exception)
            {
            }
        }


        private Dictionary<string, string> GetGetData(System.Net.HttpListenerRequest request)
        {
            var getDataString = request.RawUrl.Substring(request.RawUrl.IndexOf("?") + 1);
            var getData = parseQueryString(getDataString);
            return getData;
        }

        private Dictionary<string, string> GetPostData(System.Net.HttpListenerRequest request)
        {
            Dictionary<string, string> postData;
            using (var reader = new StreamReader(request.InputStream))
            {
                var postedData = reader.ReadToEnd();
                postData = parseQueryString(postedData);
            }

            return postData;
        }

        private Dictionary<string, string> parseQueryString(string query)
        {
            var data = new Dictionary<string, string>();
            foreach (var item in query.Split(new[] { '&' }, StringSplitOptions.RemoveEmptyEntries))
            {
                var tokens = item.Split(new[] { '=' }, StringSplitOptions.RemoveEmptyEntries);
                if (tokens.Length < 2)
                {
                    continue;
                }
                var paramName = tokens[0];
                var paramValue = Uri.UnescapeDataString(tokens[1]);
                data.Add(paramName, paramValue);
            }

            return data;
        }

        protected override void OnStop()
        {
            _logger.InfoFormat("Stop server...");
            _messageLoop.Stop();
            StopServer();
            _logger.InfoFormat("Service stopped");
            foreach (var appender in _logger.Logger.Repository.GetAppenders())
            {
                appender.Close();
            }
        }


        /// <summary>
        /// Ответ по умолчанию
        /// </summary>
        /// <param name="ctx">Объект запроса</param>
        /// <returns></returns>
        private ActionResult DefaultResponce(System.Net.HttpListenerContext ctx)
        {
            ctx.Response.Redirect(_procesName);
            return new ActionResult { Content = new byte[0] };
        }
    }
}