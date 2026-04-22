<!--
  @组件名 Home
  @描述 首页，展示帖子列表、分类筛选、搜索、分页
  @示例 <Home />
-->
<template>
  <div class="home">
    <div class="category-nav">
      <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange" type="border-card">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane v-for="category in categories" :key="category.id" :label="category.name" :name="String(category.id)" />
      </el-tabs>
    </div>

    <div v-if="searchKeyword" class="search-info">
      <span>搜索结果: "{{ searchKeyword }}"</span>
      <el-button size="small" text @click="clearSearch">清除搜索</el-button>
    </div>

    <div class="post-list">
      <div v-if="isLoading" class="loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      <div v-else-if="hasError" class="error">
        <el-alert title="加载失败" type="error" :closable="false" show-icon>
          {{ errorMessage }}
          <el-button size="small" @click="loadPosts">重试</el-button>
        </el-alert>
      </div>
      <div v-else-if="posts.length === 0" class="empty">
        <el-empty description="暂无帖子" />
      </div>
      <transition-group v-else name="post-list" tag="div" class="posts-container">
        <div v-for="post in posts" :key="post.id" class="post-card">
          <div class="post-card-clickable" @click="goToPostDetail(post.id)">
            <div class="post-header">
              <div class="user-info" @click.stop="goToUserProfile(post.userId)">
                <img :src="resolveUrl(post.avatar)" alt="用户头像" class="avatar" />
                <div class="user-details">
                  <div class="username">{{ post.username }}</div>
                  <div class="post-meta">
                    <span class="post-category">{{ post.categoryName }}</span>
                    <span class="post-time">{{ formatTime(post.createdAt) }}</span>
                  </div>
                </div>
              </div>
              <div v-if="isCurrentUser(post.userId)" class="post-actions-header" @click.stop>
                <el-dropdown trigger="click">
                  <span class="el-dropdown-link">
                    <el-icon :size="18"><MoreFilled /></el-icon>
                  </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item v-if="isEditable(post)" @click.stop="editPost(post)">编辑</el-dropdown-item>
                      <el-dropdown-item @click.stop="handleDeletePost(post.id)" style="color: #f56c6c;">删除</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
            <div class="post-content">
              <p>{{ post.content }}</p>
              <div v-if="post.image" class="post-media">
                <template v-for="(file, index) in parseFiles(post.image)" :key="index">
                  <div v-if="isImageFile(file)" class="media-image-wrapper">
                    <img :src="resolveUrl(file)" alt="分享图片" class="post-image" />
                  </div>
                  <div v-else-if="isVideoFile(file)" class="media-video-wrapper">
                    <video :src="resolveUrl(file)" controls class="post-video" />
                  </div>
                  <div v-else class="media-file-wrapper" @click.stop>
                    <div class="file-info">
                      <el-icon :size="24"><Document /></el-icon>
                      <span class="file-name">{{ getFileName(file) }}</span>
                      <a :href="getDownloadUrl(file)" target="_blank" class="file-download" @click.stop>
                        <el-icon><Download /></el-icon> 下载
                      </a>
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>
          <div class="post-stats">
            <div class="stat-item stat-comment" @click="goToPostDetail(post.id)">
              <el-icon :size="15"><ChatDotRound /></el-icon>
              <span>{{ post.commentsCount || 0 }}</span>
            </div>
            <div class="stat-item stat-like" @click.stop="toggleLike(post)">
              <svg class="heart-icon" :class="{ 'heart-liked': post.liked }" viewBox="0 0 24 24"
                :fill="post.liked ? '#f56c6c' : 'none'" :stroke="post.liked ? '#f56c6c' : 'currentColor'" stroke-width="2">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
              <span>{{ post.likesCount || 0 }}</span>
            </div>
          </div>
        </div>
      </transition-group>
    </div>

    <div v-if="total > 0" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { getPostList, getPostListByCategory, getCategories, deletePost as deletePostApi, searchPosts, likePost, unlikePost, isLiked } from '@/api/post'
import { Loading, MoreFilled, View, ChatDotRound, Document, Download } from '@element-plus/icons-vue'

const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

