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
 * 创建帖子
 * @param {Object} postData - 帖子数据
 * @param {number} postData.categoryId - 板块ID
 * @param {string} postData.content - 帖子内容
 * @param {string} [postData.image] - 附件URL
 * @returns {Promise<Object>} 响应数据
 */
export const createPost = async (postData) => {
  try {
    const token = getToken()
    const response = await axios.post(`${API_BASE_URL}/posts`, postData, {
      headers: { 'Authorization': token ? `Bearer ${token}` : '', 'Content-Type': 'application/json' }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { code: 500, message: '创建帖子失败' }
  }
}

/**
 * 更新帖子
 * @param {number} postId - 帖子ID
 * @param {Object} postData - 更新数据
 * @returns {Promise<Object>} 响应数据
 */
export const updatePost = async (postId, postData) => {
  try {
    const token = getToken()
    const response = await axios.put(`${API_BASE_URL}/posts/${postId}`, postData, {
      headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '更新帖子失败' }
  }
}

/**
 * 删除帖子
 * @param {number} postId - 帖子ID
 * @returns {Promise<Object>} 响应数据
 */
export const deletePost = async (postId) => {
  try {
    const token = getToken()
    const response = await axios.delete(`${API_BASE_URL}/posts/${postId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '删除帖子失败' }
  }
}

/**
 * 获取帖子详情
 * @param {number} postId - 帖子ID
 * @returns {Promise<Object>} 帖子详情数据
 */
export const getPost = async (postId) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/posts/${postId}`)
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取帖子详情失败' }
  }
}

/**
 * 获取帖子列表
 * @param {number} [page=1] - 页码
 * @param {number} [size=10] - 每页数量
 * @returns {Promise<Object>} 分页帖子数据
 */
export const getPostList = async (page = 1, size = 10) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/posts`, { params: { page, size } })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取帖子列表失败' }
  }
}

/**
 * 按分类获取帖子列表
 * @param {number} categoryId - 分类ID
 * @param {number} [page=1] - 页码
 * @param {number} [size=10] - 每页数量
 * @returns {Promise<Object>} 分页帖子数据
 */
export const getPostListByCategory = async (categoryId, page = 1, size = 10) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/posts/category/${categoryId}`, { params: { page, size } })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取分类帖子失败' }
  }
}

/**
 * 按用户ID获取帖子列表
 * @param {number} userId - 用户ID
 * @param {number} [page=1] - 页码
 * @param {number} [size=10] - 每页数量
 * @returns {Promise<Object>} 分页帖子数据
 */
export const getPostListByUser = async (userId, page = 1, size = 10) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/posts/user/${userId}`, { params: { page, size } })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取用户帖子失败' }
  }
}

/**
 * 点赞帖子
 * @param {number} postId - 帖子ID
 * @returns {Promise<Object>} 响应数据
 */
export const likePost = async (postId) => {
  try {
    const token = getToken()
    const response = await axios.post(`${API_BASE_URL}/posts/${postId}/like`, {}, {
      headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '点赞失败' }
  }
}

/**
 * 取消点赞
 * @param {number} postId - 帖子ID
 * @returns {Promise<Object>} 响应数据
 */
export const unlikePost = async (postId) => {
  try {
    const token = getToken()
    const response = await axios.post(`${API_BASE_URL}/posts/${postId}/unlike`, {}, {
      headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '取消点赞失败' }
  }
}

/**
 * 检查当前用户是否已点赞
 * @param {number} postId - 帖子ID
 * @returns {Promise<Object>} 包含点赞状态的数据
 */
export const isLiked = async (postId) => {
  try {
    const token = getToken()
    const response = await axios.get(`${API_BASE_URL}/posts/${postId}/liked`, {
      headers: { 'Authorization': token ? `Bearer ${token}` : '' }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '检查点赞状态失败' }
  }
}

/**
 * 获取帖子点赞数
 * @param {number} postId - 帖子ID
 * @returns {Promise<Object>} 点赞数数据
 */
export const getLikeCount = async (postId) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/posts/${postId}/like-count`)
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取点赞数失败' }
  }
}

/**
 * 创建评论
 * @param {number} postId - 帖子ID
 * @param {string} content - 评论内容
 * @returns {Promise<Object>} 响应数据
 */
export const createComment = async (postId, content) => {
  try {
    const token = getToken()
    const response = await axios.post(`${API_BASE_URL}/posts/${postId}/comments`, { content }, {
      headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '发布评论失败' }
  }
}

/**
 * 获取评论列表
 * @param {number} postId - 帖子ID
 * @param {number} [page=1] - 页码
 * @param {number} [size=10] - 每页数量
 * @returns {Promise<Object>} 分页评论数据
 */
export const getComments = async (postId, page = 1, size = 10) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/posts/${postId}/comments`, { params: { page, size } })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取评论列表失败' }
  }
}

/**
 * 删除评论
 * @param {number} commentId - 评论ID
 * @returns {Promise<Object>} 响应数据
 */
export const deleteComment = async (commentId) => {
  try {
    const token = getToken()
    const response = await axios.delete(`${API_BASE_URL}/comments/${commentId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '删除评论失败' }
  }
}

/**
 * 获取评论数
 * @param {number} postId - 帖子ID
 * @returns {Promise<Object>} 评论数数据
 */
export const getCommentCount = async (postId) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/posts/${postId}/comment-count`)
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取评论数失败' }
  }
}

/**
 * 获取分类列表
 * @returns {Promise<Object>} 分类数据
 */
export const getCategories = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/categories`)
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取分类列表失败' }
  }
}

/**
 * 获取当前登录用户的帖子
 * @param {number} [page=1] - 页码
 * @param {number} [size=10] - 每页数量
 * @returns {Promise<Object>} 分页帖子数据
 */
export const getPostsByUser = async (page = 1, size = 10) => {
  try {
    const token = getToken()
    const response = await axios.get(`${API_BASE_URL}/posts/user`, {
      params: { page, size },
      headers: { 'Authorization': `Bearer ${token}` }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '获取用户帖子失败' }
  }
}

/**
 * 搜索帖子
 * @param {string} keyword - 搜索关键词
 * @param {number} [page=1] - 页码
 * @param {number} [size=10] - 每页数量
 * @returns {Promise<Object>} 搜索结果数据
 */
export const searchPosts = async (keyword, page = 1, size = 10) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/posts/search`, { params: { keyword, page, size } })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '搜索帖子失败' }
  }
}

/**
 * 检查帖子是否可编辑
 * @param {number} postId - 帖子ID
 * @returns {Promise<Object>} 包含可编辑状态的数据
 */
export const checkPostEditable = async (postId) => {
  try {
    const token = getToken()
    const response = await axios.get(`${API_BASE_URL}/posts/${postId}/editable`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    return response.data
  } catch (error) {
    if (error.response && error.response.data) throw error.response.data
    throw { message: '检查编辑权限失败' }
  }
}
