import { defineStore } from 'pinia'
import axios from 'axios'

export const useSdInfoStore = defineStore('sdInfo', {
  state: () => ({
    sdInfo: null,
  }),
  actions: {
    async fetchSdInfo() {
      try {
        const response = await axios.get('/api/sd-info')
        this.sdInfo = response.data
      } catch (error) {
        console.error(error)
      }
    },
  },
})
