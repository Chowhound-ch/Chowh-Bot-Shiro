<template>
  <div class="login">
    <el-row class="login-row">
      <el-col :span="6" :offset="9">
        <div class="login-form">
            <el-card class="login-card">
              <template #header>
                <div class="card-header">
                  <h2>登录</h2>
                </div>
              </template>
              <div>
                <el-form :model="form" label-width="auto" >
                  <el-form-item label="账号">
                    <el-input placeholder="请输入账号" v-model="form.username" ></el-input>
                  </el-form-item>
                  <el-form-item label="密码">
                    <el-input type="password" placeholder="请输入密码" v-model="form.password"></el-input>
                  </el-form-item>
                  <el-form-item class="login-btn">
                    <el-button type="primary" @click="onSubmit">登录</el-button>
                  </el-form-item>
                </el-form>
              </div>
              <!-- <template #footer>Footer content</template> -->
            </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import request from '@/utils/request.js'
import { reactive } from 'vue'

let form = reactive({
  username: '',
  password: ''
})

function onSubmit() {
  console.log("提交表单", import.meta.env)
  request({
    url: '/system/login',
    method: 'post',
    data: {
      userId: form.username,
      password: form.password
    }
  }).then(response => {
    console.log("登录成功", response)
    // vue-router跳转到首页
    window.location.href = '/'
  }).catch(error => {
    console.error("登录失败", error)
  })
}

</script>

<style lang='scss' scoped>
.login {
  display: block;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background-image: url("../assets/images/login-background.png");
  background-size: cover;
  opacity: 0.9;
}
.login-row {
  margin-top: 20%;
}
.login-form {
  margin-top: -100px;
  width: 100%;

}
.card-header {
  text-align: center;
}
.login-btn{
  margin-left: 60%;
}
</style>