export default {
  name: 'Home',
  components: { Loading, MoreFilled, View, ChatDotRound, Document, Download },
  data() {
    return {
      posts: [],
      categories: [],
      activeCategory: 'all',
      isLoading: false,
      hasError: false,
      errorMessage: null,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      searchKeyword: '',
      _likeThrottling: {}
    }
  },
  watch: {
    '$route.query.search': {
      handler(val) {
        if (val) {
          this.searchKeyword = val
          this.currentPage = 1
          this.loadPosts()
        }
      },
      immediate: true
    }
  },
  mounted() {
    this.loadCategories()
    this.loadPosts()
  },
  methods: {
    resolveUrl(url) {
      if (!url) return DEFAULT_AVATAR
      if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('/uploads') || url.startsWith('blob:')) return url
      if (url.startsWith('/')) return url
      return url
    },
    async loadCategories() {
      try {
        const response = await getCategories()
        if (response.code === 200) this.categories = response.data
      } catch (error) {
        console.error('获取分类失败', error)
      }
    },
    async loadPosts() {
      this.isLoading = true
      this.hasError = false
      this.errorMessage = null
      try {
        let response
        if (this.searchKeyword) {
          response = await searchPosts(this.searchKeyword, this.currentPage, this.pageSize)
        } else if (this.activeCategory === 'all') {
          response = await getPostList(this.currentPage, this.pageSize)
        } else {
          response = await getPostListByCategory(this.activeCategory, this.currentPage, this.pageSize)
        }
        if (response.code === 200) {
          this.posts = (response.data.items || []).map(post => ({ ...post, liked: false }))
          this.total = response.data.total || 0
          this.loadLikeStatuses()
        } else {
          this.hasError = true
          this.errorMessage = response.message || '加载失败'
        }
      } catch (error) {
        this.hasError = true
        this.errorMessage = error.message || '加载失败'
      } finally {
        this.isLoading = false
      }
    },
    async loadLikeStatuses() {
      const userStr = localStorage.getItem('user')
      if (!userStr) return
      try {
        const user = JSON.parse(userStr)
        if (!user.token) return
        for (const post of this.posts) {
          try {
            const res = await isLiked(post.id)
            if (res.code === 200) post.liked = res.data
          } catch (e) { /* ignore */ }
        }
      } catch (e) { /* ignore */ }
    },
    handleCategoryChange(name) {
      this.activeCategory = name
      this.currentPage = 1
      this.searchKeyword = ''
      this.loadPosts()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.currentPage = 1
      this.loadPosts()
    },
    handleCurrentChange(page) {
      this.currentPage = page
      this.loadPosts()
    },
    goToPostDetail(postId) {
      this.$router.push(`/post/${postId}`)
    },
    goToUserProfile(userId) {
      this.$router.push(`/user/${userId}`)
    },
    editPost(post) {
      this.$router.push(`/publish?edit=${post.id}`)
    },
    async toggleLike(post) {
      const userStr = localStorage.getItem('user')
      if (!userStr) { this.$message.warning('请先登录'); return }
      try { if (!JSON.parse(userStr).token) { this.$message.warning('请先登录'); return } } catch { return }
      if (this._likeThrottling[post.id]) return
      this._likeThrottling[post.id] = true
      try {
        if (post.liked) {
          const response = await unlikePost(post.id)
          if (response.code === 200) { post.liked = false; post.likesCount = Math.max(0, (post.likesCount || 1) - 1) }
        } else {
          const response = await likePost(post.id)
          if (response.code === 200) { post.liked = true; post.likesCount = (post.likesCount || 0) + 1 }
        }
      } catch (err) { this.$message.error(err.message || '操作失败') }
      finally { setTimeout(() => { this._likeThrottling[post.id] = false }, 500) }
    },
    isEditable(post) {
      if (!post.createdAt) return false
      const created = new Date(post.createdAt).getTime()
      return (Date.now() - created) <= 10 * 60 * 1000
    },
    async handleDeletePost(postId) {
      try {
        await this.$confirm('确定要删除这篇帖子吗？删除后不可恢复。', '提示', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        })
        const response = await deletePostApi(postId)
        if (response.code === 200) {
          this.$message.success('删除成功')
          this.loadPosts()
        } else {
          this.$message.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') this.$message.error(error.message || '删除失败')
      }
    },
    clearSearch() {
      this.searchKeyword = ''
      this.$router.replace({ path: '/' })
      this.loadPosts()
    },
    isCurrentUser(userId) {
      const userStr = localStorage.getItem('user')
      if (!userStr) return false
      try { return Number(JSON.parse(userStr).id) === Number(userId) } catch { return false }
    },
    parseFiles(imageStr) {
      if (!imageStr) return []
      return imageStr.split(',').map(f => f.trim()).filter(Boolean)
    },
    isImageFile(url) {
      return /\.(jpg|jpeg|png|gif|bmp|webp|svg)(\?.*)?$/i.test(url)
    },
    isVideoFile(url) {
      return /\.(mp4|webm|ogg|mov|avi)(\?.*)?$/i.test(url)
    },
    getFileName(url) {
      try { return decodeURIComponent(url.split('/').pop()) } catch { return url }
    },
    getDownloadUrl(file) {
      const filename = file.split('/').pop()
      return `/api/files/download/${encodeURIComponent(filename)}`
    },
    formatTime(timeString) {
      if (!timeString) return ''
      const date = new Date(timeString)
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
  }
}
</script>

