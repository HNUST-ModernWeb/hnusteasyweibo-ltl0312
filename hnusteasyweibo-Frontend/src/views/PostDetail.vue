<!--
  @组件名 PostDetail
  @描述 帖子详情页，展示帖子内容、评论、点赞功能
  @示例 <PostDetail />
-->
<template>
  <div class="post-detail-container">
    <div v-if="isLoading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
    <template v-else-if="post">
      <div class="post-card">
        <div class="post-header">
          <div class="user-info" @click="goToUserProfile(post.userId)">
            <img :src="resolveUrl(post.avatar)" alt="用户头像" class="avatar" />
            <div class="user-details">
              <div class="username">{{ post.username }}</div>
              <div class="post-meta">
                <span class="post-category">{{ post.categoryName }}</span>
                <span class="post-time">{{ formatTime(post.createdAt) }}</span>
              </div>
            </div>
          </div>
          <div v-if="isCurrentUser" class="post-actions" @click.stop>
            <el-button v-if="isEditable" size="small" type="primary" text @click="editPost">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button size="small" type="danger" text @click="handleDeletePost">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </div>
        </div>

        <div class="post-content">
          <p>{{ post.content }}</p>
          <div v-if="post.image" class="post-media">
            <template v-for="(file, index) in parseFiles(post.image)" :key="index">
              <div v-if="isImageFile(file)" class="media-image-wrapper">
                <img :src="resolveUrl(file)" alt="分享图片" class="post-image" loading="lazy" />
                <a :href="getDownloadUrl(file)" target="_blank" class="file-download image-download" download @click.stop>下载图片</a>
              </div>
              <div v-else-if="isVideoFile(file)" class="media-video-wrapper">
                <video :src="resolveUrl(file)" controls class="post-video" preload="metadata" playsinline />
              </div>
              <div v-else class="media-file-wrapper">
                <div class="file-info">
                  <el-icon :size="24"><Document /></el-icon>
                  <div class="file-details">
                    <span class="file-name">{{ getFileName(file) }}</span>
                  </div>
                  <a :href="getDownloadUrl(file)" target="_blank" class="file-download">
                    <el-icon><Download /></el-icon> 下载
                  </a>
                </div>
              </div>
            </template>
          </div>
        </div>

        <div class="post-stats">
          <div class="stat-item stat-comment">
            <el-icon :size="16"><ChatDotRound /></el-icon>
            <span>{{ post.commentsCount || 0 }}</span>
          </div>
          <div class="stat-item stat-like" @click="toggleLike">
            <svg class="heart-icon" :class="{ 'heart-liked': hasLiked }" viewBox="0 0 24 24"
                 :fill="hasLiked ? '#f56c6c' : 'none'" :stroke="hasLiked ? '#f56c6c' : 'currentColor'" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
            <span>{{ post.likesCount || 0 }}</span>
          </div>
        </div>
      </div>

      <div class="comments-section">
        <h3 class="comments-title">评论 ({{ post.commentsCount || 0 }})</h3>

        <div v-if="isLoggedIn" class="comment-form">
          <el-input v-model="commentContent" type="textarea" :rows="3" placeholder="写下你的评论..." maxlength="500" show-word-limit />
          <el-button type="primary" @click="submitComment" :loading="isSubmittingComment" :disabled="isSubmittingComment" class="comment-submit-btn">
            发表评论
          </el-button>
        </div>
        <div v-else class="login-prompt">
          <router-link to="/login">登录</router-link> 后可以发表评论
        </div>

        <div v-if="isLoadingComments" class="loading-container">
          <el-icon class="is-loading"><Loading /></el-icon>
        </div>
        <div v-else-if="comments.length === 0" class="no-comments">
          <el-empty description="暂无评论" :image-size="60" />
        </div>
        <div v-else class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <div class="comment-user-info">
                <span class="comment-username">{{ comment.username || '用户' + comment.userId }}</span>
                <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
              </div>
              <el-button v-if="isCommentOwner(comment.userId)" size="small" type="danger" text @click="handleDeleteComment(comment.id)">
                删除
              </el-button>
            </div>
            <p class="comment-content">{{ comment.content }}</p>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, View, ChatDotRound, Edit, Delete, Document, Download } from '@element-plus/icons-vue'
import { getPost, likePost, unlikePost, isLiked, createComment, getComments, deleteComment, deletePost as deletePostApi } from '../api/post'

const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

