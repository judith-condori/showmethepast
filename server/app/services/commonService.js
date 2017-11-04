const _ = require('underscore');
const CommonDao = require('../dao/commonDao.js');
const BlueBird = require('bluebird');
const moment = require('moment');
const SyncTools = require('../helpers/syncTools.js');
const RestError = require('../common/restError.js');
const HttpStatus = require('http-status-codes');

class CommonServices {
    get(user, collectionName, documentId, callback) {
        CommonDao.get(user, collectionName, documentId, function(error, creationResult) {
            callback(error, creationResult);
        });
    }

    create(user, collectionName, document, callback) {
        var parsedDocument;

        if (!_.isObject(document)) {
            callback(new RestError('Invalid Document.', HttpStatus.BAD_REQUEST));
        } else {
            parsedDocument = _.omit(document, '_id', 'updatedAt', 'createdAt');

            CommonDao.create(user, collectionName, parsedDocument, function(error, creationResult) {
                callback(error, creationResult);
            });
        }
    }

    update(user, collectionName, documentId, document, callback) {
        var parsedDocument;

        if (!_.isObject(document)) {
            callback(new RestError('Invalid Document.', HttpStatus.BAD_REQUEST));
        } else {
            parsedDocument = _.omit(document, '_id', 'updatedAt', 'createdAt', 'image');

            CommonDao.update(user, collectionName, documentId, parsedDocument, function(error, updatedDocument) {
                callback(error, updatedDocument);
            });
        }        
    }

    getCreations(user, collectionName, filterInformation, callback) {
        var momentObject = moment(filterInformation.startDate);

        if (!momentObject.isValid()) {
            callback(new Error('The date has a invalid format.'), undefined);
        } else {
            filterInformation.startDate = momentObject.toDate();
            CommonDao.getCreations(user, collectionName, filterInformation, function(error, listOfCreations) {
                callback(error, { list: listOfCreations });
            })
        }
    }

    getEditions(user, collectionName, filterInformation, callback) {
        var momentObject = moment(filterInformation.startDate);

        if (!momentObject.isValid()) {
            callback(new Error('The date has a invalid format.'), undefined);
        } else {
            filterInformation.startDate = momentObject.toDate();
            CommonDao.getEditions(user, collectionName, filterInformation, function(error, listOfEditions) {
                callback(error, { list: listOfEditions });
            })
        }
    }

    getDeletions(user, collectionName, filterInformation, callback) {
        var momentObject = moment(filterInformation.startDate);

        if (!momentObject.isValid()) {
            callback(new Error('The date has a invalid format.'), undefined);
        } else {
            filterInformation.startDate = momentObject.toDate();
            CommonDao.getDeletions(user, collectionName, filterInformation, function(error, listOfDeletions) {
                listOfDeletions = _.map(listOfDeletions, function(x) {
                    return {_id: x.documentId}
                });
                callback(error, { list: listOfDeletions });
            })
        }
    }

    getChanges(user, collectionName, filterInformation, callback) {
        console.log(JSON.stringify(filterInformation));
        var momentObject = moment(filterInformation.startDate),
            self = this,
            getCreationsAsync,
            getEditionsAsync,
            getDeletionsAsync,
            groupResults = {};

        if (!momentObject.isValid()) {
            callback(new Error('The date has a invalid format.'), undefined);
        } else {
            filterInformation.startDate = momentObject.toDate();

            getCreationsAsync = BlueBird.promisify(this.getCreations, { context: this});
            getEditionsAsync = BlueBird.promisify(this.getEditions, { context: this});
            getDeletionsAsync = BlueBird.promisify(this.getDeletions, { context: this});

            getCreationsAsync(user, collectionName, filterInformation)
            .then(function(listOfCreations) {
                groupResults.creations = listOfCreations.list;
                return getEditionsAsync(user, collectionName, filterInformation);
            })
            .then(function(listOfEditions) {
                groupResults.editions = listOfEditions.list;
                return getDeletionsAsync(user, collectionName, filterInformation);
            })
            .then(function(listOfDeletions) {
                groupResults.deletions = listOfDeletions.list;
                callback(undefined, groupResults);
            })
            .catch(function(error) {
                callback(error, undefined);
            });
        }
    }    
}

module.exports = new CommonServices();