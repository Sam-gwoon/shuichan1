<template>
  <div id="app">
    <div v-if="$route.path === '/login' || $route.path === '/public'">
      <router-view />
    </div>
    <div v-else class="app-layout">
      <!-- Sidebar -->
      <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
        <div class="brand">
          <div class="brand-icon"><span class="mdi mdi-fish"></span></div>
          <div class="brand-text">
            <strong>水产监管系统</strong>
            <span>Aquatic Processing</span>
          </div>
        </div>
        <nav class="nav">
          <div class="nav-group">
            <div class="nav-label">监管中心</div>
            <button class="nav-item" :class="{ active: $route.path === '/home' }" @click="$router.push('/home')">
              <i class="el-icon-s-home"></i><span>首页 Dashboard</span>
            </button>
          </div>
          <div class="nav-group">
            <div class="nav-label">批次管理</div>
            <button class="nav-item" :class="{ active: $route.path === '/batches' }" @click="$router.push('/batches')">
              <i class="el-icon-s-order"></i><span>批次总览列表</span>
            </button>
            <button class="nav-item" v-if="userRole === 'ADMIN' || userRole === 'PROD_MANAGER' || userRole === 'OPERATOR'" :class="{ active: $route.path === '/batches/create' }" @click="$router.push('/batches/create')">
              <i class="el-icon-circle-plus-outline"></i><span>原料登记入库</span>
            </button>
            <button class="nav-item" v-if="userRole === 'ADMIN' || userRole === 'PROD_MANAGER'" :class="{ active: $route.path === '/batches/scan' }" @click="$router.push('/batches/scan')">
              <i class="el-icon-s-data"></i><span>扫码快速录入</span>
            </button>
          </div>
          <div class="nav-group" v-if="userRole === 'ADMIN' || userRole === 'INSPECTOR'">
            <div class="nav-label">质量管控</div>
            <button class="nav-item" :class="{ active: $route.path === '/inspection/workbench' }" @click="$router.push('/inspection/workbench')">
              <i class="el-icon-s-check"></i><span>质检工作台</span>
            </button>
            <button class="nav-item" :class="{ active: $route.path === '/inspection/records' }" @click="$router.push('/inspection/records')">
              <i class="el-icon-document-copy"></i><span>质检记录档案</span>
            </button>
          </div>
          <div class="nav-group">
            <div class="nav-label">放行与追溯</div>
            <button class="nav-item" v-if="userRole === 'ADMIN' || userRole === 'PROD_MANAGER'" :class="{ active: $route.path === '/release' }" @click="$router.push('/release')">
              <i class="el-icon-circle-check"></i><span>产品放行管理</span>
            </button>
            <button class="nav-item" :class="{ active: $route.path === '/traceability' }" @click="$router.push('/traceability')">
              <i class="el-icon-s-marketing"></i><span>追溯记录中心</span>
            </button>
            <button class="nav-item" :class="{ active: $route.path === '/traceability/preview' }" @click="$router.push('/traceability/preview')">
              <i class="el-icon-view"></i><span>公共追溯查询</span>
            </button>
          </div>
          <div class="nav-group" v-if="userRole === 'ADMIN'">
            <div class="nav-label">系统管理</div>
            <button class="nav-item" :class="{ active: $route.path === '/users' }" @click="$router.push('/users')">
              <i class="el-icon-user-solid"></i><span>人员权限管理</span>
            </button>
            <button class="nav-item" :class="{ active: $route.path === '/enterprise' }" @click="$router.push('/enterprise')">
              <i class="el-icon-office-building"></i><span>企业信息管理</span>
            </button>
            <button class="nav-item" :class="{ active: $route.path === '/announcements' }" @click="$router.push('/announcements')">
              <i class="el-icon-bell"></i><span>系统公告</span>
            </button>
          </div>
          <div class="nav-group" style="margin-top:auto">
            <div class="nav-label">个人</div>
            <button class="nav-item" :class="{ active: $route.path === '/profile' }" @click="$router.push('/profile')">
              <i class="el-icon-user"></i><span>个人资料</span>
            </button>
            <button class="nav-item" @click="logout">
              <i class="el-icon-switch-button"></i><span>退出登录</span>
            </button>
          </div>
        </nav>
      </aside>

      <div class="main">
        <header class="topbar">
          <div class="topbar-left">
            <button class="toggle-btn" @click="sidebarCollapsed = !sidebarCollapsed">
              <i class="el-icon-s-unfold" v-if="sidebarCollapsed"></i>
              <i class="el-icon-s-fold" v-else></i>
            </button>
            <div class="page-title">
              <strong>{{ pageTitle }}</strong>
              <span>{{ pageSub }}</span>
            </div>
          </div>
          <div class="topbar-right">
            <div class="topbar-icon"><i class="el-icon-bell"></i><span class="notif-dot"></span></div>
            <div class="user-chip" @click="$router.push('/profile')">
              <div class="user-avatar">{{ userInitial }}</div>
              <div class="user-name"><strong>{{ userRealName }}</strong><span>{{ userRoleName }}</span></div>
            </div>
          </div>
        </header>
        <div class="content">
          <router-view />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      sidebarCollapsed: false
    };
  },
  computed: {
    pageTitle() {
      const titles = {
        '/home': '首页 Dashboard', '/batches': '批次总览列表', '/batches/create': '原料登记入库',
        '/batches/scan': '扫码快速录入', '/inspection/workbench': '质检工作台',
        '/inspection/records': '质检记录档案', '/release': '产品放行管理',
        '/traceability': '追溯记录中心', '/traceability/preview': '公共追溯查询',
        '/users': '人员权限管理', '/enterprise': '企业信息管理',
        '/announcements': '系统公告', '/profile': '个人资料'
      };
      const path = this.$route.path;
      // 处理动态路由
      if (path.startsWith('/batches/') && !titles[path]) return '批次详情';
      return titles[path] || '首页 Dashboard';
    },
    pageSub() {
      return 'Aquatic Product Processing Supervision System';
    },
    userInitial() {
      const u = this.getUser();
      return u ? (u.realName || 'U').charAt(0).toUpperCase() : 'U';
    },
    userRealName() {
      const u = this.getUser();
      return u ? u.realName : 'User';
    },
    userRole() {
      const u = this.getUser();
      return u ? u.role : '';
    },
    userRoleName() {
      const u = this.getUser();
      const map = { ADMIN: '管理员', PROD_MANAGER: '生产主管', INSPECTOR: '质检员', OPERATOR: '操作员' };
      return u ? (map[u.role] || u.role) : '';
    }
  },
  methods: {
    getUser() {
      try { return JSON.parse(localStorage.getItem('user')); } catch (e) { return null; }
    },
    logout() {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      this.$router.push('/login');
    }
  }
};
</script>

