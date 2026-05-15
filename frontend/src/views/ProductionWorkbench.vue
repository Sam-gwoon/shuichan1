<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <span>生产工序管理</span></div>
    <div style="margin-bottom:20px">
      <h1 style="font-size:22px;margin-bottom:4px">生产工序管理</h1>
      <p style="color:#6f7e92;font-size:13px">已通过来料质检的批次列表，点击进入工序录入</p>
    </div>
    <div class="card">
      <div class="card-body">
        <el-table :data="workList" stripe>
          <el-table-column prop="batchNo" label="批次编号" width="180"><template slot-scope="s"><strong>{{ s.row.batchNo }}</strong></template></el-table-column>
          <el-table-column prop="productType" label="产品类型"></el-table-column>
          <el-table-column label="来料质检">
            <template slot-scope="s"><span class="tag tag-green">{{ s.row.incomingInspection | capitalize }}</span></template>
          </el-table-column>
          <el-table-column label="生产状态">
            <template slot-scope="s"><span :class="'tag tag-' + (s.row.productionStatus === 'completed' ? 'blue' : 'orange')">{{ s.row.productionStatus | capitalize }}</span></template>
          </el-table-column>
          <el-table-column label="工序进度">
            <template slot-scope="s">{{ s.row.processSteps || '-' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template slot-scope="s">
              <el-button type="primary" size="small" @click="$router.push('/batches/' + s.row.id + '?tab=process')">录入工序</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="workList.length === 0" style="text-align:center;padding:40px;color:#6f7e92">暂无待加工批次</div>
      </div>
    </div>
  </div>
</template>

<script>
import { getBatches } from '../api';
export default {
  data() { return { batches: [] }; },
  computed: {
    workList() {
      return this.batches.filter(b =>
        b.incomingInspection === 'PASS' &&
        b.releaseStatus === 'unreleased'
      );
    }
  },
  created() { this.load(); },
  methods: {
    load() { getBatches().then(res => { if (res.code === 200) this.batches = res.data; }); }
  }
};
</script>
