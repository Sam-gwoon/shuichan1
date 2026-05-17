<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">Home</a> / <span>Public Trace Preview</span></div>
    <div style="margin-bottom:20px"><h1 style="font-size:22px;margin-bottom:4px">Public Trace Preview</h1><p style="color:#6f7e92;font-size:13px">Preview the public traceability page</p></div>
    <div style="margin-bottom:16px">
      <el-input v-model="traceCode" placeholder="Enter trace code, e.g. AQ-2025-008-M2P4" style="width:400px;margin-right:10px"></el-input>
      <el-button type="primary" @click="query">Query</el-button>
    </div>
    <div class="public-page" style="border-radius:14px;overflow:hidden;border:1px solid #e6edf7" v-if="data">
      <div class="public-header">
        <h1>Product Traceability Information</h1>
        <p style="margin:0 0 8px;font-size:16px;opacity:0.9">Trace Code: <strong>{{ traceCode }}</strong></p>
        <p style="margin:0;font-size:14px;opacity:0.75">Aquatic Processing Supervision Platform</p>
      </div>
      <div class="public-body">
        <section class="public-card" v-if="data.enterprise"><h2 class="public-card__title">Enterprise Information</h2><table class="public-table">
          <tr><td class="label">Company Name</td><td>{{ data.enterprise.name }}</td></tr>
          <tr><td class="label">Production Address</td><td>{{ data.enterprise.address }}</td></tr>
          <tr><td class="label">License No.</td><td>{{ data.enterprise.licenseNo }}</td></tr>
          <tr><td class="label">Contact</td><td>{{ data.enterprise.phone }}</td></tr>
        </table></section>
        <section class="public-card" v-if="data.batch"><h2 class="public-card__title">Batch Information</h2><table class="public-table">
          <tr><td class="label">Batch No.</td><td>{{ data.batch.batchNo }}</td></tr>
          <tr><td class="label">Product Type</td><td>{{ data.batch.productType }}</td></tr>
          <tr><td class="label">Trace Code</td><td>{{ data.batch.traceCode }}</td></tr>
        </table></section>
        <section class="public-card" v-if="data.incomingInspection || data.finishedInspection"><h2 class="public-card__title">Inspection Results</h2><table class="public-table">
          <tr v-if="data.incomingInspection"><td class="label">Incoming Inspection</td><td><span style="color:#047857;font-weight:600">{{ data.incomingInspection.result }}</span><span style="display:block;font-size:12px;color:#8a98ab;margin-top:2px">{{ data.incomingInspection.time }}</span></td></tr>
          <tr v-if="data.finishedInspection"><td class="label">Finished Inspection</td><td><span style="color:#047857;font-weight:600">{{ data.finishedInspection.result }}</span><span style="display:block;font-size:12px;color:#8a98ab;margin-top:2px">{{ data.finishedInspection.time }}</span></td></tr>
        </table></section>
        <section class="public-card" v-if="data.release"><h2 class="public-card__title">Release Information</h2><table class="public-table">
          <tr><td class="label">Status</td><td><span style="color:#047857;font-weight:600">{{ data.release.status }}</span></td></tr>
          <tr><td class="label">Release Time</td><td>{{ data.release.time }}</td></tr>
        </table></section>
      </div>
    </div>
    <div v-else style="text-align:center;padding:60px;color:#6f7e92">
      <i class="el-icon-view" style="font-size:48px;color:#d1d5db;display:block;margin-bottom:12px"></i>
      <strong style="display:block;color:#17324f;margin-bottom:8px">Enter a trace code to query</strong>
    </div>
  </div>
</template>

<script>
import { getPublicTrace } from '../api';
export default {
  data() { return { traceCode: '', data: null }; },
  methods: {
    query() {
      if (!this.traceCode.trim()) { this.$message.warning('Please enter a trace code'); return; }
      getPublicTrace(this.traceCode.trim()).then(res => {
        if (res.code === 200) this.data = res.data;
        else { this.$message.error(res.message); this.data = null; }
      });
    }
  }
};
</script>
