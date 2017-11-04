const Mongoose = require('mongoose');
const Schema = Mongoose.Schema;

const GPSSchema = require('./gpsPointSchema.js');
const ImageInformationSchema = require('./imageSizeInformationSchema.js');

var ARPositionSchema = Schema({
    uAngle: {
        type: Number,
        required: true
    },
    vAngle: {
        type: Number,
        required: true
    },
    wAngle: {
        type: Number,
        required: true
    },
    uDistanceCalibration: {
        type: Number,
        required: true
    },
    vDistanceCalibration: {
        type: Number,
        required: true
    },
    imageSizeInformation: {
        type: ImageInformationSchema,
        require: true
    },
    startPosition: {
        type: GPSSchema,
        require: true
    },
    targetPosition: {
        type: GPSSchema,
        require: true
    }
}, {_id: false});

module.exports = ARPositionSchema;