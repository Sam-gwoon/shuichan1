<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <a @click="$router.push('/batches')">批次列表</a> / <span>{{ batch.batchNo }} 详情</span></div>

    <!-- 状态条 -->
    <div class="status-ribbon" v-if="batch.batchNo">
      <div class="step done"><div class="dot"><i class="el-icon-check"></i></div><span>已登记</span></div><div class="line done"></div>
      <div class="step" :class="incStepClass"><div class="dot"><i :class="incStepIcon"></i></div><span>来料质检</span></div><div class="line" :class="incStepClass"></div>
      <div class="step" :class="prodStepClass"><div class="dot"><i :class="prodStepIcon"></i></div><span>生产加工</span></div><div class="line" :class="prodStepClass"></div>
      <div class="step" :class="finStepClass"><div class="dot"><i :class="finStepIcon"></i></div><span>成品质检</span></div><div class="line" :class="releaseStepClass"></div>
      <div class="step" :class="releaseStepClass"><div class="dot"><i :class="releaseStepIcon"></i></div><span>{{ batch.releaseStatus === 'released' ? '已放行' : '待放行' }}</span></div><div class="line"></div>
      <div class="step"><div class="dot"><i class="el-icon-link"></i></div><span>追溯码</span></div>
    </div>

    <!-- 标题栏 -->
    <div style="display:flex;align-items:flex-start;justify-content:space-between;margin-bottom:20px">
      <div>
        <h1 style="font-size:24px">{{ batch.batchNo }}</h1>
        <p style="color:#6f7e92;font-size:13px" v-if="batch.batch">{{ batch.batch.productType }}批次 · 来源：{{ batch.batch.source }} · 登记：{{ batch.batch.createTime }}</p>
      </div>
      <div style="display:flex;gap:10px">
        <el-button v-if="canRelease" type="primary" size="small" @click="doRelease">执行放行</el-button>
        <el-button v-if="canEditBatch" type="default" size="small" @click="showBatchEditDialog">编辑批次信息</el-button>
        <el-button v-if="canDeleteBatch" type="danger" size="small" @click="doDeleteBatch">删除批次</el-button>
      </div>
    </div>

    <!-- 信息卡片 -->
    <div class="info-row" v-if="batch.batch">
      <div class="info-cell"><span class="label">生产状态</span><span class="tag" :class="batch.batch.productionStatus === 'completed' ? 'tag-blue' : batch.batch.productionStatus === 'processing' ? 'tag-orange' : 'tag-gray'">{{ batch.batch.productionStatus | capitalize }}</span></div>
      <div class="info-cell"><span class="label">来料质检结果</span>
        <span class="tag" :class="incResultClass">{{ incResultText | capitalize }}</span>
      </div>
      <div class="info-cell"><span class="label">成品质检结果</span>
        <span class="tag" :class="finResultClass">{{ finResultText | capitalize }}</span>
      </div>
      <div class="info-cell"><span class="label">放行状态</span>
        <span class="tag" :class="batch.batch.releaseStatus === 'released' ? 'tag-green' : 'tag-orange'">{{ batch.batch.releaseStatus === 'released' ? '已放行' : '待放行' }}</span>
      </div>
    </div>

    <!-- Tabs -->
    <el-tabs v-model="activeTab" style="margin-top:10px">
      <el-tab-pane label="总览概况" name="overview">
        <div class="grid-2" v-if="batch.batch">
          <div class="card">
            <div class="card-header"><h3>基本信息</h3></div>
            <div class="card-body">
              <table style="width:100%">
                <tr v-for="row in basicInfo" :key="row.label">
                  <td style="width:140px;color:#6f7e92;padding:6px 0">{{ row.label }}</td>
                  <td style="padding:6px 0"><strong>{{ row.value }}</strong></td>
                </tr>
              </table>
            </div>
          </div>
          <div>
            <div class="card" style="margin-bottom:20px">
              <div class="card-header"><h3>状态时间线</h3></div>
              <div class="card-body">
                <div class="timeline">
                  <div v-for="(item, i) in history" :key="i" class="t-item" :class="i === history.length - 1 ? 'current' : 'done'">
                    <div class="t-dot"><i :class="i === history.length - 1 ? 'el-icon-arrow-right' : 'el-icon-check'"></i></div>
                    <div class="t-title">{{ item.action }} <span class="tag tag-blue" style="font-size:11px" v-if="item.result">{{ item.result | capitalize }}</span></div>
                    <div class="t-time" v-if="item.time">{{ item.time }}</div>
                    <div class="t-desc">{{ item.person }} {{ item.remark }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="来料与质检" name="inspection">
        <div class="grid-2" v-if="batch.batch">
          <div class="card">
            <div class="card-header"><h3>原料收货详情</h3></div>
            <div class="card-body">
              <table style="width:100%">
                <tr><td style="width:140px;color:#6f7e92;padding:6px 0">原料类型</td><td>{{ batch.batch.rawMaterialType }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">来源/供应商</td><td>{{ batch.batch.source }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">产地</td><td>{{ batch.batch.origin }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">到货时间</td><td>{{ batch.batch.receiptTime }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">数量</td><td>{{ batch.batch.quantity }} kg</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">证书编号</td><td>{{ batch.batch.certificateNo }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">备注</td><td>{{ batch.batch.remark }}</td></tr>
              </table>
            </div>
          </div>
          <div class="card" v-if="batch.incomingInspection">
            <div class="card-header"><div><h3>来料质检记录</h3><p>此批次可进入加工环节</p></div><span class="tag tag-green" v-if="batch.incomingInspection.inspection.result === 'PASS'">{{ batch.incomingInspection.inspection.result | capitalize }}</span></div>
            <div class="card-body">
              <table style="width:100%">
                <tr><td style="width:140px;color:#6f7e92;padding:6px 0">检验员工</td><td>{{ batch.incomingInspection.inspectorName }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">检验时间</td><td>{{ batch.incomingInspection.inspection.inspectTime }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">检验结果</td><td><span :class="'tag tag-' + (batch.incomingInspection.inspection.result === 'PASS' ? 'green' : 'red')">{{ batch.incomingInspection.inspection.result | capitalize }}</span></td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">处置方式</td><td>{{ batch.incomingInspection.inspection.disposal }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">检验备注</td><td>{{ batch.incomingInspection.inspection.remark }}</td></tr>
              </table>
              <div v-if="batch.incomingInspection.inspection.result === 'PASS'" style="margin-top:16px;padding:12px;background:#f0fdf4;border-radius:8px;border:1px solid #bbf7d0;color:#166534;font-size:13px">
                <i class="el-icon-circle-check" style="margin-right:8px"></i>来料质检已通过，加工权限已开放
              </div>
            </div>
          </div>
          <div v-else class="card">
            <div class="card-header"><h3>来料质检记录</h3></div>
            <div class="card-body"><p style="color:#6f7e92">暂无来料质检记录</p></div>
          </div>
          <div class="card" style="grid-column:1/-1" v-if="batch.finishedInspection">
            <div class="card-header"><div><h3>成品质检记录</h3><p>批次完成后进行的最终检验</p></div><span class="tag tag-green" v-if="batch.finishedInspection.inspection.result === 'PASS'">{{ batch.finishedInspection.inspection.result | capitalize }}</span></div>
            <div class="card-body">
              <table style="width:100%">
                <tr><td style="width:140px;color:#6f7e92;padding:6px 0">检验员工</td><td>{{ batch.finishedInspection.inspectorName }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">检验时间</td><td>{{ batch.finishedInspection.inspection.inspectTime }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">检验结果</td><td><span class="tag tag-green">{{ batch.finishedInspection.inspection.result | capitalize }}</span></td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">检验项目</td><td>{{ batch.finishedInspection.inspection.items }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">备注</td><td>{{ batch.finishedInspection.inspection.remark }}</td></tr>
              </table>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="生产工序" name="process">
        <div class="card" style="margin-bottom:20px">
          <div class="card-body">
            <div class="process-flow">
              <div class="p-step" :class="hasProcessType('sorting') ? 'done' : ''"><div class="p-num"><i v-if="hasProcessType('sorting')" class="el-icon-check"></i><span v-else>1</span></div><strong>分拣工序</strong><span>{{ processCount('sorting') }} 条记录</span></div>
              <div class="p-arrow"><i class="el-icon-arrow-right"></i></div>
              <div class="p-step" :class="hasProcessType('cleaning') ? 'done' : ''"><div class="p-num"><i v-if="hasProcessType('cleaning')" class="el-icon-check"></i><span v-else>2</span></div><strong>清洗工序</strong><span>{{ processCount('cleaning') }} 条记录</span></div>
              <div class="p-arrow"><i class="el-icon-arrow-right"></i></div>
              <div class="p-step" :class="hasProcessType('packaging') ? 'done' : 'active'"><div class="p-num">3</div><strong>包装工序</strong><span>{{ processCount('packaging') }} 条记录</span></div>
            </div>
          </div>
        </div>
        <div class="grid-3">
          <div class="card" v-for="pt in ['sorting','cleaning','packaging']" :key="pt">
            <div class="card-header"><h3>{{ {sorting:'分拣记录',cleaning:'清洗记录',packaging:'包装记录'}[pt] }}</h3></div>
            <div class="card-body">
              <el-table :data="getProcessByType(pt)" stripe>
                <el-table-column prop="equipment" label="设备"></el-table-column>
                <el-table-column prop="recordTime" label="时间"></el-table-column>
                <el-table-column v-if="canAddProcess" label="操作" width="120">
                  <template slot-scope="pr">
                    <el-button type="text" @click="editProcessRecord(pr.row)">编辑</el-button>
                    <el-button type="text" style="color:#ef4444" @click="deleteProcessRecord(pr.row.id)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-button v-if="getProcessByType(pt).length === 0 && canAddProcess" type="text" @click="addProcessRecord(pt)" style="margin-top:8px">+ 添加记录</el-button>
              <el-button v-if="getProcessByType(pt).length > 0 && canAddProcess" type="text" @click="addProcessRecord(pt)" style="margin-top:8px">+ 添加记录</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="追溯与二维码" name="trace">
        <div class="grid-2">
          <div class="card">
            <div class="card-header"><h3>追溯信息</h3></div>
            <div class="card-body">
              <table style="width:100%">
                <tr><td style="width:140px;color:#6f7e92;padding:6px 0">追溯码</td><td><strong>{{ batch.traceability ? batch.traceability.traceCode : '尚未生成（需先放行）' }}</strong></td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">生成时间</td><td>{{ batch.traceability ? batch.traceability.generateTime : '尚未生成' }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">公共查询</td><td><span :class="batch.traceability && batch.traceability.isPublic ? 'tag tag-green' : 'tag tag-gray'">{{ batch.traceability && batch.traceability.isPublic ? '已开放' : '未开放' }}</span></td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">扫码入口</td><td><a :href="'/#/public?code=' + (batch.traceability ? batch.traceability.traceCode : '')" style="color:#2d79ea">{{ batch.traceability ? batch.traceability.qrUrl : '-' }}</a></td></tr>
              </table>
              <div v-if="!batch.traceability" style="margin-top:16px;padding:12px;background:#fffbeb;border-radius:8px;border:1px solid #fde68a;color:#92400e;font-size:13px">
                <i class="el-icon-warning-outline" style="margin-right:8px"></i>请先执行「产品放行」，系统将自动生成追溯码并开放公共查询。
              </div>
            </div>
          </div>
          <div class="card" v-if="batch.traceability">
            <div class="card-header"><h3>批次二维码</h3></div>
            <div class="card-body" style="text-align:center">
              <div style="width:140px;height:140px;border:1px solid #e6edf7;border-radius:12px;display:flex;align-items:center;justify-content:center;margin:0 auto 16px;font-size:48px;color:#d1d5db">
                <i class="el-icon-s-data"></i>
              </div>
              <p style="font-size:13px;color:#6f7e92;margin-bottom:8px">{{ batch.traceability.qrUrl }}</p>
              <el-button size="small" @click="$message.success('已复制链接')">复制链接</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="操作历史" name="history">
        <div class="card">
          <div class="card-header"><h3>操作历史记录</h3></div>
          <div class="card-body">
            <el-table :data="history" stripe>
              <el-table-column prop="action" label="操作"></el-table-column>
              <el-table-column prop="person" label="人员"></el-table-column>
              <el-table-column label="结果/状态">
                <template slot-scope="s"><span class="tag tag-blue" style="font-size:11px">{{ s.row.result | capitalize }}</span></template>
              </el-table-column>
              <el-table-column prop="time" label="时间" width="180"></el-table-column>
              <el-table-column prop="remark" label="备注"></el-table-column>
            </el-table>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog title="编辑批次信息" :visible.sync="batchEditVisible" width="600px">
      <el-form :model="batchEditForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="产品类型"><el-input v-model="batchEditForm.productType"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="原料类型"><el-input v-model="batchEditForm.rawMaterialType"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="来源/供应商"><el-input v-model="batchEditForm.source"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="产地"><el-input v-model="batchEditForm.origin"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="数量(kg)"><el-input v-model="batchEditForm.quantity"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="证书编号"><el-input v-model="batchEditForm.certificateNo"></el-input></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="batchEditForm.remark" type="textarea" rows="3"></el-input></el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="batchEditVisible = false">取消</el-button><el-button type="primary" @click="saveBatchEdit">保存</el-button></div>
    </el-dialog>

    <el-dialog title="编辑生产记录" :visible.sync="processEditVisible" width="400px">
      <el-form :model="processEditForm" label-width="80px">
        <el-form-item label="设备名称"><el-input v-model="processEditForm.equipment"></el-input></el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="processEditVisible = false">取消</el-button><el-button type="primary" @click="saveProcessEdit">保存</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { getBatchDetail, executeRelease, addProductionRecord, updateBatch, deleteBatch, updateProductionRecord, deleteProductionRecord } from '../api';
export default {
  data() {
    return {
      activeTab: 'overview',
      batch: {},
      batchEditVisible: false,
      batchEditForm: {},
      processEditVisible: false,
      processEditForm: { id: null, equipment: '' }
    };
  },
  computed: {
    user() { try { return JSON.parse(localStorage.getItem('user')); } catch(e) { return {}; } },
    canEditBatch() {
      const u = this.user;
      return u.role === 'ADMIN' || u.role === 'PROD_MANAGER';
    },
    canDeleteBatch() {
      const u = this.user;
      return (u.role === 'ADMIN' || u.role === 'PROD_MANAGER') && this.batch.batch && this.batch.batch.releaseStatus !== 'released';
    },
    canRelease() {
      const u = this.user;
      return (u.role === 'ADMIN' || u.role === 'PROD_MANAGER')
        && this.batch.releaseStatus === 'unreleased'
        && this.batch.finishedInspection
        && this.batch.finishedInspection.inspection.result === 'PASS';
    },
    canAddProcess() {
      const u = this.user;
      return u.role === 'ADMIN' || u.role === 'PROD_MANAGER' || u.role === 'OPERATOR';
    },
    basicInfo() {
      const b = this.batch.batch;
      if (!b) return [];
      return [
        { label: '批次编号', value: b.batchNo },
        { label: '产品类型', value: b.productType },
        { label: '原料类型', value: b.rawMaterialType },
        { label: '来源/供应商', value: b.source },
        { label: '产地', value: b.origin },
        { label: '数量', value: b.quantity + ' kg' },
        { label: '证书编号', value: b.certificateNo },
        { label: '登记人员', value: this.batch.operator },
        { label: '备注', value: b.remark }
      ];
    },
    incResultClass() {
      const inc = this.batch.incomingInspection;
      return inc ? (inc.inspection.result === 'PASS' ? 'tag-green' : inc.inspection.result === 'FAIL' ? 'tag-red' : 'tag-gray') : 'tag-gray';
    },
    incResultText() {
      const inc = this.batch.incomingInspection;
      return inc ? inc.inspection.result : 'PENDING';
    },
    finResultClass() {
      const fin = this.batch.finishedInspection;
      return fin ? (fin.inspection.result === 'PASS' ? 'tag-green' : fin.inspection.result === 'FAIL' ? 'tag-red' : 'tag-gray') : 'tag-gray';
    },
    finResultText() {
      const fin = this.batch.finishedInspection;
      return fin ? fin.inspection.result : 'PENDING';
    },
    incStepClass() {
      const inc = this.batch.incomingInspection;
      return inc && inc.inspection.result === 'PASS' ? 'done' : inc && inc.inspection.result === 'FAIL' ? 'done' : 'current';
    },
    incStepIcon() {
      const inc = this.batch.incomingInspection;
      return inc && inc.inspection.result === 'PASS' ? 'el-icon-check' : 'el-icon-close';
    },
    prodStepClass() {
      return this.batch.batch && this.batch.batch.productionStatus === 'completed' ? 'done' : 'current';
    },
    prodStepIcon() {
      return this.batch.batch && this.batch.batch.productionStatus === 'completed' ? 'el-icon-check' : 'el-icon-more';
    },
    finStepClass() {
      const fin = this.batch.finishedInspection;
      return fin && fin.inspection.result === 'PASS' ? 'done' : 'current';
    },
    finStepIcon() {
      const fin = this.batch.finishedInspection;
      return fin && fin.inspection.result === 'PASS' ? 'el-icon-check' : 'el-icon-more';
    },
    releaseStepClass() {
      return this.batch.batch && this.batch.batch.releaseStatus === 'released' ? 'done' : 'current';
    },
    releaseStepIcon() {
      return this.batch.batch && this.batch.batch.releaseStatus === 'released' ? 'el-icon-check' : 'el-icon-upload2';
    },
    history() {
      return this.batch.history || [];
    }
  },
  created() {
    this.loadDetail();
  },
  methods: {
    loadDetail() {
      const id = this.$route.params.id;
      getBatchDetail(id).then(res => {
        if (res.code === 200) this.batch = res.data;
      });
    },
    doRelease() {
      this.$confirm('确认对该批次执行放行操作？', '提示', { type: 'info' }).then(() => {
        const user = JSON.parse(localStorage.getItem('user'));
        executeRelease(this.$route.params.id, user.userId).then(res => {
          if (res.code === 200) {
            this.$message.success('放行成功，追溯码已生成');
            this.loadDetail();
          } else this.$message.error(res.message);
        });
      }).catch(() => {});
    },
    hasProcessType(type) {
      return (this.batch.productionRecords || []).some(r => r.processType === type);
    },
    processCount(type) {
      return (this.batch.productionRecords || []).filter(r => r.processType === type).length;
    },
    getProcessByType(type) {
      return (this.batch.productionRecords || []).filter(r => r.processType === type);
    },
    addProcessRecord(type) {
      const user = JSON.parse(localStorage.getItem('user'));
      const typeMap = { sorting: '分拣', cleaning: '清洗', packaging: '包装' };
      this.$prompt('请输入设备名称', '添加' + typeMap[type] + '记录', {}).then(({ value }) => {
        addProductionRecord({
          batchId: this.$route.params.id,
          processType: type,
          operatorId: user.userId,
          equipment: value
        }).then(res => {
          if (res.code === 200) {
            this.$message.success('记录已添加');
            this.loadDetail();
          }
        });
      }).catch(() => {});
    },
    showBatchEditDialog() {
      const b = this.batch.batch;
      this.batchEditForm = {
        productType: b.productType || '',
        rawMaterialType: b.rawMaterialType || '',
        source: b.source || '',
        origin: b.origin || '',
        quantity: b.quantity || '',
        certificateNo: b.certificateNo || '',
        remark: b.remark || ''
      };
      this.batchEditVisible = true;
    },
    saveBatchEdit() {
      updateBatch(this.$route.params.id, this.batchEditForm).then(res => {
        if (res.code === 200) { this.$message.success('批次信息已更新'); this.batchEditVisible = false; this.loadDetail(); }
        else this.$message.error(res.message);
      });
    },
    doDeleteBatch() {
      this.$confirm('确认删除该批次及其所有关联记录？此操作不可恢复。', '警告', { type: 'warning' }).then(() => {
        deleteBatch(this.$route.params.id).then(res => {
          if (res.code === 200) { this.$message.success('批次已删除'); this.$router.push('/batches'); }
          else this.$message.error(res.message);
        });
      }).catch(() => {});
    },
    editProcessRecord(row) {
      this.processEditForm = { id: row.id, equipment: row.equipment };
      this.processEditVisible = true;
    },
    saveProcessEdit() {
      updateProductionRecord(this.processEditForm.id, { equipment: this.processEditForm.equipment }).then(res => {
        if (res.code === 200) { this.$message.success('记录已更新'); this.processEditVisible = false; this.loadDetail(); }
        else this.$message.error(res.message);
      });
    },
    deleteProcessRecord(id) {
      this.$confirm('确认删除该生产记录？', '提示', { type: 'warning' }).then(() => {
        deleteProductionRecord(id).then(res => {
          if (res.code === 200) { this.$message.success('记录已删除'); this.loadDetail(); }
          else this.$message.error(res.message);
        });
      }).catch(() => {});
    }
  }
};
</script>
