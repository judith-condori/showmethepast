const Mongoose = require('mongoose');
const Schema = Mongoose.Schema;

var MultiLanguageSchema = Schema({
    english: {
        type: String,
        required: true
    },
    spanish: {
        type: String,
        required: true
    }
}, {_id: false});

module.exports = MultiLanguageSchema;