<style scoped>
.home { padding: 0; }

.category-nav {
  margin-bottom: 16px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  padding: 8px;
  position: sticky;
  top: 64px;
  z-index: 100;
}

body.dark-mode-app .category-nav {
  background-color: #161b22 !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3) !important;
}

.category-nav :deep(.el-tabs__header) { margin-bottom: 0; }
.category-nav :deep(.el-tabs__item) { font-size: 14px; }
.category-nav :deep(.el-tabs--border-card) { border: none; box-shadow: none; }

body.dark-mode-app .category-nav :deep(.el-tabs--border-card) {
  background-color: #161b22 !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__header) {
  background-color: #161b22 !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__nav) {
  background-color: #161b22 !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__item) {
  color: #e5e7eb !important;
  background-color: #161b22 !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__item:hover) {
  color: #8ea2ff !important;
  background: rgba(142, 162, 255, 0.1) !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__item.is-active) {
  color: #8ea2ff !important;
  background: rgba(142, 162, 255, 0.1) !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__active-bar) {
  background-color: #8ea2ff !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs--border-card) {
  border: none !important;
  box-shadow: none !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__header) {
  border: none !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__nav) {
  border: none !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__item) {
  border: none !important;
  box-shadow: none !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__content) {
  border: none !important;
  background-color: #161b22 !important;
}

.search-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: white;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  font-size: 14px;
  color: #606266;
}

body.dark-mode-app .search-info {
  background: #161b22 !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3) !important;
  color: #e5e7eb !important;
}

body.dark-mode-app .search-info .el-button {
  color: #8ea2ff !important;
}

.posts-container { display: flex; flex-direction: column; gap: 12px; }

.post-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  padding: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.post-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.post-card-clickable { cursor: pointer; }

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
  transition: border-color 0.3s;
}

