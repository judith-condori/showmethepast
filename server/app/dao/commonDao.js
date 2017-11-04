const _ = require('underscore');
const moment = require('moment');
const HttpStatus = require('http-status-codes');

require('./mongoConnection.js');
const Images = require('../collections/imageDataCollection.js');
const OldPictures = require('../collections/oldPictureCollection.js');
const TouristicPlaces = require('../collections/touristicPlaceCollection.js');
const DeletionsCache = require('../collections/deletionsCacheCollection.js');

const DeletionsCacheDao = require('./deletionsCacheDao.js');
const serverConfiguration = require('../helpers/configuration.js');
const RestError = require('../common/restError.js');

var collectionsGroup = {
    images: Images,
    oldPictures: OldPictures,
    touristicPlaces: TouristicPlaces
}

class CommonDao {

    get(user, collectionName, documentId, callback) {
        var targetCollection;

        if(!_.has(collectionsGroup, collectionName)) {
            callback(new RestError("Invalid collection name : " + (collectionName || '', HttpStatus.BAD_REQUEST)));
        } else {
            targetCollection = collectionsGroup[collectionName];
            targetCollection.findOne({_id: documentId})
                .then(function(result) {
                    callback(undefined, result);
                })
                .catch(callback);
        }
    }

    create(user, collectionName, document, callback) {
        var smtpToken,
            targetCollection,
            currentDate = new Date();

        if(!_.has(collectionsGroup, collectionName)) {
            callback(new RestError("Invalid collection name : " + (collectionName || '', HttpStatus.BAD_REQUEST)));
        } else {
            targetCollection = collectionsGroup[collectionName];
            document.owner = user._id;
            targetCollection.create(document, function(error, result) {
                if (error) {
                    callback(error, undefined);
                } else {
                    callback(undefined, result);
                }
            });
        }
    }

    update(user, collectionName, documentId, document, callback) {
        var smtpToken,
            targetCollection,
            currentDate = new Date();

        if(!_.has(collectionsGroup, collectionName)) {
            callback(new RestError("Invalid collection name : " + (collectionName || '', HttpStatus.BAD_REQUEST)));
        } else {
            targetCollection = collectionsGroup[collectionName];
            document.updatedAt = currentDate;

            targetCollection.findById(documentId)
                .then(function(existingDocument) {
                    if (!existingDocument) {
                        throw new RestError(`The document [${documentId}] doesn't exists.`, HttpStatus.NOT_FOUND);
                    }
                    existingDocument = _.extend(existingDocument, document);
                    return existingDocument.save();
                })
                .then(function(updatedDocument) {
                    callback(undefined, updatedDocument);
                })
                .catch(callback);
        }
    }

    filterByFieldValue(user, collectionName, filterKey, filterValue, callback) {
        var smtpToken,
            targetCollection,
            currentDate = new Date(),
            query = {};

        if(!_.has(collectionsGroup, collectionName)) {
            callback(new Error("Invalid collection name : " + (collectionName || '')), undefined);
        } else {
            query[filterKey] = filterValue;

            targetCollection = collectionsGroup[collectionName];
            targetCollection
            
            targetCollection.find(query, {_id: 1}, function(error, listOfResults) {
                if (error) {
                    callback(error, undefined);
                } else {
                    callback(undefined, listOfResults);
                }
            });
        }
    }

    delete(user, collectionName, documentId, callback) {
        var smtpToken,
            targetCollection,
            currentDate = new Date();

        if(!_.has(collectionsGroup, collectionName)) {
            callback(new Error("Invalid collection name : " + (collectionName || '')), undefined);
        } else {
            targetCollection = collectionsGroup[collectionName];
            targetCollection.remove({_id: documentId}, function(error, result) {
                if (error) {
                    callback(error, undefined);
                } else {
                    DeletionsCacheDao.registerDeletion(user, collectionName, documentId, function(error) {
                        if (error) {
                            callback(error);
                        } else {
                            callback(undefined, result);
                        }
                    });
                }
            });
        }
    }

    getCreations(user, collectionName, filterInformation, callback) {
        var targetCollection,
            currentDate = new Date();

        if(!_.has(collectionsGroup, collectionName)) {
            callback(new Error("Invalid collection name : " + (collectionName || '')), undefined);
        } else {
            targetCollection = collectionsGroup[collectionName];
            targetCollection.find({ createdAt: { $gte: filterInformation.startDate} }, {_id: 1, createdAt: 1, updatedAt: 1}, function(error, listOfCreations) {
                if (error) {
                    callback(error, undefined);
                } else {
                    callback(undefined, listOfCreations);
                }
            });
        }
    }

    getEditions(user, collectionName, filterInformation, callback) {
        var targetCollection,
            currentDate = new Date();

        if(!_.has(collectionsGroup, collectionName)) {
            callback(new Error("Invalid collection name : " + (collectionName || '')), undefined);
        } else {
            targetCollection = collectionsGroup[collectionName];
            targetCollection.find({ updatedAt: { $gte: filterInformation.startDate} }, {_id: 1, createdAt: 1, updatedAt: 1}, function(error, listOfEditions) {
                if (error) {
                    callback(error, undefined);
                } else {
                    callback(undefined, listOfEditions);
                }
            });
        }
    }

    getDeletions(user, collectionName, filterInformation, callback) {
        var targetCollection,
            currentDate = new Date();

        if(!_.has(collectionsGroup, collectionName)) {
            callback(new Error("Invalid collection name : " + (collectionName || '')), undefined);
        } else {
            console.log('*** Query for deletions...');
            console.log(filterInformation.startDate);
            console.log(collectionName);
            console.log('**************************')

            DeletionsCache.find({ createdAt: { $gte: filterInformation.startDate}, collectionName: collectionName }, {documentId: 1, _id: 0}, function(error, listOfDeletions) {
                if (error) {
                    callback(error, undefined);
                } else {
                    console.log('Deletions count: ' + listOfDeletions.length);
                    callback(undefined, listOfDeletions);
                }
            });
        }
    }
}

module.exports = new CommonDao();