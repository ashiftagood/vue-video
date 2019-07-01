class Store {
    constructor() {
        this._state = {
            login: sessionStorage.getItem('dss_login_state')
        };
        this.isLogin = function () {
            return this._state.login;
        };
        this.login = function () {
            this._state.login = true;
            sessionStorage.setItem('dss_login_state',true);
        };
        this.logout = function() {
            this._state.login = null;
            sessionStorage.clear();
        };
    }
}

export default new Store();