<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">Home</a> / <span>Inspection Workbench</span></div>
    <div style="margin-bottom:20px"><h1 style="font-size:22px;margin-bottom:4px">Inspection Workbench</h1><p style="color:#6f7e92;font-size:13px">Pending inspections and quick-action entries</p></div>

    <div class="grid-2" style="margin-bottom:20px">
      <div class="card"><div class="card-body" style="display:flex;align-items:center;gap:16px">
        <div style="width:56px;height:56px;border-radius:14px;background:#eff6ff;color:#2d79ea;display:flex;align-items:center;justify-content:center;font-size:24px"><i class="el-icon-document-checked"></i></div>
        <div><div style="font-size:12px;color:#6f7e92">Pending Incoming</div><div style="font-size:24px;font-weight:700">{{ pendingIncoming.length }}</div></div>
      </div></div>
      <div class="card"><div class="card-body" style="display:flex;align-items:center;gap:16px">
        <div style="width:56px;height:56px;border-radius:14px;background:#f0fdf4;color:#10b981;display:flex;align-items:center;justify-content:center;font-size:24px"><i class="el-icon-circle-check"></i></div>
        <div><div style="font-size:12px;color:#6f7e92">Pending Finished</div><div style="font-size:24px;font-weight:700">{{ pendingFinished.length }}</div></div>
      </div></div>
    </div>

    <div class="card"><div class="card-header"><h3>Pending Inspection Batches</h3></div><div class="card-body">
      <el-table :data="allPending" stripe>
        <el-table-column prop="batchNo" label="Batch No." width="180"><template slot-scope="s"><strong>{{ s.row.batchNo }}</strong></template></el-table-column>
        <el-table-column prop="productType" label="Product Type"></el-table-column>
        <el-table-column prop="type" label="Inspection Type"></el-table-column>
        <el-table-column label="Current Status"><template slot-scope="s"><span class="tag tag-gray">{{ s.row.status }}</span></template></el-table-column>
        <el-table-column label="Action">
          <template slot-scope="s">
            <el-button type="text" v-if="s.row.type === 'Incoming Inspection'" @click="doIncoming(s.row)">Inspect (Incoming)</el-button>
            <el-button type="text" v-else @click="doFinished(s.row)">Inspect (Finished)</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="allPending.length === 0" style="text-align:center;padding:40px;color:#6f7e92">No pending inspections</div>
    </div></div>
  </div>
</template>

<script>
import { getInspectionWorkbench, performIncomingInspection, performFinishedInspection } from '../api';
export default {
  data() { return { pendingList: [] }; },
  computed: {
    pendingIncoming() { return this.pendingList.filter(p => p.type === 'Incoming Inspection'); },
    pendingFinished() { return this.pendingList.filter(p => p.type === 'Finished Inspection'); },
    allPending() { return this.pendingList; }
  },
  created() { this.load(); },
  methods: {
    load() { getInspectionWorkbench().then(res => { if (res.code === 200) this.pendingList = res.data; }); },
    doIncoming(row) {
      const user = JSON.parse(localStorage.getItem('user'));
      this.$prompt('Enter inspection remarks (PASS or FAIL)', 'Incoming Inspection — ' + row.batchNo, {
        inputValue: 'Sensory normal, drug residue test passed'
      }).then(({ value }) => {
        const isPass = !value.toUpperCase().includes('FAIL');
        performIncomingInspection({
          batchId: row.batchId, inspectorId: user.userId,
          result: isPass ? 'PASS' : 'FAIL',
          disposal: isPass ? 'Approved for processing' : 'Return to supplier',
          remark: value
        }).then(res => {
          if (res.code === 200) { this.$message.success('Inspection completed'); this.load(); }
          else this.$message.error(res.message);
        });
      }).catch(() => {});
    },
    doFinished(row) {
      const user = JSON.parse(localStorage.getItem('user'));
      this.$prompt('Enter inspection remarks', 'Finished Inspection — ' + row.batchNo, {
        inputValue: 'All items passed, approved for release'
      }).then(({ value }) => {
        const isPass = !value.toUpperCase().includes('FAIL');
        performFinishedInspection({
          batchId: row.batchId, inspectorId: user.userId,
          result: isPass ? 'PASS' : 'FAIL',
          items: 'Net weight, sensory, microbiology, core temperature, labeling',
          remark: value
        }).then(res => {
          if (res.code === 200) { this.$message.success('Inspection completed'); this.load(); }
          else this.$message.error(res.message);
        });
      }).catch(() => {});
    }
  }
};
</script>
