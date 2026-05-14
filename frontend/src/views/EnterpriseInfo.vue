<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <span>企业信息管理</span></div>
    <div style="margin-bottom:20px">
      <h1 style="font-size:22px;margin-bottom:4px">企业信息管理</h1>
      <p style="color:#6f7e92;font-size:13px">配置企业基本信息，用于公共追溯页面展示</p>
    </div>
    <div style="max-width:800px">
      <div class="card">
        <div class="card-body">
          <el-form :model="form" label-width="140px">
            <el-row :gutter="20">
              <el-col :span="12"><el-form-item label="企业名称"><el-input v-model="form.name"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="生产许可证编号"><el-input v-model="form.licenseNo"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="生产地址"><el-input v-model="form.address"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="联系电话"><el-input v-model="form.phone"></el-input></el-form-item></el-col>
            </el-row>
            <el-form-item label="经营范围"><el-input v-model="form.scope" type="textarea" rows="3"></el-input></el-form-item>
            <el-form-item v-if="isAdmin"><el-button type="primary" @click="save" :loading="saving">保存信息</el-button></el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getEnterpriseInfo, updateEnterpriseInfo } from '../api';
export default {
  data() { return { form: {}, saving: false }; },
  computed: {
    isAdmin() { const u = JSON.parse(localStorage.getItem('user') || '{}'); return u.role === 'ADMIN'; }
  },
  created() { getEnterpriseInfo().then(res => { if (res.code === 200 && res.data) this.form = res.data; }); },
  methods: {
    save() {
      this.saving = true;
      updateEnterpriseInfo(this.form).then(res => {
        if (res.code === 200) this.$message.success('保存成功');
        else this.$message.error(res.message);
      }).finally(() => this.saving = false);
    }
  }
};
</script>
