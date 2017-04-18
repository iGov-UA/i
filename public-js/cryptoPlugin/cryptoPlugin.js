angular.module('cryptoPlugin', [])
    .factory('cryptoPluginFactory', function () {
        return {
            initialize: function () {
                if (typeof CryptoPlugin === "undefined") {
                    window.CryptoPlugin = {
                        config: {
                            pluginspage: "https://www.privatbank.ua",
                            minimalVersion: "1.2.2.0",
                            debugFunction: null,
                            enableWarning: true,
                            OCXPath: "/cryptoplugin.cab",
                            lagDelay: 300,
                            chromeExtensionId: ["pgfbdgicjmhenccemcijooffohcdanic", "hhgceiljneockhjiakblpicadmfcipii", "idfiabaafjemgcecklpgnebaebonghka"],
                            chromeExtensionPreamble: ["bankid.crypto.plugin", "papka24.crypto.plugin", "crypto.plugin"],
                            operaExtensionId: ["iofcmdkhogeiiijjppbnbclpikhldfac", "aiikngbhbnkcahmaelhdfeaeenccfkej"],
                            operaExtensionPreamble: ["crypto.plugin", "bankid.crypto.plugin"],
                            extensionIndex: 0,
                            extensionId: null,
                            extensionPreamble: null,
                            extensionFound: false,
                            chromeExtensionMode: 2,
                            isOpera: false,
                            isMozilla: false,
                            browserRegexp: {
                                kindle: /(kindle)\/([\w.]+)/,
                                avant: /avant\sbrowser?[\/\s]?([\w.]*)/,
                                chromium: /(chromium)\/([\w.-]+)/,
                                skyfire: /(skyfire)\/([\w.-]+)/,
                                vivaldi: /(vivaldi)\/([\w.-]+)/,
                                yandex: /(yabrowser)\/([\w.]+)/,
                                ucbrowser: /(uc\s?browser)[\/\s]?([\w.]+)/,
                                firefox: /(firefox)\/([\w.-]+)/,
                                netscape: /(navigator|netscape)\/([\w.-]+)/,
                                coast: /(coast)\/([\w.]+)/,
                                opera: /(op(era|r|ios))[\/\s]+([\w.]+)/,
                                msie: /(?:ms|\()(ie)|((trident).+rv[:\s]([\w.]+).+like\sgecko)|(edge)\/((\d+)?[\w.]+)/,
                                androidBrowser: /android.+version\/([\w.]+)\s+(?:mobile\s?safari|safari)/,
                                safari: /version\/([\w.]+).+?(mobile\s?safari|safari)/,
                                chrome: /(chrome|crmo|crios)\/([\w.]+)/,
                                spartan: /edge\/12/
                            }
                        }, connect: function () {
                            return function (n, e) {
                                CryptoPlugin.config.isOpera = CryptoPlugin.config.browserRegexp.opera.test(navigator.userAgent.toLowerCase());
                                CryptoPlugin.config.isMozilla = CryptoPlugin.config.browserRegexp.firefox.test(navigator.userAgent.toLowerCase());
                                var i = navigator.userAgent.toLowerCase();
                                if (CryptoPlugin.config.browserRegexp.androidBrowser.test(i) || CryptoPlugin.config.browserRegexp.vivaldi.test(i) || CryptoPlugin.config.browserRegexp.spartan.test(i)) {
                                    e({code: -100, message: "Браузер не поддерживается", source: "CryptoPlugin"});
                                    return null
                                }
                                if (typeof CryptoPlugin.plugin === "undefined" || CryptoPlugin.plugin === null || CryptoPlugin.plugin.pluginType !== "OCX") {
                                    t()
                                }
                                if (CryptoPlugin.plugin === null || CryptoPlugin.plugin.pluginType !== "OCX" && typeof CryptoPlugin.plugin.send === "undefined") {
                                    if (typeof e === "function") {
                                        e({
                                            code: 0,
                                            message: "Плагин не обнаружен или заблокирован",
                                            source: "CryptoPlugin"
                                        });
                                        e = null
                                    }
                                    return null
                                }
                                var o = {pluginType: CryptoPlugin.plugin.pluginType, version: ""};
                                l(o);
                                window.setTimeout(function () {
                                    if (CryptoPlugin.plugin.pluginType === "NM") {
                                        var i = function () {
                                            return function () {
                                                CryptoPlugin.plugin.callback = function () {
                                                    return function (i) {
                                                        if (typeof i.answer !== "undefined" && typeof i.answer.api !== "undefined") {
                                                            CryptoPlugin.API = JSON.parse(i.answer.api);
                                                            s(o);
                                                            u("getPluginInfo", null, null, function (e) {
                                                                l(o, e);
                                                                u("getVersion", null, null, function (e) {
                                                                    if (e && e.hasOwnProperty("version")) {
                                                                        o.version = e.version
                                                                    }
                                                                    if (typeof n === "function") {
                                                                        n(o)
                                                                    }
                                                                }, null)
                                                            }, null)
                                                        } else if (typeof e === "function") {
                                                            e({
                                                                code: 0,
                                                                message: "Плагин не обнаружен или заблокирован",
                                                                source: "connect"
                                                            });
                                                            e = null
                                                        }
                                                    }
                                                }();
                                                CryptoPlugin.plugin.send('{"function":"getAPI"}');
                                                return o
                                            }
                                        }();
                                        var t = 0;
                                        var r = CryptoPlugin.config.chromeExtensionId.length - 1;
                                        var f = CryptoPlugin.config.chromeExtensionId;
                                        var a = CryptoPlugin.config.chromeExtensionPreamble;
                                        if (CryptoPlugin.config.isOpera) {
                                            r = CryptoPlugin.config.operaExtensionId.length - 1;
                                            f = CryptoPlugin.config.operaExtensionId;
                                            a = CryptoPlugin.config.operaExtensionPreamble
                                        }
                                        if (!CryptoPlugin.config.extensionFound) {
                                            if (CryptoPlugin.config.isMozilla) {
                                                if (document.head.querySelector(".CryptoPluginExtensionLoaded") !== null) {
                                                    CryptoPlugin.config.extensionIndex = 0;
                                                    CryptoPlugin.config.extensionFound = true;
                                                    CryptoPlugin.config.extensionPreamble = "crypto.plugin";
                                                    CryptoPlugin.config.extensionId = "";
                                                    window.setTimeout(function () {
                                                        var n = i();
                                                        return function () {
                                                            if (typeof e === "function" && n.version === "") {
                                                                e({
                                                                    code: 0,
                                                                    message: "Плагин не обнаружен или заблокирован",
                                                                    source: "connect"
                                                                });
                                                                e = null
                                                            }
                                                        }
                                                    }(), 600)
                                                } else {
                                                    e({
                                                        code: -1,
                                                        message: "Расширение для Firefox не установлено",
                                                        source: "connect"
                                                    })
                                                }
                                            } else {
                                                var g = function (n) {
                                                    CryptoPlugin.config.extensionIndex = n;
                                                    var o = new XMLHttpRequest;
                                                    o.open("GET", "chrome-extension://" + f[CryptoPlugin.config.extensionIndex] + "/manifest.json", true);
                                                    o.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                                                    o.timeout = 100;
                                                    o.onreadystatechange = function () {
                                                        if (o.readyState === 4) {
                                                            if (o.status === 200) {
                                                                CryptoPlugin.config.extensionFound = true;
                                                                CryptoPlugin.config.extensionId = f[CryptoPlugin.config.extensionIndex];
                                                                CryptoPlugin.config.extensionPreamble = a[CryptoPlugin.config.extensionIndex];
                                                                var t = i();
                                                                window.setTimeout(function () {
                                                                    if (typeof e === "function" && t.version === "") {
                                                                        e({
                                                                            code: 0,
                                                                            message: "Плагин не обнаружен или заблокирован",
                                                                            source: "connect"
                                                                        });
                                                                        e = null
                                                                    }
                                                                }, 600)
                                                            } else {
                                                                if (n === r) {
                                                                    if (typeof e === "function") {
                                                                        window.setTimeout(function () {
                                                                            e({
                                                                                code: -1,
                                                                                message: "Расширение для " + (CryptoPlugin.config.isOpera ? "Opera" : "Chrome") + " не установлено",
                                                                                source: "connect"
                                                                            })
                                                                        }, 0)
                                                                    }
                                                                } else {
                                                                    g(n + 1)
                                                                }
                                                            }
                                                        }
                                                    };
                                                    o.send()
                                                };
                                                g(t)
                                            }
                                        } else {
                                            o = i();
                                            window.setTimeout(function () {
                                                if (typeof e === "function" && o.version === "") {
                                                    e({
                                                        code: 0,
                                                        message: "Плагин не обнаружен или заблокирован",
                                                        source: "connect"
                                                    });
                                                    e = null
                                                }
                                            }, 100)
                                        }
                                    } else {
                                        try {
                                            var c = CryptoPlugin.plugin.send('{"function":"getAPI"}');
                                            CryptoPlugin.API = JSON.parse(JSON.parse(c).answer.api);
                                            s(o)
                                        } catch (n) {
                                            if (typeof e === "function") {
                                                e({
                                                    code: 0,
                                                    message: "Плагин не обнаружен или заблокирован",
                                                    source: "connect"
                                                });
                                                e = null;
                                                return
                                            }
                                        }
                                        u("getPluginInfo", null, null, function (e) {
                                            l(o, e);
                                            u("getVersion", null, null, function (e) {
                                                if (e && e.hasOwnProperty("version")) {
                                                    o.version = e.version
                                                }
                                                if (typeof n === "function") {
                                                    n(o)
                                                }
                                            }, null)
                                        }, null)
                                    }
                                }, CryptoPlugin.plugin.pluginType === "OCX" ? 333 : 50)
                            };
                            function n() {
                                return "\v" == "v" || /msie/.test(navigator.userAgent.toLowerCase()) || /trident.*rv[ :]*11\./.test(navigator.userAgent.toLowerCase()) || typeof window.ActiveXObject !== "undefined"
                            }

                            function e() {
                                navigator.plugins.refresh(false);
                                if (document.getElementById("CryptoPluginInstance") !== null) {
                                    var n = document.getElementById("CryptoPluginInstance");
                                    n.parentNode.removeChild(n)
                                }
                                var e = document.createElement("EMBED");
                                e.setAttribute("id", "CryptoPluginInstance");
                                e.setAttribute("type", "application/x-vnd-cryptoplugin");
                                e.setAttribute("embed", "false");
                                e.setAttribute("width", "1");
                                e.setAttribute("height", "1");
                                e.setAttribute("pluginspage", CryptoPlugin.config.pluginspage);
                                document.body.appendChild(e);
                                CryptoPlugin.plugin = document.getElementById("CryptoPluginInstance");
                                CryptoPlugin.plugin.pluginType = "NPAPI"
                            }

                            function i() {
                                if (document.getElementById("CryptoPluginInstance") === null) {
                                    var n = document.createElement("INPUT");
                                    n.setAttribute("id", "CryptoPluginInstance");
                                    n.setAttribute("type", "hidden");
                                    n.setAttribute("value", "");
                                    document.body.appendChild(n)
                                }
                                if (typeof chrome === "undefined" || typeof chrome.runtime === "undefined" || typeof chrome.runtime.sendMessage === "undefined") {
                                    CryptoPlugin.config.chromeExtensionMode = 1
                                }
                                CryptoPlugin.plugin = {};
                                CryptoPlugin.plugin.onMessage = function (n) {
                                    if (n.data.sender === CryptoPlugin.config.extensionPreamble + ".native") {
                                        if (n.data.error && n.data.error === "disconnected") {
                                            if (typeof CryptoPlugin.config.debugFunction === "function") {
                                                CryptoPlugin.config.debugFunction("Chrome NM disconnect!", "warn")
                                            }
                                        }
                                        window.removeEventListener("message", CryptoPlugin.plugin.onMessage, true);
                                        if (CryptoPlugin.plugin.callback !== null && typeof CryptoPlugin.plugin.callback === "function") {
                                            CryptoPlugin.plugin.callback.call(null, n.data)
                                        }
                                    }
                                };
                                CryptoPlugin.plugin.send = function (n) {
                                    window.addEventListener("message", CryptoPlugin.plugin.onMessage, true);
                                    if (CryptoPlugin.config.chromeExtensionMode === 1) {
                                        document.getElementById("CryptoPluginInstance").value = n;
                                        window.postMessage({sender: CryptoPlugin.config.extensionPreamble + ".js"}, "*")
                                    } else {
                                        chrome.runtime.sendMessage(CryptoPlugin.config.extensionId, n, {includeTlsChannelId: true}, function (e) {
                                            if (typeof e === "undefined") {
                                                CryptoPlugin.config.chromeExtensionMode = 1;
                                                document.getElementById("CryptoPluginInstance").value = n;
                                                window.postMessage({sender: CryptoPlugin.config.extensionPreamble + ".js"}, "*")
                                            }
                                        })
                                    }
                                };
                                CryptoPlugin.plugin.pluginType = "NM"
                            }

                            function o() {
                                if (document.getElementById("CryptoPluginInstance") !== null) {
                                    var n = document.getElementById("CryptoPluginInstance");
                                    n.parentNode.removeChild(n)
                                }
                                var e = document.createElement("OBJECT");
                                e.setAttribute("ID", "CryptoPluginInstance");
                                e.setAttribute("codebase", CryptoPlugin.config.OCXPath + "#version=" + CryptoPlugin.config.minimalVersion.replace(/\./g, ","));
                                e.setAttribute("CLASSID", "CLSID:03EBA73D-329C-45D1-A2E4-9D7719BAD366");
                                e.setAttribute("width", "0");
                                e.setAttribute("height", "0");
                                document.body.appendChild(e);
                                CryptoPlugin.plugin = e;
                                CryptoPlugin.plugin.pluginType = "OCX"
                            }

                            function t() {
                                var t;
                                if (n()) {
                                    o()
                                } else if (window.chrome) {
                                    t = parseInt(navigator.userAgent.match(/Chrome\/(\d+)\./)[1], 10);
                                    if ((navigator.appVersion.indexOf("Linux") !== -1 || navigator.appVersion.indexOf("X11") !== -1) && t >= 35 || navigator.appVersion.indexOf("Win") === -1 && t >= 42 || navigator.appVersion.indexOf("Mac") === -1 && t >= 41 || CryptoPlugin.config.isOpera() && t >= 40) {
                                        i()
                                    } else {
                                        e()
                                    }
                                } else {
                                    t = 0;
                                    if (navigator.userAgent.match(/Firefox\/(\d+)\./)[1]) {
                                        t = parseInt(navigator.userAgent.match(/Firefox\/(\d+)\./)[1], 10)
                                    }
                                    if (CryptoPlugin.config.isMozilla && t > 51) {
                                        i()
                                    } else {
                                        e()
                                    }
                                }
                            }

                            function r(n, e, i, o, t) {
                                if (n.type === "success") {
                                    var r = ((new Date).getTime() - CryptoPlugin.plugin.time) / 1e3;
                                    if (n.hasOwnProperty("answer")) {
                                        if (typeof i === "function") {
                                            if (typeof CryptoPlugin.config.debugFunction === "function" && t !== "getSessionInfo") {
                                                CryptoPlugin.config.debugFunction("P1(" + r + "s)✈ " + JSON.stringify(n.answer), "info")
                                            }
                                            if (CryptoPlugin.plugin.pluginType === "NM" && e !== null && e.functionLevel > 0) {
                                                u("getSessionInfo", null, e, function () {
                                                    return function (o) {
                                                        l(e, o);
                                                        setTimeout(function () {
                                                            i(n.answer)
                                                        }, 0)
                                                    }
                                                }(), null)
                                            } else {
                                                setTimeout(function () {
                                                    i(n.answer)
                                                }, 0)
                                            }
                                        } else {
                                            if (typeof CryptoPlugin.config.debugFunction === "function" && t !== "getSessionInfo") {
                                                CryptoPlugin.config.debugFunction("P2(" + r + "s)→ " + JSON.stringify(n.answer), "info")
                                            }
                                            return n.answer
                                        }
                                    } else {
                                        if (typeof i === "function") {
                                            if (typeof CryptoPlugin.config.debugFunction === "function" && t !== "getSessionInfo") {
                                                CryptoPlugin.config.debugFunction("P3(" + r + "s)✈ [without arguments]", "info")
                                            }
                                            if (e !== null && e.functionLevel > 0) {
                                                u("getSessionInfo", null, e, function () {
                                                    return function (o) {
                                                        l(e, o);
                                                        setTimeout(function () {
                                                            i(n.answer)
                                                        }, 0)
                                                    }
                                                }(), function () {
                                                    return function () {
                                                        l(e, {sessionState: ""});
                                                        setTimeout(function () {
                                                            i(n.answer)
                                                        }, 0)
                                                    }
                                                }())
                                            } else {
                                                setTimeout(function () {
                                                    i(n.answer)
                                                }, 0)
                                            }
                                        } else {
                                            if (typeof CryptoPlugin.config.debugFunction === "function" && t !== "getSessionInfo") {
                                                CryptoPlugin.config.debugFunction("P4(" + r + "s)→ [without arguments]", "info")
                                            }
                                        }
                                    }
                                } else if (n.type === "error" || n.type === "message") {
                                    if (t === "getSessionInfo" && n.answer.errorCode === 2) {
                                        if (e !== null) {
                                            l(e, {sessionState: ""})
                                        }
                                    } else if (typeof CryptoPlugin.config.debugFunction === "function" && t !== "getSessionInfo") {
                                        CryptoPlugin.config.debugFunction("✗1 code = " + n.answer.errorCode + (n.answer.hasOwnProperty("errorText") ? ", message = " + n.answer.errorText : "") + (typeof t !== "undefined" ? ", source = " + t : ""), "warn")
                                    }
                                    if (t === "restoreSession") {
                                        l(e, {sessionState: ""})
                                    }
                                    if (e !== null && e.functionLevel > 0) {
                                        u("getSessionInfo", null, e, function (i) {
                                            l(e, i);
                                            if (typeof o === "function") {
                                                o({
                                                    code: n.answer.errorCode,
                                                    message: n.answer.hasOwnProperty("errorText") ? n.answer.errorText : "",
                                                    source: t
                                                })
                                            }
                                        }, null)
                                    } else if (typeof o === "function") {
                                        o({
                                            code: n.answer.errorCode,
                                            message: n.answer.hasOwnProperty("errorText") ? n.answer.errorText : "",
                                            source: t
                                        })
                                    }
                                }
                            }

                            function u(n, e, i, o, t) {
                                setTimeout(function () {
                                    var u = "";
                                    if (CryptoPlugin.plugin !== null && typeof CryptoPlugin.plugin.send !== "undefined") {
                                        if (CryptoPlugin.config.enableWarning && console.warn) {
                                            if (typeof o !== "function" && o !== null) {
                                                if (typeof o === "undefined") {
                                                    console.warn("successCallback isn't set in " + n)
                                                } else {
                                                    console.warn("successCallback isn't function in " + n)
                                                }
                                            }
                                            if (typeof t !== "function" && t !== null) {
                                                if (typeof t === "undefined") {
                                                    console.warn("errorCallback isn't set in " + n)
                                                } else {
                                                    console.warn("errorCallback isn't function in " + n)
                                                }
                                            }
                                        }
                                        if (typeof i !== "undefined" && i !== null && i.hasOwnProperty("sessionId") && i.sessionId !== null && i.sessionId !== "") {
                                            u = i.sessionId
                                        }
                                        var s = null;
                                        if (typeof e !== "undefined" && e !== null && e !== "") {
                                            s = {function: n, params: e, sessionId: u}
                                        } else {
                                            s = {function: n, sessionId: u}
                                        }
                                        if (typeof i !== "undefined" && i !== null && !i.callback && (n === "selectDir" && navigator.appVersion.indexOf("Win") !== -1 || n === "selectFile" && navigator.appVersion.indexOf("Win") !== -1 || n === "getDeviceList" || n === "checkOCSP" || n === "testConnection" || i.storageType === "device" && n !== "getSessionInfo" && n !== "getPluginInfo" && n !== "getCertificateInfo") && CryptoPlugin.plugin.pluginType !== "NM") {
                                            s.lag = true
                                        }
                                        var l = JSON.parse(JSON.stringify(s));
                                        if (typeof l.params !== "undefined") {
                                            delete l.url;
                                            for (var f in l.params) {
                                                if (l.params.hasOwnProperty(f) && f.toLowerCase().indexOf("password") !== -1) {
                                                    l.params[f] = new Array(l.params[f].length).join("*")
                                                }
                                            }
                                        }
                                        if (typeof CryptoPlugin.config.debugFunction === "function" && n !== "getSessionInfo") {
                                            CryptoPlugin.config.debugFunction("P5← " + JSON.stringify(l), "info")
                                        }
                                        if (n !== "getSessionInfo") {
                                            CryptoPlugin.plugin.time = (new Date).getTime()
                                        }
                                        var a = null;
                                        var g = null;
                                        if (n !== "getSessionInfo" && typeof i !== "undefined" && i && i.hasOwnProperty("sessionId") && i.sessionId !== "") {
                                            g = i
                                        }
                                        var c = null;
                                        s.url = document.URL;
                                        if (CryptoPlugin.plugin.pluginType === "NM") {
                                            if (document.getElementById("CryptoPluginInstance").value !== "") {
                                                var p = JSON.parse(document.getElementById("CryptoPluginInstance").value)["function"];
                                                if (typeof t === "function") {
                                                    t({
                                                        code: 5,
                                                        message: 'Выполняется метод "' + p + '", команда проигнорирована',
                                                        source: n
                                                    })
                                                }
                                                if (typeof CryptoPlugin.config.debugFunction === "function" && n !== "getSessionInfo") {
                                                    CryptoPlugin.config.debugFunction('✗2 code = 5, message = Выполняется метод "' + p + '", команда проигнорирована, source = ' + n, "warn")
                                                }
                                                return
                                            }
                                            if (typeof o === "function") {
                                                CryptoPlugin.plugin.callback = function () {
                                                    return function (e) {
                                                        r(e, g, o, t, n)
                                                    }
                                                }()
                                            }
                                            CryptoPlugin.plugin.send(JSON.stringify(s))
                                        } else {
                                            var y = CryptoPlugin.plugin.send(JSON.stringify(s));
                                            try {
                                                a = JSON.parse(y)
                                            } catch (e) {
                                                if (typeof CryptoPlugin.config.debugFunction === "function" && n !== "getSessionInfo") {
                                                    CryptoPlugin.config.debugFunction("Unpredictable behavior of plugin", "warn")
                                                }
                                                return
                                            }
                                            if (a.type === "lag") {
                                                if (typeof o === "function") {
                                                    var d = "interval" + Math.floor(Math.random() * 1e5);
                                                    CryptoPlugin[d] = window.setInterval(function () {
                                                        var e = d;
                                                        return function () {
                                                            var i = "";
                                                            try {
                                                                i = CryptoPlugin.plugin.send(JSON.stringify(s));
                                                                a = JSON.parse(i);
                                                                if (a.type !== "lag") {
                                                                    clearInterval(CryptoPlugin[e]);
                                                                    delete CryptoPlugin[e];
                                                                    r(a, g, o, t, n)
                                                                }
                                                            } catch (e) {
                                                                if (typeof CryptoPlugin.config.debugFunction === "function" && n !== "getSessionInfo") {
                                                                    CryptoPlugin.config.debugFunction("Unpredictable behavior of plugin: " + i, "warn")
                                                                }
                                                            }
                                                        }
                                                    }(), CryptoPlugin.config.lagDelay)
                                                } else {
                                                    while (true) {
                                                        c = (new Date).getTime();
                                                        while (true) {
                                                            if ((new Date).getTime() - c > CryptoPlugin.config.lagDelay) {
                                                                break
                                                            }
                                                        }
                                                        try {
                                                            y = CryptoPlugin.plugin.send(JSON.stringify(s));
                                                            a = JSON.parse(y)
                                                        } catch (e) {
                                                            if (typeof CryptoPlugin.config.debugFunction === "function" && n !== "getSessionInfo") {
                                                                CryptoPlugin.config.debugFunction("Unpredictable behavior of plugin: " + y, "warn")
                                                            }
                                                            return
                                                        }
                                                        if (a.type !== "lag") {
                                                            return r(a, g, null, t, n)
                                                        }
                                                    }
                                                }
                                            } else {
                                                return r(a, g, o, t, n)
                                            }
                                        }
                                    } else {
                                        if (typeof t === "function") {
                                            t({code: 0, message: "Плагин не обнаружен или заблокирован", source: n});
                                            t = null
                                        }
                                    }
                                }, 10)
                            }

                            function s(n) {
                                if (CryptoPlugin.hasOwnProperty("API")) {
                                    for (var e = CryptoPlugin.API.length - 1; e >= 0; e--) {
                                        var i = CryptoPlugin.API[e];
                                        if (!n.hasOwnProperty(CryptoPlugin.API[e].name)) {
                                            n[i.name] = function () {
                                                var e = i;
                                                var o;
                                                return function () {
                                                    var i = {}, t = false;
                                                    if (e.hasOwnProperty("params")) {
                                                        if (arguments.length > e.params.length + 2) {
                                                            if (typeof CryptoPlugin.config.debugFunction === "function") {
                                                                CryptoPlugin.config.debugFunction("Передано больше параметров чем нужно для функции " + e.name, "warn")
                                                            }
                                                        } else {
                                                            var r = 0;
                                                            for (o = 0; o < e.params.length; o++) {
                                                                if (!e.params[o].hasOwnProperty("optional") || !e.params[o]["optional"]) {
                                                                    r++
                                                                }
                                                            }
                                                            if (typeof CryptoPlugin.config.debugFunction === "function") {
                                                                if (arguments.length >= 2 && typeof arguments[arguments.length - 2] === "function" && r + 2 > arguments.length) {
                                                                    CryptoPlugin.config.debugFunction("Передано меньше параметров чем нужно для функции " + e.name, "warn")
                                                                }
                                                                if (arguments.length >= 1 && typeof arguments[arguments.length - 1] === "function" && r + 1 > arguments.length) {
                                                                    CryptoPlugin.config.debugFunction("Передано меньше параметров чем нужно для функции " + e.name, "warn")
                                                                }
                                                            }
                                                        }
                                                    } else if (arguments.length > 2 && typeof CryptoPlugin.config.debugFunction === "function") {
                                                        CryptoPlugin.config.debugFunction("Передано больше параметров чем нужно для функции " + e.name, "warn")
                                                    }
                                                    var s = null;
                                                    var f = null;
                                                    if (e.hasOwnProperty("params")) {
                                                        for (o = 0; o < arguments.length; o++) {
                                                            if (typeof arguments[o] === "function") {
                                                                if (o === arguments.length - 2 && typeof arguments[o + 1] === "function") {
                                                                    s = arguments[o];
                                                                    f = arguments[o + 1];
                                                                    break
                                                                } else if (o === arguments.length - 1) {
                                                                    s = arguments[o];
                                                                    break
                                                                } else if (typeof CryptoPlugin.config.debugFunction === "function") {
                                                                    CryptoPlugin.config.debugFunction("Функции обратного вызова переданы в неправильных позициях " + e.name, "warn")
                                                                }
                                                            } else {
                                                                if (e.params[o].hasOwnProperty("type")) {
                                                                    var a = e.params[o].type;
                                                                    if (a === "Number") {
                                                                        arguments[o] = parseInt(arguments[o]);
                                                                        if (isNaN(arguments[o])) {
                                                                            continue
                                                                        }
                                                                    } else if (a === "Boolean" && typeof arguments[o] !== "boolean") {
                                                                        if (arguments[o] === "") {
                                                                            continue
                                                                        }
                                                                        arguments[o] = arguments[o].toLowerCase() === "true"
                                                                    }
                                                                }
                                                                t = true;
                                                                if (arguments[o] !== null) {
                                                                    i[e.params[o].name] = arguments[o]
                                                                }
                                                                if (e.params[o].name === "sessionId" && e.name === "restoreSession") {
                                                                    n.sessionId = arguments[o]
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (!t) {
                                                        i = null
                                                    }
                                                    var g = null;
                                                    if (e.name === "updateInfo") {
                                                        u("getPluginInfo", null, null, function (e) {
                                                            l(n, e);
                                                            if (n.sessionId !== null && n.sessionId.length > 0) {
                                                                u("getSessionInfo", null, n, function (e) {
                                                                    l(n, e);
                                                                    s()
                                                                }, function (e) {
                                                                    if (e.code === 2) {
                                                                        l(n);
                                                                        if (typeof s === "function") {
                                                                            s()
                                                                        }
                                                                    } else {
                                                                        if (typeof f === "function") {
                                                                            f()
                                                                        }
                                                                    }
                                                                })
                                                            } else {
                                                                if (typeof s === "function") {
                                                                    s()
                                                                }
                                                            }
                                                        }, f);
                                                        return
                                                    }
                                                    if (e.name === "openSession" || e.name === "restoreSession") {
                                                        g = function () {
                                                            return function (e) {
                                                                l(n);
                                                                if (typeof n !== "undefined" && typeof e !== "undefined" && e && n && n.hasOwnProperty("sessionId") && n.sessionId !== "" && e.hasOwnProperty("sessionId")) {
                                                                    n.sessionId = e.sessionId
                                                                }
                                                                u("getPluginInfo", null, null, function (e) {
                                                                    l(n, e);
                                                                    u("getSessionInfo", null, n, function (e) {
                                                                        l(n, e);
                                                                        s(n.sessionId)
                                                                    }, f)
                                                                }, f);
                                                                return e
                                                            }
                                                        }()
                                                    } else {
                                                        g = s
                                                    }
                                                    return u(e.name, i, n, g, f)
                                                }
                                            }()
                                        }
                                    }
                                }
                            }

                            function l(n, e) {
                                if (typeof n === "undefined" || n === null) {
                                    return
                                }
                                if (typeof e === "undefined") {
                                    n.functionLevel = 0;
                                    n.activeSessionCount = 0;
                                    n.sessionId = null;
                                    n.sessionExpiryTime = null;
                                    n.sessionState = "absent";
                                    n.filePath = null;
                                    n.deviceId = null;
                                    n.storageType = null;
                                    n.storageSpec = null;
                                    n.storageVersion = null;
                                    n.deviceDriverVersion = null;
                                    n.deviceModel = null;
                                    n.deviceName = null;
                                    n.deviceSerialNumber = null;
                                    n.deviceState = null;
                                    n.deviceHasPuk = null;
                                    n.keyType = null;
                                    n.keyAlias = null;
                                    n.fileModified = null
                                } else {
                                    for (var i in e) {
                                        if (e.hasOwnProperty(i)) {
                                            if (e[i] === "" || e[i] === null) {
                                                if (n.hasOwnProperty(i)) {
                                                    n[i] = null
                                                }
                                            } else {
                                                if (i === "sessionExpiryTime") {
                                                    n[i] = new Date(parseInt(e[i] * 1e3))
                                                } else {
                                                    n[i] = e[i]
                                                }
                                            }
                                            if (i === "sessionState") {
                                                switch (e[i]) {
                                                    case"":
                                                    case"absent":
                                                        n.functionLevel = 0;
                                                        n.sessionId = null;
                                                        n.sessionState = "absent";
                                                        break;
                                                    case"new":
                                                        n.functionLevel = 1;
                                                        break;
                                                    case"createStorage":
                                                    case"selectModifiedFileStorage":
                                                        n.functionLevel = 2;
                                                        break;
                                                    case"selectFileStorage":
                                                        n.functionLevel = 3;
                                                        break;
                                                    case"selectDeviceStorage":
                                                        n.functionLevel = 4;
                                                        break;
                                                    case"selectKey":
                                                        n.functionLevel = 5;
                                                        break
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }()
                    }
                }
                return "undefined" !== typeof CryptoPlugin;
            }
        }
    });