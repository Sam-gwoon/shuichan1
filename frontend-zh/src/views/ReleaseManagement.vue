<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <span>产品放行管理</span></div>
    <div style="margin-bottom:20px">
      <h1 style="font-size:22px;margin-bottom:4px">产品放行管理</h1>
      <p style="color:#6f7e92;font-size:13px">管理批次放行审批与已放行记录</p>
    </div>

    <div class="grid-2" style="margin-bottom:20px">
      <div class="card">
        <div class="card-body" style="display:flex;align-items:center;gap:16px">
          <div style="width:56px;height:56px;border-radius:14px;background:#fffbeb;color:#f59e0b;display:flex;align-items:center;justify-content:center;font-size:24px"><i class="el-icon-time"></i></div>
          <div><div style="font-size:12px;color:#6f7e92">待放行</div><div style="font-size:24px;font-weight:700">{{ stats.pendingRelease || 0 }}</div></div>
        </div>
      </div>
      <div class="card">
        <div class="card-body" style="display:flex;align-items:center;gap:16px">
          <div style="width:56px;height:56px;border-radius:14px;background:#f0fdf4;color:#10b981;display:flex;align-items:center;justify-content:center;font-size:24px"><i class="el-icon-circle-check"></i></div>
          <div><div style="font-size:12px;color:#6f7e92">已放行</div><div style="font-size:24px;font-weight:700">{{ stats.released || 0 }}</div></div>
        </div>
      </div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="待放行批次" name="pending">
        <div class="card">
          <div class="card-body">
            <el-table :data="pendingList" stripe>
              <el-table-column prop="batchNo" label="批次编号" width="180"><template slot-scope="s"><strong>{{ s.row.batchNo }}</strong></template></el-table-column>
              <el-table-column prop="productType" label="产品类型"></el-table-column>
              <el-table-column label="成品质检">
                <template slot-scope="s"><span class="tag tag-green">{{ s.row.finishedInspection | capitalize }}</span></template>
              </el-table-column>
              <el-table-column label="工序完整性">
                <template slot-scope="s"><span :class="s.row.processSteps === '3/3' ? 'tag tag-green' : 'tag tag-orange'">{{ s.row.processSteps }}</span></template>
              </el-table-column>
              <el-table-column label="操作" v-if="canReleaseRole">
                <template slot-scope="s">
                  <el-button type="primary" size="small" @click="doRelease(s.row)">执行放行</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="pendingList.length === 0" style="text-align:center;padding:40px;color:#6f7e92">暂无待放行批次</div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="已放行批次" name="released">
        <div class="card">
          <div class="card-body">
            <el-table :data="releasedList" stripe>
              <el-table-column prop="batchNo" label="批次编号" width="180"></el-table-column>
              <el-table-column prop="productType" label="产品类型"></el-table-column>
              <el-table-column prop="releaseTime" label="放行时间" width="180"></el-table-column>
            </el-table>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { getReleaseStats, getPendingReleases, getReleasedList, executeRelease } from '../api';
export default {
  data() {
    return {
      activeTab: 'pending',
      stats: { pendingRelease: 0, released: 0 },
      pendingList: [],
      releasedList: []
    };
  },
  computed: {
    canReleaseRole() {
      const u = JSON.parse(localStorage.getItem('user') || '{}');
      return u.role === 'ADMIN' || u.role === 'PROD_MANAGER';
    }
  },
  created() { this.load(); },
  methods: {
    load() {
      getReleaseStats().then(res => { if (res.code === 200) this.stats = res.data; });
      getPendingReleases().then(res => { if (res.code === 200) this.pendingList = res.data; });
      getReleasedList().then(res => { if (res.code === 200) this.releasedList = res.data; });
    },
    doRelease(row) {
      this.$confirm('确认对批次 ' + row.batchNo + ' 执行放行？', '提示', { type: 'info' }).then(() => {
        const user = JSON.parse(localStorage.getItem('user'));
        executeRelease(row.batchId, user.userId).then(res => {
          if (res.code === 200) { this.$message.success('放行成功，追溯码已生成'); this.load(); }
          else this.$message.error(res.message);
        });
      }).catch(() => {});
    }
  }
};
</script>
