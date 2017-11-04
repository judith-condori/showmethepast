// External libraries
var express = require('express')
var bodyParser = require('body-parser');
var GoogleAuth = require('google-auth-library');
var HttpStatus = require('http-status-codes');

// Express configuration
var app = express();

app.use(bodyParser.json({limit: '50mb'}));

// Custom js files
var serverConfiguration = require('./app/helpers/configuration.js')

// Register controllers
require('./app/controllers/securityController.js')(app);
require('./app/controllers/touristicPlaceController.js')(app);
require('./app/controllers/oldPictureController.js')(app);
require('./app/controllers/commonController.js')(app);
require('./app/controllers/adminController.js')(app);

app.get('/', function (req, res) {
    res.send('Show me the past, work in progress xD!!!!')
})

app.listen(3000, function () {
    console.log();
    console.log();
    console.log('\t░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░');
    console.log('\t░░░░░░░░░░░░░▄▄██████████▄▄░░░░░░░░░░░░░');
    console.log('\t░░░░░░░░░░░░░▀▀▀░░░██░░░▀▀▀░░░░░░░░░░░░░');
    console.log('\t░░░░░▄██▄░░░▄▄████████████▄▄░░░▄██▄░░░░░');
    console.log('\t░░░▄███▀░░▄████▀▀▀░░░░▀▀▀████▄░░▀███▄░░░');
    console.log('\t░░████▄░▄███▀░░░░░░░░░░░░░░▀███▄░▄████░░');
    console.log('\t░███▀█████▀▄████▄░░░░░░▄████▄▀█████▀███░');
    console.log('\t░██▀░░███▀░██████░░░░░░██████░▀███░░▀██░');
    console.log('\t░░▀░░▄██▀░░▀████▀░░▄▄░░▀████▀░░▀██▄░░▀░░');
    console.log('\t░░░░░███░░░░░░░░░░░▀▀░░░░░░░░░░░███░░░░░');
    console.log('\t░░░░░██████████████████████████████░░░░░');
    console.log('\t░▄█░░▀██░░███░░▀██░░░░███░░███░░██▀░░█▄░');
    console.log('\t░███░░███░███░░░██░░░░███░░███▄███░░███░');
    console.log('\t░▀██▄████████░░░██░░░░███░░████████▄██▀░');
    console.log('\t░░▀███▀░▀████░░░██░░░░███░░████▀░▀███▀░░');
    console.log('\t░░░▀███▄░░▀███████░░░░███████▀░░▄███▀░░░');
    console.log('\t░░░░░▀███░░░░▀▀██████████▀▀▀░░░███▀░░░░░');
    console.log('\t░░░░░░░▀░░░░░▄▄▄░░░██░░░▄▄▄░░░░░▀░░░░░░░');
    console.log('\t░░░░░░░░░░░░░▀▀██████████▀▀░░░░░░░░░░░░░');
    console.log('\t░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░');
    console.log('   ╔═╗┬ ┬┌─┐┬ ┬  ┌┬┐┌─┐  ┌┬┐┬ ┬┌─┐  ┌─┐┌─┐┌─┐┌┬┐  ┬┬┬');
    console.log('   ╚═╗├─┤│ ││││  │││├┤    │ ├─┤├┤   ├─┘├─┤└─┐ │   │││');
    console.log('   ╚═╝┴ ┴└─┘└┴┘  ┴ ┴└─┘   ┴ ┴ ┴└─┘  ┴  ┴ ┴└─┘ ┴   ooo');

    console.log('Running on port 3000!!!');
})
