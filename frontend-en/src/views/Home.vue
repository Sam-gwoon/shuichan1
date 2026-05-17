<template>
  <div>
    <div class="breadcrumb"><span>Dashboard</span></div>

    <div class="kpi-grid">
      <div class="kpi-card">
        <div class="kpi-icon blue"><i class="el-icon-document-checked"></i></div>
        <div><div class="kpi-title">Pending Incoming Inspection</div><div class="kpi-value">{{ stats.pendingIncoming }}</div><div class="kpi-note">Awaiting incoming checks</div></div>
      </div>
      <div class="kpi-card">
        <div class="kpi-icon green"><i class="el-icon-circle-check"></i></div>
        <div><div class="kpi-title">Pending Finished Inspection</div><div class="kpi-value">{{ stats.pendingFinished }}</div><div class="kpi-note">Completed batches waiting</div></div>
      </div>
      <div class="kpi-card">
        <div class="kpi-icon orange"><i class="el-icon-warning-outline"></i></div>
        <div><div class="kpi-title">Pending Release</div><div class="kpi-value">{{ stats.pendingRelease }}</div><div class="kpi-note">Ready after final PASS</div></div>
      </div>
      <div class="kpi-card">
        <div class="kpi-icon purple"><i class="el-icon-s-marketing"></i></div>
        <div><div class="kpi-title">Released Batches</div><div class="kpi-value">{{ stats.released }}</div><div class="kpi-note">Public traceability ready</div></div>
      </div>
    </div>

    <div style="display:grid;grid-template-columns:1.6fr 0.4fr;gap:20px;margin-bottom:20px">
      <div class="card">
        <div class="card-header"><div><h3>Process Workflow</h3><p>Complete supervision flow from intake to public query</p></div></div>
        <div class="card-body">
          <div class="workflow-flow">
            <div class="workflow-step"><div class="workflow-step__badge">1</div><i class="el-icon-truck" style="font-size:18px;color:#2d79ea;display:block;margin-bottom:8px"></i><strong>Raw Material Intake</strong><span>Material receiving</span></div>
            <div class="workflow-arrow"><i class="el-icon-arrow-right"></i></div>
            <div class="workflow-step"><div class="workflow-step__badge">2</div><i class="el-icon-document-checked" style="font-size:18px;color:#2d79ea;display:block;margin-bottom:8px"></i><strong>Incoming Inspection</strong><span>Quality check</span></div>
            <div class="workflow-arrow"><i class="el-icon-arrow-right"></i></div>
            <div class="workflow-step"><div class="workflow-step__badge">3</div><i class="el-icon-setting" style="font-size:18px;color:#2d79ea;display:block;margin-bottom:8px"></i><strong>Production</strong><span>Sort/Clean/Pack</span></div>
            <div class="workflow-arrow"><i class="el-icon-arrow-right"></i></div>
            <div class="workflow-step"><div class="workflow-step__badge">4</div><i class="el-icon-circle-check" style="font-size:18px;color:#2d79ea;display:block;margin-bottom:8px"></i><strong>Finished Inspection</strong><span>Final check</span></div>
            <div class="workflow-arrow"><i class="el-icon-arrow-right"></i></div>
            <div class="workflow-step"><div class="workflow-step__badge">5</div><i class="el-icon-circle-check" style="font-size:18px;color:#2d79ea;display:block;margin-bottom:8px"></i><strong>Release</strong><span>Approval</span></div>
            <div class="workflow-arrow"><i class="el-icon-arrow-right"></i></div>
            <div class="workflow-step"><div class="workflow-step__badge">6</div><i class="el-icon-view" style="font-size:18px;color:#2d79ea;display:block;margin-bottom:8px"></i><strong>Traceability</strong><span>Public query</span></div>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="card-header"><div><h3>Quick Search</h3><p>Enter batch number to navigate to details</p></div></div>
        <div class="card-body">
          <div style="display:flex;gap:10px">
            <el-input v-model="searchBatchNo" placeholder="Batch No. e.g. BT-20250512-003" style="flex:1"></el-input>
            <el-button type="primary" @click="searchBatch">Search</el-button>
          </div>
          <div style="margin-top:14px;display:flex;gap:8px;flex-wrap:wrap">
            <el-tag v-for="b in recentBatches" :key="b.id" style="cursor:pointer" type="info" @click="$router.push('/batches/' + b.id)">{{ b.batchNo }}</el-tag>
          </div>
        </div>
      </div>
    </div>

    <div class="grid-2" style="margin-bottom:20px">
      <div class="card">
        <div class="card-header"><div><h3>Alerts</h3><p>Lightweight warnings based on batch & inspection data</p></div></div>
        <div class="card-body">
          <div class="warning-item">
            <div class="warning-item__main"><strong>Incoming Inspection FAIL</strong><span>Batches blocked at incoming inspection.</span></div>
            <span class="tag tag-red">{{ stats.failedIncoming }}</span>
          </div>
          <div class="warning-item">
            <div class="warning-item__main"><strong>Completed — No Finished Inspection</strong><span>Completed batches still needing final inspection.</span></div>
            <span class="tag tag-orange">{{ stats.noFinishedInspection }}</span>
          </div>
          <div class="warning-item">
            <div class="warning-item__main"><strong>Finished Inspection FAIL</strong><span>Final inspection failed, release blocked.</span></div>
            <span class="tag tag-gray">{{ stats.failedFinished }}</span>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="card-header"><div><h3>Status Summary</h3><p>Unified status across batch, inspection, release & traceability</p></div></div>
        <div class="card-body">
          <div class="stats-line" v-for="(item, idx) in statusLines" :key="idx">
            <div class="stats-line__head"><strong>{{ item.label }}</strong><span>{{ item.count }}</span></div>
            <div class="stats-line__bar"><div class="stats-line__fill" :style="{width: item.pct + '%', background: item.color}"></div></div>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-header">
        <div><h3>Recent Batches</h3><p>Quick overview and status filtering</p></div>
        <el-button type="primary" size="small" @click="$router.push('/batches')">View All</el-button>
      </div>
      <div class="card-body">
        <el-table :data="recentBatches" stripe>
          <el-table-column prop="batchNo" label="Batch No." width="180"><template slot-scope="s"><strong>{{ s.row.batchNo }}</strong></template></el-table-column>
          <el-table-column prop="productType" label="Product Type"></el-table-column>
          <el-table-column prop="incomingInspection" label="Incoming Inspection">
            <template slot-scope="s"><span :class="'tag tag-' + (s.row.incomingInspection === 'PASS' ? 'green' : s.row.incomingInspection === 'FAIL' ? 'red' : 'gray')">{{ s.row.incomingInspection }}</span></template>
          </el-table-column>
          <el-table-column prop="productionStatus" label="Production Status">
            <template slot-scope="s"><span :class="'tag tag-' + (s.row.productionStatus === 'completed' ? 'blue' : s.row.productionStatus === 'processing' ? 'orange' : 'gray')">{{ s.row.productionStatus }}</span></template>
          </el-table-column>
          <el-table-column prop="updateTime" label="Updated" width="180"></el-table-column>
          <el-table-column label="Action" width="100">
            <template slot-scope="s"><el-button type="text" @click="$router.push('/batches/' + s.row.id)">Details</el-button></template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { getDashboardStats, getRecentBatches, getBatches } from '../api';
