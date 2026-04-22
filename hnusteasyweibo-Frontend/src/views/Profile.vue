<!--
  @组件名 Profile
  @描述 当前登录用户的个人中心，展示用户信息、帖子管理、资料编辑
  @示例 <Profile />
-->
<template>
  <div class="profile-container">
    <div class="profile-header">
      <div class="profile-avatar">
        <img :src="userInfo.avatar || DEFAULT_AVATAR" alt="用户头像">
        <div class="avatar-overlay" @click="triggerAvatarUpload">
          <el-icon :size="20"><Camera /></el-icon>
          <span>更换头像</span>
        </div>
        <input ref="avatarInputRef" type="file" accept="image/*" style="display:none" @change="handleAvatarChange" />
      </div>
      <div class="profile-info">
        <h2 class="profile-username">{{ userInfo.username }}</h2>
        <p class="profile-bio">{{ userInfo.bio || '这个人很懒，还没有填写个人简介' }}</p>
        <div class="profile-stats">
          <div class="stat-item">
            <el-icon :size="15"><Document /></el-icon>
            <div class="stat-detail">
              <span class="stat-value">{{ statistics.postCount || 0 }}</span>
              <span class="stat-label">帖子</span>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-detail">
              <span class="stat-value">{{ statistics.followerCount || 0 }}</span>
              <span class="stat-label">关注者</span>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-detail">
              <span class="stat-value">{{ statistics.followingCount || 0 }}</span>
              <span class="stat-label">关注中</span>
            </div>
          </div>
          <div class="stat-item">
            <svg class="heart-icon-stat" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
            <div class="stat-detail">
              <span class="stat-value">{{ statistics.likeCount || 0 }}</span>
              <span class="stat-label">获赞</span>
            </div>
          </div>
        </div>
        <el-button type="primary" @click="editProfile" class="edit-button">
          <el-icon><Edit /></el-icon> 编辑资料
        </el-button>
      </div>
    </div>

    <div class="profile-posts">
      <h3 class="posts-title"><el-icon><Document /></el-icon> 我的帖子</h3>
      <div v-if="isLoading" class="loading-container"><el-icon class="is-loading"><Loading /></el-icon><span>加载中...</span></div>
      <div v-else-if="hasError" class="error-container">
        <el-alert title="加载失败" type="error" :closable="false" show-icon />
        <el-button type="primary" @click="loadPosts" style="margin-top: 12px;">重试</el-button>
      </div>
      <div v-else-if="posts.length === 0" class="empty-container">
        <el-empty description="还没有发布帖子">
          <el-button type="primary" @click="$router.push('/publish')">发布第一篇帖子</el-button>
        </el-empty>
      </div>
      <div v-else class="post-list">
        <div v-for="post in posts" :key="post.id" class="post-item" @click="goToPost(post.id)">
          <div class="post-content">
            <p class="post-text">{{ post.content }}</p>
          </div>
          <div class="post-meta">
            <span class="post-time">{{ formatTime(post.createdAt) }}</span>
            <div class="post-stats-mini">
              <span class="action-item"><el-icon :size="13"><ChatDotRound /></el-icon> {{ post.commentsCount || 0 }}</span>
              <span class="action-item">
                <svg class="heart-icon-sm" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
                {{ post.likesCount || 0 }}
              </span>
            </div>
          </div>
          <div class="post-buttons" @click.stop>
            <el-button v-if="isEditable(post)" size="small" type="primary" text @click="editPost(post.id)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button size="small" type="danger" text @click="handleDeletePost(post.id)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </div>
        </div>
      </div>
      <div v-if="total > 0" class="pagination-container">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper" :total="total"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>

    <el-dialog v-model="editDialogVisible" title="编辑资料" width="500px" :close-on-click-modal="false">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" placeholder="请输入用户名" maxlength="20" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input v-model="editForm.bio" type="textarea" placeholder="请输入个人简介" :rows="3" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="头像">
          <div class="avatar-upload-area">
            <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar-preview" />
            <el-button size="small" @click="triggerEditAvatarUpload">选择图片</el-button>
            <input ref="editAvatarInputRef" type="file" accept="image/*" style="display:none" @change="handleEditAvatarChange" />
          </div>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProfile" :loading="isSavingProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Document, View, ChatDotRound, Edit, Delete, Camera } from '@element-plus/icons-vue'
import { getUserProfile, updateUserProfile, getUserStatistics, uploadAvatar } from '../api/user'
import { getPostsByUser, deletePost as deletePostApi } from '../api/post'

const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

