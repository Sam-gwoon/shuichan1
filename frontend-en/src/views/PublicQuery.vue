<template>
  <div class="public-page">
    <div class="public-header" v-if="data">
      <h1>Product Traceability Information</h1>
      <p style="margin:0 0 8px;font-size:16px;opacity:0.9">Trace Code: <strong>{{ $route.query.code }}</strong></p>
      <p style="margin:0;font-size:14px;opacity:0.75">Aquatic Processing Supervision Platform</p>
    </div>
    <div class="public-header" v-else-if="loading"><h1>Querying...</h1></div>
    <div class="public-header" v-else>
      <h1>Invalid Trace Code</h1>
      <p style="margin:0;font-size:14px;opacity:0.75">This trace code does not exist or is not yet public</p>
    </div>

    <div class="public-body" v-if="data">
      <section class="public-card" v-if="data.enterprise"><h2 class="public-card__title">Enterprise Information</h2><table class="public-table">
        <tr><td class="label">Company Name</td><td>{{ data.enterprise.name }}</td></tr>
        <tr><td class="label">Production Address</td><td>{{ data.enterprise.address }}</td></tr>
        <tr><td class="label">License No.</td><td>{{ data.enterprise.licenseNo }}</td></tr>
        <tr><td class="label">Contact</td><td>{{ data.enterprise.phone }}</td></tr>
      </table></section>
      <section class="public-card" v-if="data.batch"><h2 class="public-card__title">Batch Information</h2><table class="public-table">
        <tr><td class="label">Batch No.</td><td>{{ data.batch.batchNo }}</td></tr>
        <tr><td class="label">Product Type</td><td>{{ data.batch.productType }}</td></tr>
        <tr><td class="label">Trace Code</td><td>{{ data.batch.traceCode }}</td></tr>
      </table></section>
      <section class="public-card" v-if="data.incomingInspection || data.finishedInspection"><h2 class="public-card__title">Inspection Results</h2><table class="public-table">
        <tr v-if="data.incomingInspection"><td class="label">Incoming Inspection</td><td><span :style="{color: data.incomingInspection.result === 'PASS' ? '#047857' : '#b91c1c', fontWeight: 600}">{{ data.incomingInspection.result }}</span><span style="display:block;font-size:12px;color:#8a98ab;margin-top:2px">{{ data.incomingInspection.time }}</span></td></tr>
        <tr v-if="data.finishedInspection"><td class="label">Finished Inspection</td><td><span :style="{color: data.finishedInspection.result === 'PASS' ? '#047857' : '#b91c1c', fontWeight: 600}">{{ data.finishedInspection.result }}</span><span style="display:block;font-size:12px;color:#8a98ab;margin-top:2px">{{ data.finishedInspection.time }}</span></td></tr>
      </table></section>
      <section class="public-card" v-if="data.release"><h2 class="public-card__title">Release Information</h2><table class="public-table">
        <tr><td class="label">Status</td><td><span style="color:#047857;font-weight:600">{{ data.release.status }}</span></td></tr>
        <tr><td class="label">Release Time</td><td>{{ data.release.time }}</td></tr>
      </table></section>
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
      getPublicTrace(code).then(res => { if (res.code === 200) this.data = res.data; }).finally(() => this.loading = false);
    } else { this.loading = false; }
  }
};
</script>
