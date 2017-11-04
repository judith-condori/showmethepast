var localhostConfiguration = {
    MONGO_HOST: '127.0.0.1',
    MONGO_PORT: '27017',
    MONGO_USER: 'administrator',
    MONGO_PASSWORD: '12345678',
    MONGO_DATABASE: 'admin',
    GOOGLE_LOGIN_KEY: '<LOCAL_GOOGLE_LOGIN_KEY_HERE>'
}

ServerConfiguration = {
    getConfiguration:  function() {
        var result = {
            GOOGLE_LOGIN_KEY:   process.env.GOOGLE_LOGIN_KEY    || localhostConfiguration.GOOGLE_LOGIN_KEY,
            MONGO_HOST:         process.env.MONGO_HOST          || localhostConfiguration.MONGO_HOST,
            MONGO_PORT:         process.env.MONGO_PORT          || localhostConfiguration.MONGO_PORT,
            MONGO_USER:         process.env.MONGO_USER          || localhostConfiguration.MONGO_USER,
            MONGO_PASSWORD:     process.env.MONGO_PASSWORD      || localhostConfiguration.MONGO_PASSWORD,
            MONGO_DATABASE:     process.env.MONGO_DATABASE      || localhostConfiguration.MONGO_DATABASE
        };
        return result;
    },
    getMongoConnectionString: function() {
        var configuration = this.getConfiguration(),
            mongoUser = configuration.MONGO_USER,
            mongoPassword = configuration.MONGO_PASSWORD,
            mongoHost = configuration.MONGO_HOST,
            mongoPort = configuration.MONGO_PORT,
            mongoDatabase = configuration.MONGO_DATABASE,
            connectionString = `mongodb://${mongoUser}:${mongoPassword}@${mongoHost}:${mongoPort}/${mongoDatabase}`;
            
            return connectionString;
    }
}

module.exports = ServerConfiguration;