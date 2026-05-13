<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <span>批次总览列表</span></div>
    <div style="margin-bottom:20px">
      <h1 style="font-size:22px;margin-bottom:4px">批次监管列表</h1>
      <p style="color:#6f7e92;font-size:13px">查看活跃批次及来料质检状态</p>
    </div>

    <div class="info-row" style="grid-template-columns:repeat(3,1fr);margin-bottom:16px">
      <div class="info-cell"><span class="label">活跃批次总数</span><strong style="font-size:22px;color:#17324f">{{ batches.length }}</strong></div>
      <div class="info-cell"><span class="label">来料质检 PASS</span><strong style="font-size:22px;color:#10b981">{{ passCount }}</strong></div>
      <div class="info-cell"><span class="label">来料质检 FAIL</span><strong style="font-size:22px;color:#ef4444">{{ failCount }}</strong></div>
    </div>

    <div class="card">
      <div class="card-body">
        <el-form :inline="true" :model="filters" style="margin-bottom:14px">
          <el-form-item label="批次编号"><el-input v-model="filters.batchNo" placeholder="搜索批次编号" size="small"></el-input></el-form-item>
          <el-form-item label="产品类型"><el-input v-model="filters.productType" placeholder="筛选产品类型" size="small"></el-input></el-form-item>
          <el-form-item label="生产状态">
            <el-select v-model="filters.productionStatus" placeholder="全部" clearable size="small">
              <el-option label="created" value="created"></el-option>
              <el-option label="processing" value="processing"></el-option>
              <el-option label="completed" value="completed"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="质检状态">
            <el-select v-model="filters.inspectionStatus" placeholder="全部" clearable size="small">
              <el-option label="PENDING" value="PENDING"></el-option>
              <el-option label="PASS" value="PASS"></el-option>
              <el-option label="FAIL" value="FAIL"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="search">搜索</el-button>
            <el-button size="small" @click="reset">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table :data="batches" stripe>
          <el-table-column prop="batchNo" label="批次编号" width="180"><template slot-scope="s"><strong>{{ s.row.batchNo }}</strong></template></el-table-column>
          <el-table-column prop="productType" label="产品类型"></el-table-column>
          <el-table-column prop="rawMaterialType" label="原料类型"></el-table-column>
          <el-table-column prop="operatorName" label="收货员工"></el-table-column>
          <el-table-column label="质检状态">
            <template slot-scope="s">
              <span :class="'tag tag-' + (s.row.incomingInspection === 'PASS' ? 'green' : s.row.incomingInspection === 'FAIL' ? 'red' : 'gray')">{{ s.row.incomingInspection }}</span>
            </template>
          </el-table-column>
          <el-table-column label="生产状态">
            <template slot-scope="s"><span :class="'tag tag-' + (s.row.productionStatus === 'completed' ? 'blue' : s.row.productionStatus === 'processing' ? 'orange' : 'gray')">{{ s.row.productionStatus }}</span></template>
          </el-table-column>
          <el-table-column label="放行状态" width="100">
            <template slot-scope="s"><span class="tag tag-gray" style="font-size:11px">{{ s.row.releaseStatus === 'released' ? '已放行' : '未放行' }}</span></template>
          </el-table-column>
          <el-table-column label="追溯状态" width="100">
            <template slot-scope="s"><span class="tag" :class="s.row.tracePublic ? 'tag-green' : 'tag-gray'" style="font-size:11px">{{ s.row.tracePublic ? '已生成' : '未生成' }}</span></template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template slot-scope="s"><el-button type="text" @click="$router.push('/batches/' + s.row.id)">详情</el-button></template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { getBatches } from '../api';
export default {
  data() {
    return {
      batches: [],
      filters: { batchNo: '', productType: '', productionStatus: '', inspectionStatus: '' }
    };
  },
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
