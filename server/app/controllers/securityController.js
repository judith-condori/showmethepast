const check = require('check-types');
const HttpStatus = require('http-status-codes');
const GoogleAuth = require('google-auth-library');
const BlueBird = require('bluebird');
const _ = require('underscore');

// Custom js files
const serverConfiguration = require('../helpers/configuration.js');
const BaseController = require('./baseController.js');
const UserService = require('../services/userService.js');

SecurityHelper = {
    validateGoogleToken: function(token, callback) {
        var auth = new GoogleAuth,
            clientId = serverConfiguration.GOOGLE_LOGIN_KEY,
            client = new auth.OAuth2(clientId, '', '');

        client.verifyIdToken(token, clientId, callback);
    }
}

module.exports = function(app) {
    var prefix = '/api/security';

    app.post(`${prefix}/logout`, function(req, res) {
        BaseController.processJsonRequest(req, res, function(req, res, errorCallback) {
            var data = req.headers,
                resObject = {},
                tokenId = data["smtp-token"];

            console.log(`New logout!!!`);

            UserService.logout(tokenId, function(error) {
                if (error) {
                    errorCallback(error);
                } else {
                    res.status(HttpStatus.OK).send();
                }
            });
        });
    });

    app.post(`${prefix}/login`, function(req, res) {        
        BaseController.processJsonRequest(req, res, function(req, res, errorCallback) {
            var data = req.body,
                result = {};
            
            // Validation
            check.assert.string(data.token);

            console.log(`New login!!!`);

            SecurityHelper.validateGoogleToken(data.token, function(error, login) {
                var resObject = {};

                if (error) {
                    // The token is invalid
                    resObject.message = "Error, Invalid token " + error.message;
                    res.status(HttpStatus.UNAUTHORIZED).send(JSON.stringify(resObject));
                } else {
                    // Validation succeded, validate fields
                    var payload = login.getPayload();
                    /*
                    resObject.userInformation = {
                        name: payload.given_name + ' ' + payload.family_name,
                        email: payload.email,
                        picture: payload.picture
                    };
                    */
                    UserService.loginUser(payload, function(error, servicesUserInformation) {
                        if (error) {
                            errorCallback(error);
                        } else {
                            resObject = {
                                userInformation: _.pick(servicesUserInformation, '_id', 'userId', 'email', 'name', 'picture', 'smtpToken', 'role', 'createdAt', 'updatedAt')
                            };
                            res.status(HttpStatus.OK).send(JSON.stringify(resObject));
                        }
                    });
                }
            });
        });
    });

    app.get(`${prefix}/status`, function(req, res) {
        BaseController.processJsonRequest(req, res, function(req, res, errorCallback) {
            var data = req.headers,
                resObject = {},
                tokenId = data["smtp-token"];

            console.log(`New status!!!`);

            // Validation
            check.assert.string(tokenId);

            UserService.getTokenInformation(tokenId, function(error, userInformation) {
                if (error) {
                    errorCallback(error);
                } else {
                    resObject = {
                        userInformation: _.pick(userInformation, '_id', 'userId', 'email', 'name', 'picture', 'smtpToken', 'role', 'createdAt', 'updatedAt')
                    };
                    res.status(HttpStatus.OK).send(JSON.stringify(resObject));
                }
            });
        });
    });
}