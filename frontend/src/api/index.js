import axios from 'axios';
import Vue from 'vue';

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
});

// 请求拦截器 - 附加 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = 'Bearer ' + token;
  }
  return config;
});

// 响应拦截器 - 统一错误处理
request.interceptors.response.use(
  response => {
    const res = response.data;
    if (res.code === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.hash = '#/login';
      Vue.prototype.$message.error(res.message || '登录已过期');
      return Promise.reject(new Error(res.message));
    }
    return res;
  },
  error => {
    Vue.prototype.$message.error('网络请求失败');
    return Promise.reject(error);
  }
);

// ===== API 接口 =====

// 认证
export const loginApi = (data) => request.post('/auth/login', data);

// 仪表盘
export const getDashboardStats = () => request.get('/dashboard/stats');
export const getRecentBatches = () => request.get('/dashboard/recent-batches');

// 批次
export const getBatches = (params) => request.get('/batches', { params });
export const getBatchDetail = (id) => request.get(`/batches/${id}`);
export const createBatch = (data) => request.post('/batches', data);
export const updateBatch = (id, data) => request.put(`/batches/${id}`, data);
export const deleteBatch = (id) => request.delete(`/batches/${id}`);

// 质检
export const getInspectionWorkbench = () => request.get('/inspections/workbench');
export const getInspectionRecords = (params) => request.get('/inspections/records', { params });
export const performIncomingInspection = (data) => request.post('/inspections/incoming', data);
export const performFinishedInspection = (data) => request.post('/inspections/finished', data);

// 生产工序
export const getProductionRecords = (batchId) => request.get(`/production/batch/${batchId}`);
export const addProductionRecord = (data) => request.post('/production/records', data);
export const updateProductionRecord = (id, data) => request.put(`/production/records/${id}`, data);
export const deleteProductionRecord = (id) => request.delete(`/production/records/${id}`);

// 放行
export const getReleaseStats = () => request.get('/release/stats');
export const getPendingReleases = () => request.get('/release/pending');
export const getReleasedList = () => request.get('/release/released');
export const getTraceabilityRecords = () => request.get('/release/traceability');
export const executeRelease = (batchId, managerId) => request.post(`/release/execute/${batchId}?managerId=${managerId}`);

// 用户管理
export const getUsers = () => request.get('/users');
export const createUser = (data) => request.post('/users', data);
export const updateUser = (id, data) => request.put(`/users/${id}`, data);
export const deleteUser = (id) => request.delete(`/users/${id}`);

// 企业信息
export const getEnterpriseInfo = () => request.get('/enterprise');
export const updateEnterpriseInfo = (data) => request.put('/enterprise', data);

// 公告
export const getLatestAnnouncement = () => request.get('/announcements/latest');
export const updateAnnouncement = (data) => request.put('/announcements', data);

// 公共追溯查询
export const getPublicTrace = (code) => request.get(`/public/trace/${code}`);

export default request;
