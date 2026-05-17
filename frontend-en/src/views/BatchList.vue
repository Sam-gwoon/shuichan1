<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">Home</a> / <span>Batch List</span></div>
    <div style="margin-bottom:20px"><h1 style="font-size:22px;margin-bottom:4px">Batch Supervision List</h1><p style="color:#6f7e92;font-size:13px">View active batches and incoming inspection status</p></div>

    <div class="info-row" style="grid-template-columns:repeat(3,1fr);margin-bottom:16px">
      <div class="info-cell"><span class="label">Total Active Batches</span><strong style="font-size:22px;color:#17324f">{{ batches.length }}</strong></div>
      <div class="info-cell"><span class="label">Incoming PASS</span><strong style="font-size:22px;color:#10b981">{{ passCount }}</strong></div>
      <div class="info-cell"><span class="label">Incoming FAIL</span><strong style="font-size:22px;color:#ef4444">{{ failCount }}</strong></div>
    </div>

    <div class="card"><div class="card-body">
      <el-form :inline="true" :model="filters" style="margin-bottom:14px">
        <el-form-item label="Batch No."><el-input v-model="filters.batchNo" placeholder="Search batch no." size="small"></el-input></el-form-item>
        <el-form-item label="Product Type"><el-input v-model="filters.productType" placeholder="Filter product type" size="small"></el-input></el-form-item>
        <el-form-item label="Prod. Status">
          <el-select v-model="filters.productionStatus" placeholder="All" clearable size="small">
            <el-option label="created" value="created"></el-option>
            <el-option label="processing" value="processing"></el-option>
            <el-option label="completed" value="completed"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Inspection">
          <el-select v-model="filters.inspectionStatus" placeholder="All" clearable size="small">
            <el-option label="PENDING" value="PENDING"></el-option>
            <el-option label="PASS" value="PASS"></el-option>
            <el-option label="FAIL" value="FAIL"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" @click="search">Search</el-button>
          <el-button size="small" @click="reset">Reset</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="batches" stripe>
        <el-table-column prop="batchNo" label="Batch No." width="180"><template slot-scope="s"><strong>{{ s.row.batchNo }}</strong></template></el-table-column>
        <el-table-column prop="productType" label="Product Type"></el-table-column>
        <el-table-column prop="rawMaterialType" label="Raw Material"></el-table-column>
        <el-table-column prop="operatorName" label="Operator"></el-table-column>
        <el-table-column label="Inspection">
          <template slot-scope="s"><span :class="'tag tag-' + (s.row.incomingInspection === 'PASS' ? 'green' : s.row.incomingInspection === 'FAIL' ? 'red' : 'gray')">{{ s.row.incomingInspection }}</span></template>
        </el-table-column>
        <el-table-column label="Prod. Status">
          <template slot-scope="s"><span :class="'tag tag-' + (s.row.productionStatus === 'completed' ? 'blue' : s.row.productionStatus === 'processing' ? 'orange' : 'gray')">{{ s.row.productionStatus }}</span></template>
        </el-table-column>
        <el-table-column label="Release" width="100">
          <template slot-scope="s"><span class="tag tag-gray" style="font-size:11px">{{ s.row.releaseStatus === 'released' ? 'Released' : 'Unreleased' }}</span></template>
        </el-table-column>
        <el-table-column label="Trace" width="100">
          <template slot-scope="s"><span :class="s.row.tracePublic ? 'tag tag-green' : 'tag tag-gray'" style="font-size:11px">{{ s.row.tracePublic ? 'Generated' : 'Pending' }}</span></template>
        </el-table-column>
        <el-table-column label="Action" width="80">
          <template slot-scope="s"><el-button type="text" @click="$router.push('/batches/' + s.row.id)">Details</el-button></template>
        </el-table-column>
      </el-table>
    </div></div>
  </div>
</template>

<script>
import { getBatches } from '../api';
export default {
  data() { return { batches: [], filters: { batchNo: '', productType: '', productionStatus: '', inspectionStatus: '' } }; },
  computed: {
    passCount() { return this.batches.filter(b => b.incomingInspection === 'PASS').length; },
    failCount() { return this.batches.filter(b => b.incomingInspection === 'FAIL').length; }
  },
  created() { this.search(); },
  methods: {
    search() { getBatches(this.filters).then(res => { if (res.code === 200) this.batches = res.data; }); },
    reset() { this.filters = { batchNo: '', productType: '', productionStatus: '', inspectionStatus: '' }; this.search(); }
  }
};
</script>
