<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">Home</a> / <span>Traceability Center</span></div>
    <div style="margin-bottom:20px"><h1 style="font-size:22px;margin-bottom:4px">Traceability Center</h1><p style="color:#6f7e92;font-size:13px">Manage batches with generated trace codes and public queries</p></div>
    <div class="card"><div class="card-body">
      <el-table :data="records" stripe>
        <el-table-column prop="batchNo" label="Batch No." width="180"></el-table-column>
        <el-table-column prop="productType" label="Product Type"></el-table-column>
        <el-table-column prop="traceCode" label="Trace Code" width="200"><template slot-scope="s"><strong>{{ s.row.traceCode }}</strong></template></el-table-column>
        <el-table-column prop="generateTime" label="Generated At" width="180"></el-table-column>
        <el-table-column label="Public" width="100"><template slot-scope="s"><span :class="s.row.isPublic ? 'tag tag-green' : 'tag tag-gray'">{{ s.row.isPublic ? 'Open' : 'Closed' }}</span></template></el-table-column>
        <el-table-column label="Action">
          <template slot-scope="s">
            <el-button type="text" @click="$router.push('/batches/' + s.row.batchId)">Details</el-button>
            <el-button type="text" @click="$router.push('/public?code=' + s.row.traceCode)">Public Page</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div></div>
  </div>
</template>

<script>
import { getTraceabilityRecords } from '../api';
export default {
  data() { return { records: [] }; },
  created() { getTraceabilityRecords().then(res => { if (res.code === 200) this.records = res.data; }); }
};
</script>
