import Vue from 'vue';
import Router from 'vue-router';
import Home from './views/Home.vue';
import Login from './views/Login.vue';
import store from './store';
import VideoLive from '@/components/videoLive.vue';
import VideoHistory from '@/components/videoHistory.vue';

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/login',
            name: 'login',
            component: Login
        },
        {
            path: '/home',
            name: 'home',
            component: Home,
            children:[
                {
                    path: 'videoLive',
                    component: VideoLive
                },
                {
                    path: 'videoHistory',
                    component: VideoHistory
                }
            ],
            beforeEnter(to, from, next) {
                if(store.isLogin()) {
                    next();
                } else {
                    next('/');
                }
            }
        }
    ]
});
