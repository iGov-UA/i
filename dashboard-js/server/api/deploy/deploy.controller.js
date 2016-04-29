/**
 * Created by GFalcon-UA on 28.04.2016.
 */
'use strict';

var activiti = require('../../components/activiti');

exports.setBP = function(req, res){
//todo добавление файла БП
};

exports.getBP = function(req, res){
//todo загрузка файла БП
};

exports.getListBP = function(req, res){
  var options = {
    path: 'action/task/getListBP',
    query: req.query,
    json: true
  };

  activiti.get(options, function (error, statusCode, body) {
    if (error) {
      error = errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR, 'Error while loading task data', error);
      res.status(500).send(error);
      return;
    }

    res.status(200).send(body);
  });
};

exports.removeListBP = function(req, res){
//todo удаление списка БП
};
