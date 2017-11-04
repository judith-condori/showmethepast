const UserDao = require('../dao/userDao.js');
const BlueBird = require('bluebird');

class UserServices {
    loginUser(loginData, callback) {
        var userInformation = {
            userId: loginData.sub,
            email: loginData.email,
            name: loginData.name,
            picture: loginData.picture
        };

        UserDao.loginUser(userInformation, function(error, daoUserInformation) {
            callback(error, daoUserInformation);
        });
    }

    logout(tokenId, callback) {
        var targetMethod = BlueBird.promisify(UserDao.expireToken);

        targetMethod(tokenId)
            .then(function(){
                callback();
            })
            .catch(function(error) {
                callback(error);
            });
    }

    getTokenInformation(smtpTokenId, callback) {
        UserDao.getTokenInformation(smtpTokenId, function(error, result) {
            callback(error, result);
        });
    }
}

module.exports = new UserServices();