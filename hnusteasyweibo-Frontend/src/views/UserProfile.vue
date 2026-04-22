<!--
  @组件名 UserProfile
  @描述 其他用户的个人主页，展示用户信息、帖子列表、关注功能
  @Props 无（通过路由参数获取用户ID）
  @示例 <UserProfile />
-->
<template>
  <div class="user-profile-container">
    <div v-if="isLoading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
    <template v-else-if="userInfo">
      <div class="profile-header">
        <div class="profile-avatar">
          <img :src="userInfo.avatar || DEFAULT_AVATAR" alt="用户头像">
        </div>
        <div class="profile-info">
          <h2 class="profile-username">{{ userInfo.username }}</h2>
          <p class="profile-bio">{{ userInfo.bio || '这个人很懒，还没有填写个人简介' }}</p>
          <div class="profile-stats">
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.postCount || 0 }}</span>
              <span class="stat-label">帖子</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.followerCount || 0 }}</span>
              <span class="stat-label">关注者</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.followingCount || 0 }}</span>
              <span class="stat-label">关注中</span>
            </div>
          </div>
          <el-button
            v-if="!isSelf"
            :type="userInfo.isFollowing ? 'default' : 'primary'"
            @click="toggleFollow"
            :loading="isFollowLoading"
          >
            {{ userInfo.isFollowing ? '取消关注' : '关注' }}
          </el-button>
        </div>
      </div>

      <div class="profile-posts">
        <h3 class="posts-title">帖子</h3>
        <div v-if="isPostsLoading" class="loading-container">
          <el-icon class="is-loading"><Loading /></el-icon>
        </div>
        <div v-else-if="posts.length === 0" class="empty-container">
          <el-empty description="暂无帖子" />
        </div>
        <div v-else class="post-list">
          <div v-for="post in posts" :key="post.id" class="post-item" @click="goToPost(post.id)">
            <div class="post-content">
              <p class="post-text">{{ post.content }}</p>
              <div v-if="post.image" class="post-media">
                <template v-for="(file, index) in parseFiles(post.image)" :key="index">
                  <img v-if="isImageFile(file)" :src="resolveUrl(file)" class="post-thumb" />
                  <video v-else-if="isVideoFile(file)" :src="resolveUrl(file)" class="post-thumb-video" />
                  <div v-else class="post-file-badge">
                    <el-icon><Document /></el-icon>
                  </div>
                </template>
              </div>
            </div>
            <div class="post-meta">
              <span class="post-time">{{ formatTime(post.createdAt) }}</span>
              <div class="post-stats-mini">
                <span class="action-item"><el-icon :size="13"><View /></el-icon> {{ post.viewCount || 0 }}</span>
                <span class="action-item"><el-icon :size="13"><ChatDotRound /></el-icon> {{ post.commentsCount || 0 }}</span>
                <span class="action-item">
                  <svg class="heart-icon-sm" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
                  {{ post.likesCount || 0 }}
                </span>
              </div>
            </div>
          </div>
        </div>
        <div v-if="total > 0" class="pagination-container">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading, View, ChatDotRound, Document } from '@element-plus/icons-vue'
import { getUserById, followUser, unfollowUser } from '../api/user'
import { getPostListByUser } from '../api/post'

const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

export default {
  name: 'UserProfile',
  components: { Loading, View, ChatDotRound, Document },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const userInfo = ref(null)
    const isLoading = ref(true)
    const posts = ref([])
    const isPostsLoading = ref(false)
    const page = ref(1)
    const size = ref(10)
    const total = ref(0)
    const isFollowLoading = ref(false)

    const isSelf = computed(() => {
      const userStr = localStorage.getItem('user')
      if (!userStr) return false
      try {
        const currentUser = JSON.parse(userStr)
        return Number(currentUser.id) === Number(route.params.id)
      } catch { return false }
    })

    const loadUserInfo = async () => {
      isLoading.value = true
      try {
        const response = await getUserById(route.params.id)
        if (response.code === 200) userInfo.value = response.data
      } catch (err) {
        ElMessage.error('获取用户信息失败')
      } finally {
        isLoading.value = false
      }
    }

    const loadPosts = async () => {
      isPostsLoading.value = true
      try {
        const response = await getPostListByUser(route.params.id, page.value, size.value)
        if (response.code === 200) {
          posts.value = response.data.items
          total.value = response.data.total
        }
      } catch (err) {
        console.error('获取帖子失败:', err)
      } finally {
        isPostsLoading.value = false
      }
    }

    const toggleFollow = async () => {
      if (isFollowLoading.value) return
      isFollowLoading.value = true
      try {
        if (userInfo.value.isFollowing) {
          await unfollowUser(userInfo.value.id)
          userInfo.value.isFollowing = false
          userInfo.value.followerCount--
          ElMessage.success('已取消关注')
        } else {
          await followUser(userInfo.value.id)
          userInfo.value.isFollowing = true
          userInfo.value.followerCount++
          ElMessage.success('关注成功')
        }
      } catch (err) {
        ElMessage.error(err.message || '操作失败')
      } finally {
        isFollowLoading.value = false
      }
    }

    const goToPost = (postId) => { router.push(`/post/${postId}`) }
    const handleSizeChange = (newSize) => { size.value = newSize; page.value = 1; loadPosts() }
    const handleCurrentChange = (newPage) => { page.value = newPage; loadPosts() }

    const parseFiles = (imageStr) => {
      if (!imageStr) return []
      return imageStr.split(',').map(f => f.trim()).filter(Boolean)
    }
    const isImageFile = (url) => /\.(jpg|jpeg|png|gif|bmp|webp|svg)(\?.*)?$/i.test(url)
    const isVideoFile = (url) => /\.(mp4|webm|ogg|mov|avi)(\?.*)?$/i.test(url)
    const resolveUrl = (url) => {
      if (!url) return DEFAULT_AVATAR
      if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('/uploads') || url.startsWith('blob:')) return url
      if (url.startsWith('/')) return url
      return url
    }

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

    onMounted(() => {
      loadUserInfo()
      loadPosts()
    })

    return {
      userInfo, isLoading, posts, isPostsLoading, page, size, total,
      isFollowLoading, isSelf, DEFAULT_AVATAR, goToPost, toggleFollow,
      handleSizeChange, handleCurrentChange, parseFiles, isImageFile,
      isVideoFile, resolveUrl, formatTime
    }
  }
}
</script>

