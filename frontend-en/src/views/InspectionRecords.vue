<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">Home</a> / <span>Inspection Records</span></div>
    <div style="margin-bottom:20px"><h1 style="font-size:22px;margin-bottom:4px">Inspection Records Archive</h1><p style="color:#6f7e92;font-size:13px">Historical incoming and finished inspection records</p></div>
    <div class="card"><div class="card-body">
      <el-form :inline="true" style="margin-bottom:16px">
        <el-form-item label="Batch No."><el-input v-model="filters.batchNo" placeholder="Search batch no." size="small"></el-input></el-form-item>
        <el-form-item label="Type">
          <el-select v-model="filters.type" placeholder="All" clearable size="small">
            <el-option label="Incoming" value="Incoming Inspection"></el-option>
            <el-option label="Finished" value="Finished Inspection"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Result">
          <el-select v-model="filters.result" placeholder="All" clearable size="small">
            <el-option label="PASS" value="PASS"></el-option>
            <el-option label="FAIL" value="FAIL"></el-option>
            <el-option label="PENDING" value="PENDING"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" size="small" @click="search">Search</el-button></el-form-item>
      </el-form>
      <el-table :data="pagedRecords" stripe>
        <el-table-column prop="batchNo" label="Batch No." width="180"></el-table-column>
        <el-table-column prop="type" label="Type" width="180"></el-table-column>
        <el-table-column prop="inspector" label="Inspector" width="180"></el-table-column>
        <el-table-column label="Result" width="120"><template slot-scope="s"><span :class="'tag tag-' + (s.row.result === 'PASS' ? 'green' : s.row.result === 'FAIL' ? 'red' : 'gray')">{{ s.row.result }}</span></template></el-table-column>
        <el-table-column prop="time" label="Time" width="240"></el-table-column>
        <el-table-column prop="remark" label="Remarks"></el-table-column>
      </el-table>
      <div style="display:flex;justify-content:flex-end;margin-top:16px" v-if="records.length > 0">
        <el-pagination
          background
          :current-page="page"
          :page-sizes="pageSizes"
          :page-size="pageSize"
          :total="records.length"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handlePageChange">
        </el-pagination>
      </div>
    </div></div>
  </div>
</template>

<script>
import { getInspectionRecords } from '../api';
export default {
  data() {
    return {
      records: [],
      filters: { batchNo: '', type: '', result: '' },
      page: 1,
      pageSize: 10,
      pageSizes: [5, 10, 20, 50]
    };
  },
  computed: {
    pagedRecords() {
      const start = (this.page - 1) * this.pageSize;
      return this.records.slice(start, start + this.pageSize);
    }
  },
  created() { this.search(); },
  methods: {
    search() {
      getInspectionRecords(this.filters).then(res => {
        if (res.code === 200) {
          this.records = res.data;
          this.page = 1;
        }
      });
    },
    handleSizeChange(size) {
      this.pageSize = size;
      this.page = 1;
    },
    handlePageChange(page) {
      this.page = page;
    }
  }
};
</script>
