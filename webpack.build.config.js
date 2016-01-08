var path = require('path');

var reactFrontend = 'react-frontend';

module.exports = {
    entry: ['./' + reactFrontend + '/index'],
    output: {
        path: path.join(__dirname, reactFrontend, 'dist'),
        filename: 'bundle.js'
    },
    resolve: {
        extensions: ['', '.js', '.cjsx', '.coffee']
    },
    module: {
        loaders: [
            {test: /\.js$/, loaders: ['react-hot', 'babel'], include: path.join(__dirname, reactFrontend)},
            {test: /\.cjsx$/, loaders: ['react-hot', 'coffee', 'cjsx'], include: path.join(__dirname, reactFrontend)},
            {test: /\.coffee$/, loaders: ['react-hot', 'coffee'], include: path.join(__dirname, reactFrontend)}
        ]
    }
};