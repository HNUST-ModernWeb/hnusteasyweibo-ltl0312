import axios from 'axios'
import { createStore } from 'vuex'
import { API_BASE_URL } from '../api/config.js'

const store = createStore({
  state() {
    return {
      user: null,
      isLoggedIn: false
    }
  },
  getters: {
    currentUser: state => state.user,
    isLoggedIn: state => state.isLoggedIn
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
    }
  }
})

export default store
