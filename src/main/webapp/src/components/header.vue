<template>
    <el-menu :default-active="activeIndex" mode="horizontal" :router="true" background-color="#545c64" text-color="#fff" active-text-color="#ffd04b">
        <el-menu-item v-for="menu in menus" :index="menu.index" :key="menu.id">{{menu.name}}</el-menu-item>
        <el-button icon="el-icon-switch-button" circle class="logoutBtn" @click="logoutHandler"></el-button>
    </el-menu>
</template>

<script>
    export default {
        name: 'vHeader',
        data() {
            return {
                menus: [
                    {id: 1, index:'/home/videoLive', name:'视频直播'},
                    {id: 2, index:'/home/videoHistory', name:'直播历史'}
                ],
                activeIndex: '/home/videoLive'
            }
        },
        created() {
            this.$router.push('/home/videoLive');
        },
        methods: {
            logoutHandler() {
                this.$store.logout();
                this.$router.push('/login');
            }
        }
    }
</script>

<style scoped>
    .logoutBtn {
        float: right;
        margin-top: 0.5rem;
        margin-right: 0.5rem;
    }
</style>