<style scoped>
.user-profile-container { max-width: 1000px; margin: 0 auto; }

.loading-container {
  display: flex; align-items: center; justify-content: center;
  padding: 60px 0; gap: 10px; color: #909399;
}

.profile-header {
  display: flex; padding: 30px; background-color: #fff;
  border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); margin-bottom: 20px;
}

.profile-avatar { width: 100px; height: 100px; margin-right: 24px; flex-shrink: 0; }
.profile-avatar img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; border: 3px solid #f0f0f0; }

.profile-info { flex: 1; }
.profile-username { font-size: 22px; font-weight: 700; margin: 0 0 6px; color: #303133; }
.profile-bio { color: #909399; margin: 0 0 16px; line-height: 1.6; font-size: 14px; }

.profile-stats { display: flex; margin-bottom: 16px; gap: 28px; }
.stat-item { display: flex; flex-direction: column; align-items: center; }
.stat-value { font-size: 18px; font-weight: 700; color: #303133; }
.stat-label { font-size: 12px; color: #909399; }

.profile-posts {
  background-color: #fff; border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); padding: 24px;
}

.posts-title {
  font-size: 17px; font-weight: 700; margin: 0 0 16px;
  padding-bottom: 12px; border-bottom: 1px solid #f0f0f0; color: #303133;
}

.post-list { display: flex; flex-direction: column; gap: 12px; }

.post-item {
  padding: 16px; border: 1px solid #f0f0f0; border-radius: 10px;
  transition: all 0.3s; cursor: pointer;
}
.post-item:hover { box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06); border-color: #e4e7ed; }

.post-text {
  color: #606266; line-height: 1.6; margin: 0 0 8px;
  overflow: hidden; text-overflow: ellipsis;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; font-size: 14px;
}

.post-media { display: flex; gap: 6px; margin-bottom: 8px; flex-wrap: wrap; }
.post-thumb { width: 60px; height: 60px; object-fit: cover; border-radius: 6px; }
.post-thumb-video { width: 120px; height: 60px; object-fit: cover; border-radius: 6px; }
.post-file-badge { width: 60px; height: 60px; background: #f5f7fa; border-radius: 6px; display: flex; align-items: center; justify-content: center; color: #909399; }

.post-meta { display: flex; justify-content: space-between; align-items: center; color: #909399; font-size: 12px; }
.post-stats-mini { display: flex; gap: 12px; }
.action-item { display: flex; align-items: center; gap: 3px; }
.heart-icon-sm { width: 13px; height: 13px; }

.pagination-container { margin-top: 20px; display: flex; justify-content: center; }

.empty-container { padding: 40px 0; }

:deep(.dark .el-pagination) {
  --el-pagination-bg-color: #1e1e1e;
  --el-pagination-button-bg-color: #2a2a2a;
  color: #ddd;
}

:deep(.dark .el-pager li) {
  background-color: #2a2a2a;
  color: #ccc;
}

:deep(.dark .el-pager li.is-active) {
  background-color: #409eff;
  color: #fff;
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

@media (max-width: 768px) {
  .profile-header { flex-direction: column; align-items: center; text-align: center; padding: 20px; }
  .profile-avatar { margin-right: 0; margin-bottom: 12px; width: 80px; height: 80px; }
  .profile-stats { justify-content: center; }
}
</style>