export default {
  name: 'Profile',
  components: { Loading, Document, View, ChatDotRound, Edit, Delete, Camera },
  setup() {
    const router = useRouter()
    const userInfo = ref({})
    const statistics = ref({ postCount: 0, commentCount: 0, likeCount: 0, followerCount: 0, followingCount: 0 })
    const posts = ref([])
    const isLoading = ref(false)
    const hasError = ref(false)
    const page = ref(1)
    const size = ref(10)
    const total = ref(0)
    const editDialogVisible = ref(false)
    const editForm = ref({})
    const isSavingProfile = ref(false)
    const avatarInputRef = ref(null)
    const editAvatarInputRef = ref(null)
    const isUploadingAvatar = ref(false)

    const loadUserInfo = async () => {
      try {
        const response = await getUserProfile()
        if (response.code === 200) userInfo.value = response.data
      } catch (err) { console.error('获取用户信息失败:', err) }
    }

    const loadStatistics = async () => {
      try {
        const response = await getUserStatistics()
        if (response.code === 200) statistics.value = response.data
      } catch (err) { console.error('获取统计信息失败:', err) }
    }

    const loadPosts = async () => {
      isLoading.value = true; hasError.value = false
      try {
        const response = await getPostsByUser(page.value, size.value)
        if (response.code === 200) { posts.value = response.data.items; total.value = response.data.total }
        else hasError.value = true
      } catch (err) { hasError.value = true } finally { isLoading.value = false }
    }

    const triggerAvatarUpload = () => { avatarInputRef.value?.click() }
    const triggerEditAvatarUpload = () => { editAvatarInputRef.value?.click() }

    const handleAvatarChange = async (event) => {
      const file = event.target.files[0]
      if (!file) return
      if (!file.type.startsWith('image/')) { ElMessage.error('只能上传图片文件'); return }
      if (file.size > 5 * 1024 * 1024) { ElMessage.error('图片大小不能超过5MB'); return }
      isUploadingAvatar.value = true
      try {
        const response = await uploadAvatar(file)
        if (response.code === 200) {
          await updateUserProfile({ avatar: response.data })
          userInfo.value.avatar = response.data
          ElMessage.success('头像更新成功')
          loadUserInfo()
        } else { ElMessage.error(response.message || '头像上传失败') }
      } catch (err) { ElMessage.error('头像上传失败') }
      finally { isUploadingAvatar.value = false; event.target.value = '' }
    }

    const handleEditAvatarChange = async (event) => {
      const file = event.target.files[0]
      if (!file) return
      if (!file.type.startsWith('image/')) { ElMessage.error('只能上传图片文件'); return }
      if (file.size > 5 * 1024 * 1024) { ElMessage.error('图片大小不能超过5MB'); return }
      try {
        const response = await uploadAvatar(file)
        if (response.code === 200) { editForm.value.avatar = response.data }
        else ElMessage.error(response.message || '头像上传失败')
      } catch (err) { ElMessage.error('头像上传失败') }
      finally { event.target.value = '' }
    }

    const editProfile = () => { editForm.value = { ...userInfo.value }; editDialogVisible.value = true }

    const saveProfile = async () => {
      if (isSavingProfile.value) return
      isSavingProfile.value = true
      try {
        const response = await updateUserProfile(editForm.value)
        if (response.code === 200) { userInfo.value = response.data; editDialogVisible.value = false; ElMessage.success('资料更新成功') }
        else ElMessage.error(response.message || '更新资料失败')
      } catch (err) { ElMessage.error('更新资料失败') } finally { isSavingProfile.value = false }
    }

    const goToPost = (postId) => { router.push(`/post/${postId}`) }
    const editPost = (postId) => { router.push(`/publish?edit=${postId}`) }

    const isEditable = (post) => {
      if (!post.createdAt) return false
      return (Date.now() - new Date(post.createdAt).getTime()) <= 10 * 60 * 1000
    }

    const handleDeletePost = async (postId) => {
      try {
        await ElMessageBox.confirm('确定要删除这篇帖子吗？删除后不可恢复。', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
        const response = await deletePostApi(postId)
        if (response.code === 200) { ElMessage.success('帖子删除成功'); loadPosts(); loadStatistics() }
        else ElMessage.error(response.message || '删除帖子失败')
      } catch (action) { if (action !== 'cancel') ElMessage.error('删除帖子失败') }
    }

    const handleSizeChange = (newSize) => { size.value = newSize; page.value = 1; loadPosts() }
    const handleCurrentChange = (newPage) => { page.value = newPage; loadPosts() }

    const formatTime = (time) => {
      if (!time) return ''
      const date = new Date(time); const now = new Date(); const diff = now - date
      const minutes = Math.floor(diff / 60000); const hours = Math.floor(diff / 3600000); const days = Math.floor(diff / 86400000)
      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      if (hours < 24) return `${hours}小时前`
      if (days < 30) return `${days}天前`
      return date.toLocaleDateString()
    }

    onMounted(() => { loadUserInfo(); loadStatistics(); loadPosts() })

    return {
      userInfo, statistics, posts, isLoading, hasError, page, size, total,
      editDialogVisible, editForm, isSavingProfile, DEFAULT_AVATAR, avatarInputRef,
      editAvatarInputRef, isUploadingAvatar, loadPosts, editProfile, saveProfile,
      goToPost, editPost, isEditable, handleDeletePost, handleSizeChange,
      handleCurrentChange, formatTime, triggerAvatarUpload, triggerEditAvatarUpload,
      handleAvatarChange, handleEditAvatarChange
    }
  }
}
</script>

<style scoped>
.profile-container { max-width: 1000px; margin: 0 auto; }

.profile-header {
  display: flex; padding: 30px; background-color: #fff;
  border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); margin-bottom: 20px;
}

