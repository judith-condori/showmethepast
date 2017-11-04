const _ = require('underscore');
const check = require('check-types');
const HttpStatus = require('http-status-codes');
const GoogleAuth = require('google-auth-library');
const BlueBird = require('bluebird');
const SmtpRoles = require('../domain/roles.js');

// Custom js files
const serverConfiguration = require('../helpers/configuration.js');
const BaseController = require('./baseController.js');
const CommonService = require('../services/commonService.js');
const RestError = require('../common/restError.js');

module.exports = function(app) {
    var prefix = '/api/collections';
    var allowedCollections = ['images', 'oldPictures', 'touristicPlaces'];

    // get
    app.get(`${prefix}/:collectionName/:documentId`, function(req, res) {
        BaseController.processJsonRequestAuth(req, res, function(user, req, res, errorCallback) {
            var collectionName = req.params.collectionName,
                documentId = req.params.documentId;
            
            check.assert.string(collectionName);
            check.assert.string(documentId);

            if (!_.isString(collectionName) || !_.contains(allowedCollections, collectionName)) {
                throw new RestError('Invalid collection name: ' + (collectionName || '') + '.', HttpStatus.BAD_REQUEST);
            }       
            
            CommonService.get(user, collectionName, documentId, function(error, document) {
                if (error) {
                    errorCallback(error);
                } else {
                    if (document == null) {
                        res.status(HttpStatus.NOT_FOUND).send();
                    } else {
                        res.status(HttpStatus.OK).send(JSON.stringify(document));
                    }
                }
            });
        });
    });

    // Creation
    app.post(`${prefix}/:collectionName`, function(req, res) {
        BaseController.processJsonRequestAuth(req, res, function(user, req, res, errorCallback) {
            var document = req.body,
                collectionName = req.params.collectionName,
                resObject = {},
                reducedResult;

            if (!SmtpRoles.isEditor(user.role)) {
                throw new RestError('You have no creation rights.', HttpStatus.UNAUTHORIZED);
            }                

            if (!_.isString(collectionName) || !_.contains(allowedCollections, collectionName)) {
                throw new RestError('Invalid collection name: ' + (collectionName || '') + '.', HttpStatus.BAD_REQUEST);
            }

            CommonService.create(user, collectionName, document, function(error, creationResult) {
                if (error) {
                    errorCallback(error);
                } else {
                    reducedResult = _.pick(creationResult, '_id', 'createdAt', 'updatedAt');
                    res.status(HttpStatus.CREATED).send(JSON.stringify(reducedResult));
                }
            });
        });
    });

    // Edition
    app.put(`${prefix}/:collectionName/:documentId`, function(req, res) {
        BaseController.processJsonRequestAuth(req, res, function(user, req, res, errorCallback) {
            var document = req.body,
                collectionName = req.params.collectionName,
                documentId = req.params.documentId,
                resObject = {},
                reducedResult;

            if (!SmtpRoles.isEditor(user.role)) {
                throw new RestError('You have no creation rights.', HttpStatus.UNAUTHORIZED);
            }                   

            if (!_.isString(collectionName) || !_.contains(allowedCollections, collectionName)) {
                throw new RestError('Invalid collection name: ' + (collectionName || '') + '.', HttpStatus.BAD_REQUEST);
            }

            if (!_.isObject(document) || _.isEmpty(document)) {
                throw new RestError('Invalid document information.', HttpStatus.BAD_REQUEST);
            }            

            if (!_.isString(documentId)) {
                throw new RestError('Invalid document Id', HttpStatus.BAD_REQUEST);
            }

            CommonService.update(user, collectionName, documentId, document, function(error, updatedDocument) {
                if (error) {
                    console.log(error);
                    errorCallback(error);
                } else {
                    reducedResult = _.pick(updatedDocument, '_id', 'createdAt', 'updatedAt');
                    res.status(HttpStatus.OK).send(JSON.stringify(reducedResult));
                }
            });
        });
    });

    // Get new documents
    app.post(`${prefix}/:collectionName/creations`, function(req, res) {
        BaseController.processJsonRequestAuth(req, res, function(user, req, res, errorCallback) {
            var document = req.body,
                collectionName = req.params.collectionName
                resObject = {};

            check.assert.string(document.startDate);

            if (!_.isString(collectionName) || !_.contains(allowedCollections, collectionName)) {
                errorCallback(new Error('Invalid collection name: ' + (collectionName || '') + '.'));
                return;
            }

            CommonService.getCreations(user, collectionName, document, function(error, creations) {
                if (error) {
                    errorCallback(error);
                } else {
                    res.status(HttpStatus.OK).send(JSON.stringify(creations));
                }
            });
        });
    });

    // Get updated documents
    app.post(`${prefix}/:collectionName/editions`, function(req, res) {
        BaseController.processJsonRequestAuth(req, res, function(user, req, res, errorCallback) {
            var document = req.body,
                collectionName = req.params.collectionName
                resObject = {};

            check.assert.string(document.startDate);

            if (!_.isString(collectionName) || !_.contains(allowedCollections, collectionName)) {
                errorCallback(new Error('Invalid collection name: ' + (collectionName || '') + '.'));
                return;
            }

            CommonService.getEditions(user, collectionName, document, function(error, editionsList) {
                if (error) {
                    errorCallback(error);
                } else {
                    res.status(HttpStatus.OK).send(JSON.stringify(editionsList));
                }
            });
        });
    });

    // Get deleted documents
    app.post(`${prefix}/:collectionName/deletions`, function(req, res) {
        BaseController.processJsonRequestAuth(req, res, function(user, req, res, errorCallback) {
            var document = req.body,
                collectionName = req.params.collectionName
                resObject = {};

            check.assert.string(document.startDate);

            if (!_.isString(collectionName) || !_.contains(allowedCollections, collectionName)) {
                errorCallback(new Error('Invalid collection name: ' + (collectionName || '') + '.'));
                return;
            }

            CommonService.getDeletions(user, collectionName, document, function(error, deletionsList) {
                if (error) {
                    errorCallback(error);
                } else {
                    res.status(HttpStatus.OK).send(JSON.stringify(deletionsList));
                }
            });
        });
    });

    // Get deleted documents
    app.post(`${prefix}/:collectionName/changes`, function(req, res) {
        BaseController.processJsonRequestAuth(req, res, function(user, req, res, errorCallback) {
            var document = req.body,
                collectionName = req.params.collectionName
                resObject = {};

            check.assert.string(document.startDate);

            if (!_.isString(collectionName) || !_.contains(allowedCollections, collectionName)) {
                errorCallback(new Error('Invalid collection name: ' + (collectionName || '') + '.'));
                return;
            }

            CommonService.getChanges(user, collectionName, document, function(error, changesInformation) {
                if (error) {
                    errorCallback(error);
                } else {
                    res.status(HttpStatus.OK).send(JSON.stringify(changesInformation));
                }
            });
        });
    });    
}