<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">Home</a> / <a @click="$router.push('/batches')">Batch List</a> / <span>Material Intake</span></div>
    <div style="max-width:900px;margin:0 auto">
      <div class="card">
        <div class="card-header">
          <div><h2>Raw Material Intake & Batch Registration</h2><p>Register incoming raw materials and create a new batch</p></div>
          <el-button plain size="small" @click="$router.push('/batches')">Back to List</el-button>
        </div>
        <div class="card-body">
          <div class="user-info-bar">
            <span>User: {{ user.username }}</span>
            <span>Role: {{ roleMap[user.role] }}</span>
            <span>Employee No.: {{ user.employeeNo }}</span>
          </div>
          <el-form :model="form" label-width="130px">
            <el-row :gutter="20">
              <el-col :span="12"><el-form-item label="Product Type"><el-input v-model="form.productType" placeholder="e.g. Frozen Shrimp"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="Raw Material"><el-input v-model="form.rawMaterialType" placeholder="e.g. Whiteleg Shrimp"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="Source / Supplier"><el-input v-model="form.source" placeholder="Supplier name"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="Origin"><el-input v-model="form.origin" placeholder="Place of origin"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="Receipt Time"><el-date-picker v-model="form.receiptTime" type="datetime" placeholder="Select date & time" style="width:100%"></el-date-picker></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="Quantity (kg)"><el-input v-model="form.quantity" placeholder="Quantity in kg"></el-input></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="Certificate No."><el-input v-model="form.certificateNo" placeholder="Certificate number"></el-input></el-form-item></el-col>
            </el-row>
            <el-form-item label="Remarks"><el-input v-model="form.remark" type="textarea" rows="4" placeholder="Additional notes"></el-input></el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submit" :loading="submitting">Register Batch</el-button>
              <el-button @click="reset">Reset</el-button>
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
      roleMap: { ADMIN: 'Administrator', PROD_MANAGER: 'Production Manager', INSPECTOR: 'Quality Inspector', OPERATOR: 'Operator' }
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
        if (res.code === 200) { this.$message.success('Batch registered successfully'); this.$router.push('/batches'); }
        else this.$message.error(res.message);
      }).finally(() => this.submitting = false);
    },
    reset() {
      this.form = { productType: '', rawMaterialType: '', source: '', origin: '', receiptTime: '', quantity: '', certificateNo: '', remark: '' };
    }
  }
};
</script>
