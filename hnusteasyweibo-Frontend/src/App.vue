<!--
  @组件名 App
  @描述 应用根组件，包含导航栏、搜索、主题切换、路由视图
  @示例 <App />
-->
<template>
  <div class="app" :class="{ 'dark-mode-app': isDarkMode }">
    <header class="header" :class="{ scrolled: isScrolled }">
      <div class="logo" @click="$router.push('/')">社交分享平台</div>

      <div class="search-box">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索帖子或用户..."
            size="small"
            clearable
            @keyup.enter="handleSearch"
            @clear="handleSearchClear"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <nav class="nav">
        <router-link to="/" class="nav-item">首页</router-link>
        <template v-if="isLoggedIn">
          <router-link to="/publish" class="nav-item">发布</router-link>
          <router-link to="/profile" class="nav-item">个人主页</router-link>
        </template>
      </nav>

      <button class="theme-toggle-btn" type="button" @click="toggleTheme" :title="isDarkMode ? '切换到浅色模式' : '切换到深色模式'">
        <span v-if="isDarkMode">☀️</span>
        <span v-else>🌙</span>
      </button>

      <div class="user-actions">
        <template v-if="isLoggedIn">
          <span class="username" @click="$router.push('/profile')">{{ currentUser?.username }}</span>
          <button @click="handleLogout" class="logout-btn">退出</button>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-item">登录</router-link>
          <router-link to="/register" class="nav-item">注册</router-link>
        </template>
      </div>
    </header>

    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'App',
  components: { Search },
  data() {
    return {
      isScrolled: false,
      searchKeyword: ''
    }
  },
  computed: {
    ...mapGetters(['currentUser', 'isLoggedIn', 'isDarkMode'])
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll)
    this.searchKeyword = this.$route.query.search || ''
  },
  beforeUnmount() {
    window.removeEventListener('scroll', this.handleScroll)
  },
  watch: {
    '$route.query.search'(val) {
      this.searchKeyword = val || ''
    }
  },
  methods: {
    handleScroll() {
      this.isScrolled = window.scrollY > 10
    },
    handleSearch() {
      if (this.searchKeyword && this.searchKeyword.trim()) {
        this.$router.push({ path: '/', query: { search: this.searchKeyword.trim() } })
      }
    },
    handleSearchClear() {
      if (this.$route.query.search) {
        this.$router.push({ path: '/' })
      }
    },
    toggleTheme() {
      this.$store.dispatch('toggleTheme')
    },
    handleLogout() {
      this.$store.dispatch('logout')
      this.$message.success('退出成功')
      this.$router.push('/')
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  color-scheme: light;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
  color: #303133;
  transition: background-color 0.3s ease, color 0.3s ease;
}

body.dark-mode-app {
  background-color: #0f1115;
  color: #e5e7eb;
  color-scheme: dark;
}

body.dark-mode-app .header {
  background: linear-gradient(135deg, #1f2937 0%, #111827 100%);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.35);
}

body.dark-mode-app .header.scrolled {
  background: rgba(17, 24, 39, 0.95);
}

body.dark-mode-app .nav-item:hover,
body.dark-mode-app .nav-item.router-link-active {
  background: rgba(255, 255, 255, 0.12);
}

body.dark-mode-app .logout-btn,
body.dark-mode-app .theme-toggle-btn {
  background: rgba(255, 255, 255, 0.08);
}

body.dark-mode-app .main {
  color: #e5e7eb;
}

body.dark-mode-app .post-card,
body.dark-mode-app .profile-header,
body.dark-mode-app .profile-posts,
body.dark-mode-app .post-detail-container .post-card,
body.dark-mode-app .comments-section,
body.dark-mode-app .category-nav,
body.dark-mode-app .post-item,
body.dark-mode-app .publish-card {
  background: #161b22;
  color: #e5e7eb;
  border-color: #2b3240;
}

body.dark-mode-app .posts-title,
body.dark-mode-app .profile-username,
body.dark-mode-app .stat-value,
body.dark-mode-app .username,
body.dark-mode-app .post-content,
body.dark-mode-app .comment-content,
body.dark-mode-app .comment-username,
body.dark-mode-app .post-text {
  color: #e5e7eb;
}

body.dark-mode-app .post-category {
  background: rgba(99, 102, 241, 0.18);
  color: #a5b4fc;
}

body.dark-mode-app .search-box .el-input__wrapper {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.15);
}

body.dark-mode-app .search-box .el-input__inner {
  color: #fff;
}

body.dark-mode-app .search-box .el-input__inner::placeholder {
  color: rgba(255, 255, 255, 0.55);
}

body.dark-mode-app .empty-container,
body.dark-mode-app .loading-container,
body.dark-mode-app .error-container,
body.dark-mode-app .login-prompt,
body.dark-mode-app .empty,
body.dark-mode-app .loading,
body.dark-mode-app .error {
  background: #161b22;
  color: #9ca3af;
  border-color: #2b3240;
}

body.dark-mode-app .category-nav {
  background: #161b22 !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3) !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs--border-card) {
  background: #161b22 !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__item) {
  color: #e5e7eb !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__item.is-active) {
  color: #8ea2ff !important;
  background: rgba(142, 162, 255, 0.1) !important;
}

body.dark-mode-app .category-nav :deep(.el-tabs__active-bar) {
  background-color: #8ea2ff !important;
}

body.dark-mode-app .pagination {
  background: #161b22 !important;
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

body.dark-mode-app .pagination :deep(.el-pagination__jump input) {
  background: #1f2937 !important;
  border-color: #374151 !important;
  color: #e5e7eb !important;
}

body.dark-mode-app .el-select-dropdown {
  background: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .el-select-dropdown__item {
  color: #e5e7eb !important;
}

body.dark-mode-app .el-select-dropdown__item:hover {
  background: rgba(142, 162, 255, 0.1) !important;
  color: #8ea2ff !important;
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

body.dark-mode-app .el-input__wrapper {
  background: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .el-input__inner {
  color: #e5e7eb !important;
}

body.dark-mode-app .el-textarea__inner {
  background: #1f2937 !important;
  border-color: #374151 !important;
  color: #e5e7eb !important;
}

body.dark-mode-app .el-input__count,
body.dark-mode-app .el-textarea__count {
  color: #9ca3af !important;
  background: transparent !important;
}

/* 输入框字数统计样式 */
body.dark-mode-app .el-input__suffix-inner .el-input__count {
  color: #9ca3af !important;
  background: transparent !important;
}

/* 文本域字数统计样式 */
body.dark-mode-app .el-textarea__editor .el-textarea__count {
  color: #9ca3af !important;
  background: transparent !important;
}

/* 确保所有输入框字数统计样式 */
body.dark-mode-app .el-input__count {
  color: #9ca3af !important;
  background: transparent !important;
}

/* 确保对话框内的字数统计样式 */
body.dark-mode-app .el-dialog .el-input__count,
body.dark-mode-app .el-dialog .el-textarea__count {
  color: #9ca3af !important;
  background: transparent !important;
}

body.dark-mode-app .el-select .el-input__wrapper {
  background: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .el-select .el-input__inner {
  color: #e5e7eb !important;
}

body.dark-mode-app .el-form-item__label {
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

body.dark-mode-app .media-file-wrapper,
body.dark-mode-app .post-file-badge {
  background: #111827;
  border-color: #374151;
}

body.dark-mode-app .comment-item {
  border-color: #2b3240;
}

body.dark-mode-app .file-download,
body.dark-mode-app .login-prompt a {
  color: #8ea2ff;
}

.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.header.scrolled {
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(12px);
  background: rgba(102, 126, 234, 0.95);
}

.logo {
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
  letter-spacing: 1px;
  flex-shrink: 0;
  white-space: nowrap;
}

.search-box {
  width: 240px;
  flex-shrink: 0;
  margin-left: 16px;
}

.search-box .el-input__wrapper {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.15);
  box-shadow: none;
  border: 1px solid rgba(255, 255, 255, 0.25);
}

.search-box .el-input__inner {
  color: white;
}

.search-box .el-input__inner::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.nav {
  display: flex;
  gap: 4px;
  margin-left: auto;
  flex-shrink: 0;
}

.nav-item {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  padding: 8px 14px;
  border-radius: 8px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: white;
  background: rgba(255, 255, 255, 0.18);
}

.theme-toggle-btn {
  flex-shrink: 0;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.16);
  color: white;
  border-radius: 10px;
  padding: 7px 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  line-height: 1;
}

.theme-toggle-btn:hover {
  transform: translateY(-1px);
  background: rgba(255, 255, 255, 0.26);
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.username {
  font-size: 13px;
  cursor: pointer;
  opacity: 0.9;
  transition: opacity 0.3s;
  white-space: nowrap;
}

.username:hover {
  opacity: 1;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.18);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 6px 14px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
  font-weight: 500;
  white-space: nowrap;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

.main {
  flex: 1;
  padding: 84px 20px 20px;
  max-width: 900px;
  margin: 0 auto;
  width: 100%;
}

@media (max-width: 768px) {
  .main {
    padding: 80px 10px 10px;
  }

  .search-box {
    width: 120px;
    margin-left: 8px;
  }

  .nav {
    gap: 2px;
  }

  .nav-item {
    padding: 6px 10px;
    font-size: 13px;
  }

  .username {
    display: none;
  }

  .theme-toggle-btn {
    padding: 6px 8px;
  }
}
</style>
