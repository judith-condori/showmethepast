const _ = require('underscore');
const Bluebird = require('bluebird');
const CommonDao = Bluebird.promisifyAll(require('../dao/commonDao.js'));

const moment = require('moment');
const SyncTools = require('../helpers/syncTools.js');
//const ImageService = require('../dao/imageService.js');
const RestError = require('../common/restError.js');
const HttpStatus = require('http-status-codes');

class OldPictureService {
    delete(user, documentId, callback) {
        var oldPicturesCollectionName = 'oldPictures',
            imageCollectionName = 'images';

        CommonDao.getAsync(user, oldPicturesCollectionName, documentId)
            .then(function(oldPicture) {
                if (!_.isObject(oldPicture)) {
                    throw new RestError('The document doesn\'t exists.', HttpStatus.NOT_FOUND);
                }
                return CommonDao.deleteAsync(user, imageCollectionName, oldPicture.image);
            })
            .then(function() {
                return CommonDao.deleteAsync(user, oldPicturesCollectionName, documentId);
            })
            .then(function() {
                callback();
            })
            .catch(callback);
    }
}

module.exports = new OldPictureService();