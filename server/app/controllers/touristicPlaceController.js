const _ = require('underscore');
const check = require('check-types');
const HttpStatus = require('http-status-codes');
const GoogleAuth = require('google-auth-library');
const BlueBird = require('bluebird');

// Custom js files
const ServerConfiguration = require('../helpers/configuration.js');
const BaseController = require('./baseController.js');
const TouristicPlaceService = require('../services/touristicPlaceService.js');
const RestError = require('../common/restError.js');

module.exports = function(app) {
    var prefix = '/api/collections';

    // Creation
    app.delete(`${prefix}/touristicPlaces/:documentId`, function(req, res) {
        BaseController.processJsonRequestAuth(req, res, function(user, req, res, errorCallback) {
            var documentId = req.params.documentId,
                resObject = {},
                reducedResult;
                
            console.log('Delete touristic place!!!');

            if (!SmtpRoles.isAdmin(user.role)) {
                throw new RestError('Error, you need to be an editor or administrator to delete a record.', HttpStatus.UNAUTHORIZED);
            }

            TouristicPlaceService.delete(user, documentId, function(error) {
                if (error) {
                    errorCallback(error);
                } else {
                    res.status(HttpStatus.OK).send();
                }
            });
        });
    });
}