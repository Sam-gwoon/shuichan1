<template>
  <div class="login-page">
    <div class="login-card">
      <div style="text-align:center;margin-bottom:24px">
        <div style="width:64px;height:64px;border-radius:50%;background:#eff6ff;color:#2d79ea;display:flex;align-items:center;justify-content:center;font-size:32px;margin:0 auto 16px">
          <i class="el-icon-s-data"></i>
        </div>
        <h2>水产品加工监管系统</h2>
        <p style="color:#606266;font-size:14px;margin-top:8px">Sign in to manage batch registration, inspection, and supervision.</p>
      </div>
      <el-form :model="form" :rules="rules" ref="loginForm">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="el-icon-lock"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="handleLogin" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
      <div style="color:#606266;font-size:13px;line-height:1.8;text-align:center">
        <div>生产操作员：operator1 / 123456</div>
        <div>质检员：inspector1 / 123456</div>
        <div>生产主管：manager1 / 123456</div>
        <div>管理员：admin1 / 123456</div>
      </div>
    </div>
  </div>
</template>

<script>
import { loginApi } from '../api';

export default {
  data() {
    return {
      form: { username: '', password: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    };
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return;
        this.loading = true;
        loginApi(this.form).then(res => {
          if (res.code === 200) {
            localStorage.setItem('token', res.data.token);
            localStorage.setItem('user', JSON.stringify(res.data));
            this.$message.success('登录成功');
            this.$router.push('/home');
          } else {
            this.$message.error(res.message || '登录失败');
          }
        }).catch(() => {
          this.$message.error('登录失败');
        }).finally(() => {
          this.loading = false;
        });
      });
    }
  }
};
</script>
