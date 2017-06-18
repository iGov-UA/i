var url = require('url')
  , request = require('request')
  , fs = require('fs')
  , config = require('../../config/environment')
  , _ = require('lodash')
  , async = require('async')
  , activiti = require('../../components/activiti')
  , errors = require('../../components/errors')
  , loggerFactory = require('../../components/logger')
  , logger = loggerFactory.createLogger(module);

module.exports.submit = function (req, res) {
  var formData = req.body;
  var nID_Subject = 20049;
  var sHost = "https://alpha.test.region.igov.org.ua/wf";
  var keys = [];
  var properties = [];

  function formSubmit() {
    for (var id in formData.params) {
      if (formData.params.hasOwnProperty(id)) {
        var value = formData.params[id];
        if (id === 'nID_Subject') {
          value = nID_Subject;
        }
        if (id === 'sID_UA' && formData.sID_UA_Common !== null) {
          value = formData.sID_UA_Common;
        } else if (id === 'sID_UA') {
          value = formData.sID_UA;
        }

        properties.push({
          id: id,
          value: value
        });
      }
    }

    var callback = function (error, response, body) {
      res.send(body);
      res.end();
    };

    var qs = {
      sID_BP: "_FAQ",
      nID_Subject: 20049,
      nID_Service: 22,
      nID_ServiceData: 3339,
      sID_UA: "",
      sLogin: "tester",
    };

    var body = { "aFormProperty": properties};

    activiti.post('/service/action/task/startProcess', qs, body, callback, sHost);

  }

  for (var key in formData.params) {
    if (formData.params[key] !== null && typeof formData.params[key] === 'object') {
      keys.push(key);
    }
  }

  if (keys.length > 0) {
    async.forEach(keys, function (key, next) {
        function putTableToRedis (table, callback) {
          var url,
            params = {},
            nameAndExt = table.id + '.json',
            checkForNewService = table.name.split(';');

          // now we have two services for saving table, so we checking what service is needed;
          if(checkForNewService.length === 3 && checkForNewService[2].indexOf('bNew=true') > -1) {
            url = '/object/file/setProcessAttach';
            params = {
              sID_StorageType:'Redis',
              sID_Field:table.id,
              sFileNameAndExt:nameAndExt
            };
          } else {
            url = '/object/file/upload_file_to_redis';
          }

          activiti.upload(url, params, nameAndExt, JSON.stringify(table), callback);
        }

        putTableToRedis(formData.params[key], function (error, response, data) {
          formData.params[key] = data;
          next();
        });
      },
      function (err) {
        formSubmit();
      });
  } else {
    formSubmit();
  }
};
