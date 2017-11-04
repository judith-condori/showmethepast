const _ = require('underscore');
const CommonDao = require('../dao/commonDao.js');
const BlueBird = require('bluebird');
const moment = require('moment');
const SyncTools = require('../helpers/syncTools.js');

class ImageService {
    delete(user, documentId, callback) {
        var targetCollection = 'images';
        CommonDao.delete(user, targetCollection, documentId, callback);
    }
}

module.exports = new ImageService();