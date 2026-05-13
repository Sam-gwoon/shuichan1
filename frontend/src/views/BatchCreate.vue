<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <a @click="$router.push('/batches')">批次列表</a> / <span>原料登记入库</span></div>
    <div style="max-width:900px;margin:0 auto">
      <div class="card">
        <div class="card-header">
          <div><h2>原料收货与批次登记</h2><p>登记原料收货信息，批次进入生产监管流程</p></div>
          <el-button plain size="small" @click="$router.push('/batches')">返回列表</el-button>
        </div>
        <div class="card-body">
          <div class="user-info-bar">
            <span>用户：{{ user.username }}</span>
            <span>角色：{{ roleMap[user.role] }}</span>
            <span>员工编号：{{ user.employeeNo }}</span>
          </div>
          <el-form :model="form" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="12"><el-form-item label="产品类型"><el-input v-model="form.productType" placeholder="请输入产品类型"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="原料类型"><el-input v-model="form.rawMaterialType" placeholder="请输入原料类型"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="来源/供应商"><el-input v-model="form.source" placeholder="请输入来源或供应商"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="产地"><el-input v-model="form.origin" placeholder="请输入产地"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="收货时间"><el-date-picker v-model="form.receiptTime" type="datetime" placeholder="选择收货时间" style="width:100%"></el-date-picker></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="数量(kg)"><el-input v-model="form.quantity" placeholder="请输入数量"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="证书编号"><el-input v-model="form.certificateNo" placeholder="请输入证书编号"></el-input></el-form-item></el-col>
            </el-row>
            <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="4" placeholder="请输入备注信息"></el-input></el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submit" :loading="submitting">登记批次</el-button>
              <el-button @click="reset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { createBatch } from '../api';
export default {
  data() {
    return {
      form: { productType: '', rawMaterialType: '', source: '', origin: '', receiptTime: '', quantity: '', certificateNo: '', remark: '', operatorId: null },
      submitting: false,
      roleMap: { ADMIN: '管理员', PROD_MANAGER: '生产主管', INSPECTOR: '质检员', OPERATOR: '操作员' }
    };
  },
  computed: {
    user() { try { return JSON.parse(localStorage.getItem('user')); } catch(e) { return {}; } }
  },
  methods: {
    submit() {
      this.submitting = true;
      this.form.operatorId = this.user.userId;
      createBatch(this.form).then(res => {
        if (res.code === 200) { this.$message.success('批次登记成功'); this.$router.push('/batches'); }
        else this.$message.error(res.message);
      }).finally(() => this.submitting = false);
    },
    reset() {
      this.f = { productType: '', rawMaterialType: '', source: '', origin: '', receiptTime: '', quantity: '', certificateNo: '', remark: '' };
    }
  }
};
</script>