<style>
* { box-sizing: border-box; margin: 0; padding: 0; }
body { font-family: "Nunito","Segoe UI","PingFang SC","Microsoft YaHei",sans-serif; background: #f3f5f8; color: #17324f; overflow: hidden; }
.app-layout { display: flex; height: 100vh; }
.sidebar { width: 260px; background: #0a3f79; color: rgba(235,242,255,0.9); display: flex; flex-direction: column; transition: width .3s; overflow-y: auto; }
.sidebar.collapsed { width: 72px; }
.brand { display: flex; align-items: center; gap: 12px; padding: 20px 18px 16px; border-bottom: 1px solid rgba(255,255,255,0.08); }
.brand-icon { width: 42px; height: 42px; border-radius: 50%; background: rgba(255,255,255,0.12); border: 1px solid rgba(255,255,255,0.3); display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0; }
.brand-text strong { display: block; font-size: 17px; color: #fff; }
.brand-text span { display: block; font-size: 11px; color: rgba(224,235,255,0.65); margin-top: 4px; }
.sidebar.collapsed .brand-text { display: none; }
.nav { padding: 12px 10px; display: flex; flex-direction: column; gap: 6px; flex: 1; }
.nav-group { margin-bottom: 10px; }
.nav-label { padding: 8px 14px 6px; font-size: 10px; text-transform: uppercase; letter-spacing: 0.1em; color: rgba(224,235,255,0.5); }
.sidebar.collapsed .nav-label { display: none; }
.nav-item { display: flex; align-items: center; gap: 12px; padding: 10px 14px; border-radius: 10px; cursor: pointer; transition: all .2s; border: none; background: transparent; color: inherit; width: 100%; text-align: left; font-size: 14px; }
.nav-item:hover { background: rgba(255,255,255,0.08); }
.nav-item.active { background: #2d79ea; color: #fff; }
.sidebar.collapsed .nav-item span { display: none; }
.sidebar.collapsed .nav-item { justify-content: center; padding: 12px; }

.main { flex: 1; display: flex; flex-direction: column; min-width: 0; overflow: hidden; }
.topbar { height: 64px; background: #fff; border-bottom: 1px solid #e6edf7; display: flex; align-items: center; justify-content: space-between; padding: 0 24px; flex-shrink: 0; }
.topbar-left { display: flex; align-items: center; gap: 16px; }
.toggle-btn { width: 36px; height: 36px; border-radius: 10px; border: 1px solid #e6edf7; background: #f8fafc; cursor: pointer; display: flex; align-items: center; justify-content: center; color: #17324f; }
.page-title strong { font-size: 20px; display: block; }
.page-title span { font-size: 12px; color: #6f7e92; }
.topbar-right { display: flex; align-items: center; gap: 18px; }
.topbar-icon { width: 36px; height: 36px; border-radius: 50%; border: 1px solid #e6edf7; background: #f8fafc; display: flex; align-items: center; justify-content: center; cursor: pointer; position: relative; }
.notif-dot { width: 8px; height: 8px; background: #ef4444; border-radius: 50%; position: absolute; top: 2px; right: 2px; border: 2px solid #f8fafc; }
.user-chip { display: flex; align-items: center; gap: 10px; border: 1px solid #e6edf7; border-radius: 999px; padding: 4px 14px 4px 4px; cursor: pointer; background: #fff; }
.user-avatar { width: 34px; height: 34px; border-radius: 50%; background: #2d79ea; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 14px; }
.user-name strong { font-size: 13px; display: block; }
.user-name span { font-size: 11px; color: #6f7e92; }
.content { flex: 1; overflow-y: auto; padding: 20px 24px; }

/* 通用卡片样式 */
.card { background: #fff; border-radius: 14px; border: 1px solid #e6edf7; box-shadow: 0 2px 12px rgba(15,52,96,0.06); overflow: hidden; margin-bottom: 20px; }
.card-header { padding: 16px 20px; border-bottom: 1px solid #e6edf7; display: flex; align-items: center; justify-content: space-between; }
.card-header h3 { font-size: 16px; font-weight: 600; }
.card-header p { font-size: 12px; color: #6f7e92; margin-top: 2px; }
.card-body { padding: 20px; }

/* KPI 卡片 */
.kpi-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 20px; }
.kpi-card { background: #fff; border: 1px solid #e6edf7; border-radius: 14px; padding: 18px 20px; box-shadow: 0 2px 12px rgba(15,52,96,0.06); display: grid; grid-template-columns: 56px 1fr; gap: 16px; align-items: center; }
.kpi-icon { width: 56px; height: 56px; border-radius: 14px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 24px; }
.kpi-icon.blue { background: #2d79ea; } .kpi-icon.green { background: #10b981; } .kpi-icon.orange { background: #f97316; } .kpi-icon.purple { background: #6366f1; }
.kpi-title { font-size: 12px; color: #6f7e92; margin-bottom: 4px; }
.kpi-value { font-size: 28px; font-weight: 700; color: #17324f; line-height: 1; }
.kpi-note { font-size: 12px; color: #8c99aa; margin-top: 4px; }

/* 网格布局 */
.grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.grid-3 { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.grid-4 { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }

/* 信息行 */
.info-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 20px; }
.info-cell { background: #fbfcfe; border: 1px solid #e6edf7; border-radius: 12px; padding: 14px 16px; display: flex; flex-direction: column; gap: 6px; }
.info-cell .label { font-size: 12px; color: #6f7e92; }
.info-cell .value { font-size: 18px; font-weight: 700; color: #17324f; }

/* 标签 */
.tag { display: inline-block; padding: 2px 10px; border-radius: 6px; font-size: 12px; font-weight: 600; }
.tag-green { background: #d1fae5; color: #047857; }
.tag-red { background: #fee2e2; color: #b91c1c; }
.tag-blue { background: #dbeafe; color: #1d4ed8; }
.tag-gray { background: #f3f4f6; color: #4b5563; }
.tag-orange { background: #ffedd5; color: #c2410c; }

/* 流程步骤 */
.workflow-flow { display: flex; align-items: stretch; gap: 10px; }
.workflow-step { flex: 1; min-width: 0; border: 1px solid #e6ebf2; border-radius: 10px; padding: 14px 10px 12px; background: #fbfcfe; text-align: center; }
.workflow-step__badge { width: 26px; height: 26px; border-radius: 50%; background: #2d79ea; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 700; margin: 0 auto 10px; }
.workflow-step strong { display: block; margin-bottom: 4px; color: #17324f; font-size: 14px; }
.workflow-step span { color: #6f7e92; font-size: 12px; }
.workflow-arrow { display: flex; align-items: center; justify-content: center; color: #7a90ad; font-size: 16px; }

/* 状态条 */
.status-ribbon { display: flex; align-items: center; gap: 8px; background: #fff; border: 1px solid #e6edf7; border-radius: 14px; padding: 14px 20px; margin-bottom: 20px; box-shadow: 0 2px 12px rgba(15,52,96,0.06); }
.status-ribbon .step { display: flex; align-items: center; gap: 8px; font-size: 13px; color: #6f7e92; flex-shrink: 0; }
.status-ribbon .step .dot { width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: #edf2f8; color: #8b97a6; font-size: 14px; border: 2px solid #e3eaf4; }
.status-ribbon .step.done .dot { background: #ecfdf3; color: #10b981; border-color: #a7f3d0; }
.status-ribbon .step.current .dot { background: #eff6ff; color: #2d79ea; border-color: #bfdbfe; box-shadow: 0 0 0 3px rgba(45,121,234,0.15); }
.status-ribbon .line { flex: 1; height: 2px; background: #e3eaf4; border-radius: 2px; min-width: 30px; }
.status-ribbon .line.done { background: #a7f3d0; }
.status-ribbon .line.current { background: #bfdbfe; }

/* 面包屑 */
.breadcrumb { margin-bottom: 16px; font-size: 13px; color: #6f7e92; }
.breadcrumb a { color: #2d79ea; cursor: pointer; }
.breadcrumb span { color: #999; }

/* 时间线 */
.timeline { display: flex; flex-direction: column; gap: 0; position: relative; padding-left: 28px; }
.timeline::before { content: ''; position: absolute; left: 11px; top: 8px; bottom: 8px; width: 2px; background: #e3eaf4; }
.t-item { position: relative; padding: 14px 0; }
.t-item .t-dot { position: absolute; left: -28px; top: 18px; width: 24px; height: 24px; border-radius: 50%; background: #fff; border: 2px solid #d1d5db; display: flex; align-items: center; justify-content: center; font-size: 12px; color: #9ca3af; }
.t-item.done .t-dot { border-color: #10b981; color: #10b981; }
.t-item.current .t-dot { border-color: #2d79ea; color: #2d79ea; background: #eff6ff; }
.t-item .t-title { font-weight: 600; font-size: 14px; margin-bottom: 4px; }
.t-item .t-time { font-size: 12px; color: #6f7e92; margin-bottom: 4px; }
.t-item .t-desc { font-size: 13px; color: #6f7e92; line-height: 1.5; }

/* 预警项 */
.warning-item { display: flex; align-items: center; justify-content: space-between; gap: 12px; border: 1px solid #e7edf5; border-radius: 10px; background: #fbfcfe; padding: 12px 14px; margin-bottom: 10px; }
.warning-item__main strong { display: block; margin-bottom: 4px; color: #17324f; font-size: 13px; }
.warning-item__main span { color: #6f7e92; font-size: 12px; }

/* 统计条 */
.stats-line { border: 1px solid #e7edf5; border-radius: 10px; background: #fbfcfe; padding: 12px 14px; margin-bottom: 10px; }
.stats-line__head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 8px; }
.stats-line__head strong { color: #17324f; font-size: 13px; }
.stats-line__head span { color: #17324f; font-weight: 700; }
.stats-line__bar { height: 8px; border-radius: 999px; background: #edf2f8; overflow: hidden; }
.stats-line__fill { height: 100%; border-radius: inherit; background: #2d79ea; }

/* 流程步骤卡片 */
.process-flow { display: flex; align-items: center; gap: 12px; }
.p-step { flex: 1; border: 1px solid #e6edf7; border-radius: 12px; padding: 16px; text-align: center; background: #fbfcfe; transition: all .2s; }
.p-step.active { border-color: #2d79ea; background: #eff6ff; }
.p-step.done { border-color: #d1fae5; background: #f0fdf4; }
.p-step .p-num { width: 32px; height: 32px; border-radius: 50%; background: #e3eaf4; color: #6b7280; display: flex; align-items: center; justify-content: center; margin: 0 auto 10px; font-weight: 700; font-size: 14px; }
.p-step.done .p-num { background: #d1fae5; color: #047857; }
.p-step.active .p-num { background: #2d79ea; color: #fff; }
.p-step strong { font-size: 14px; display: block; margin-bottom: 4px; }
.p-step span { font-size: 12px; color: #6f7e92; }
.p-arrow { color: #d1d5db; font-size: 20px; }

/* 登录页 */
.login-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 24px; background: linear-gradient(180deg, #eef7f5, #ffffff); }
.login-card { width: 100%; max-width: 460px; border-radius: 16px; background: #fff; border: 1px solid #e6edf7; box-shadow: 0 2px 12px rgba(15,52,96,0.06); padding: 32px; }

/* 公共查询页 */
.public-page { min-height: 100vh; background: #f5f7fa; }
.public-header { background: linear-gradient(135deg, #0a3f79 0%, #1a5cad 100%); color: #fff; padding: 48px 24px; text-align: center; }
.public-body { max-width: 720px; margin: 0 auto; padding: 24px 16px 48px; display: grid; gap: 20px; }
.public-card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.public-card__title { margin: 0 0 16px; font-size: 18px; color: #0a3f79; padding-bottom: 10px; border-bottom: 2px solid #e8eef8; }
.public-table { width: 100%; border-collapse: collapse; }
.public-table tr { border-bottom: 1px solid #f0f3f8; }
.public-table td { padding: 10px 0; font-size: 14px; line-height: 1.6; }
.public-table td.label { width: 180px; color: #6f7e92; font-weight: 500; }

/* 工具栏 */
.toolbar { display: grid; grid-template-columns: 1.5fr 1fr 1fr repeat(2, 0.85fr) auto; gap: 12px; align-items: end; margin-bottom: 14px; }
.toolbar-field label { display: block; margin-bottom: 6px; color: #6f7e92; font-size: 12px; }
.toolbar-actions { display: flex; align-items: center; gap: 10px; }

/* 用户信息栏 */
.user-info-bar { margin-bottom: 20px; display: flex; gap: 20px; flex-wrap: wrap; color: #606266; font-size: 14px; padding: 14px 16px; border-radius: 14px; background: linear-gradient(180deg, #f8fbfd, #eef3f8); }

/* 响应式 */
@media (max-width: 1200px) {
  .info-row, .kpi-grid { grid-template-columns: repeat(2, 1fr); }
  .grid-2, .grid-3, .grid-4 { grid-template-columns: 1fr; }
  .toolbar { grid-template-columns: repeat(2, 1fr); }
  .workflow-flow { flex-wrap: wrap; }
  .workflow-arrow { display: none; }
}
</style>
