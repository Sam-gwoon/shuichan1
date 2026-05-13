<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <span>质检记录档案</span></div>
    <div style="margin-bottom:20px">
      <h1 style="font-size:22px;margin-bottom:4px">质检记录档案</h1>
      <p style="color:#6f7e92;font-size:13px">来料质检与成品质检的历史记录查询</p>
    </div>
    <div class="card">
      <div class="card-body">
        <el-form :inline="true" style="margin-bottom:16px">
          <el-form-item label="批次编号"><el-input v-model="filters.batchNo" placeholder="搜索批次编号" size="small"></el-input></el-form-item>
          <el-form-item label="质检类型">
            <el-select v-model="filters.type" placeholder="全部" clearable size="small">
              <el-option label="来料质检" value="来料质检"></el-option>
              <el-option label="成品质检" value="成品质检"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="结果">
            <el-select v-model="filters.result" placeholder="全部" clearable size="small">
              <el-option label="PASS" value="PASS"></el-option>
              <el-option label="FAIL" value="FAIL"></el-option>
              <el-option label="PENDING" value="PENDING"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item><el-button type="primary" size="small" @click="search">搜索</el-button></el-form-item>
        </el-form>
        <el-table :data="records" stripe>
          <el-table-column prop="batchNo" label="批次编号" width="180"></el-table-column>
          <el-table-column prop="type" label="质检类型" width="100"></el-table-column>
          <el-table-column prop="inspector" label="检验员工" width="120"></el-table-column>
          <el-table-column label="结果" width="80">
            <template slot-scope="s"><span :class="'tag tag-' + (s.row.result === 'PASS' ? 'green' : s.row.result === 'FAIL' ? 'red' : 'gray')">{{ s.row.result }}</span></template>
          </el-table-column>
          <el-table-column prop="time" label="检验时间" width="180"></el-table-column>
          <el-table-column prop="remark" label="备注"></el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { getInspectionRecords } from '../api';
export default {
  data() {
    return {
      records: [],
      filters: { batchNo: '', type: '', result: '' }
    };
  },
  created() { this.search(); },
  methods: {
    search() { getInspectionRecords(this.filters).then(res => { if (res.code === 200) this.records = res.data; }); }
  }
};
</script>
