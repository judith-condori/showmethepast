const check = require('check-types');
const HttpStatus = require('http-status-codes');
const GoogleAuth = require('google-auth-library');
const BlueBird = require('bluebird');
const _ = require('underscore');

// Custom js files
const serverConfiguration = require('../helpers/configuration.js');
const BaseController = require('./baseController.js');
const UserService = require('../services/userService.js');
const AdminService = require('../services/adminService.js');
const SmtpRoles = require('../domain/roles.js');
const RestError = require('../common/restError.js');

SecurityHelper = {
    validateGoogleToken: function(token, callback) {
        var auth = new GoogleAuth,
            clientId = serverConfiguration.GOOGLE_LOGIN_KEY,
            client = new auth.OAuth2(clientId, '', '');

        client.verifyIdToken(token, clientId, callback);
    }
}

module.exports = function(app) {
    var prefix = '/api/admin';

    app.post(`${prefix}/users/search`, function(req, res) {
        BaseController.processJsonRequestAuth(req, res, function(user, req, res, errorCallback) {
            var data = req.body,
                reducedResult;

            check.assert.string(data.hint);

            if (!SmtpRoles.isAdmin(user.role)) {
                throw new RestError('You must have admin rights.', HttpStatus.UNAUTHORIZED);
            }

            AdminService.searchUsers(data.hint, function(error, usersList) {
                if (error) {
                    errorCallback(error);
                } else {
                    reducedResult = _.map(usersList, (value)=> { return _.pick(value, '_id', 'email', 'role'); });
                    res.status(HttpStatus.OK).send(JSON.stringify({ result: reducedResult }));
                }
            });
        });
    });

    app.put(`${prefix}/users/changerole/`, function(req, res) {
        BaseController.processJsonRequestAuth(req, res, function(user, req, res, errorCallback) {
            var targetUser = req.body,
                reducedResult;

            console.log(targetUser);

            check.assert.object(targetUser);
            check.assert.string(targetUser.role);
            check.assert.string(targetUser._id);

            targetUser = _.pick(targetUser, '_id', 'role');

            if (!SmtpRoles.isAdmin(user.role)) {
                throw new RestError('You must have admin rights.', HttpStatus.UNAUTHORIZED);
            }

            if (user._id.toString() === targetUser._id.toString()) {
                throw new RestError('You cannot change your own rights.', HttpStatus.UNAUTHORIZED);
            }            

            AdminService.editUserRole(targetUser, function(error, usersList) {
                if (error) {
                    errorCallback(error);
                } else {
                    res.status(HttpStatus.OK).send(JSON.stringify({}));
                }
            });
        });
    });    
}