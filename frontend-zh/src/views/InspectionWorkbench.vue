<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <span>质检工作台</span></div>
    <div style="margin-bottom:20px">
      <h1 style="font-size:22px;margin-bottom:4px">质检工作台</h1>
      <p style="color:#6f7e92;font-size:13px">待质检批次列表与快速操作入口</p>
    </div>

    <div class="grid-2" style="margin-bottom:20px">
      <div class="card">
        <div class="card-body" style="display:flex;align-items:center;gap:16px">
          <div style="width:56px;height:56px;border-radius:14px;background:#eff6ff;color:#2d79ea;display:flex;align-items:center;justify-content:center;font-size:24px"><i class="el-icon-document-checked"></i></div>
          <div><div style="font-size:12px;color:#6f7e92">待来料质检</div><div style="font-size:24px;font-weight:700">{{ pendingIncoming.length }}</div></div>
        </div>
      </div>
      <div class="card">
        <div class="card-body" style="display:flex;align-items:center;gap:16px">
          <div style="width:56px;height:56px;border-radius:14px;background:#f0fdf4;color:#10b981;display:flex;align-items:center;justify-content:center;font-size:24px"><i class="el-icon-circle-check"></i></div>
          <div><div style="font-size:12px;color:#6f7e92">待成品质检</div><div style="font-size:24px;font-weight:700">{{ pendingFinished.length }}</div></div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-header"><h3>待质检批次</h3></div>
      <div class="card-body">
        <el-table :data="allPending" stripe>
          <el-table-column prop="batchNo" label="批次编号" width="180"><template slot-scope="s"><strong>{{ s.row.batchNo }}</strong></template></el-table-column>
          <el-table-column prop="productType" label="产品类型"></el-table-column>
          <el-table-column prop="type" label="质检类型"></el-table-column>
          <el-table-column label="当前状态">
            <template slot-scope="s"><span class="tag tag-gray">{{ s.row.status | capitalize }}</span></template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="s">
              <el-button type="text" v-if="s.row.type === '来料质检'" @click="doIncoming(s.row)">去质检 (来料)</el-button>
              <el-button type="text" v-else @click="doFinished(s.row)">去质检 (成品)</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="allPending.length === 0" style="text-align:center;padding:40px;color:#6f7e92">暂无待质检批次</div>
      </div>
    </div>
  </div>
</template>

<script>
import { getInspectionWorkbench, performIncomingInspection, performFinishedInspection } from '../api';
export default {
  data() { return { pendingList: [] }; },
  computed: {
    pendingIncoming() { return this.pendingList.filter(p => p.type === '来料质检'); },
    pendingFinished() { return this.pendingList.filter(p => p.type === '成品质检'); },
    allPending() { return this.pendingList; }
  },
  created() { this.load(); },
  methods: {
    load() { getInspectionWorkbench().then(res => { if (res.code === 200) this.pendingList = res.data; }); },
    doIncoming(row) {
      const user = JSON.parse(localStorage.getItem('user'));
      this.$prompt('请输入检验备注（PASS 或 FAIL）', '来料质检 - ' + row.batchNo, {
        inputValue: '感官正常，药残检测合格'
      }).then(({ value }) => {
        const isPass = !value.toUpperCase().includes('FAIL');
        performIncomingInspection({
          batchId: row.batchId,
          inspectorId: user.userId,
          result: isPass ? 'PASS' : 'FAIL',
          disposal: isPass ? '准许加工' : '退回供应商',
          remark: value
        }).then(res => {
          if (res.code === 200) { this.$message.success('质检完成'); this.load(); }
          else this.$message.error(res.message);
        });
      }).catch(() => {});
    },
    doFinished(row) {
      const user = JSON.parse(localStorage.getItem('user'));
      this.$prompt('请输入检验备注', '成品质检 - ' + row.batchNo, {
        inputValue: '全部项目合格，准予放行'
      }).then(({ value }) => {
        const isPass = !value.toUpperCase().includes('FAIL');
        performFinishedInspection({
          batchId: row.batchId,
          inspectorId: user.userId,
          result: isPass ? 'PASS' : 'FAIL',
          items: '净含量、感官、微生物、中心温度、标签标识',
          remark: value
        }).then(res => {
          if (res.code === 200) { this.$message.success('质检完成'); this.load(); }
          else this.$message.error(res.message);
        });
      }).catch(() => {});
    }
  }
};
</script>
