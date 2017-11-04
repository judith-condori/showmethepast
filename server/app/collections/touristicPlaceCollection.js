var Mongoose = require('mongoose');
const MultiLanguageSchema = require('./schemas/multiLanguageSchema.js');
const GPSSchema = require('./schemas/gpsPointSchema.js');

var Schema = Mongoose.Schema;

var TouristicPlaceSchema = Schema({
    name: {
        type: MultiLanguageSchema,
        required: true
    },
    description: {
        type: MultiLanguageSchema,
        required: true
    },
    position: {
        type: GPSSchema,
        required: true
    },
    image: {
        type: Schema.Types.ObjectId,
        required: true
    },
    informationList: {
        type: [MultiLanguageSchema]
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

TouristicPlacesModel = Mongoose.model('touristicPlaces', TouristicPlaceSchema);

module.exports = TouristicPlacesModel;