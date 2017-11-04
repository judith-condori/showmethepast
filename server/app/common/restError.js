function RestError(message, httpStatus) {
  this.name = 'RestError';
  this.message = message || 'Default Message';
  this.httpStatus = httpStatus;
  this.stack = (new Error()).stack;
}

RestError.prototype = Object.create(Error.prototype);
RestError.prototype.constructor = RestError;

module.exports = RestError;