export default {
  name: 'PostDetail',
  components: { Loading, View, ChatDotRound, Edit, Delete, Document, Download },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const post = ref(null)
    const isLoading = ref(true)
    const hasLiked = ref(false)
    const comments = ref([])
    const isLoadingComments = ref(false)
    const commentContent = ref('')
    const isSubmittingComment = ref(false)
    const _isLikeThrottling = ref(false)

    const isLoggedIn = computed(() => {
      const userStr = localStorage.getItem('user')
      if (!userStr) return false
      try { return !!JSON.parse(userStr).token } catch { return false }
    })

    const isCurrentUser = computed(() => {
      if (!post.value) return false
      const userStr = localStorage.getItem('user')
      if (!userStr) return false
      try { return Number(JSON.parse(userStr).id) === Number(post.value.userId) } catch { return false }
    })

    const isEditable = computed(() => {
      if (!post.value || !post.value.createdAt) return false
      return (Date.now() - new Date(post.value.createdAt).getTime()) <= 10 * 60 * 1000
    })

    const resolveUrl = (url) => {
      if (!url) return DEFAULT_AVATAR
      if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('/uploads') || url.startsWith('blob:')) return url
      if (url.startsWith('/')) return url
      return url
    }

    const loadPost = async () => {
      isLoading.value = true
      try {
        const response = await getPost(route.params.id)
        if (response.code === 200) post.value = response.data
        else { ElMessage.error('帖子不存在'); router.replace('/') }
      } catch (err) { ElMessage.error('加载失败'); router.replace('/') }
      finally { isLoading.value = false }
    }

    const checkLiked = async () => {
      if (!isLoggedIn.value) return
      try {
        const response = await isLiked(route.params.id)
        if (response.code === 200) hasLiked.value = response.data
      } catch (err) { console.error('检查点赞状态失败:', err) }
    }

    const toggleLike = async () => {
      if (!isLoggedIn.value) { ElMessage.warning('请先登录'); return }
      if (_isLikeThrottling.value) return
      _isLikeThrottling.value = true
      try {
        if (hasLiked.value) {
          const response = await unlikePost(post.value.id)
          if (response.code === 200) { hasLiked.value = false; post.value.likesCount = Math.max(0, (post.value.likesCount || 1) - 1) }
        } else {
          const response = await likePost(post.value.id)
          if (response.code === 200) { hasLiked.value = true; post.value.likesCount = (post.value.likesCount || 0) + 1 }
        }
      } catch (err) { ElMessage.error(err.message || '操作失败') }
      finally { setTimeout(() => { _isLikeThrottling.value = false }, 500) }
    }

    const loadComments = async () => {
      isLoadingComments.value = true
      try {
        const response = await getComments(route.params.id, 1, 50)
        if (response.code === 200) comments.value = response.data.items || []
      } catch (err) { console.error('加载评论失败:', err) }
      finally { isLoadingComments.value = false }
    }

    const submitComment = async () => {
      if (isSubmittingComment.value) return
      if (!commentContent.value.trim()) { ElMessage.warning('评论内容不能为空'); return }
      isSubmittingComment.value = true
      try {
        const response = await createComment(route.params.id, commentContent.value.trim())
        if (response.code === 200) {
          ElMessage.success('评论成功')
          commentContent.value = ''
          loadComments()
          if (post.value) post.value.commentsCount = (post.value.commentsCount || 0) + 1
        } else { ElMessage.error(response.message || '评论失败') }
      } catch (err) { ElMessage.error(err.message || '评论失败') }
      finally { isSubmittingComment.value = false }
    }

    const handleDeleteComment = async (commentId) => {
      try {
        await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
        const response = await deleteComment(commentId)
        if (response.code === 200) {
          ElMessage.success('评论已删除')
          loadComments()
          if (post.value) post.value.commentsCount = Math.max(0, (post.value.commentsCount || 1) - 1)
        } else ElMessage.error(response.message || '删除失败')
      } catch (action) { if (action !== 'cancel') ElMessage.error('删除失败') }
    }

    const isCommentOwner = (userId) => {
      const userStr = localStorage.getItem('user')
      if (!userStr) return false
      try { return Number(JSON.parse(userStr).id) === Number(userId) } catch { return false }
    }

    const editPost = () => { router.push(`/publish?edit=${post.value.id}`) }

    const handleDeletePost = async () => {
      try {
        await ElMessageBox.confirm('确定要删除这篇帖子吗？删除后不可恢复。', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
        const response = await deletePostApi(post.value.id)
        if (response.code === 200) { ElMessage.success('帖子已删除'); router.replace('/') }
        else ElMessage.error(response.message || '删除失败')
      } catch (action) { if (action !== 'cancel') ElMessage.error('删除失败') }
    }

    const goToUserProfile = (userId) => { router.push(`/user/${userId}`) }

    const parseFiles = (imageStr) => {
      if (!imageStr) return []
      return imageStr.split(',').map(f => f.trim()).filter(Boolean)
    }

    const isImageFile = (url) => /\.(jpg|jpeg|png|gif|bmp|webp|svg)(\?.*)?$/i.test(url)
    const isVideoFile = (url) => /\.(mp4|webm|ogg|mov|avi)(\?.*)?$/i.test(url)
    const getFileName = (url) => { try { return decodeURIComponent(url.split('/').pop()) } catch { return url } }
    const getDownloadUrl = (file) => `/api/files/download/${encodeURIComponent(file.split('/').pop())}`

    const formatTime = (time) => {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)
      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      if (hours < 24) return `${hours}小时前`
      if (days < 30) return `${days}天前`
      return date.toLocaleDateString()
    }

    onMounted(() => { loadPost(); checkLiked(); loadComments() })

    return {
      post, isLoading, hasLiked, comments, isLoadingComments, commentContent,
      isSubmittingComment, isLoggedIn, isCurrentUser, isEditable,
      toggleLike, submitComment, handleDeleteComment, isCommentOwner,
      editPost, handleDeletePost, goToUserProfile, resolveUrl, parseFiles,
      isImageFile, isVideoFile, getFileName, getDownloadUrl, formatTime
    }
  }
}
</script>

