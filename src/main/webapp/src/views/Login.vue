<template>
    <el-card class="login-card">
        <span slot="header">登 录</span>
        <el-form :label-position="'left'" label-width="80px" :model="loginForm">
            <el-form-item label="用户名">
                <el-input v-model="loginForm.userName" suffix-icon="el-icon-user"></el-input>
            </el-form-item>
            <el-form-item label="密码">
                <el-input type="password" v-model="loginForm.password" suffix-icon="el-icon-lock"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button id="subBtn" type="primary" @click.stop="onSubmit" :disabled="btnDisabled">登 录</el-button>
            </el-form-item>
        </el-form>
    </el-card>
</template>

<script>
    export default {
        name: "Login",
        data() {
            return {
                loginForm: {
                    userName: '',
                    password: ''
                }
            }
        },
        computed: {
            btnDisabled() {
                let bd = this.loginForm.userName == '' || this.loginForm.password == '';
                return bd;
            }
        },
        methods: {
            onSubmit() {
                let form = new FormData();
                form.append('username', this.loginForm.userName);
                form.append('userPwd', this.loginForm.password);
                this.$axios.post(location.origin.concat('/api/login'),form,{headers: { "Content-Type": "application/x-www-form-urlencoded" }}).then(resp => {
                    if(resp.data == 'success') {
                        this.$store.login();
                        this.$router.push('/home');
                    } else {
                        this.$message.error('用户名或密码错误');
                    }
                }).catch(err => {
                    this.$message.error('登录失败');
                    console.log(err);
                });
            }
        }
    }
</script>

<style scoped>
    .login-card {
        width: 25%;
        margin: 15% auto;
    }
</style>