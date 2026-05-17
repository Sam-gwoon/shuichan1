<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">Home</a> / <span>User Management</span></div>
    <div style="margin-bottom:20px;display:flex;justify-content:space-between;align-items:flex-start">
      <div><h1 style="font-size:22px;margin-bottom:4px">User Management</h1><p style="color:#6f7e92;font-size:13px">Manage system user accounts and role permissions</p></div>
      <el-button type="primary" size="small" @click="showAddDialog">+ Add User</el-button>
    </div>
    <div class="card"><div class="card-body">
      <el-table :data="users" stripe>
        <el-table-column prop="username" label="Username" width="120"></el-table-column>
        <el-table-column prop="realName" label="Name" width="150"></el-table-column>
        <el-table-column label="Role" width="180"><template slot-scope="s"><span :class="'tag tag-' + roleColor(s.row.role)">{{ roleMap[s.row.role] }}</span></template></el-table-column>
        <el-table-column prop="employeeNo" label="Employee No." width="120"></el-table-column>
        <el-table-column label="Status" width="80"><template slot-scope="s"><span :class="s.row.status === 1 ? 'tag tag-green' : 'tag tag-red'">{{ s.row.status === 1 ? 'Active' : 'Disabled' }}</span></template></el-table-column>
        <el-table-column label="Action">
          <template slot-scope="s">
            <el-button type="text" @click="showEditDialog(s.row)">Edit</el-button>
            <el-button type="text" style="color:#ef4444" @click="doDelete(s.row)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div></div>

    <el-dialog :title="isEdit ? 'Edit User' : 'Add User'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="Username"><el-input v-model="form.username" :disabled="isEdit"></el-input></el-form-item>
        <el-form-item label="Full Name"><el-input v-model="form.realName"></el-input></el-form-item>
        <el-form-item label="Password"><el-input v-model="form.password" placeholder="Leave blank to keep" type="password"></el-input></el-form-item>
        <el-form-item label="Role">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="Administrator" value="ADMIN"></el-option>
            <el-option label="Production Manager" value="PROD_MANAGER"></el-option>
            <el-option label="Quality Inspector" value="INSPECTOR"></el-option>
            <el-option label="Operator" value="OPERATOR"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Employee No."><el-input v-model="form.employeeNo"></el-input></el-form-item>
        <el-form-item label="Status"><el-switch v-model="form.statusBool" active-text="Active" inactive-text="Disabled"></el-switch></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="saveUser">Save</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUsers, createUser, updateUser, deleteUser } from '../api';
export default {
  data() {
    return {
      users: [], dialogVisible: false, isEdit: false,
      form: { username: '', realName: '', password: '', role: 'OPERATOR', employeeNo: '', statusBool: true },
      roleMap: { ADMIN: 'Administrator', PROD_MANAGER: 'Production Manager', INSPECTOR: 'Quality Inspector', OPERATOR: 'Operator' }
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
        if (res.code === 200) { this.$message.success(this.isEdit ? 'Updated' : 'Created'); this.dialogVisible = false; this.load(); }
        else this.$message.error(res.message);
      });
    },
    doDelete(row) {
      this.$confirm('Delete user ' + row.username + '?', 'Confirm', { type: 'warning' }).then(() => {
        deleteUser(row.id).then(res => {
          if (res.code === 200) { this.$message.success('Deleted'); this.load(); }
          else this.$message.error(res.message);
        });
      }).catch(() => {});
    }
  }
};
</script>