<style scoped>
.post-detail-container { max-width: 800px; margin: 0 auto; }

.loading-container {
  display: flex; align-items: center; justify-content: center;
  padding: 60px 0; gap: 10px; color: #909399;
}

.post-card {
  background-color: #fff; border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); padding: 24px; margin-bottom: 20px;
}

.post-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }

.user-info { display: flex; align-items: center; gap: 12px; cursor: pointer; }

.avatar {
  width: 48px; height: 48px; border-radius: 50%; object-fit: cover;
  border: 2px solid #f0f0f0; transition: border-color 0.3s;
}

.user-info:hover .avatar { border-color: #667eea; }

.user-details { display: flex; flex-direction: column; }
.username { font-weight: 600; font-size: 16px; color: #303133; }
.post-meta { display: flex; gap: 8px; font-size: 12px; color: #909399; }

.post-category {
  background: linear-gradient(135deg, #667eea20, #764ba220);
  color: #667eea; padding: 2px 8px; border-radius: 10px; font-weight: 500;
}

.post-actions { display: flex; gap: 4px; }

.post-content { font-size: 16px; line-height: 1.8; color: #303133; margin-bottom: 16px; }

.post-media { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 12px; }

.media-image-wrapper { max-width: 100%; border-radius: 8px; overflow: hidden; display: flex; flex-direction: column; gap: 6px; }
.post-image { max-width: 100%; height: auto; max-height: 360px; object-fit: contain; border-radius: 8px; background: #f5f7fa; }
.image-download { align-self: flex-start; }

.media-video-wrapper { width: 100%; max-width: 420px; border-radius: 8px; overflow: hidden; }
.post-video { width: 100%; max-height: 240px; object-fit: contain; border-radius: 8px; background: #000; }

.media-file-wrapper { width: 100%; padding: 14px 18px; background: #f5f7fa; border-radius: 8px; border: 1px dashed #dcdfe6; }
.file-info { display: flex; align-items: center; gap: 12px; font-size: 14px; color: #606266; }
.file-details { flex: 1; }
.file-name { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.file-download { color: #667eea; text-decoration: none; font-size: 13px; display: flex; align-items: center; gap: 4px; transition: color 0.3s; }
.file-download:hover { color: #764ba2; }

.post-stats {
  display: flex; gap: 24px; padding-top: 16px;
  border-top: 1px solid #f0f0f0; color: #909399; font-size: 14px;
}

.stat-item { display: flex; align-items: center; gap: 5px; transition: color 0.3s; }
.stat-view:hover { color: #409EFF; }
.stat-comment:hover { color: #67c23a; }
.stat-like { cursor: pointer; }
.stat-like:hover { color: #f56c6c; }

.heart-icon { width: 18px; height: 18px; transition: all 0.3s; }
.heart-liked { animation: heartBeat 0.3s ease-in-out; }

@keyframes heartBeat {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

.comments-section {
  background-color: #fff; border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); padding: 24px;
}

.comments-title {
  font-size: 17px; font-weight: 700; margin: 0 0 16px;
  padding-bottom: 12px; border-bottom: 1px solid #f0f0f0; color: #303133;
}

.comment-form { margin-bottom: 20px; }
.comment-submit-btn { margin-top: 10px; }

.login-prompt { padding: 16px; text-align: center; color: #909399; margin-bottom: 16px; }
.login-prompt a { color: #667eea; text-decoration: none; font-weight: 500; }

.no-comments { padding: 20px 0; }

.comment-list { display: flex; flex-direction: column; }
.comment-item { padding: 14px 0; border-bottom: 1px solid #f5f5f5; }
.comment-item:last-child { border-bottom: none; }

.comment-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.comment-user-info { display: flex; align-items: center; gap: 8px; }
.comment-username { font-weight: 500; font-size: 14px; color: #303133; }
.comment-time { font-size: 12px; color: #c0c4cc; }

.comment-content { font-size: 14px; color: #606266; line-height: 1.6; margin: 0; }

@media (max-width: 768px) {
  .post-card { padding: 16px; }
  .comments-section { padding: 16px; }
  .post-content { font-size: 15px; }
}
</style>