.user-info:hover .avatar { border-color: #667eea; }

.user-details { display: flex; flex-direction: column; }
.username { font-weight: 600; font-size: 15px; color: #303133; }
.post-meta { display: flex; gap: 8px; font-size: 12px; color: #909399; }

.post-category {
  background: linear-gradient(135deg, #667eea20, #764ba220);
  color: #667eea;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
}

.el-dropdown-link {
  display: flex; align-items: center; justify-content: center;
  width: 32px; height: 32px; border-radius: 50%;
  cursor: pointer; transition: background-color 0.3s; color: #909399;
}

.el-dropdown-link:hover { background-color: #f5f7fa; color: #667eea; }

.post-content { margin-bottom: 12px; line-height: 1.7; font-size: 15px; color: #303133; }

.post-media { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 10px; }

.media-image-wrapper { width: calc(33.333% - 6px); border-radius: 8px; overflow: hidden; }
.post-image { width: 100%; height: auto; max-height: 300px; object-fit: contain; border-radius: 8px; background: #f5f7fa; }

.media-video-wrapper { width: 100%; border-radius: 8px; overflow: hidden; }
.post-video { width: 100%; max-height: 400px; object-fit: contain; border-radius: 8px; background: #000; }

.media-file-wrapper { width: 100%; padding: 12px 16px; background: #f5f7fa; border-radius: 8px; border: 1px dashed #dcdfe6; }
.file-info { display: flex; align-items: center; gap: 10px; font-size: 13px; color: #606266; }
.file-name { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.file-download { color: #667eea; text-decoration: none; font-size: 13px; display: flex; align-items: center; gap: 4px; transition: color 0.3s; }
.file-download:hover { color: #764ba2; }

.post-stats {
  display: flex; gap: 20px; padding-top: 12px;
  border-top: 1px solid #f0f0f0; color: #909399; font-size: 13px;
}

.stat-item { display: flex; align-items: center; gap: 4px; transition: color 0.3s; }
.stat-view:hover { color: #409EFF; }
.stat-comment { cursor: pointer; }
.stat-comment:hover { color: #67c23a; }
.stat-like { cursor: pointer; }
.stat-like:hover { color: #f56c6c; }

.heart-icon { width: 15px; height: 15px; transition: all 0.3s; }

.loading, .error, .empty {
  background-color: white; border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  padding: 40px 20px; text-align: center;
}

body.dark-mode-app .loading, body.dark-mode-app .error, body.dark-mode-app .empty {
  background-color: #161b22 !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3) !important;
  color: #9ca3af !important;
  border-color: #2b3240 !important;
}

.loading { display: flex; align-items: center; justify-content: center; gap: 10px; color: #909399; }

.pagination {
  margin-top: 20px; text-align: center; background-color: white;
  border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); padding: 16px;
}

body.dark-mode-app .pagination {
  background-color: #161b22 !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3) !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__item) {
  background: #1f2937 !important;
  border-color: #374151 !important;
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__item:hover) {
  color: #8ea2ff !important;
  border-color: #8ea2ff !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__item.is-current) {
  background: #8ea2ff !important;
  border-color: #8ea2ff !important;
  color: #fff !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__button) {
  background: #1f2937 !important;
  border-color: #374151 !important;
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__button:hover) {
  color: #8ea2ff !important;
  border-color: #8ea2ff !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__sizes .el-select .el-input__wrapper) {
  background: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__sizes .el-select .el-input__inner) {
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__jump .el-input__wrapper) {
  background: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__jump .el-input__inner) {
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__total) {
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-select .el-input__wrapper) {
  background: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .pagination :deep(.el-select .el-input__inner) {
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-input__wrapper) {
  background: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .pagination :deep(.el-input__inner) {
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-select-dropdown) {
  background: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .pagination :deep(.el-select-dropdown__item) {
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-select-dropdown__item:hover) {
  background: rgba(142, 162, 255, 0.1) !important;
  color: #8ea2ff !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__jump) {
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__jump input) {
  background: #1f2937 !important;
  border-color: #374151 !important;
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__sizes) {
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__sizes .el-select) {
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__sizes .el-select .el-input__inner) {
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__sizes .el-select .el-input__wrapper) {
  background: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__button) {
  background: #1f2937 !important;
  border-color: #374151 !important;
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__button:hover) {
  background: rgba(142, 162, 255, 0.1) !important;
  border-color: #8ea2ff !important;
  color: #8ea2ff !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__item) {
  background: #1f2937 !important;
  border-color: #374151 !important;
  color: #e5e7eb !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__item:hover) {
  background: rgba(142, 162, 255, 0.1) !important;
  border-color: #8ea2ff !important;
  color: #8ea2ff !important;
}

body.dark-mode-app .pagination :deep(.el-pagination__item.is-current) {
  background: #8ea2ff !important;
  border-color: #8ea2ff !important;
  color: #fff !important;
}

:deep(.el-pagination) {
  background: transparent;
}

:deep(.dark .el-pagination) {
  --el-pagination-bg-color: #1e1e1e;
  --el-pagination-button-bg-color: #2a2a2a;
  --el-pagination-hover-color: #409eff;
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

:deep(.dark .btn-prev),
:deep(.dark .btn-next) {
  background-color: #2a2a2a;
  color: #ccc;
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

.post-list-enter-active { transition: all 0.4s ease; }
.post-list-enter-from { opacity: 0; transform: translateY(20px); }

@media (max-width: 768px) {
  .media-image-wrapper { width: calc(50% - 4px); }
  .post-card { padding: 16px; }
  .post-stats { gap: 14px; }
}
</style>
