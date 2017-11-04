SmtpRoles = {
    CLIENT: 'client',
    EDITOR: 'editor',
    ADMIN: 'admin',

    isEditor: function(role) {
        return SmtpRoles.EDITOR === role || this.isAdmin(role);
    },

    isAdmin: function(role) {
        return SmtpRoles.ADMIN === role;
    }
};

module.exports = SmtpRoles;