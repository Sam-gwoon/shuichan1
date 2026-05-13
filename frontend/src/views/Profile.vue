<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <span>个人资料</span></div>
    <div style="max-width:600px;margin:0 auto">
      <div class="card" style="text-align:center;padding:40px 20px">
        <div class="card-body">
          <div style="width:80px;height:80px;border-radius:50%;background:#2d79ea;color:#fff;display:flex;align-items:center;justify-content:center;font-size:32px;font-weight:700;margin:0 auto 16px">
            {{ userInitial }}
          </div>
          <h2 style="margin-bottom:4px">{{ user.realName }}</h2>
          <p style="color:#6f7e92;margin-bottom:24px">{{ roleMap[user.role] }} · {{ user.username }}</p>
          <div style="display:grid;gap:12px;text-align:left;max-width:400px;margin:0 auto">
            <div style="display:flex;justify-content:space-between;padding:12px 16px;background:#f8fafc;border-radius:10px">
              <span style="color:#6f7e92">用户名</span><strong>{{ user.username }}</strong>
            </div>
            <div style="display:flex;justify-content:space-between;padding:12px 16px;background:#f8fafc;border-radius:10px">
              <span style="color:#6f7e92">姓名</span><strong>{{ user.realName }}</strong>
            </div>
            <div style="display:flex;justify-content:space-between;padding:12px 16px;background:#f8fafc;border-radius:10px">
              <span style="color:#6f7e92">角色</span><strong>{{ roleMap[user.role] }}</strong>
            </div>
            <div style="display:flex;justify-content:space-between;padding:12px 16px;background:#f8fafc;border-radius:10px">
              <span style="color:#6f7e92">员工编号</span><strong>{{ user.employeeNo }}</strong>
            </div>
          </div>
          <div style="margin-top:24px;display:flex;gap:10px;justify-content:center">
            <el-button type="primary" @click="showPwdDialog">修改密码</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-dialog title="修改密码" :visible.sync="pwdDialogVisible" width="400px">
      <el-form :model="pwdForm" label-width="80px">
        <el-form-item label="旧密码"><el-input v-model="pwdForm.oldPwd" type="password"></el-input></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwdForm.newPwd" type="password"></el-input></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="changePwd">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { updateUser } from '../api';
export default {
  data() {
    return {
      pwdDialogVisible: false,
      pwdForm: { oldPwd: '', newPwd: '' },
      roleMap: { ADMIN: '管理员', PROD_MANAGER: '生产主管', INSPECTOR: '质检员', OPERATOR: '操作员' }
    };
  },
  computed: {
    user() { try { return JSON.parse(localStorage.getItem('user')); } catch(e) { return {}; } },
    userInitial() { return this.user.realName ? this.user.realName.charAt(0).toUpperCase() : 'U'; }
  },
  methods: {
    showPwdDialog() { this.pwdForm = { oldPwd: '', newPwd: '' }; this.pwdDialogVisible = true; },
    changePwd() {
      updateUser(this.user.userId, { password: this.pwdForm.newPwd }).then(res => {
        if (res.code === 200) { this.$message.success('密码修改成功，请重新登录'); this.pwdDialogVisible = false; }
        else this.$message.error(res.message);
      });
    }
  }
};
</script>
