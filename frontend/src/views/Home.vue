<template>
  <div>
    <div class="breadcrumb"><span>首页 Dashboard</span></div>

    <!-- KPI 卡片 -->
    <div class="kpi-grid">
      <div class="kpi-card">
        <div class="kpi-icon blue"><i class="el-icon-document-checked"></i></div>
        <div>
          <div class="kpi-title">待来料质检</div>
          <div class="kpi-value">{{ stats.pendingIncoming }}</div>
          <div class="kpi-note">Awaiting incoming checks</div>
        </div>
      </div>
      <div class="kpi-card">
        <div class="kpi-icon green"><i class="el-icon-circle-check"></i></div>
        <div>
          <div class="kpi-title">待成品质检</div>
          <div class="kpi-value">{{ stats.pendingFinished }}</div>
          <div class="kpi-note">Completed batches waiting</div>
        </div>
      </div>
      <div class="kpi-card">
        <div class="kpi-icon orange"><i class="el-icon-warning-outline"></i></div>
        <div>
          <div class="kpi-title">待放行</div>
          <div class="kpi-value">{{ stats.pendingRelease }}</div>
          <div class="kpi-note">Ready after final PASS</div>
        </div>
      </div>
      <div class="kpi-card">
        <div class="kpi-icon purple"><i class="el-icon-s-marketing"></i></div>
        <div>
          <div class="kpi-title">已放行批次</div>
          <div class="kpi-value">{{ stats.released }}</div>
          <div class="kpi-note">Public traceability ready</div>
        </div>
      </div>
    </div>

    <!-- 业务流程 + 快速查询 -->
    <div style="display:grid;grid-template-columns:1.6fr 0.4fr;gap:20px;margin-bottom:20px">
      <div class="card">
        <div class="card-header">
          <div><h3>业务流程</h3><p>从原料入库到最终查询的完整监管流程</p></div>
        </div>
        <div class="card-body">
          <div class="workflow-flow">
            <div class="workflow-step"><div class="workflow-step__badge">1</div><i class="el-icon-truck" style="font-size:18px;color:#2d79ea;display:block;margin-bottom:8px"></i><strong>原料收货</strong><span>Raw material intake</span></div>
            <div class="workflow-arrow"><i class="el-icon-arrow-right"></i></div>
            <div class="workflow-step"><div class="workflow-step__badge">2</div><i class="el-icon-document-checked" style="font-size:18px;color:#2d79ea;display:block;margin-bottom:8px"></i><strong>来料质检</strong><span>Incoming inspection</span></div>
            <div class="workflow-arrow"><i class="el-icon-arrow-right"></i></div>
            <div class="workflow-step"><div class="workflow-step__badge">3</div><i class="el-icon-setting" style="font-size:18px;color:#2d79ea;display:block;margin-bottom:8px"></i><strong>生产加工</strong><span>Sorting/Cleaning/Packaging</span></div>
            <div class="workflow-arrow"><i class="el-icon-arrow-right"></i></div>
            <div class="workflow-step"><div class="workflow-step__badge">4</div><i class="el-icon-circle-check" style="font-size:18px;color:#2d79ea;display:block;margin-bottom:8px"></i><strong>成品质检</strong><span>Finished inspection</span></div>
            <div class="workflow-arrow"><i class="el-icon-arrow-right"></i></div>
            <div class="workflow-step"><div class="workflow-step__badge">5</div><i class="el-icon-circle-check" style="font-size:18px;color:#2d79ea;display:block;margin-bottom:8px"></i><strong>产品放行</strong><span>Release management</span></div>
            <div class="workflow-arrow"><i class="el-icon-arrow-right"></i></div>
            <div class="workflow-step"><div class="workflow-step__badge">6</div><i class="el-icon-view" style="font-size:18px;color:#2d79ea;display:block;margin-bottom:8px"></i><strong>追溯查询</strong><span>Public traceability</span></div>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="card-header"><div><h3>快速查询</h3><p>输入批次编号直接跳转到监管详情</p></div></div>
        <div class="card-body">
          <div style="display:flex;gap:10px">
            <el-input v-model="searchBatchNo" placeholder="输入批次编号，如 BT-20250512-003" style="flex:1"></el-input>
            <el-button type="primary" @click="searchBatch">搜索</el-button>
          </div>
          <div style="margin-top:14px;display:flex;gap:8px;flex-wrap:wrap">
            <el-tag v-for="b in recentBatches" :key="b.id" style="cursor:pointer" type="info" @click="$router.push('/batches/' + b.id)">
              {{ b.batchNo }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 预警 + 状态汇总 -->
    <div class="grid-2" style="margin-bottom:20px">
      <div class="card">
        <div class="card-header"><div><h3>异常批次预警</h3><p>基于当前批次、质检、放行数据的轻量预警</p></div></div>
        <div class="card-body">
          <div class="warning-item">
            <div class="warning-item__main"><strong>来料质检 FAIL</strong><span>Batches blocked at incoming inspection.</span></div>
            <span class="tag tag-red">{{ stats.failedIncoming }}</span>
          </div>
          <div class="warning-item">
            <div class="warning-item__main"><strong>已完成但无成品质检</strong><span>Completed batches that still need final inspection.</span></div>
            <span class="tag tag-orange">{{ stats.noFinishedInspection }}</span>
          </div>
          <div class="warning-item">
            <div class="warning-item__main"><strong>成品质检 FAIL</strong><span>Final inspection failed and release is blocked.</span></div>
            <span class="tag tag-gray">{{ stats.failedFinished }}</span>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="card-header"><div><h3>状态汇总</h3><p>基于当前批次、质检、放行、追溯数据的统一状态</p></div></div>
        <div class="card-body">
          <div class="stats-line" v-for="(item, idx) in statusLines" :key="idx">
            <div class="stats-line__head"><strong>{{ item.label }}</strong><span>{{ item.count }}</span></div>
            <div class="stats-line__bar"><div class="stats-line__fill" :style="{width: item.pct + '%', background: item.color}"></div></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最近批次 -->
    <div class="card">
      <div class="card-header">
        <div><h3>最近批次记录</h3><p>快速检索和状态筛选</p></div>
        <el-button type="primary" size="small" @click="$router.push('/batches')">查看全部</el-button>
      </div>
      <div class="card-body">
        <el-table :data="recentBatches" stripe>
          <el-table-column prop="batchNo" label="批次编号" width="180">
            <template slot-scope="scope"><strong>{{ scope.row.batchNo }}</strong></template>
          </el-table-column>
          <el-table-column prop="productType" label="产品类型"></el-table-column>
          <el-table-column prop="incomingInspection" label="来料质检">
            <template slot-scope="scope">
              <span class="tag" :class="scope.row.incomingInspection === 'PASS' ? 'tag-green' : scope.row.incomingInspection === 'FAIL' ? 'tag-red' : 'tag-gray'">{{ scope.row.incomingInspection | capitalize }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="productionStatus" label="生产状态">
            <template slot-scope="scope">
              <span class="tag" :class="scope.row.productionStatus === 'completed' ? 'tag-blue' : scope.row.productionStatus === 'processing' ? 'tag-orange' : 'tag-gray'">{{ scope.row.productionStatus | capitalize }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="updateTime" label="更新时间" width="180"></el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <el-button type="text" @click="$router.push('/batches/' + scope.row.id)">查看详情</el-button>
            </template>
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
        { label: '待来料质检', count: s.pendingIncoming, pct: Math.round(s.pendingIncoming / total * 100), color: '#2d79ea' },
        { label: '待成品质检', count: s.pendingFinished, pct: Math.round(s.pendingFinished / total * 100), color: '#f59e0b' },
        { label: '待放行', count: s.pendingRelease, pct: Math.round(s.pendingRelease / total * 100), color: '#10b981' },
        { label: '已放行', count: s.released, pct: Math.round(s.released / total * 100), color: '#10b981' }
      ];
    }
  },
  created() {
    this.loadData();
  },
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
          this.$message.warning('未找到该批次');
        }
      });
    }
  }
};
</script>
