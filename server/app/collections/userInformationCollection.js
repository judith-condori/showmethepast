var Mongoose = require('mongoose');
var Schema = Mongoose.Schema;

var SmptTokenSchema = Schema({
    token: {
        type: String,
        required: true
    },
    alive: {
        type: Boolean,
        required: true
    },
    
    expirationDate: {
        type: Date,
        required: true
    }
}, {_id: false});

var UserInformationSchema = Schema({
    userId: {
        type: String,
        required: true
    },
    email: {
        type: String,
        required: true
    },
    name: {
        type: String,
        required: true
    },
    picture: {
        type: String,
        required: true
    },
    role: {
        type: String,
        required: true
    },
    smtpToken: SmptTokenSchema,
    createdAt: {
        type: Date,
        default: Date.now 
    }
});

UserInformationCollection = Mongoose.model('users', UserInformationSchema);

module.exports = UserInformationCollection;