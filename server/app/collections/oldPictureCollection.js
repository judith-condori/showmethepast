var Mongoose = require('mongoose');
const MultiLanguageSchema = require('./schemas/multiLanguageSchema.js');
const GPSSchema = require('./schemas/gpsPointSchema.js');
const ARPositionSchema = require('./schemas/arPositionSchema.js');

var Schema = Mongoose.Schema;

var OldPicturesSchema = Schema({
    name: {
        type: MultiLanguageSchema,
        required: true
    },
    description: {
        type: MultiLanguageSchema,
        required: true
    },
    image: {
        type: Schema.Types.ObjectId,
        required: true
    },
    position: {
        type: ARPositionSchema,
        required: true
    },
    touristicPlace: {
        type: Schema.Types.ObjectId,
        required: true
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

OldPicturesCollection = Mongoose.model('oldPictures', OldPicturesSchema);

module.exports = OldPicturesCollection;