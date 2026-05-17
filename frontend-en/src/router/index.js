import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

import Home from '../views/Home.vue';
import Login from '../views/Login.vue';
import BatchList from '../views/BatchList.vue';
import BatchCreate from '../views/BatchCreate.vue';
import BatchDetail from '../views/BatchDetail.vue';
import ScanEntry from '../views/ScanEntry.vue';
import InspectionWorkbench from '../views/InspectionWorkbench.vue';
import InspectionRecords from '../views/InspectionRecords.vue';
import ReleaseManagement from '../views/ReleaseManagement.vue';
import TraceabilityRecords from '../views/TraceabilityRecords.vue';
import PublicTrace from '../views/PublicTrace.vue';
import Users from '../views/Users.vue';
import EnterpriseInfo from '../views/EnterpriseInfo.vue';
import SystemSettings from '../views/SystemSettings.vue';
import Profile from '../views/Profile.vue';
import PublicQuery from '../views/PublicQuery.vue';

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/login', component: Login, meta: { noAuth: true } },
  { path: '/public', component: PublicQuery, meta: { noAuth: true } },
  { path: '/home', component: Home },
  { path: '/batches', component: BatchList },
  { path: '/batches/create', component: BatchCreate },
  { path: '/batches/scan', component: ScanEntry },
  { path: '/batches/:id', component: BatchDetail },
  { path: '/inspection/workbench', component: InspectionWorkbench },
  { path: '/inspection/records', component: InspectionRecords },
  { path: '/release', component: ReleaseManagement },
  { path: '/traceability', component: TraceabilityRecords },
  { path: '/traceability/preview', component: PublicTrace },
  { path: '/users', component: Users },
  { path: '/enterprise', component: EnterpriseInfo },
  { path: '/announcements', component: SystemSettings },
  { path: '/profile', component: Profile },
];

const router = new VueRouter({ routes });

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.meta && to.meta.noAuth) {
    next();
  } else if (!token) {
    next('/login');
  } else {
    next();
  }
});

export default router;
