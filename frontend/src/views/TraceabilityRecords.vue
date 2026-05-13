<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <span>追溯记录中心</span></div>
    <div style="margin-bottom:20px">
      <h1 style="font-size:22px;margin-bottom:4px">追溯记录中心</h1>
      <p style="color:#6f7e92;font-size:13px">已生成追溯码的批次管理与查询</p>
    </div>
    <div class="card">
      <div class="card-body">
        <el-table :data="records" stripe>
          <el-table-column prop="batchNo" label="批次编号" width="180"></el-table-column>
          <el-table-column prop="productType" label="产品类型"></el-table-column>
          <el-table-column prop="traceCode" label="追溯码" width="200"><template slot-scope="s"><strong>{{ s.row.traceCode }}</strong></template></el-table-column>
          <el-table-column prop="generateTime" label="生成时间" width="180"></el-table-column>
          <el-table-column label="公共查询" width="100">
            <template slot-scope="s"><span :class="s.row.isPublic ? 'tag tag-green' : 'tag tag-gray'">{{ s.row.isPublic ? '已开放' : '未开放' }}</span></template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="s">
              <el-button type="text" @click="$router.push('/batches/' + s.row.batchId)">查看详情</el-button>
              <el-button type="text" @click="$router.push('/public?code=' + s.row.traceCode)">公共页</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { getTraceabilityRecords } from '../api';
export default {
  data() { return { records: [] }; },
  created() { getTraceabilityRecords().then(res => { if (res.code === 200) this.records = res.data; }); }
};
</script>
