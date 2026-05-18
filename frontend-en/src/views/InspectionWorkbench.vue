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
      <el-table :data="pagedPending" stripe>
        <el-table-column prop="batchNo" label="Batch No." width="180"><template slot-scope="s"><strong>{{ s.row.batchNo }}</strong></template></el-table-column>
        <el-table-column prop="productType" label="Product Type"></el-table-column>
        <el-table-column prop="type" label="Inspection Type"></el-table-column>
        <el-table-column label="Current Status"><template slot-scope="s"><span class="tag tag-gray">{{ s.row.status }}</span></template></el-table-column>
        <el-table-column label="Action">
          <template slot-scope="s">
            <el-button type="text" v-if="s.row.type === 'Incoming Inspection'" @click="openInspectionDialog(s.row, 'incoming')">Start Incoming Check</el-button>
            <el-button type="text" v-else @click="openInspectionDialog(s.row, 'finished')">Start Final Check</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="allPending.length === 0" style="text-align:center;padding:40px;color:#6f7e92">No pending inspections</div>
      <div style="display:flex;justify-content:flex-end;margin-top:16px" v-if="allPending.length > 0">
        <el-pagination
          background
          :current-page="page"
          :page-sizes="pageSizes"
          :page-size="pageSize"
          :total="allPending.length"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handlePageChange">
        </el-pagination>
      </div>
    </div></div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="520px">
      <el-form label-width="80px">
        <el-form-item label="Batch">
          <strong>{{ currentRow ? currentRow.batchNo : '-' }}</strong>
        </el-form-item>
        <el-form-item label="Result">
          <el-radio-group v-model="inspectionForm.result">
            <el-radio label="PASS">PASS</el-radio>
            <el-radio label="FAIL">FAIL</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Remarks">
          <el-input
            v-model="inspectionForm.remark"
            type="textarea"
            :rows="4"
            placeholder="Enter inspection remarks">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="submitting" @click="submitInspection">Submit</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getInspectionWorkbench, performIncomingInspection, performFinishedInspection } from '../api';
export default {
  data() {
    return {
      pendingList: [],
      page: 1,
      pageSize: 10,
      pageSizes: [5, 10, 20, 50],
      dialogVisible: false,
      dialogMode: 'incoming',
      currentRow: null,
      submitting: false,
      inspectionForm: {
        result: 'PASS',
        remark: ''
      }
    };
  },
  computed: {
    pendingIncoming() { return this.pendingList.filter(p => p.type === 'Incoming Inspection'); },
    pendingFinished() { return this.pendingList.filter(p => p.type === 'Finished Inspection'); },
    allPending() { return this.pendingList; },
    pagedPending() {
      const start = (this.page - 1) * this.pageSize;
      return this.allPending.slice(start, start + this.pageSize);
    },
    dialogTitle() {
      if (!this.currentRow) return 'Inspection';
      return `${this.dialogMode === 'incoming' ? 'Incoming Inspection' : 'Finished Inspection'} - ${this.currentRow.batchNo}`;
    }
  },
  created() { this.load(); },
  methods: {
    load() {
      getInspectionWorkbench().then(res => {
        if (res.code === 200) {
          this.pendingList = res.data;
          this.page = 1;
        }
      });
    },
    handleSizeChange(size) {
      this.pageSize = size;
      this.page = 1;
    },
    handlePageChange(page) {
      this.page = page;
    },
    openInspectionDialog(row, mode) {
      this.currentRow = row;
      this.dialogMode = mode;
      this.inspectionForm = {
        result: 'PASS',
        remark: mode === 'incoming'
          ? 'Sensory normal, drug residue test passed'
          : 'All items passed, approved for release'
      };
      this.dialogVisible = true;
    },
    async submitInspection() {
      if (!this.currentRow) return;
      const user = JSON.parse(localStorage.getItem('user'));
      this.submitting = true;
      try {
        if (this.dialogMode === 'incoming') {
          const res = await performIncomingInspection({
            batchId: this.currentRow.batchId,
            inspectorId: user.userId,
            result: this.inspectionForm.result,
            disposal: this.inspectionForm.result === 'PASS' ? 'Approved for processing' : 'Return to supplier',
            remark: this.inspectionForm.remark
          });

          if (res.code === 200) {
            this.$message.success(`Incoming inspection marked as ${this.inspectionForm.result}`);
            this.dialogVisible = false;
            this.load();
          } else {
            this.$message.error(res.message);
          }
          return;
        }

        const res = await performFinishedInspection({
          batchId: this.currentRow.batchId,
          inspectorId: user.userId,
          result: this.inspectionForm.result,
          items: 'Net weight, sensory, microbiology, core temperature, labeling',
          remark: this.inspectionForm.remark
        });

        if (res.code === 200) {
          this.$message.success(`Finished inspection marked as ${this.inspectionForm.result}`);
          this.dialogVisible = false;
          this.load();
        } else {
          this.$message.error(res.message);
        }
      } finally {
        this.submitting = false;
      }
    }
  }
};
</script>