export default {
  data() {
    return {
      stats: { pendingIncoming: 0, pendingFinished: 0, pendingRelease: 0, released: 0, failedIncoming: 0, noFinishedInspection: 0, failedFinished: 0 },
      recentBatches: [],
      searchBatchNo: ''
    };
  },
  computed: {
    statusLines() {
      const s = this.stats;
      const total = s.pendingIncoming + s.pendingFinished + s.pendingRelease + s.released || 1;
      return [
        { label: 'Pending Incoming Inspection', count: s.pendingIncoming, pct: Math.round(s.pendingIncoming / total * 100), color: '#2d79ea' },
        { label: 'Pending Finished Inspection', count: s.pendingFinished, pct: Math.round(s.pendingFinished / total * 100), color: '#f59e0b' },
        { label: 'Pending Release', count: s.pendingRelease, pct: Math.round(s.pendingRelease / total * 100), color: '#10b981' },
        { label: 'Released', count: s.released, pct: Math.round(s.released / total * 100), color: '#10b981' }
      ];
    }
  },
  created() { this.loadData(); },
  methods: {
    loadData() {
      getDashboardStats().then(res => { if (res.code === 200) this.stats = res.data; });
      getRecentBatches().then(res => { if (res.code === 200) this.recentBatches = res.data; });
    },
    searchBatch() {
      if (!this.searchBatchNo.trim()) return;
      getBatches({ batchNo: this.searchBatchNo.trim() }).then(res => {
        if (res.code === 200 && res.data && res.data.length > 0) {
          this.$router.push('/batches/' + res.data[0].id);
        } else {
          this.$message.warning('Batch not found');
        }
      });
    }
  }
};
</script>
