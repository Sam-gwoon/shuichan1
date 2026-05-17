<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">Home</a> / <span>Enterprise Info</span></div>
    <div style="margin-bottom:20px"><h1 style="font-size:22px;margin-bottom:4px">Enterprise Information</h1><p style="color:#6f7e92;font-size:13px">Configure company information displayed on the public trace page</p></div>
    <div style="max-width:800px"><div class="card"><div class="card-body">
      <el-form :model="form" label-width="160px">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="Company Name"><el-input v-model="form.name"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="License No."><el-input v-model="form.licenseNo"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="Address"><el-input v-model="form.address"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="Contact Phone"><el-input v-model="form.phone"></el-input></el-form-item></el-col>
        </el-row>
        <el-form-item label="Business Scope"><el-input v-model="form.scope" type="textarea" rows="3"></el-input></el-form-item>
        <el-form-item><el-button type="primary" @click="save" :loading="saving">Save</el-button></el-form-item>
      </el-form>
    </div></div></div>
  </div>
</template>

<script>
import { getEnterpriseInfo, updateEnterpriseInfo } from '../api';
export default {
  data() { return { form: {}, saving: false }; },
  created() { getEnterpriseInfo().then(res => { if (res.code === 200 && res.data) this.form = res.data; }); },
  methods: {
    save() {
      this.saving = true;
      updateEnterpriseInfo(this.form).then(res => {
        if (res.code === 200) this.$message.success('Saved');
        else this.$message.error(res.message);
      }).finally(() => this.saving = false);
    }
  }
};
</script>
