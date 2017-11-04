const UIDGenerator = require('uid-generator');
const moment = require('moment');

require('./mongoConnection.js');
const Users = require('../collections/userInformationCollection.js');
const RestError = require('../common/restError.js');
const MongoClient = require('mongodb').MongoClient;
const serverConfiguration = require('../helpers/configuration.js');
const HttpStatus = require('http-status-codes');

UserDaoHelper = {
    generateToken: function() {
        var generator = new UIDGenerator(256, UIDGenerator.BASE62),
            smtpToken = {
                token: generator.generateSync(),
                expirationDate: moment().utc().toDate(),
                alive: true
            };

        UserDaoHelper.expandToken(smtpToken);
        return smtpToken;
    },
    expandToken: function(targetToken) {
        var hoursToExpand = 24;
        //targetToken.expirationDate = moment(targetToken.expirationDate).add(hoursToExpand, 'hours').toDate();
        targetToken.expirationDate = moment().utc().add(hoursToExpand, 'hours').toDate();
        targetToken.alive = true;
        console.log('\tExpand operation : ' + targetToken.expirationDate + ' from ' + moment().utc().toDate());        
    },
    expireToken: function(targetToken) {
        // The token will expire if we set the value in the current date
        targetToken.expirationDate = moment().utc().toDate();
        targetToken.alive = false;
        console.log('Expiring token!!!!');
    }
}

class UserDao {
    updateToken(document) {
        var smtpToken = document.smtpToken,
            currentDate = moment().utc().toDate();

        if (smtpToken.alive && smtpToken.expirationDate < currentDate) {
            console.log('Expiring token from update!!!! ' + smtpToken.expirationDate + ' ' + currentDate);
            console.log(smtpToken.expirationDate);
            console.log(currentDate);
            
            UserDaoHelper.expireToken(smtpToken);
            return true;
        }

        return false;
    }

    loginUser(userInformation, callback) {
        var smtpToken,
            targetUserInformation,
            currentDate = moment().utc().toDate();
        
        Users.findOne({userId: userInformation.userId})
        .then(function(document){
            if (document) {
                smtpToken = document.smtpToken;
                if (!smtpToken || !smtpToken.token || !smtpToken.expirationDate || smtpToken.expirationDate >= currentDate) {
                    // Need regenerate the token
                    console.log('\tRegenerating Token');
                    document.smtpToken = UserDaoHelper.generateToken();
                } else {
                    // Extend the token duration                    
                    UserDaoHelper.expandToken(document.smtpToken);
                    console.log('\tExpanding Token ' + document.smtpToken.expirationDate);
                }
                targetUserInformation = document;
                return Users.update({_id: document._id}, document);
            } else {
                // Insert a new user
                var targetUser = userInformation;
                targetUser.smtpToken = UserDaoHelper.generateToken();
                targetUser.role = 'client';
                smtpToken = targetUser.smtpToken;
                targetUserInformation = targetUser;
                return Users.create(targetUser);
            }
        })
        .then(function() {
            callback(null, targetUserInformation);
        })
        .catch(function(error){
            console.log('Error!!! : ' + error.message);
            callback(error, null);
        });
    }

    getTokenInformation(smtpTokenId, callback) {
        var targetDocument,
            self = this;

        Users.findOne({'smtpToken.token': smtpTokenId}, function(error, document) {
            if (error) {
                callback(error);
            } else {
                if (!document) {
                    callback(new RestError('Cant find a user with that token.', HttpStatus.NOT_FOUND));
                } else {
                    if (self.updateToken(document)) {
                        document.save(function(error) {
                            if (error) {
                                callback(error);
                            } else {
                                callback(null, document);
                            }
                        });
                    } else {
                        callback(null, document);
                    }
                }
            }
        });
    }

    expireToken(smtpTokenId, callback) {
        Users.findOne({'smtpToken.token': smtpTokenId}, function(error, document) {
            if (!document) {
                callback(new Error('Target token doesnt exists.'));
            } else {
                UserDaoHelper.expireToken(document.smtpToken);
                document.save(function(error) {
                    if(error) {
                        callback(new Error('Error updating user.'));
                    } else {
                        callback();
                    }
                });
            }
        });
    }
}

module.exports = new UserDao();