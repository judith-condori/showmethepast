const _ = require('underscore');
const BlueBird = require('bluebird');
const CommonDao = BlueBird.promisifyAll(require('../dao/commonDao.js'));
const moment = require('moment');
const SyncTools = require('../helpers/syncTools.js');
const OldPictureService = BlueBird.promisifyAll(require('./oldPictureService.js'));
const RestError = require('../common/restError.js');
const HttpStatus = require('http-status-codes');

class TouristicPlaceService {
    delete(user, documentId, callback) {
        var touristicPlaceCollectionName = 'touristicPlaces',
            oldPicturesCollectionName = 'oldPictures',
            imagesCollectionName = 'images',
            targetField = 'touristicPlace',
            promisses,
            touristicPlaceObject;

        CommonDao.getAsync(user, touristicPlaceCollectionName, documentId)
            .then(function(touristicPlace) {
                if (!_.isObject(touristicPlace)) {
                    throw new RestError('The touristic place doesn\'t exists.', HttpStatus.NOT_FOUND);
                }
                touristicPlaceObject = touristicPlace;
                return CommonDao.filterByFieldValueAsync(user, oldPicturesCollectionName, targetField, documentId);
            })
            .then(function(listOfChilds) {
                // Delete childs, that means old pictures
                if (_.isArray(listOfChilds) && listOfChilds.length > 0 ) {

                    promisses = _.map(listOfChilds, function(value) {
                        return OldPictureService.deleteAsync(user, value._id);
                    });

                    return BlueBird.all(promisses);
                }
            })
            .then(function() {
                // Delete related image
                return CommonDao.deleteAsync(user, imagesCollectionName, touristicPlaceObject.image);
            })
            .then(function() {
                // Finally delete current touristic place
                return CommonDao.deleteAsync(user, touristicPlaceCollectionName, documentId);
            })
            .then(function() {
                callback();
            })
            .catch(function(error) {
                callback(error);
            });
    }
}

module.exports = new TouristicPlaceService();