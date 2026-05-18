<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">Home</a> / <span>Release Management</span></div>
    <div style="margin-bottom:20px"><h1 style="font-size:22px;margin-bottom:4px">Release Management</h1><p style="color:#6f7e92;font-size:13px">Manage batch release approvals and released records</p></div>

    <div class="grid-2" style="margin-bottom:20px">
      <div class="card"><div class="card-body" style="display:flex;align-items:center;gap:16px">
        <div style="width:56px;height:56px;border-radius:14px;background:#fffbeb;color:#f59e0b;display:flex;align-items:center;justify-content:center;font-size:24px"><i class="el-icon-time"></i></div>
        <div><div style="font-size:12px;color:#6f7e92">Pending Release</div><div style="font-size:24px;font-weight:700">{{ stats.pendingRelease || 0 }}</div></div>
      </div></div>
      <div class="card"><div class="card-body" style="display:flex;align-items:center;gap:16px">
        <div style="width:56px;height:56px;border-radius:14px;background:#f0fdf4;color:#10b981;display:flex;align-items:center;justify-content:center;font-size:24px"><i class="el-icon-circle-check"></i></div>
        <div><div style="font-size:12px;color:#6f7e92">Released</div><div style="font-size:24px;font-weight:700">{{ stats.released || 0 }}</div></div>
      </div></div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="Pending Release" name="pending">
        <div class="card"><div class="card-body">
          <el-table :data="pagedPendingList" stripe>
            <el-table-column prop="batchNo" label="Batch No." width="180"><template slot-scope="s"><strong>{{ s.row.batchNo }}</strong></template></el-table-column>
            <el-table-column prop="productType" label="Product Type"></el-table-column>
            <el-table-column label="Finished Inspection"><template slot-scope="s"><span class="tag tag-green">{{ s.row.finishedInspection }}</span></template></el-table-column>
            <el-table-column label="Process Steps"><template slot-scope="s"><span :class="s.row.processSteps === '3/3' ? 'tag tag-green' : 'tag tag-orange'">{{ s.row.processSteps }}</span></template></el-table-column>
            <el-table-column label="Action"><template slot-scope="s"><el-button type="primary" size="small" @click="doRelease(s.row)">Release</el-button></template></el-table-column>
          </el-table>
          <div v-if="pendingList.length === 0" style="text-align:center;padding:40px;color:#6f7e92">No batches pending release</div>
          <div style="display:flex;justify-content:flex-end;margin-top:16px" v-if="pendingList.length > 0">
            <el-pagination
              background
              :current-page="pendingPage"
              :page-sizes="pageSizes"
              :page-size="pendingPageSize"
              :total="pendingList.length"
              layout="total, sizes, prev, pager, next"
              @size-change="handlePendingSizeChange"
              @current-change="handlePendingPageChange">
            </el-pagination>
          </div>
        </div></div>
      </el-tab-pane>
      <el-tab-pane label="Released Batches" name="released">
        <div class="card"><div class="card-body">
          <el-table :data="pagedReleasedList" stripe>
            <el-table-column prop="batchNo" label="Batch No." width="180"></el-table-column>
            <el-table-column prop="productType" label="Product Type"></el-table-column>
            <el-table-column prop="releaseTime" label="Release Time" width="180"></el-table-column>
          </el-table>
          <div style="display:flex;justify-content:flex-end;margin-top:16px" v-if="releasedList.length > 0">
            <el-pagination
              background
              :current-page="releasedPage"
              :page-sizes="pageSizes"
              :page-size="releasedPageSize"
              :total="releasedList.length"
              layout="total, sizes, prev, pager, next"
              @size-change="handleReleasedSizeChange"
              @current-change="handleReleasedPageChange">
            </el-pagination>
          </div>
        </div></div>
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
      releasedList: [],
      pendingPage: 1,
      pendingPageSize: 10,
      releasedPage: 1,
      releasedPageSize: 10,
      pageSizes: [5, 10, 20, 50]
    };
  },
  computed: {
    pagedPendingList() {
      const start = (this.pendingPage - 1) * this.pendingPageSize;
      return this.pendingList.slice(start, start + this.pendingPageSize);
    },
    pagedReleasedList() {
      const start = (this.releasedPage - 1) * this.releasedPageSize;
      return this.releasedList.slice(start, start + this.releasedPageSize);
    }
  },
  created() { this.load(); },
  methods: {
    load() {
      getReleaseStats().then(res => { if (res.code === 200) this.stats = res.data; });
      getPendingReleases().then(res => {
        if (res.code === 200) {
          this.pendingList = res.data;
          this.pendingPage = 1;
        }
      });
      getReleasedList().then(res => {
        if (res.code === 200) {
          this.releasedList = res.data;
          this.releasedPage = 1;
        }
      });
    },
    handlePendingSizeChange(size) {
      this.pendingPageSize = size;
      this.pendingPage = 1;
    },
    handlePendingPageChange(page) {
      this.pendingPage = page;
    },
    handleReleasedSizeChange(size) {
      this.releasedPageSize = size;
      this.releasedPage = 1;
    },
    handleReleasedPageChange(page) {
      this.releasedPage = page;
    },
    doRelease(row) {
      this.$confirm('Confirm release of batch ' + row.batchNo + '?', 'Confirm', { type: 'info' }).then(() => {
        const user = JSON.parse(localStorage.getItem('user'));
        executeRelease(row.batchId, user.userId).then(res => {
          if (res.code === 200) { this.$message.success('Release successful — trace code generated'); this.load(); }
          else this.$message.error(res.message);
        });
      }).catch(() => {});
    }
  }
};
</script>