.profile-avatar {
  width: 120px; height: 120px; margin-right: 30px; flex-shrink: 0;
  position: relative; border-radius: 50%; overflow: hidden;
}

.profile-avatar img { width: 100%; height: 100%; object-fit: cover; }

.avatar-overlay {
  position: absolute; bottom: 0; left: 0; right: 0;
  background: rgba(0,0,0,0.5); color: white;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 6px 0; font-size: 11px; cursor: pointer;
  opacity: 0; transition: opacity 0.3s;
}

.profile-avatar:hover .avatar-overlay { opacity: 1; }

.profile-info { flex: 1; }
.profile-username { font-size: 22px; font-weight: 700; margin: 0 0 6px; color: #303133; }
.profile-bio { color: #909399; margin: 0 0 16px; line-height: 1.6; font-size: 14px; }

.profile-stats { display: flex; margin-bottom: 16px; gap: 24px; }
.stat-item { display: flex; align-items: center; gap: 6px; color: #606266; }
.stat-detail { display: flex; flex-direction: column; }
.stat-value { font-size: 18px; font-weight: 700; color: #303133; line-height: 1.2; }
.stat-label { font-size: 11px; color: #909399; }
.heart-icon-stat { width: 15px; height: 15px; color: #f56c6c; }

.edit-button { margin-top: 8px; }

.profile-posts {
  background-color: #fff; border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); padding: 24px;
}

.posts-title {
  font-size: 17px; font-weight: 700; margin: 0 0 16px;
  padding-bottom: 12px; border-bottom: 1px solid #f0f0f0;
  display: flex; align-items: center; gap: 6px; color: #303133;
}

.loading-container { display: flex; align-items: center; justify-content: center; padding: 40px 0; gap: 10px; color: #909399; }
.error-container { text-align: center; padding: 40px 0; }
.empty-container { padding: 40px 0; }

.post-list { display: flex; flex-direction: column; gap: 12px; }

.post-item {
  padding: 16px; border: 1px solid #f0f0f0; border-radius: 10px;
  transition: all 0.3s; cursor: pointer;
}
.post-item:hover { box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06); border-color: #e4e7ed; }

.post-content { margin-bottom: 8px; }
.post-text { color: #606266; line-height: 1.6; margin: 0; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; font-size: 14px; }

.post-meta { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; color: #909399; font-size: 12px; }
.post-stats-mini { display: flex; gap: 12px; }
.action-item { display: flex; align-items: center; gap: 3px; }
.heart-icon-sm { width: 13px; height: 13px; }

.post-buttons { display: flex; gap: 8px; justify-content: flex-end; }
.pagination-container { margin-top: 20px; display: flex; justify-content: center; }

.avatar-upload-area { display: flex; align-items: center; gap: 12px; }
.avatar-preview { width: 60px; height: 60px; border-radius: 50%; object-fit: cover; border: 2px solid #f0f0f0; }

@media (max-width: 768px) {
  .profile-container { padding: 0; }
  .profile-header { flex-direction: column; align-items: center; text-align: center; padding: 20px; }
  .profile-avatar { margin-right: 0; margin-bottom: 12px; width: 80px; height: 80px; }
  .profile-stats { justify-content: center; gap: 16px; }
  .edit-button { width: 100%; }
  .post-item { padding: 12px; }
}

body.dark-mode-app .profile-header {
  background-color: #161b22 !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3) !important;
}

body.dark-mode-app .profile-username {
  color: #e5e7eb !important;
}

body.dark-mode-app .profile-bio {
  color: #9ca3af !important;
}

body.dark-mode-app .stat-item {
  color: #9ca3af !important;
}

body.dark-mode-app .stat-value {
  color: #e5e7eb !important;
}

body.dark-mode-app .stat-label {
  color: #6b7280 !important;
}

body.dark-mode-app .profile-posts {
  background-color: #161b22 !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3) !important;
}

body.dark-mode-app .posts-title {
  color: #e5e7eb !important;
  border-bottom-color: #2b3240 !important;
}

body.dark-mode-app .post-item {
  background-color: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .post-text {
  color: #d1d5db !important;
}

body.dark-mode-app .post-meta {
  color: #9ca3af !important;
}

body.dark-mode-app .el-dialog {
  background: #161b22 !important;
  border-color: #2b3240 !important;
}

body.dark-mode-app .el-dialog__header {
  border-bottom: 1px solid #2b3240 !important;
}

body.dark-mode-app .el-dialog__title {
  color: #e5e7eb !important;
}

body.dark-mode-app .el-dialog__body {
  color: #e5e7eb !important;
}

body.dark-mode-app .el-dialog__footer {
  border-top: 1px solid #2b3240 !important;
}

body.dark-mode-app .el-form-item__label {
  color: #e5e7eb !important;
}

body.dark-mode-app .el-input__wrapper {
  background: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .el-input__inner {
  color: #e5e7eb !important;
}

body.dark-mode-app .el-input__placeholder {
  color: rgba(229, 231, 235, 0.5) !important;
}

body.dark-mode-app .el-textarea__inner {
  background: #1f2937 !important;
  border-color: #374151 !important;
  color: #e5e7eb !important;
}

body.dark-mode-app .el-textarea__inner::placeholder {
  color: rgba(229, 231, 235, 0.5) !important;
}

body.dark-mode-app .el-input__count {
  color: #e5e7eb !important;
  background: transparent !important;
}

body.dark-mode-app .el-textarea__count {
  color: #e5e7eb !important;
  background: transparent !important;
}

:deep(.dark .el-input__wrapper) {
  background-color: #2a2a2a;
  border: 1px solid #444;
  color: #ddd;
}

:deep(.dark .el-input__inner) {
  color: #ddd !important;
}

:deep(.dark .el-textarea__inner) {
  background-color: #2a2a2a !important;
  color: #ddd !important;
  border: 1px solid #444 !important;
}

:deep(.dark .el-input__count),
:deep(.dark .el-textarea__count) {
  color: #aaa;
  background: transparent;
}

:deep(.dark textarea::placeholder),
:deep(.dark input::placeholder) {
  color: #888;
}

body.dark-mode-app .el-dialog .el-input__wrapper {
  background-color: #2a2a2a !important;
  border: 1px solid #444 !important;
}

body.dark-mode-app .el-dialog .el-input__inner {
  color: #ddd !important;
}

body.dark-mode-app .el-dialog .el-textarea__inner {
  background-color: #2a2a2a !important;
  color: #ddd !important;
  border: 1px solid #444 !important;
}

/* 全局暗黑模式文本域样式 */
body.dark-mode-app .el-textarea__inner {
  background-color: #2a2a2a !important;
  color: #ddd !important;
  border: 1px solid #444 !important;
}

body.dark-mode-app .el-dialog .el-input__count,
body.dark-mode-app .el-dialog .el-textarea__count {
  color: #aaa !important;
  background: transparent !important;
}

body.dark-mode-app .el-dialog textarea::placeholder,
body.dark-mode-app .el-dialog input::placeholder {
  color: #888 !important;
}

:deep(.dark .el-select__wrapper) {
  background-color: #2a2a2a;
  border: 1px solid #444;
  color: #ddd;
}

:deep(.dark .el-select__placeholder) {
  color: #ccc;
}

:deep(.dark .el-select__suffix) {
  color: #ccc;
}

body.dark-mode-app .el-pagination {
  background: #161b22 !important;
}

body.dark-mode-app .el-pagination__item {
  background: #1f2937 !important;
  border-color: #374151 !important;
  color: #e5e7eb !important;
}

body.dark-mode-app .el-pagination__item:hover {
  color: #8ea2ff !important;
  border-color: #8ea2ff !important;
}

body.dark-mode-app .el-pagination__item.is-current {
  background: #8ea2ff !important;
  border-color: #8ea2ff !important;
  color: #fff !important;
}

body.dark-mode-app .el-pagination__button {
  background: #1f2937 !important;
  border-color: #374151 !important;
  color: #e5e7eb !important;
}

body.dark-mode-app .el-pagination__total {
  color: #e5e7eb !important;
}
</style>
