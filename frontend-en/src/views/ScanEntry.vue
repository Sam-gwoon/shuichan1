<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">Home</a> / <span>Scan Entry</span></div>
    <div style="max-width:600px;margin:40px auto">
      <div class="card" style="text-align:center;padding:40px"><div class="card-body">
        <div style="width:80px;height:80px;border-radius:50%;background:#eff6ff;color:#2d79ea;display:flex;align-items:center;justify-content:center;font-size:40px;margin:0 auto 20px"><i class="el-icon-s-data"></i></div>
        <h2 style="margin-bottom:8px">Scan Quick Entry</h2>
        <p style="color:#6f7e92;margin-bottom:24px">Scan a traceability code to navigate to batch details or add process records</p>
        <div style="display:flex;gap:10px;justify-content:center">
          <el-input v-model="code" placeholder="Enter trace code or batch no." style="max-width:320px"></el-input>
          <el-button type="primary" @click="search">Confirm</el-button>
        </div>
        <div style="margin-top:20px;font-size:13px;color:#6f7e92">Example: AQ-2025-008-M2P4</div>
      </div></div>
    </div>
  </div>
</template>

<script>
import { getBatches } from '../api';
export default {
  data() { return { code: '' }; },
  methods: {
    search() {
      if (!this.code.trim()) { this.$message.warning('Please enter a trace code or batch number'); return; }
      getBatches({ batchNo: this.code.trim() }).then(res => {
        if (res.code === 200 && res.data.length > 0) this.$router.push('/batches/' + res.data[0].id);
        else this.$message.warning('No matching batch found');
      });
    }
  }
};
</script>
