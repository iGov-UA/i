'use strict';

var should = require('should')
  , appTest = require('../../app.spec')
  , region = require('./index');

require('../../api/subject/subject.service.nock');

describe('get sHost', function () {
  it('should return status 200 and region object in query of req', function (done) {
    var nID_Server = 1;
    var req = {query : { nID_Server : nID_Server}};

    region._searchForHost(req, {
      status: function (statusResult) {
        return req;
      }, json: function (jsonResult) {

      }
    }, function () {
      req.should.have.property('region');
      req.region.should.have.property('sHost');
      req.region.should.have.property('nID_Server');
      req.region.should.have.property('isCacheUsed');
      req.region.isCacheUsed.should.be.exactly(false);
      done();
    });
  });

  it('should return status 200 and region object in body of req', function (done) {
    var nID_Server = 2;
    var req = {body : { nID_Server : nID_Server}, query: {}};

    region._searchForHost(req, {
      status: function (statusResult) {
        return req;
      }, json: function (jsonResult) {

      }
    }, function () {
      req.should.have.property('region');
      req.region.should.have.property('sHost');
      req.region.should.have.property('nID_Server');
      req.region.should.have.property('isCacheUsed');
      req.region.isCacheUsed.should.be.exactly(false);
      done();
    });
  });

  it('should use cache', function (done) {
    var nID_Server = 3;
    var req = {query : { nID_Server : nID_Server}};

    region._searchForHost(req, {
      status: function (statusResult) {
        return req;
      }, json: function (jsonResult) {

      }
    }, function () {
      req.should.have.property('region');
      req.region.should.have.property('sHost');
      req.region.should.have.property('nID_Server');
      req.region.should.have.property('isCacheUsed');
      req.region.isCacheUsed.should.be.exactly(false);

      req = {query : { nID_Server : nID_Server}};
      region._searchForHost(req, {
        status: function (statusResult) {
          return req;
        }, json: function (jsonResult) {

        }
      }, function () {
        req.should.have.property('region');
        req.region.should.have.property('sHost');
        req.region.should.have.property('nID_Server');
        req.region.should.have.property('isCacheUsed');
        req.region.isCacheUsed.should.be.exactly(true);
        done();
      });
    });
  });
});

