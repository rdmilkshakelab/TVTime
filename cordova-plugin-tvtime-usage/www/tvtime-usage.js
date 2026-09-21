var exec = require('cordova/exec');

module.exports = {
    hasAccess: function (success, failure) {
        exec(success, failure, 'TVTimeUsage', 'hasAccess', []);
    },

    openSettings: function (success, failure) {
        exec(success, failure, 'TVTimeUsage', 'openSettings', []);
    },

    queryToday: function (success, failure) {
        exec(success, failure, 'TVTimeUsage', 'queryToday', []);
    }
};
