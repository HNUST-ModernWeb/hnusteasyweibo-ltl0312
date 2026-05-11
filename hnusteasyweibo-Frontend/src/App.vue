<!--
  @组件名 App
  @描述 应用根组件，包含导航栏、搜索、主题切换、路由视图
  @示例 <App />
-->
<template>
  <div class="app">
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
    ...mapGetters(['currentUser', 'isLoggedIn'])
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


}
</style>
