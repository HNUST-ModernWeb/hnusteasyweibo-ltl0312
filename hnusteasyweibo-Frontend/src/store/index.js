import axios from 'axios'
import { createStore } from 'vuex'
import { API_BASE_URL } from '../api/config.js'

/**
 * 应用主题class到body
 * @param {string} theme - 主题名称 'light' | 'dark'
 */
const applyThemeClass = (theme) => {
  if (typeof document === 'undefined') return
  document.body.classList.toggle('dark-mode-app', theme === 'dark')
}

const store = createStore({
  state() {
    return {
      user: null,
      isLoggedIn: false,
      theme: localStorage.getItem('theme') || 'light'
    }
  },
  getters: {
    currentUser: state => state.user,
    isLoggedIn: state => state.isLoggedIn,
    theme: state => state.theme,
    isDarkMode: state => state.theme === 'dark'
  },
  mutations: {
    login(state, userData) {
      state.user = userData
      state.isLoggedIn = true
      localStorage.setItem('user', JSON.stringify(userData))
    },
    logout(state) {
      state.user = null
      state.isLoggedIn = false
      localStorage.removeItem('user')
    },
    setTheme(state, theme) {
      state.theme = theme
      localStorage.setItem('theme', theme)
      applyThemeClass(theme)
    },
    initializeStore(state) {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        try {
          const userData = JSON.parse(userStr)
          if (userData && userData.token) {
            state.user = userData
            state.isLoggedIn = true
          }
        } catch (e) {
          localStorage.removeItem('user')
        }
      }

      const theme = localStorage.getItem('theme') || 'light'
      state.theme = theme
      applyThemeClass(theme)
    }
  },
  actions: {
    login({ commit }, userData) {
      commit('login', userData)
    },
    logout({ commit, state }) {
      const token = state.user?.token || (() => {
        try {
          const stored = localStorage.getItem('user')
          return stored ? JSON.parse(stored)?.token : null
        } catch (e) {
          return null
        }
      })()

      const request = token
        ? axios.post(`${API_BASE_URL}/users/logout`, {}, {
          headers: { Authorization: `Bearer ${token}` }
        })
        : Promise.resolve()

      return request
        .catch(() => {})
        .finally(() => {
          commit('logout')
        })
    },
    toggleTheme({ commit, state }) {
      commit('setTheme', state.theme === 'dark' ? 'light' : 'dark')
    }
  }
})

export default store
