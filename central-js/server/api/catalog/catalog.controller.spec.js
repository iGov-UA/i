'use strict';

var should = require('should')
  , appTest = require('../../app.spec')
  , testRequest = appTest.testRequest;

require('./catalog.service.nock');

describe('GET /api/catalog', function () {
  it('if bShowEmptyFolders=false it should respond with 200 and catalog services', function (done) {
    var catalog = testRequest.get('/api/catalog?bShowEmptyFolders=false');
    catalog.expect(200).then(function (res) {
      res.should.have.property('body');
      res.body.should.be.an.Array;
      res.body.should.have.length(1);
      res.body[0].should.have.keys('nID', 'sName', 'sID', 'nOrder', 'aSubcategory');
      done();
    }).catch(function (err) {
      done(err)
    });
  });
});
