const _ = require('underscore');

SyncTools = {
    reduceSyncInformation: function(creations, editions, deletions) {
        var creationsArray = _.map(creations, (x) => x._id),
            editionsArray = _.map(editions, (x) => x._id),
            changesArray = _.union(creationsArray, editionsArray),
            result = {};

        result.changes = _.map(changesArray, function(x) {
            return {_id: x};
        });

        result.deletions = deletions;

        return result;
    }
}

module.exports = SyncTools;