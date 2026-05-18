<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">Home</a> / <a @click="$router.push('/batches')">Batch List</a> / <span>{{ batch.batchNo }} Detail</span></div>

    <div class="status-ribbon" v-if="batch.batchNo">
      <div class="step done"><div class="dot"><i class="el-icon-check"></i></div><span>Registered</span></div><div class="line done"></div>
      <div class="step" :class="incStepClass"><div class="dot"><i :class="incStepIcon"></i></div><span>Incoming Inspection</span></div><div class="line" :class="incStepClass"></div>
      <div class="step" :class="prodStepClass"><div class="dot"><i :class="prodStepIcon"></i></div><span>Production</span></div><div class="line" :class="prodStepClass"></div>
      <div class="step" :class="finStepClass"><div class="dot"><i :class="finStepIcon"></i></div><span>Finished Inspection</span></div><div class="line" :class="releaseStepClass"></div>
      <div class="step" :class="releaseStepClass"><div class="dot"><i :class="releaseStepIcon"></i></div><span>{{ batch.batch.releaseStatus === 'released' ? 'Released' : 'Pending Release' }}</span></div><div class="line"></div>
      <div class="step"><div class="dot"><i class="el-icon-link"></i></div><span>Trace Code</span></div>
    </div>

    <div style="display:flex;align-items:flex-start;justify-content:space-between;margin-bottom:20px">
      <div>
        <h1 style="font-size:24px">{{ batch.batchNo }}</h1>
        <p style="color:#6f7e92;font-size:13px" v-if="batch.batch">{{ batch.batch.productType }} · Source: {{ batch.batch.source }} · Registered: {{ batch.batch.createTime }}</p>
      </div>
      <div style="display:flex;gap:10px">
        <el-button v-if="batch.batch && batch.batch.releaseStatus === 'unreleased' && batch.finishedInspection && batch.finishedInspection.inspection.result === 'PASS'" type="primary" size="small" @click="doRelease">Execute Release</el-button>
      </div>
    </div>

    <div class="info-row" v-if="batch.batch">
      <div class="info-cell"><span class="label">Production Status</span><span class="tag" :class="batch.batch.productionStatus === 'completed' ? 'tag-blue' : batch.batch.productionStatus === 'processing' ? 'tag-orange' : 'tag-gray'">{{ batch.batch.productionStatus }}</span></div>
      <div class="info-cell"><span class="label">Incoming Inspection</span><span :class="'tag ' + incResultClass">{{ incResultText }}</span></div>
      <div class="info-cell"><span class="label">Finished Inspection</span><span :class="'tag ' + finResultClass">{{ finResultText }}</span></div>
      <div class="info-cell"><span class="label">Release Status</span><span class="tag" :class="batch.batch.releaseStatus === 'released' ? 'tag-green' : 'tag-orange'">{{ batch.batch.releaseStatus === 'released' ? 'Released' : 'Pending Release' }}</span></div>
    </div>

    <el-tabs v-model="activeTab" style="margin-top:10px">
      <el-tab-pane label="Overview" name="overview">
        <div class="grid-2" v-if="batch.batch">
          <div class="card"><div class="card-header"><h3>Basic Information</h3></div><div class="card-body">
            <table style="width:100%">
              <tr v-for="row in basicInfo" :key="row.label"><td style="width:150px;color:#6f7e92;padding:6px 0">{{ row.label }}</td><td style="padding:6px 0"><strong>{{ row.value }}</strong></td></tr>
            </table>
          </div></div>
          <div>
            <div class="card" style="margin-bottom:20px"><div class="card-header"><h3>Status Timeline</h3></div><div class="card-body">
              <div class="timeline">
                <div v-for="(item, i) in history" :key="i" class="t-item" :class="i === history.length - 1 ? 'current' : 'done'">
                  <div class="t-dot"><i :class="i === history.length - 1 ? 'el-icon-arrow-right' : 'el-icon-check'"></i></div>
                  <div class="t-title">{{ item.action }} <span class="tag tag-blue" style="font-size:11px" v-if="item.result">{{ item.result }}</span></div>
                  <div class="t-time" v-if="item.time">{{ item.time }}</div>
                  <div class="t-desc">{{ item.person }} — {{ item.remark }}</div>
                </div>
              </div>
            </div></div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="Receiving & Inspection" name="inspection">
        <div class="grid-2" v-if="batch.batch">
          <div class="card"><div class="card-header"><h3>Raw Material Receiving</h3></div><div class="card-body">
            <table style="width:100%">
              <tr><td style="width:150px;color:#6f7e92;padding:6px 0">Raw Material</td><td>{{ batch.batch.rawMaterialType }}</td></tr>
              <tr><td style="color:#6f7e92;padding:6px 0">Source / Supplier</td><td>{{ batch.batch.source }}</td></tr>
              <tr><td style="color:#6f7e92;padding:6px 0">Origin</td><td>{{ batch.batch.origin }}</td></tr>
              <tr><td style="color:#6f7e92;padding:6px 0">Receipt Time</td><td>{{ batch.batch.receiptTime }}</td></tr>
              <tr><td style="color:#6f7e92;padding:6px 0">Quantity</td><td>{{ batch.batch.quantity }} kg</td></tr>
              <tr><td style="color:#6f7e92;padding:6px 0">Certificate No.</td><td>{{ batch.batch.certificateNo }}</td></tr>
              <tr><td style="color:#6f7e92;padding:6px 0">Remarks</td><td>{{ batch.batch.remark }}</td></tr>
            </table>
          </div></div>
          <div class="card" v-if="batch.incomingInspection">
            <div class="card-header"><div><h3>Incoming Inspection</h3><p>Batch cleared for processing</p></div><span class="tag tag-green" v-if="batch.incomingInspection.inspection.result === 'PASS'">PASS</span></div>
            <div class="card-body">
              <table style="width:100%">
                <tr><td style="width:150px;color:#6f7e92;padding:6px 0">Inspector</td><td>{{ batch.incomingInspection.inspectorName }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">Inspection Time</td><td>{{ batch.incomingInspection.inspection.inspectTime }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">Result</td><td><span :class="'tag tag-' + (batch.incomingInspection.inspection.result === 'PASS' ? 'green' : 'red')">{{ batch.incomingInspection.inspection.result }}</span></td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">Disposal</td><td>{{ batch.incomingInspection.inspection.disposal }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">Remarks</td><td>{{ batch.incomingInspection.inspection.remark }}</td></tr>
              </table>
              <div v-if="batch.incomingInspection.inspection.result === 'PASS'" style="margin-top:16px;padding:12px;background:#f0fdf4;border-radius:8px;border:1px solid #bbf7d0;color:#166534;font-size:13px">
                <i class="el-icon-circle-check" style="margin-right:8px"></i>Incoming inspection passed. Processing permission granted.
              </div>
            </div>
          </div>
          <div v-else class="card"><div class="card-header"><h3>Incoming Inspection</h3></div><div class="card-body"><p style="color:#6f7e92">No incoming inspection record yet</p></div></div>
          <div class="card" style="grid-column:1/-1" v-if="batch.finishedInspection">
            <div class="card-header"><div><h3>Finished Inspection</h3><p>Final inspection after production completion</p></div><span class="tag tag-green" v-if="batch.finishedInspection.inspection.result === 'PASS'">PASS</span></div>
            <div class="card-body">
              <table style="width:100%">
                <tr><td style="width:150px;color:#6f7e92;padding:6px 0">Inspector</td><td>{{ batch.finishedInspection.inspectorName }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">Inspection Time</td><td>{{ batch.finishedInspection.inspection.inspectTime }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">Result</td><td><span class="tag tag-green">{{ batch.finishedInspection.inspection.result }}</span></td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">Items Tested</td><td>{{ batch.finishedInspection.inspection.items }}</td></tr>
                <tr><td style="color:#6f7e92;padding:6px 0">Remarks</td><td>{{ batch.finishedInspection.inspection.remark }}</td></tr>
              </table>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="Production Process" name="process">
        <div class="card" style="margin-bottom:20px"><div class="card-body">
          <div class="process-flow">
            <div class="p-step" :class="hasProcessType('sorting') ? 'done' : ''"><div class="p-num"><i v-if="hasProcessType('sorting')" class="el-icon-check"></i><span v-else>1</span></div><strong>Sorting</strong><span>{{ processCount('sorting') }} record(s)</span></div>
            <div class="p-arrow"><i class="el-icon-arrow-right"></i></div>
            <div class="p-step" :class="hasProcessType('cleaning') ? 'done' : ''"><div class="p-num"><i v-if="hasProcessType('cleaning')" class="el-icon-check"></i><span v-else>2</span></div><strong>Cleaning</strong><span>{{ processCount('cleaning') }} record(s)</span></div>
            <div class="p-arrow"><i class="el-icon-arrow-right"></i></div>
            <div class="p-step" :class="hasProcessType('packaging') ? 'done' : 'active'"><div class="p-num">3</div><strong>Packaging</strong><span>{{ processCount('packaging') }} record(s)</span></div>
          </div>
        </div></div>
        <div class="grid-3">
          <div class="card" v-for="pt in ['sorting','cleaning','packaging']" :key="pt">
            <div class="card-header"><h3>{{ {sorting:'Sorting Records',cleaning:'Cleaning Records',packaging:'Packaging Records'}[pt] }}</h3></div>
            <div class="card-body">
              <el-table :data="getProcessByType(pt)" stripe>
                <el-table-column prop="equipment" label="Equipment"></el-table-column>
                <el-table-column prop="recordTime" label="Time"></el-table-column>
              </el-table>
              <el-button v-if="getProcessByType(pt).length === 0" type="text" @click="addProcessRecord(pt)" style="margin-top:8px">+ Add Record</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="Trace & QR Code" name="trace">
        <div class="grid-2">
          <div class="card"><div class="card-header"><h3>Trace Information</h3></div><div class="card-body">
            <table style="width:100%">
              <tr><td style="width:150px;color:#6f7e92;padding:6px 0">Trace Code</td><td><strong>{{ batch.traceability ? batch.traceability.traceCode : 'Not generated (release required first)' }}</strong></td></tr>
              <tr><td style="color:#6f7e92;padding:6px 0">Generated At</td><td>{{ batch.traceability ? batch.traceability.generateTime : 'N/A' }}</td></tr>
              <tr><td style="color:#6f7e92;padding:6px 0">Public Access</td><td><span :class="batch.traceability && batch.traceability.isPublic ? 'tag tag-green' : 'tag tag-gray'">{{ batch.traceability && batch.traceability.isPublic ? 'Open' : 'Closed' }}</span></td></tr>
              <tr><td style="color:#6f7e92;padding:6px 0">QR URL</td><td><a :href="publicTraceUrl" style="color:#2d79ea">{{ publicTraceUrl || '-' }}</a></td></tr>
            </table>
            <div v-if="!batch.traceability" style="margin-top:16px;padding:12px;background:#fffbeb;border-radius:8px;border:1px solid #fde68a;color:#92400e;font-size:13px">
              <i class="el-icon-warning-outline" style="margin-right:8px"></i>Please execute "Release" first. The system will auto-generate a trace code and enable public query.
            </div>
          </div></div>
          <div class="card" v-if="batch.traceability">
            <div class="card-header"><h3>Batch QR Code</h3></div>
            <div class="card-body" style="text-align:center">
              <div style="width:180px;height:180px;border:1px solid #e6edf7;border-radius:12px;display:flex;align-items:center;justify-content:center;margin:0 auto 16px;background:#fff">
                <img v-if="qrCodeDataUrl" :src="qrCodeDataUrl" alt="Batch QR Code" style="width:160px;height:160px" />
                <i v-else class="el-icon-loading" style="font-size:28px;color:#9ca3af"></i>
              </div>
              <p style="font-size:13px;color:#6f7e92;margin-bottom:12px;word-break:break-all">{{ publicTraceUrl }}</p>
              <div style="display:flex;justify-content:center;gap:10px">
                <el-button size="small" @click="copyTraceLink">Copy Link</el-button>
                <el-button size="small" type="primary" :disabled="!qrCodeDataUrl" @click="downloadQrCode">Download QR</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="Operation History" name="history">
        <div class="card"><div class="card-header"><h3>Operation History</h3></div><div class="card-body">
          <el-table :data="history" stripe>
            <el-table-column prop="action" label="Action"></el-table-column>
            <el-table-column prop="person" label="Person"></el-table-column>
            <el-table-column label="Result"><template slot-scope="s"><span class="tag tag-blue" style="font-size:11px">{{ s.row.result }}</span></template></el-table-column>
            <el-table-column prop="time" label="Time" width="180"></el-table-column>
            <el-table-column prop="remark" label="Remarks"></el-table-column>
          </el-table>
        </div></div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import QRCode from 'qrcode';
import { getBatchDetail, executeRelease, addProductionRecord } from '../api';
export default {
  data() { return { activeTab: 'overview', batch: {}, qrCodeDataUrl: '' }; },
  computed: {
    basicInfo() {
      const b = this.batch.batch;
      if (!b) return [];
      return [
        { label: 'Batch No.', value: b.batchNo }, { label: 'Product Type', value: b.productType },
        { label: 'Raw Material Type', value: b.rawMaterialType }, { label: 'Source / Supplier', value: b.source },
        { label: 'Origin', value: b.origin }, { label: 'Quantity', value: b.quantity + ' kg' },
        { label: 'Certificate No.', value: b.certificateNo }, { label: 'Registered By', value: this.batch.operator },
        { label: 'Remarks', value: b.remark }
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
    prodStepClass() { return this.batch.batch && this.batch.batch.productionStatus === 'completed' ? 'done' : 'current'; },
    prodStepIcon() { return this.batch.batch && this.batch.batch.productionStatus === 'completed' ? 'el-icon-check' : 'el-icon-more'; },
    finStepClass() {
      const fin = this.batch.finishedInspection;
      return fin && fin.inspection.result === 'PASS' ? 'done' : 'current';
    },
    finStepIcon() {
      const fin = this.batch.finishedInspection;
      return fin && fin.inspection.result === 'PASS' ? 'el-icon-check' : 'el-icon-more';
    },
    releaseStepClass() { return this.batch.batch && this.batch.batch.releaseStatus === 'released' ? 'done' : 'current'; },
    releaseStepIcon() { return this.batch.batch && this.batch.batch.releaseStatus === 'released' ? 'el-icon-check' : 'el-icon-upload2'; },
    history() { return this.batch.history || []; },
    publicTraceUrl() {
      const traceCode = this.batch.traceability && this.batch.traceability.traceCode;
      return traceCode ? `${window.location.origin}/#/public?code=${encodeURIComponent(traceCode)}` : '';
    }
  },
  created() { this.loadDetail(); },
  methods: {
    loadDetail() {
      const id = this.$route.params.id;
      getBatchDetail(id).then(async res => {
        if (res.code === 200) {
          this.batch = res.data;
          await this.generateQrCode();
        }
      });
    },
    doRelease() {
      this.$confirm('Confirm release of this batch?', 'Confirm', { type: 'info' }).then(() => {
        const user = JSON.parse(localStorage.getItem('user'));
        executeRelease(this.$route.params.id, user.userId).then(res => {
          if (res.code === 200) { this.$message.success('Release successful — trace code generated'); this.loadDetail(); }
          else this.$message.error(res.message);
        });
      }).catch(() => {});
    },
    hasProcessType(type) { return (this.batch.productionRecords || []).some(r => r.processType === type); },
    processCount(type) { return (this.batch.productionRecords || []).filter(r => r.processType === type).length; },
    getProcessByType(type) { return (this.batch.productionRecords || []).filter(r => r.processType === type); },
    async generateQrCode() {
      if (!this.publicTraceUrl) {
        this.qrCodeDataUrl = '';
        return;
      }
      try {
        this.qrCodeDataUrl = await QRCode.toDataURL(this.publicTraceUrl, {
          width: 320,
          margin: 1
        });
      } catch (_) {
        this.qrCodeDataUrl = '';
        this.$message.error('Failed to generate QR code');
      }
    },
    copyTraceLink() {
      if (!this.publicTraceUrl) return;
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(this.publicTraceUrl).then(() => {
          this.$message.success('Link copied');
        }).catch(() => {
          this.fallbackCopyTraceLink();
        });
        return;
      }
      this.fallbackCopyTraceLink();
    },
    fallbackCopyTraceLink() {
      const input = document.createElement('textarea');
      input.value = this.publicTraceUrl;
      input.setAttribute('readonly', 'readonly');
      input.style.position = 'absolute';
      input.style.left = '-9999px';
      document.body.appendChild(input);
      input.select();
      try {
        document.execCommand('copy');
        this.$message.success('Link copied');
      } catch (_) {
        this.$message.error('Failed to copy link');
      } finally {
        document.body.removeChild(input);
      }
    },
    downloadQrCode() {
      if (!this.qrCodeDataUrl || !this.batch.traceability) return;
      const link = document.createElement('a');
      link.href = this.qrCodeDataUrl;
      link.download = `${this.batch.traceability.traceCode || 'trace-code'}.png`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    addProcessRecord(type) {
      const user = JSON.parse(localStorage.getItem('user'));
      const typeMap = { sorting: 'Sorting', cleaning: 'Cleaning', packaging: 'Packaging' };
      this.$prompt('Enter equipment name', 'Add ' + typeMap[type] + ' Record', {}).then(({ value }) => {
        addProductionRecord({ batchId: this.$route.params.id, processType: type, operatorId: user.userId, equipment: value }).then(res => {
          if (res.code === 200) { this.$message.success('Record added'); this.loadDetail(); }
        });
      }).catch(() => {});
    }
  }
};
</script>
