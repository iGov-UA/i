var Duplex = require('stream').Duplex;
var NodeCache = require("node-cache");
var JSONStream = require('JSONStream');
var zlib = require('zlib');
var util = require('util');

util.inherits(CachedStream, Duplex);

var indexCache = new NodeCache();
var objectCache = new NodeCache();

function CachedStream (getID, onHit, onFault, onFinally) {
  if (!(this instanceof CachedStream)) {
    return new CachedStream(getID, onHit, onFault, onFinally);
  }

  Duplex.call(this);

  var self = this;

  this.objectsStream = JSONStream.parse('*', this._get.bind(this));

  try{
    //console.log(k);
    var cachedIDs = this.indexCache.get(getID());
    if (cachedIDs) {
      onHit(this);
      objectCache.mget(cachedIDs, function (err, objects) {
        console.log('%', objects);
        this.push(objects);
      });
    } else {
      onFault(this);
      this.objectsStream.on('data', function (item) {
        objectCache.set(item.nID, item.toString())
      });
    }
  } catch(err){
    this._read = function (len) {
      this.push(err.stack);
    }
  } finally {
    onFinally(this);
    this.push(null);
  }

}

CachedStream.prototype._get = function (item) {
  return objectCache.get(item.nID) ? null : item;
};

  var a = 0;
CachedStream.prototype._read = function (len) {

  if (a < 10) {
    a++;
    this.push('1');
  } else {
    this.push(null);
  }
};

CachedStream.prototype._write = function (chunk, enc, callback) {
  //console.log('write', chunk.toString().length);
  this.objectsStream.write(chunk);
  callback();
};

module.exports = CachedStream;
