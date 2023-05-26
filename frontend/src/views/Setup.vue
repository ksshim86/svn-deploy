<template>
  <v-container fluid class="fill-height flex-column">
    <v-row>
      <v-col class="d-flex justify-center align-center">
        <v-sheet
          elevation="12"
          max-width="600"
          rounded="lg"
          width="100%"
          class="pa-4 text-center mx-auto"
        >
          <v-icon
            class="mb-5"
            color="blue-grey-darken-2"
            icon="mdi-folder-check"
            size="112"
          ></v-icon>

          <h2 class="text-h5 mb-6 font-weight-bold text-blue-grey-darken-2">DEPLOYSPACE 설정</h2>

          <p class="px-9 text-medium-emphasis text-body-2 text-left">
            DEPLOYSPACE로 설정된 디렉토리에 프로젝트를 구축하여 체계적으로 관리되는 방식으로, SVN DEPLOY가 운영됩니다.
            <br>
            <br>
          </p>

          <v-divider class="mb-4" />
          <v-row justify="end" class="px-9 py-4">
              <v-text-field
                v-model="sdRootPath"
                label="DEPLOYSPACE 경로를 입력하세요."
                placeholder="ex) c:\\deploy"
                hint="입력된 경로의 디렉토리가 없으면 생성됩니다."
                variant="outlined"
                :rules="[rules.required]"
                required
                v-slot:append
                class="mx-2"
              />
            <v-btn
              color="blue-grey-darken-2"
              height="56px"
              class="mx-2"
              :loading="confirmLoading"
              :disabled="isDisabled"
              @click="isConfirmClicked"
            >
              확인
            </v-btn>
          </v-row>
        </v-sheet>
      </v-col>
    </v-row>
  </v-container>
</template>
  
<script lang="ts" setup>
  import { ref, reactive, watch } from 'vue'
  import axios from 'axios'

  const sdRootPath = ref("")
  const isConfirm = ref(false)
  const confirmLoading = ref(false)
  const isDisabled = ref(true)

  const rules = reactive({
    required: (value: String) => !!value || '경로를 입력하고, 경로 확인을 해주세요.',
  });
  
  watch(sdRootPath, async (newSdRootPath) => {
    if (newSdRootPath.length === 0) {
      isDisabled.value = true
    } else {
      isDisabled.value = false
    }
  })

  const isConfirmClicked = async () => {
    confirmLoading.value = true
    try {
      const response = await axios.post('/api/sd-info', {sdRootPath: sdRootPath.value})
      console.log(response)
    } catch (error) {
      console.error(error)
    } finally {
      confirmLoading.value = false
    }
  }
</script>