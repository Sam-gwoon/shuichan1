import axios from 'axios';
import Vue from 'vue';

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
});

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = 'Bearer ' + token;
  }
  return config;
});

request.interceptors.response.use(
  response => {
    const res = response.data;
    if (res.code === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.hash = '#/login';
      Vue.prototype.$message.error(res.message || 'Session expired');
      return Promise.reject(new Error(res.message));
    }
    return res;
  },
  error => {
    Vue.prototype.$message.error('Network request failed');
    return Promise.reject(error);
  }
);

export const loginApi = (data) => request.post('/auth/login', data);

export const getDashboardStats = () => request.get('/dashboard/stats');
export const getRecentBatches = () => request.get('/dashboard/recent-batches');

export const getBatches = (params) => request.get('/batches', { params });
export const getBatchDetail = (id) => request.get(`/batches/${id}`);
export const createBatch = (data) => request.post('/batches', data);

export const getInspectionWorkbench = () => request.get('/inspections/workbench');
export const getInspectionRecords = (params) => request.get('/inspections/records', { params });
export const performIncomingInspection = (data) => request.post('/inspections/incoming', data);
export const performFinishedInspection = (data) => request.post('/inspections/finished', data);

export const getProductionRecords = (batchId) => request.get(`/production/batch/${batchId}`);
export const addProductionRecord = (data) => request.post('/production/records', data);

export const getReleaseStats = () => request.get('/release/stats');
export const getPendingReleases = () => request.get('/release/pending');
export const getReleasedList = () => request.get('/release/released');
export const getTraceabilityRecords = () => request.get('/release/traceability');
export const executeRelease = (batchId, managerId) => request.post(`/release/execute/${batchId}?managerId=${managerId}`);

export const getUsers = () => request.get('/users');
export const createUser = (data) => request.post('/users', data);
export const updateUser = (id, data) => request.put(`/users/${id}`, data);
export const deleteUser = (id) => request.delete(`/users/${id}`);

export const getEnterpriseInfo = () => request.get('/enterprise');
export const updateEnterpriseInfo = (data) => request.put('/enterprise', data);

export const getLatestAnnouncement = () => request.get('/announcements/latest');
export const updateAnnouncement = (data) => request.put('/announcements', data);

export const getPublicTrace = (code) => request.get(`/public/trace/${code}`);

export default request;
