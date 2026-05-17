<template>
  <div class="login-page">
    <div class="login-card">
      <div style="text-align:center;margin-bottom:24px">
        <div style="width:64px;height:64px;border-radius:50%;background:#eff6ff;color:#2d79ea;display:flex;align-items:center;justify-content:center;font-size:32px;margin:0 auto 16px">
          <i class="el-icon-s-data"></i>
        </div>
        <h2>Aquatic Processing Supervision</h2>
        <p style="color:#606266;font-size:14px;margin-top:8px">Sign in to manage batch registration, inspection, and supervision.</p>
      </div>
      <el-form :model="form" :rules="rules" ref="loginForm">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="Username" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="Password" prefix-icon="el-icon-lock"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="handleLogin" :loading="loading">Sign In</el-button>
        </el-form-item>
      </el-form>
      <div style="color:#606266;font-size:13px;line-height:1.8;text-align:center">
        <div>Operator: operator1 / 123456</div>
        <div>Inspector: inspector1 / 123456</div>
        <div>Manager: manager1 / 123456</div>
        <div>Admin: admin1 / 123456</div>
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
        username: [{ required: true, message: 'Please enter username', trigger: 'blur' }],
        password: [{ required: true, message: 'Please enter password', trigger: 'blur' }]
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
            this.$message.success('Login successful');
            this.$router.push('/home');
          } else {
            this.$message.error(res.message || 'Login failed');
          }
        }).catch(() => {
          this.$message.error('Login failed');
        }).finally(() => {
          this.loading = false;
        });
      });
    }
  }
};
</script>
