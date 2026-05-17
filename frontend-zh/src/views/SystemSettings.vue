<template>
  <div>
    <div class="breadcrumb"><a @click="$router.push('/home')">首页</a> / <span>系统公告</span></div>
    <div style="margin-bottom:20px">
      <h1 style="font-size:22px;margin-bottom:4px">系统公告</h1>
      <p style="color:#6f7e92;font-size:13px">当前系统通知与公告管理</p>
    </div>
    <div style="max-width:800px">
      <div class="card" v-if="announcement">
        <div class="card-header">
          <div><h3>当前公告</h3></div>
          <el-button v-if="isAdmin" type="primary" size="small" plain @click="showEdit">编辑公告</el-button>
        </div>
        <div class="card-body">
          <div style="border:1px solid #e8edf4;border-radius:12px;background:#fbfcfe;padding:18px">
            <strong style="display:block;margin-bottom:10px;font-size:18px;color:#17324f">{{ announcement.title }}</strong>
            <p style="color:#6f7e92;line-height:1.8;margin:0">{{ announcement.content }}</p>
            <span style="display:block;margin-top:12px;font-size:13px;color:#6f7e92">Updated {{ announcement.updateTime || announcement.createTime }}</span>
          </div>
        </div>
      </div>
      <div v-else style="text-align:center;padding:60px;color:#6f7e92">
        <i class="el-icon-bell" style="font-size:48px;color:#d1d5db;display:block;margin-bottom:12px"></i>
        <strong style="display:block;color:#17324f;margin-bottom:8px">暂无公告</strong>
      </div>

      <el-dialog title="编辑公告" :visible.sync="dialogVisible" width="600px">
        <el-form :model="form" label-width="80px">
          <el-form-item label="标题"><el-input v-model="form.title"></el-input></el-form-item>
          <el-form-item label="内容"><el-input v-model="form.content" type="textarea" rows="6"></el-input></el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveAnnouncement">保存</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { getLatestAnnouncement, updateAnnouncement } from '../api';
export default {
  data() {
    return {
      announcement: null,
      dialogVisible: false,
      form: { title: '', content: '' }
    };
  },
  computed: {
    isAdmin() { const u = JSON.parse(localStorage.getItem('user') || '{}'); return u.role === 'ADMIN'; }
  },
  created() { getLatestAnnouncement().then(res => { if (res.code === 200 && res.data) this.announcement = res.data; }); },
  methods: {
    showEdit() {
      this.form = { title: this.announcement.title, content: this.announcement.content };
      this.dialogVisible = true;
    },
    saveAnnouncement() {
      const user = JSON.parse(localStorage.getItem('user'));
      updateAnnouncement({ title: this.form.title, content: this.form.content, publisherId: user.userId }).then(res => {
        if (res.code === 200) {
          this.$message.success('保存成功');
          this.dialogVisible = false;
          getLatestAnnouncement().then(res => { if (res.code === 200 && res.data) this.announcement = res.data; });
        } else this.$message.error(res.message);
      });
    }
  }
};
</script>
