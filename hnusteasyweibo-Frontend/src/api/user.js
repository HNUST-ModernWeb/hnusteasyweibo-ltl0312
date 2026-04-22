import axios from 'axios'
import { API_BASE_URL } from './config.js'

/**
 * 从localStorage获取认证Token
 * @returns {string|null} Bearer Token或null
 */
const getToken = () => {
  try {
    const userStr = localStorage.getItem('user')
    if (!userStr) return null
    const user = JSON.parse(userStr)
    if (!user || !user.token) return null
    return user.token
  } catch (e) {
    return null
  }
}

/**
 * 获取当前登录用户资料
 * @returns {Promise<Object>} 用户资料数据
 */
export const getUserProfile = async () => {
  try {
    const token = getToken()
    const response = await axios.get(`${API_BASE_URL}/users/profile`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取用户信息失败' }
  }
}

/**
 * 更新当前登录用户资料
 * @param {Object} userData - 更新数据
 * @param {string} [userData.username] - 用户名
 * @param {string} [userData.bio] - 个人简介
 * @param {string} [userData.avatar] - 头像URL
 * @param {string} [userData.email] - 邮箱
 * @returns {Promise<Object>} 更新后的用户数据
 */
export const updateUserProfile = async (userData) => {
  try {
    const token = getToken()
    const response = await axios.put(`${API_BASE_URL}/users/profile`, userData, {
      headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '更新用户信息失败' }
  }
}

/**
 * 获取当前登录用户统计数据
 * @returns {Promise<Object>} 统计数据（帖子数、关注数等）
 */
export const getUserStatistics = async () => {
  try {
    const token = getToken()
    const response = await axios.get(`${API_BASE_URL}/users/statistics`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取统计信息失败' }
  }
}

/**
 * 根据用户ID获取用户信息
 * @param {number} userId - 用户ID
 * @returns {Promise<Object>} 用户信息数据
 */
export const getUserById = async (userId) => {
  try {
    const token = getToken()
    const headers = {}
    if (token) headers['Authorization'] = `Bearer ${token}`
    const response = await axios.get(`${API_BASE_URL}/users/${userId}`, { headers })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取用户信息失败' }
  }
}

/**
 * 关注用户
 * @param {number} userId - 目标用户ID
 * @returns {Promise<Object>} 响应数据
 */
export const followUser = async (userId) => {
  try {
    const token = getToken()
    const response = await axios.post(`${API_BASE_URL}/follows/${userId}`, {}, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '关注失败' }
  }
}

/**
 * 取消关注用户
 * @param {number} userId - 目标用户ID
 * @returns {Promise<Object>} 响应数据
 */
export const unfollowUser = async (userId) => {
  try {
    const token = getToken()
    const response = await axios.delete(`${API_BASE_URL}/follows/${userId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '取消关注失败' }
  }
}

/**
 * 检查是否已关注某用户
 * @param {number} userId - 目标用户ID
 * @returns {Promise<Object>} 包含关注状态的数据
 */
export const checkFollowing = async (userId) => {
  try {
    const token = getToken()
    const headers = {}
    if (token) headers['Authorization'] = `Bearer ${token}`
    const response = await axios.get(`${API_BASE_URL}/follows/check/${userId}`, { headers })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '检查关注状态失败' }
  }
}

/**
 * 获取用户关注统计
 * @param {number} userId - 用户ID
 * @returns {Promise<Object>} 关注统计数据
 */
export const getFollowStats = async (userId) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/follows/${userId}/stats`)
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取关注统计失败' }
  }
}

/**
 * 上传头像
 * @param {File} file - 图片文件
 * @returns {Promise<Object>} 包含头像URL的数据
 */
export const uploadAvatar = async (file) => {
  try {
    const token = getToken()
    const formData = new FormData()
    formData.append('file', file)
    const response = await axios.post(`${API_BASE_URL}/files/avatar`, formData, {
      headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'multipart/form-data' }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '头像上传失败' }
  }
}

/**
 * 上传文件
 * @param {File} file - 文件对象
 * @returns {Promise<Object>} 包含文件URL的数据
 */
export const uploadFile = async (file) => {
  try {
    const token = getToken()
    const formData = new FormData()
    formData.append('file', file)
    const response = await axios.post(`${API_BASE_URL}/files/upload`, formData, {
      headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'multipart/form-data' }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '文件上传失败' }
  }
}
