<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <span>扫码快速录入</span></div>
    <div style="max-width:600px;margin:40px auto">
      <div class="card" style="text-align:center;padding:40px">
        <div class="card-body">
          <div style="width:80px;height:80px;border-radius:50%;background:#eff6ff;color:#2d79ea;display:flex;align-items:center;justify-content:center;font-size:40px;margin:0 auto 20px">
            <i class="el-icon-s-data"></i>
          </div>
          <h2 style="margin-bottom:8px">扫码快速录入</h2>
          <p style="color:#6f7e92;margin-bottom:24px">扫描追溯码快速进入批次详情或添加工序记录</p>
          <div style="display:flex;gap:10px;justify-content:center">
            <el-input v-model="code" placeholder="输入追溯码或批次号" style="max-width:320px"></el-input>
            <el-button type="primary" @click="search">确认</el-button>
          </div>
          <div style="margin-top:20px;font-size:13px;color:#6f7e92">示例：AQ-2025-003-X7K9</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getBatches } from '../api';
export default {
  data() { return { code: '' }; },
  methods: {
    search() {
      if (!this.code.trim()) { this.$message.warning('请输入追溯码或批次号'); return; }
      getBatches({ batchNo: this.code.trim() }).then(res => {
        if (res.code === 200 && res.data.length > 0) {
          this.$router.push('/batches/' + res.data[0].id);
        } else {
          this.$message.warning('未找到相关批次');
        }
      });
    }
  }
};
</script>
