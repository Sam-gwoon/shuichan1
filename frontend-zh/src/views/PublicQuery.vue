<template>
  <div class="public-page">
    <div class="public-header" v-if="data">
      <h1>产品追溯信息</h1>
      <p style="margin:0 0 8px;font-size:16px;opacity:0.9">追溯码：<strong>{{ $route.query.code }}</strong></p>
      <p style="margin:0;font-size:14px;opacity:0.75">水产品加工监管平台提供</p>
    </div>
    <div class="public-header" v-else-if="loading">
      <h1>查询中...</h1>
    </div>
    <div class="public-header" v-else>
      <h1>追溯码无效</h1>
      <p style="margin:0;font-size:14px;opacity:0.75">该追溯码不存在或尚未公开</p>
    </div>

    <div class="public-body" v-if="data">
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
          <tr v-if="data.incomingInspection"><td class="label">来料质检</td><td>
            <span :style="{color: data.incomingInspection.result === 'PASS' ? '#047857' : '#b91c1c', fontWeight: 600}">{{ data.incomingInspection.result | capitalize }}</span>
            <span style="display:block;font-size:12px;color:#8a98ab;margin-top:2px">{{ data.incomingInspection.time }}</span>
          </td></tr>
          <tr v-if="data.finishedInspection"><td class="label">成品质检</td><td>
            <span :style="{color: data.finishedInspection.result === 'PASS' ? '#047857' : '#b91c1c', fontWeight: 600}">{{ data.finishedInspection.result | capitalize }}</span>
            <span style="display:block;font-size:12px;color:#8a98ab;margin-top:2px">{{ data.finishedInspection.time }}</span>
          </td></tr>
        </table>
      </section>
      <section class="public-card" v-if="data.release">
        <h2 class="public-card__title">放行信息</h2>
        <table class="public-table">
          <tr><td class="label">放行状态</td><td><span style="color:#047857;font-weight:600">{{ data.release.status | capitalize }}</span></td></tr>
          <tr><td class="label">放行时间</td><td>{{ data.release.time }}</td></tr>
        </table>
      </section>
    </div>
  </div>
</template>

<script>
import { getPublicTrace } from '../api';
export default {
  data() { return { data: null, loading: true }; },
  created() {
    const code = this.$route.query.code;
    if (code) {
      getPublicTrace(code).then(res => {
        if (res.code === 200) this.data = res.data;
      }).finally(() => this.loading = false);
    } else {
      this.loading = false;
    }
  }
};
</script>
