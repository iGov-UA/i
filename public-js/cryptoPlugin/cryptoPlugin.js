angular.module('cryptoPlugin', [])
    .factory('cryptoPluginFactory', function () {
        return {
            initialize: function () {
                return "undefined" === typeof CryptoPlugin && (window.CryptoPlugin = {
                        config: {
                            pluginspage: "https://www.privatbank.ua",
                            minimalVersion: "1.0.2.0",
                            debugFunction: null,
                            OCXPath: "cryptoplugin.cab",
                            lagDelay: 100,
                            chromeExtensionId: ["idfiabaafjemgcecklpgnebaebonghka", "pgfbdgicjmhenccemcijooffohcdanic", ""],
                            chromeExtensionPreamble: ["crypto.plugin", "bankid.crypto.plugin", ""],
                            chromeExtensionIndex: 0
                        }, connect: function () {
                            function p() {
                                navigator.plugins.refresh(!1);
                                if (null == document.getElementById("CryptoPluginInstance") || "undefined" === typeof document.getElementById("CryptoPluginInstance").send) {
                                    if (null != document.getElementById("CryptoPluginInstance")) {
                                        var a = document.getElementById("CryptoPluginInstance");
                                        a.parentNode.removeChild(a)
                                    }
                                    a = document.createElement("EMBED");
                                    a.setAttribute("id", "CryptoPluginInstance");
                                    a.setAttribute("type", "application/x-vnd-cryptoplugin");
                                    a.setAttribute("embed", "false");
                                    a.setAttribute("width", "1");
                                    a.setAttribute("height", "1");
                                    a.setAttribute("pluginspage", CryptoPlugin.config.pluginspage);
                                    document.body.appendChild(a)
                                }
                                CryptoPlugin.plugin =
                                    document.getElementById("CryptoPluginInstance");
                                CryptoPlugin.plugin.pluginType = "NPAPI"
                            }

                            function s() {
                                if (null == document.getElementById("CryptoPluginInstance")) {
                                    var a = document.createElement("INPUT");
                                    a.setAttribute("id", "CryptoPluginInstance");
                                    a.setAttribute("type", "hidden");
                                    a.setAttribute("value", "");
                                    document.body.appendChild(a)
                                }
                                CryptoPlugin.plugin = {};
                                CryptoPlugin.plugin.onMessage = function (a) {
                                    a.data.sender == CryptoPlugin.config.chromeExtensionPreamble[CryptoPlugin.config.chromeExtensionIndex] + ".native" &&
                                    (a.data.error && "disconnected" === a.data.error && "function" === typeof CryptoPlugin.config.debugFunction && CryptoPlugin.config.debugFunction("Chrome NM disconnect!", "warn"), window.removeEventListener("message", CryptoPlugin.plugin.onMessage, !0), null != CryptoPlugin.plugin.callback && CryptoPlugin.plugin.callback.call(null, a.data))
                                };
                                CryptoPlugin.plugin.send = function (a) {
                                    window.addEventListener("message", CryptoPlugin.plugin.onMessage, !0);
                                    document.getElementById("CryptoPluginInstance").value = a;
                                    window.postMessage({
                                        sender: CryptoPlugin.config.chromeExtensionPreamble[CryptoPlugin.config.chromeExtensionIndex] +
                                        ".js"
                                    }, "*")
                                };
                                CryptoPlugin.plugin.pluginType = "NM"
                            }

                            function t() {
                                if ("\v" == "v" || /msie/.test(navigator.userAgent.toLowerCase()) || /trident.*rv[ :]*11\./.test(navigator.userAgent.toLowerCase()) || "undefined" !== typeof window.ActiveXObject) {
                                    if (null != document.getElementById("CryptoPluginInstance")) {
                                        var a = document.getElementById("CryptoPluginInstance");
                                        a.parentNode.removeChild(a)
                                    }
                                    a = document.createElement("OBJECT");
                                    a.setAttribute("ID", "CryptoPluginInstance");
                                    a.setAttribute("codebase", CryptoPlugin.config.OCXPath +
                                        "#version\x3d" + CryptoPlugin.config.minimalVersion.replace(/\./g, ","));
                                    a.setAttribute("CLASSID", "CLSID:03EBA73D-329C-45D1-A2E4-9D7719BAD366");
                                    a.setAttribute("width", "0");
                                    a.setAttribute("height", "0");
                                    document.body.appendChild(a);
                                    CryptoPlugin.plugin = a;
                                    CryptoPlugin.plugin.pluginType = "OCX"
                                } else window.chrome ? (a = parseInt(window.navigator.appVersion.match(/Chrome\/(\d+)\./)[1], 10), -1 != navigator.appVersion.indexOf("Linux") && 35 <= a || -1 === navigator.appVersion.indexOf("Win") && 42 <= a || -1 === navigator.appVersion.indexOf("Mac") &&
                                41 <= a || -1 !== navigator.userAgent.toLowerCase().indexOf("opr/") && 40 <= a ? s() : p()) : p()
                            }

                            function n(a, b, d, l, c) {
                                if ("success" === a.type)if (null !== b && 0 < b.functionLevel && "NM" !== CryptoPlugin.plugin.pluginType && ("closeSession" === c || "closeAllSessions" === c ? e(b, k("getPluginInfo", null, b, null, null)) : e(b, k("getSessionInfo", null, b, null, null))), l = ((new Date).getTime() - CryptoPlugin.plugin.time) / 1E3, a.hasOwnProperty("answer"))if ("function" === typeof d) "function" === typeof CryptoPlugin.config.debugFunction && "getSessionInfo" !==
                                c && "getPluginInfo" !== c && CryptoPlugin.config.debugFunction("P(" + l + "s)\u2708 " + JSON.stringify(a.answer), "info"), "NM" === CryptoPlugin.plugin.pluginType && null != b && 0 < b.functionLevel ? k("getSessionInfo", null, b, function () {
                                    return function (c) {
                                        e(b, c);
                                        d(a.answer)
                                    }
                                }(), null) : d(a.answer); else return "function" === typeof CryptoPlugin.config.debugFunction && "getSessionInfo" !== c && "getPluginInfo" !== c && CryptoPlugin.config.debugFunction("P(" + l + "s)\u2192 " + JSON.stringify(a.answer), "info"), a.answer; else"function" === typeof d ?
                                    ("function" === typeof CryptoPlugin.config.debugFunction && "getSessionInfo" !== c && "getPluginInfo" !== c && CryptoPlugin.config.debugFunction("P(" + l + "s)\u2708 [without arguments]", "info"), "NM" === CryptoPlugin.plugin.pluginType && null != b && 0 < b.functionLevel ? k("getSessionInfo", null, b, function () {
                                        return function (a) {
                                            e(b, a);
                                            d()
                                        }
                                    }(), null) : d()) : "function" === typeof CryptoPlugin.config.debugFunction && "getSessionInfo" !== c && "getPluginInfo" !== c && CryptoPlugin.config.debugFunction("P(" + l + "s)\u2192 [without arguments]", "info");
                                else if ("error" === a.type || "message" === a.type) "getSessionInfo" === c && 2 === a.answer.errorCode ? null != b && e(b, {sessionState: "absent"}) : "function" === typeof CryptoPlugin.config.debugFunction && CryptoPlugin.config.debugFunction("\u2717 code \x3d " + a.answer.errorCode + (a.answer.hasOwnProperty("errorText") ? ", message \x3d " + a.answer.errorText : "") + ("undefined" !== typeof c ? ", source \x3d " + c : ""), "warn"), "restoreSession" === c && e(b, {sessionState: ""}), null != b && 0 < b.functionLevel && ("NM" === CryptoPlugin.plugin.pluginType ?
                                    k("getSessionInfo", null, b, function (a) {
                                        e(b, a)
                                    }, null) : e(b, k("getSessionInfo", null, b, null, null))), "function" === typeof l && l({
                                    code: a.answer.errorCode,
                                    message: a.answer.hasOwnProperty("errorText") ? a.answer.errorText : "",
                                    source: c
                                })
                            }

                            function k(a, b, d, l, c) {
                                var e = "";
                                if (null != CryptoPlugin.plugin && "undefined" !== typeof CryptoPlugin.plugin.send) {
                                    "undefined" !== typeof d && null != d && d.hasOwnProperty("sessionId") && null != d.sessionId && "" != d.sessionId && (e = d.sessionId);
                                    var g = null, g = "undefined" !== typeof b && null != b && "" != b ? {
                                        "function": a,
                                        params: b, sessionId: e
                                    } : {"function": a, sessionId: e};
                                    "undefined" === typeof d || null == d || d.callback || "selectDir" !== a && "selectFile" !== a && "getDeviceList" !== a && "device" !== d.storageType || "NM" == CryptoPlugin.plugin.pluginType || -1 == navigator.appVersion.indexOf("Win") || (g.lag = !0);
                                    b = JSON.parse(JSON.stringify(g));
                                    if ("undefined" !== typeof b.params) {
                                        delete b.url;
                                        for (var h in b.params)b.params.hasOwnProperty(h) && -1 != h.toLowerCase().indexOf("password") && (b.params[h] = Array(b.params[h].length).join("*"))
                                    }
                                    "function" === typeof CryptoPlugin.config.debugFunction &&
                                    "getSessionInfo" !== a && "getPluginInfo" !== a && CryptoPlugin.config.debugFunction("P\u2190 " + JSON.stringify(b), "info");
                                    "getSessionInfo" !== a && (CryptoPlugin.plugin.time = (new Date).getTime());
                                    var f = null, m = null;
                                    "getSessionInfo" !== a && "undefined" !== typeof d && null != d && d.hasOwnProperty("sessionId") && "" != d.sessionId && (m = d);
                                    d = null;
                                    g.url = document.URL;
                                    if ("NM" === CryptoPlugin.plugin.pluginType) "" != document.getElementById("CryptoPluginInstance").value ? (d = JSON.parse(document.getElementById("CryptoPluginInstance").value)["function"],
                                    "function" === typeof c && c({
                                        code: 5,
                                        message: '\u0412\u044b\u043f\u043e\u043b\u043d\u044f\u0435\u0442\u0441\u044f \u043c\u0435\u0442\u043e\u0434 "' + d + '", \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u043f\u0440\u043e\u0438\u0433\u043d\u043e\u0440\u0438\u0440\u043e\u0432\u0430\u043d\u0430',
                                        source: a
                                    }), "function" === typeof CryptoPlugin.config.debugFunction && CryptoPlugin.config.debugFunction('\u2717 code \x3d 5, message \x3d \u0412\u044b\u043f\u043e\u043b\u043d\u044f\u0435\u0442\u0441\u044f \u043c\u0435\u0442\u043e\u0434 "' +
                                        d + '", \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u043f\u0440\u043e\u0438\u0433\u043d\u043e\u0440\u0438\u0440\u043e\u0432\u0430\u043d\u0430, source \x3d ' + a, "warn")) : ("function" === typeof l && (CryptoPlugin.plugin.callback = function () {
                                        return function (b) {
                                            n(b, m, l, c, a)
                                        }
                                    }()), CryptoPlugin.plugin.send(JSON.stringify(g))); else {
                                        h = CryptoPlugin.plugin.send(JSON.stringify(g));
                                        try {
                                            f = JSON.parse(h)
                                        } catch (k) {
                                            "function" === typeof CryptoPlugin.config.debugFunction && CryptoPlugin.config.debugFunction("Unpredictable behavior of plugin",
                                                "warn");
                                            return
                                        }
                                        if ("lag" === f.type)if ("function" === typeof l) {
                                            var q = "interval" + Math.floor(1E5 * Math.random());
                                            CryptoPlugin[q] = window.setInterval(function () {
                                                return function () {
                                                    var b = "";
                                                    try {
                                                        b = CryptoPlugin.plugin.send(JSON.stringify(g)), f = JSON.parse(b)
                                                    } catch (d) {
                                                        "function" === typeof CryptoPlugin.config.debugFunction && CryptoPlugin.config.debugFunction("Unpredictable behavior of plugin: " + b, "warn");
                                                        return
                                                    }
                                                    "lag" !== f.type && (clearInterval(CryptoPlugin[q]), delete CryptoPlugin[q], n(f, m, l, c, a))
                                                }
                                            }(), CryptoPlugin.config.lagDelay)
                                        } else for (; ;) {
                                            for (d =
                                                     (new Date).getTime(); !((new Date).getTime() - d > CryptoPlugin.config.lagDelay););
                                            try {
                                                h = CryptoPlugin.plugin.send(JSON.stringify(g)), f = JSON.parse(h)
                                            } catch (p) {
                                                "function" === typeof CryptoPlugin.config.debugFunction && CryptoPlugin.config.debugFunction("Unpredictable behavior of plugin: " + h, "warn");
                                                break
                                            }
                                            if ("lag" !== f.type)return n(f, m, null, c, a)
                                        } else return n(f, m, l, c, a)
                                    }
                                } else"function" === typeof c && c({
                                    code: 0,
                                    message: "\u041f\u043b\u0430\u0433\u0438\u043d \u043d\u0435 \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d \u0438\u043b\u0438 \u0437\u0430\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u043d",
                                    source: a
                                })
                            }

                            function r(a) {
                                if (CryptoPlugin.hasOwnProperty("API"))for (var b = CryptoPlugin.API.length - 1; 0 <= b; b--) {
                                    var d = CryptoPlugin.API[b];
                                    a.hasOwnProperty(CryptoPlugin.API[b].name) || (a[d.name] = function () {
                                        var b = d, c;
                                        return function () {
                                            var d = {}, g = !1;
                                            if (b.hasOwnProperty("params"))if (arguments.length > b.params.length + 2) "function" === typeof CryptoPlugin.config.debugFunction && CryptoPlugin.config.debugFunction("\u041f\u0435\u0440\u0435\u0434\u0430\u043d\u043e \u0431\u043e\u043b\u044c\u0448\u0435 \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u043e\u0432 \u0447\u0435\u043c \u043d\u0443\u0436\u043d\u043e \u0434\u043b\u044f \u0444\u0443\u043d\u043a\u0446\u0438\u0438 " +
                                                b.name, "warn"); else {
                                                var h = 0;
                                                for (c = 0; c < b.params.length; c++)b.params[c].hasOwnProperty("optional") && b.params[c].optional || h++;
                                                "function" === typeof CryptoPlugin.config.debugFunction && (2 <= arguments.length && "function" === typeof arguments[arguments.length - 2] && h + 2 > arguments.length && CryptoPlugin.config.debugFunction("\u041f\u0435\u0440\u0435\u0434\u0430\u043d\u043e \u043c\u0435\u043d\u044c\u0448\u0435 \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u043e\u0432 \u0447\u0435\u043c \u043d\u0443\u0436\u043d\u043e \u0434\u043b\u044f \u0444\u0443\u043d\u043a\u0446\u0438\u0438 " +
                                                    b.name, "warn"), 1 <= arguments.length && "function" === typeof arguments[arguments.length - 1] && h + 1 > arguments.length && CryptoPlugin.config.debugFunction("\u041f\u0435\u0440\u0435\u0434\u0430\u043d\u043e \u043c\u0435\u043d\u044c\u0448\u0435 \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u043e\u0432 \u0447\u0435\u043c \u043d\u0443\u0436\u043d\u043e \u0434\u043b\u044f \u0444\u0443\u043d\u043a\u0446\u0438\u0438 " + b.name, "warn"))
                                            } else 2 < arguments.length && "function" === typeof CryptoPlugin.config.debugFunction &&
                                            CryptoPlugin.config.debugFunction("\u041f\u0435\u0440\u0435\u0434\u0430\u043d\u043e \u0431\u043e\u043b\u044c\u0448\u0435 \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u043e\u0432 \u0447\u0435\u043c \u043d\u0443\u0436\u043d\u043e \u0434\u043b\u044f \u0444\u0443\u043d\u043a\u0446\u0438\u0438 " + b.name, "warn");
                                            var f = null, m = null;
                                            if (b.hasOwnProperty("params"))for (c = 0; c < arguments.length; c++)if ("function" === typeof arguments[c])if (c === arguments.length - 2 && "function" === typeof arguments[c + 1]) {
                                                f = arguments[c];
                                                m = arguments[c + 1];
                                                break
                                            } else if (c === arguments.length - 1) {
                                                f = arguments[c];
                                                break
                                            } else"function" === typeof CryptoPlugin.config.debugFunction && CryptoPlugin.config.debugFunction("\u0424\u0443\u043d\u043a\u0446\u0438\u0438 \u043e\u0431\u0440\u0430\u0442\u043d\u043e\u0433\u043e \u0432\u044b\u0437\u043e\u0432\u0430 \u043f\u0435\u0440\u0435\u0434\u0430\u043d\u044b \u0432 \u043d\u0435\u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u044b\u0445 \u043f\u043e\u0437\u0438\u0446\u0438\u044f\u0445 " + b.name, "warn"); else {
                                                if (b.params[c].hasOwnProperty("type"))if (h =
                                                        b.params[c].type, "Number" === h) {
                                                    if (arguments[c] = parseInt(arguments[c]), isNaN(arguments[c]))continue
                                                } else"Boolean" === h && "boolean" !== typeof arguments[c] && (arguments[c] = "true" === arguments[c].toLowerCase());
                                                g = !0;
                                                null != arguments[c] && (d[b.params[c].name] = arguments[c]);
                                                "sessionId" === b.params[c].name && "restoreSession" === b.name && (a.sessionId = arguments[c])
                                            }
                                            g || (d = null);
                                            g = null;
                                            if ("updateInfo" === b.name) k("getPluginInfo", null, null, function (b) {
                                                e(a, b);
                                                null != a.sessionId && 0 < a.sessionId.length ? k("getSessionInfo", null,
                                                    a, function (b) {
                                                        e(a, b);
                                                        f()
                                                    }, function (b) {
                                                        2 === b.code ? (e(a), "function" === typeof f && f()) : "function" === typeof m && m()
                                                    }) : "function" === typeof f && f()
                                            }, m); else return g = "openSession" === b.name || "restoreSession" === b.name ? function () {
                                                return function (b) {
                                                    e(a);
                                                    a.sessionId = b.sessionId;
                                                    k("getPluginInfo", null, null, function (b) {
                                                        e(a, b);
                                                        k("getSessionInfo", null, a, function (b) {
                                                            e(a, b);
                                                            f(a.sessionId)
                                                        }, m)
                                                    }, m);
                                                    return b
                                                }
                                            }() : f, k(b.name, d, a, g, m)
                                        }
                                    }())
                                }
                            }

                            function e(a, b) {
                                if ("undefined" !== typeof a && null !== a)if ("undefined" === typeof b) a.functionLevel =
                                    0, a.activeSessionCount = 0, a.sessionId = null, a.sessionExpiryTime = null, a.sessionState = "absent", a.filePath = null, a.deviceId = null, a.storageType = null, a.storageSpec = null, a.storageVersion = null, a.deviceDriverVersion = null, a.deviceModel = null, a.deviceName = null, a.deviceSerialNumber = null, a.deviceState = null, a.deviceHasPuk = null, a.keyType = null, a.keyAlias = null, a.fileModified = null; else for (var d in b)if (b.hasOwnProperty(d) && ("" === b[d] || null == b[d] ? a.hasOwnProperty(d) && (a[d] = null) : a[d] = "sessionExpiryTime" === d ? new Date(parseInt(1E3 *
                                        b[d])) : b[d], "sessionState" === d))switch (b[d]) {
                                    case "":
                                        a.functionLevel = 0;
                                        a.sessionId = null;
                                        a.sessionState = "absent";
                                        break;
                                    case "new":
                                        a.functionLevel = 1;
                                        break;
                                    case "createStorage":
                                    case "selectModifiedFileStorage":
                                        a.functionLevel = 2;
                                        break;
                                    case "selectFileStorage":
                                        a.functionLevel = 3;
                                        break;
                                    case "selectDeviceStorage":
                                        a.functionLevel = 4;
                                        break;
                                    case "selectKey":
                                        a.functionLevel = 5
                                }
                            }

                            return function (a, b) {
                                ("undefined" === typeof CryptoPlugin.plugin || null == CryptoPlugin.plugin || "OCX" != CryptoPlugin.plugin.pluginType && "undefined" === typeof CryptoPlugin.plugin.send) && t();
                                if (null == CryptoPlugin.plugin || "OCX" != CryptoPlugin.plugin.pluginType && "undefined" === typeof CryptoPlugin.plugin.send)return "function" === typeof b && b({
                                    code: 0,
                                    message: "\u041f\u043b\u0430\u0433\u0438\u043d \u043d\u0435 \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d \u0438\u043b\u0438 \u0437\u0430\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u043d",
                                    source: "CryptoPlugin"
                                }), null;
                                var d = {pluginType: CryptoPlugin.plugin.pluginType, version: ""};
                                e(d);
                                window.setTimeout(function () {
                                    if ("NM" ===
                                        CryptoPlugin.plugin.pluginType) {
                                        var l = function () {
                                            return function () {
                                                var c = {pluginType: CryptoPlugin.plugin.pluginType, version: ""};
                                                e(c);
                                                CryptoPlugin.plugin.callback = function () {
                                                    return function (d) {
                                                        "undefined" !== typeof d.answer && "undefined" !== typeof d.answer.api ? (CryptoPlugin.API = JSON.parse(d.answer.api), r(c), k("getPluginInfo", null, null, function (b) {
                                                            e(c, b);
                                                            k("getVersion", null, null, function (b) {
                                                                c.version = b.version;
                                                                "function" === typeof a && a(c)
                                                            }, null)
                                                        }, null)) : "function" === typeof b && b({
                                                            code: 0,
                                                            message: "\u041f\u043b\u0430\u0433\u0438\u043d \u043d\u0435 \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d \u0438\u043b\u0438 \u0437\u0430\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u043d",
                                                            source: "connect"
                                                        })
                                                    }
                                                }();
                                                CryptoPlugin.plugin.send('{"function":"getAPI"}')
                                            }
                                        }(), c = 0, n = CryptoPlugin.config.chromeExtensionId.length - 2;
                                        -1 !== navigator.userAgent.toLowerCase().indexOf("opr/") && (c = CryptoPlugin.config.chromeExtensionId.length - 1, n = CryptoPlugin.config.chromeExtensionId.length - 1);
                                        var g = function (a) {
                                            CryptoPlugin.config.chromeExtensionIndex = a;
                                            var c = new XMLHttpRequest;
                                            c.open("GET", "chrome-extension://" + CryptoPlugin.config.chromeExtensionId[CryptoPlugin.config.chromeExtensionIndex] + "/manifest.json",
                                                !0);
                                            c.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                                            c.timeout = 100;
                                            c.onreadystatechange = function () {
                                                4 === c.readyState && (200 === c.status ? (l(), window.setTimeout(function () {
                                                    "function" === typeof b && null == d.version && (b({
                                                        code: 0,
                                                        message: "\u041f\u043b\u0430\u0433\u0438\u043d \u043d\u0435 \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d \u0438\u043b\u0438 \u0437\u0430\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u043d",
                                                        source: "connect"
                                                    }), b = null)
                                                }, 200)) : a == n ? window.setTimeout(b({
                                                    code: -1,
                                                    message: "\u0420\u0430\u0441\u0448\u0438\u0440\u0435\u043d\u0438\u0435 \u0434\u043b\u044f " + (-1 == navigator.userAgent.toLowerCase().indexOf("opr/")) ? "Chrome" : "Opera \u043d\u0435 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u043e",
                                                    source: "connect"
                                                }), 0) : g(a + 1))
                                            };
                                            c.send()
                                        };
                                        g(c)
                                    } else {
                                        try {
                                            c = CryptoPlugin.plugin.send('{"function":"getAPI"}'), CryptoPlugin.API = JSON.parse(JSON.parse(c).answer.api), r(d)
                                        } catch (h) {
                                            if ("function" === typeof b) {
                                                b({
                                                    code: 0,
                                                    message: "\u041f\u043b\u0430\u0433\u0438\u043d \u043d\u0435 \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d \u0438\u043b\u0438 \u0437\u0430\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u043d",
                                                    source: "connect"
                                                });
                                                return
                                            }
                                        }
                                        k("getPluginInfo", null, null, function (b) {
                                            e(d, b);
                                            k("getVersion", null, null, function (b) {
                                                d.version = b.version;
                                                "function" === typeof a && a(d)
                                            }, null)
                                        }, null)
                                    }
                                }, "OCX" == CryptoPlugin.plugin.pluginType ? 299 : 10)
                            }
                        }()
                    });
            }
        }
    });