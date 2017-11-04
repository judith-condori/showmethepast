//  Requirements
var Mongoose = require('mongoose');
var ServerConfiguration = require('../helpers/configuration.js');
var BlueBird = require('bluebird');

//  Setting up

//      Set bluebird as promises engine
Mongoose.Promise = BlueBird;

//      Connecto to the target server
Mongoose.connect(ServerConfiguration.getMongoConnectionString());