<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <span>人员权限管理</span></div>
    <div style="margin-bottom:20px;display:flex;justify-content:space-between;align-items:flex-start">
      <div><h1 style="font-size:22px;margin-bottom:4px">人员权限管理</h1><p style="color:#6f7e92;font-size:13px">管理系统用户账号与角色权限分配</p></div>
      <el-button type="primary" size="small" @click="showAddDialog">+ 新增用户</el-button>
    </div>
    <div class="card">
      <div class="card-body">
        <el-table :data="users" stripe>
          <el-table-column prop="username" label="用户名" width="120"></el-table-column>
          <el-table-column prop="realName" label="姓名" width="150"></el-table-column>
          <el-table-column label="角色" width="150">
            <template slot-scope="s"><span :class="'tag tag-' + roleColor(s.row.role)">{{ roleMap[s.row.role] }}</span></template>
          </el-table-column>
          <el-table-column prop="employeeNo" label="员工编号" width="120"></el-table-column>
          <el-table-column label="状态" width="80">
            <template slot-scope="s"><span :class="s.row.status === 1 ? 'tag tag-green' : 'tag tag-red'">{{ s.row.status === 1 ? '启用' : '禁用' }}</span></template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="s">
              <el-button type="text" @click="showEditDialog(s.row)">编辑</el-button>
              <el-button type="text" style="color:#ef4444" @click="doDelete(s.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <el-dialog :title="isEdit ? '编辑用户' : '新增用户'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="isEdit"></el-input></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName"></el-input></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" placeholder="留空则不修改" type="password"></el-input></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="生产主管" value="PROD_MANAGER"></el-option>
            <el-option label="质检员" value="INSPECTOR"></el-option>
            <el-option label="操作员" value="OPERATOR"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="员工编号"><el-input v-model="form.employeeNo"></el-input></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.statusBool" active-text="启用" inactive-text="禁用"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUser">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUsers, createUser, updateUser, deleteUser } from '../api';
export default {
  data() {
    return {
      users: [],
      dialogVisible: false,
      isEdit: false,
      form: { username: '', realName: '', password: '', role: 'OPERATOR', employeeNo: '', statusBool: true },
      roleMap: { ADMIN: 'Admin', PROD_MANAGER: 'Production Manager', INSPECTOR: 'Quality Inspector', OPERATOR: 'Production Operator' }
    };
  },
  created() { this.load(); },
  methods: {
    load() { getUsers().then(res => { if (res.code === 200) this.users = res.data; }); },
    roleColor(r) { return { ADMIN: 'blue', PROD_MANAGER: 'orange', INSPECTOR: 'green', OPERATOR: 'gray' }[r] || 'gray'; },
    showAddDialog() {
      this.isEdit = false;
      this.form = { username: '', realName: '', password: '', role: 'OPERATOR', employeeNo: '', statusBool: true };
      this.dialogVisible = true;
    },
    showEditDialog(row) {
      this.isEdit = true;
      this.form = { id: row.id, username: row.username, realName: row.realName, password: '', role: row.role, employeeNo: row.employeeNo, statusBool: row.status === 1 };
      this.dialogVisible = true;
    },
    saveUser() {
      const data = { ...this.form, status: this.form.statusBool ? 1 : 0 };
      delete data.statusBool;
      const fn = this.isEdit ? updateUser(data.id, data) : createUser(data);
      fn.then(res => {
        if (res.code === 200) { this.$message.success(this.isEdit ? '更新成功' : '创建成功'); this.dialogVisible = false; this.load(); }
        else this.$message.error(res.message);
      });
    },
    doDelete(row) {
      this.$confirm('确认删除用户 ' + row.username + '？', '提示', { type: 'warning' }).then(() => {
        deleteUser(row.id).then(res => {
          if (res.code === 200) { this.$message.success('删除成功'); this.load(); }
          else this.$message.error(res.message);
        });
      }).catch(() => {});
    }
  }
};
</script>
