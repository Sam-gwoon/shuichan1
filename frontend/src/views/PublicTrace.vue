<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <span>公共追溯查询预览</span></div>
    <div style="margin-bottom:20px">
      <h1 style="font-size:22px;margin-bottom:4px">公共追溯查询预览</h1>
      <p style="color:#6f7e92;font-size:13px">预览公共追溯页面展示效果</p>
    </div>
    <div style="margin-bottom:16px">
      <el-input v-model="traceCode" placeholder="输入追溯码，如 AQ-2025-008-M2P4" style="width:400px;margin-right:10px"></el-input>
      <el-button type="primary" @click="query">查询</el-button>
    </div>
    <div class="public-page" style="border-radius:14px;overflow:hidden;border:1px solid #e6edf7" v-if="data">
      <div class="public-header">
        <h1>产品追溯信息</h1>
        <p style="margin:0 0 8px;font-size:16px;opacity:0.9">追溯码：<strong>{{ traceCode }}</strong></p>
        <p style="margin:0;font-size:14px;opacity:0.75">水产品加工监管平台提供</p>
      </div>
      <div class="public-body">
        <section class="public-card" v-if="data.enterprise">
          <h2 class="public-card__title">企业信息</h2>
          <table class="public-table">
            <tr><td class="label">企业名称</td><td>{{ data.enterprise.name }}</td></tr>
            <tr><td class="label">生产地址</td><td>{{ data.enterprise.address }}</td></tr>
            <tr><td class="label">生产许可证</td><td>{{ data.enterprise.licenseNo }}</td></tr>
            <tr><td class="label">联系电话</td><td>{{ data.enterprise.phone }}</td></tr>
          </table>
        </section>
        <section class="public-card" v-if="data.batch">
          <h2 class="public-card__title">批次基本信息</h2>
          <table class="public-table">
            <tr><td class="label">批次编号</td><td>{{ data.batch.batchNo }}</td></tr>
            <tr><td class="label">产品类型</td><td>{{ data.batch.productType }}</td></tr>
            <tr><td class="label">追溯码</td><td>{{ data.batch.traceCode }}</td></tr>
          </table>
        </section>
        <section class="public-card" v-if="data.incomingInspection || data.finishedInspection">
          <h2 class="public-card__title">检验结果</h2>
          <table class="public-table">
            <tr v-if="data.incomingInspection"><td class="label">来料质检</td><td><span style="color:#047857;font-weight:600">{{ data.incomingInspection.result }}</span><span style="display:block;font-size:12px;color:#8a98ab;margin-top:2px">{{ data.incomingInspection.time }}</span></td></tr>
            <tr v-if="data.finishedInspection"><td class="label">成品质检</td><td><span style="color:#047857;font-weight:600">{{ data.finishedInspection.result }}</span><span style="display:block;font-size:12px;color:#8a98ab;margin-top:2px">{{ data.finishedInspection.time }}</span></td></tr>
          </table>
        </section>
        <section class="public-card" v-if="data.release">
          <h2 class="public-card__title">放行信息</h2>
          <table class="public-table">
            <tr><td class="label">放行状态</td><td><span style="color:#047857;font-weight:600">{{ data.release.status }}</span></td></tr>
            <tr><td class="label">放行时间</td><td>{{ data.release.time }}</td></tr>
          </table>
        </section>
      </div>
    </div>
    <div v-else style="text-align:center;padding:60px;color:#6f7e92">
      <i class="el-icon-view" style="font-size:48px;color:#d1d5db;display:block;margin-bottom:12px"></i>
      <strong style="display:block;color:#17324f;margin-bottom:8px">输入追溯码进行查询</strong>
      <span>查看公共追溯页面展示效果</span>
    </div>
  </div>
</template>

<script>
import { getPublicTrace } from '../api';
export default {
  data() { return { traceCode: '', data: null }; },
  methods: {
    query() {
      if (!this.traceCode.trim()) { this.$message.warning('请输入追溯码'); return; }
      getPublicTrace(this.traceCode.trim()).then(res => {
        if (res.code === 200) this.data = res.data;
        else { this.$message.error(res.message); this.data = null; }
      });
    }
  }
};
</script>
