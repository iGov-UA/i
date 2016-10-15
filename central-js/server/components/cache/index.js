'use strict';
var stream = require('stream');
var NodeCache = require("node-cache");
var util = require('util');

util.inherits(CachedStream, stream.PassThrough);

var indexCache = new NodeCache();
var objectCache = new NodeCache();

function fetchObjects (key) {
  return this[key];
}

function CachedStream (trackBy, getResourceId) {
  if (!(this instanceof CachedStream)) {
    return new CachedStream('' + trackBy, (getResourceId instanceof Function) ?
      getResourceId : function () {return getResourceId;});
  }

  stream.PassThrough.call(this);
  this._readableState.objectMode = true;
  this._writableState.objectMode = true;

  var self = this;

  var newIndex = [];

  self.on('data', function (item) {
    objectCache.set(item[trackBy], item);
    newIndex.push(item[trackBy]);
  });

  self.on('end', function () {
    indexCache.set(getResourceId(), newIndex);
  });


  self.run = function () {
    indexCache.get(getResourceId(), function (err, cachedIDs) {
      if (err) {
        self.emit('error', err);
      } else if (cachedIDs) {
        objectCache.mget(cachedIDs, function (err, objects) {
          err ?
            self.emit('error', err) :
            self.emit('hit', cachedIDs.map(fetchObjects, objects), cachedIDs);
        });
      } else {
        newIndex = [];
        self.emit('fault');
      }
    });
  };
}

module.exports = CachedStream;
