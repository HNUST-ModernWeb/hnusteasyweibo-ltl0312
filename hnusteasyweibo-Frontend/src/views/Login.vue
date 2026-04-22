<!--
  @组件名 Login
  @描述 用户登录页面，支持用户名密码登录
  @示例 <Login />
-->
<template>
  <div class="login">
    <div class="login-container">
      <div class="login-header">
        <h2>欢迎回来</h2>
        <p>登录你的账号</p>
      </div>
      <el-form :model="form" :rules="rules" ref="loginFormRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            @keyup.enter="handleLogin"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="handleLogin"
            :loading="isLogging"
            :disabled="isLogging"
            style="width: 100%;"
            size="large"
          >
            登录
          </el-button>
        </el-form-item>
        <el-form-item>
          <div class="register-link">
            还没有账号？<router-link to="/register">立即注册</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { API_BASE_URL } from '../api/config.js'

export default {
  name: 'Login',
  data() {
    return {
      form: { username: '', password: '' },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
        ]
      },
      isLogging: false
    }
  },
  methods: {
    handleLogin() {
      if (this.isLogging) return
      this.$refs.loginFormRef.validate((valid) => {
        if (valid) {
          this.isLogging = true
          axios.post(`${API_BASE_URL}/users/login`, {
            username: this.form.username,
            password: this.form.password
          })
            .then(response => {
              if (response.data.code === 200) {
                const { token, user } = response.data.data
                this.$store.commit('login', {
                  id: user.id,
                  username: user.username,
                  token: token
                })
                this.$message.success('登录成功！')
                this.$router.push('/')
              } else {
                this.$message.error(response.data.message || '登录失败')
              }
            })
            .catch(error => {
              console.error('登录请求失败:', error)
              this.$message.error('网络错误，请稍后重试')
            })
            .finally(() => { this.isLogging = false })
        }
      })
    }
  }
}
</script>

<style scoped>
.login {
  display: flex; justify-content: center; align-items: center; min-height: 70vh;
}

.login-container {
  background-color: white; border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.08);
  padding: 40px; width: 100%; max-width: 420px;
  animation: fadeInUp 0.5s ease;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.login-header { text-align: center; margin-bottom: 30px; }
.login-header h2 { font-size: 24px; font-weight: 700; color: #303133; margin: 0 0 6px; }
.login-header p { color: #909399; font-size: 14px; margin: 0; }

.register-link { width: 100%; text-align: center; font-size: 14px; color: #606266; }
.register-link a { color: #667eea; text-decoration: none; font-weight: 500; transition: color 0.3s; }
.register-link a:hover { color: #764ba2; }
</style>
