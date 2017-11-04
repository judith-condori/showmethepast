var Mongoose = require('mongoose');
const MultiLanguageSchema = require('./schemas/multiLanguageSchema.js');

var Schema = Mongoose.Schema;

var ImagesDataSchema = Schema({
    data: {
        type: String,
        required: true
    },
    author: {
        type: MultiLanguageSchema
    },
    description: {
        type: MultiLanguageSchema,
    },
    owner: {
        type: Schema.Types.ObjectId,
        required: true
    },
    createdAt: {
        type: Date,
        default: Date.now 
    },
    updatedAt: {
        type: Date
    }
});

ImagesDataCollection = Mongoose.model('images', ImagesDataSchema);

module.exports = ImagesDataCollection;