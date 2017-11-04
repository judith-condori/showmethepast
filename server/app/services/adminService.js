const AdminDao = require('../dao/adminDao.js');
const BlueBird = require('bluebird');
const RestError = require('../common/restError.js');

class AdminServices {
    searchUsers(hint, callback) {
        AdminDao.searchUsers(hint, function(error, daoUserInformation) {
            callback(error, daoUserInformation);
        });
    }

    editUserRole(targetUser, callback) {
        AdminDao.editUserRole(targetUser, function(error) {
            callback(error);
        });
    }    
}

module.exports = new AdminServices();