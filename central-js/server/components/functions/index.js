module.exports.mapObject = function (mapper) {
  return function (object) {
    return mapper(object);
  };
};
