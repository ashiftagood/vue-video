class Store {
    constructor() {
        this._state = {
            login: sessionStorage.getItem('dss_login_state')
        };
    }

    isLogin() {
        return this._state.login;
    }

    login() {
        this._state.login = true;
        sessionStorage.setItem('dss_login_state',true);
    }

    logout() {
        this._state.login = null;
        sessionStorage.clear();
    }
}

export default new Store();