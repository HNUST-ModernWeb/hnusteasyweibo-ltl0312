<!--
  @组件名 Register
  @描述 用户注册页面，支持用户名、邮箱、密码注册
  @示例 <Register />
-->
<template>
  <div class="register">
    <div class="register-container">
      <div class="register-header">
        <h2>创建账号</h2>
        <p>加入我们的社区</p>
      </div>
      <el-form :model="form" :rules="rules" ref="registerFormRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" type="email" placeholder="请输入邮箱" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="handleRegister"
            :loading="isRegistering"
            :disabled="isRegistering"
            style="width: 100%;"
            size="large"
          >
            注册
          </el-button>
        </el-form-item>
        <el-form-item>
          <div class="login-link">
            已有账号？<router-link to="/login">立即登录</router-link>
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
  name: 'Register',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入密码不一致！'))
      } else {
        callback()
      }
    }
    return {
      form: { username: '', email: '', password: '', confirmPassword: '' },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      isRegistering: false
    }
  },
  methods: {
    handleRegister() {
      if (this.isRegistering) return
      this.$refs.registerFormRef.validate((valid) => {
        if (valid) {
          this.isRegistering = true
          axios.post(`${API_BASE_URL}/users/register`, {
            username: this.form.username,
            email: this.form.email,
            password: this.form.password
          })
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success('注册成功！请登录')
                this.$router.push('/login')
              } else {
                this.$message.error(response.data.message || '注册失败')
              }
            })
            .catch(error => {
              console.error('注册请求失败:', error)
              this.$message.error('网络错误，请稍后重试')
            })
            .finally(() => { this.isRegistering = false })
        }
      })
    }
  }
}
</script>

<style scoped>
.register {
  display: flex; justify-content: center; align-items: center; min-height: 70vh;
}

.register-container {
  background-color: white; border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.08);
  padding: 40px; width: 100%; max-width: 450px;
  animation: fadeInUp 0.5s ease;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.register-header { text-align: center; margin-bottom: 30px; }
.register-header h2 { font-size: 24px; font-weight: 700; color: #303133; margin: 0 0 6px; }
.register-header p { color: #909399; font-size: 14px; margin: 0; }

.login-link { width: 100%; text-align: center; font-size: 14px; color: #606266; }
.login-link a { color: #667eea; text-decoration: none; font-weight: 500; transition: color 0.3s; }
.login-link a:hover { color: #764ba2; }
</style>
