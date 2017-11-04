var check = require('check-types');
var HttpStatus = require('http-status-codes');
var Logger = require('logger').createLogger();
var UserService = require('../services/userService.js');


BaseController = {
    dispatchException: function(status, res, message) {
        var resObject = {
            message: message
        };
        Logger.error('Controller Exception:', `[http status: ${status}] ${message}`);
        res.status(status).send(JSON.stringify(resObject));
    },

    processJsonRequest: function(req, res, method) {
        try {
            res.setHeader('Content-Type', 'application/json');
            method(req, res, function(error) {
                BaseController.dispatchException(error.httpStatus || HttpStatus.INTERNAL_SERVER_ERROR, res, error.message);
            });
        } catch (error) {
            BaseController.dispatchException(error.httpStatus || HttpStatus.INTERNAL_SERVER_ERROR, res, error.message);
        }
    },

    processJsonRequestAuth: function(req, res, method) {
        BaseController.processJsonRequest(req, res, function(req, res, callback) {
            var smtpTokenId;
            check.assert.assigned(req.headers);
            smtpTokenId = req.headers['smtp-token'];

            if (!smtpTokenId) {
                BaseController.dispatchException(HttpStatus.BAD_REQUEST, res, 'You need to specify the smtp-token in the headers.');
                return;
            }

            UserService.getTokenInformation(smtpTokenId, function(error, information) {
                if (error) {
                    BaseController.dispatchException(error.httpStatus || HttpStatus.BAD_REQUEST, res, error.message);
                } else {
                    if(!information || !information.smtpToken || !information.smtpToken.alive) {
                        //console.log(information);
                        BaseController.dispatchException(HttpStatus.UNAUTHORIZED, res, 'The token expired.');
                    } else {
                        try {
                            res.setHeader('Content-Type', 'application/json');
                            method(information, req, res, function(error) {
                                BaseController.dispatchException(error.httpStatus || HttpStatus.INTERNAL_SERVER_ERROR, res, error.message);
                            });
                        } catch (error) {
                            BaseController.dispatchException(error.httpStatus || HttpStatus.INTERNAL_SERVER_ERROR, res, error.message);
                        }
                    }
                }
            });
        });
    }
}

module.exports = BaseController;