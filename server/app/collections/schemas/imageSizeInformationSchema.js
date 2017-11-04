const Mongoose = require('mongoose');
const Schema = Mongoose.Schema;

var ImageInformationSizeSchema = Schema({
    alterationWidth: {
        type: Number,
        required: true
    },
    alterationHeight: {
        type: Number,
        required: true
    },
    scale: {
        type: Number,
        required: true
    },
    screenWidth: {
        type: Number,
        required: true
    },
    screenHeight: {
        type: Number,
        required: true
    },
    screenDensityWidth: {
        type: Number,
        require: true
    },
    screenDensityHeight: {
        type: Number,
        require: true
    }
}, {_id: false});

module.exports = ImageInformationSizeSchema;