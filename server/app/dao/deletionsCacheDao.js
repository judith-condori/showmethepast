require('./mongoConnection.js');
const DeletionsCache = require('../collections/deletionsCacheCollection.js');
const serverConfiguration = require('../helpers/configuration.js');

class DeletionsCacheDao {
    registerDeletion(user, collectionName, documentId, callback) {
        var newObject = {
            collectionName: collectionName,
            documentId: documentId,
            requiredBy: user._id
        };

        DeletionsCache.create(newObject, function(error) {
            callback(error, undefined);
        });
    }
}

module.exports = new DeletionsCacheDao();