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
    query: {
      sID_BP: req.query.sID_BP,
      sFieldType: req.query.sFieldType,
      sID_Field: req.query.sID_Field
    }
  };

  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      res.status(statusCode).json(result);
    }
  });
};

exports.removeListBP = function(req, res){
//todo удаление списка БП
};
