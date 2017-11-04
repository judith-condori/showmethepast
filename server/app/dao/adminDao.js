const UIDGenerator = require('uid-generator');
const moment = require('moment');

require('./mongoConnection.js');
const Users = require('../collections/userInformationCollection.js');
const RestError = require('../common/restError.js');
const MongoClient = require('mongodb').MongoClient;
const serverConfiguration = require('../helpers/configuration.js');
const HttpStatus = require('http-status-codes');

class AdminDao {
    searchUsers(hint, callback) {
        var targetDocument,
            self = this;

        Users.find({'email': {$regex: hint}}, callback);
    }

    editUserRole(targetUser, callback) {
        var targetDocument,
            self = this;

        Users.findOne({'_id': targetUser._id}, function(error, document) {
            if (!document) {
                callback(new Error('Target user name doesnt exists.'));
            } else {
                document.role = targetUser.role;
                document.save(callback);
            }
        });
    }    
}

module.exports = new AdminDao();