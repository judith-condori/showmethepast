const Mongoose = require('mongoose');
const Schema = Mongoose.Schema;

var GPSSchema = Schema({
    latitude: {
        type: Number,
        required: true
    },
    longitude: {
        type: Number,
        required: true
    },
    regionKey: {
        type: String,
        required: true
    }
}, {_id: false});

module.exports = GPSSchema;