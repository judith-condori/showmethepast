var Mongoose = require('mongoose');
var Schema = Mongoose.Schema;

var DeletionsCacheSchema = Schema({
    collectionName: {
        type: String,
        required: true
    },
    documentId: {
        type: Schema.Types.ObjectId,
        required: true
    },
    requiredBy: {
        type: Schema.Types.ObjectId,
        required: true
    },
    createdAt: {
        type: Date,
        default: Date.now 
    }
});

DeletionsCacheCollection = Mongoose.model('deletionsCaches', DeletionsCacheSchema);

module.exports = DeletionsCacheCollection;