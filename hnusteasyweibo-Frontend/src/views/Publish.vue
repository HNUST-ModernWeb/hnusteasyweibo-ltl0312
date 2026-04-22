<!--
  @组件名 Publish
  @描述 发布/编辑帖子页面，支持选择板块、输入内容、上传附件
  @示例 <Publish />
-->
<template>
  <div class="publish-container">
    <div class="publish-card">
      <h2 class="publish-title">{{ isEditing ? '编辑帖子' : '发布帖子' }}</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="选择板块" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择板块" style="width: 100%;">
            <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="帖子内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="分享你的想法..." maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item label="附件">
          <div class="upload-area">
            <el-upload
              v-model:file-list="fileList"
              :auto-upload="false"
              :on-change="handleFileChange"
              :on-remove="handleFileRemove"
              :limit="5"
              :on-exceed="handleExceed"
              multiple
            >
              <el-button type="primary" plain><el-icon><Upload /></el-icon> 选择文件</el-button>
              <template #tip>
                <div class="upload-tip">支持所有文件格式，单个文件不超过50MB，最多5个文件</div>
              </template>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="isSubmitting" :disabled="isSubmitting" class="submit-btn">
            {{ isEditing ? '保存修改' : '发布帖子' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
      <div v-if="isEditing" class="edit-notice">
        <el-alert type="warning" :closable="false" show-icon>
          帖子创建后10分钟内可编辑，超时后仅可删除
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { createPost, updatePost, getPost, checkPostEditable } from '../api/post'
import { getCategories } from '../api/post'
import { uploadFile } from '../api/user'

export default {
  name: 'Publish',
  components: { Upload },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const formRef = ref(null)
    const form = ref({ categoryId: null, content: '', image: '' })
    const rules = {
      categoryId: [{ required: true, message: '请选择板块', trigger: 'change' }],
      content: [
        { required: true, message: '请输入帖子内容', trigger: 'blur' },
        { min: 10, max: 2000, message: '帖子内容长度在10到2000字之间', trigger: 'blur' }
      ]
    }
    const categories = ref([])
    const fileList = ref([])
    const isSubmitting = ref(false)
    const isEditing = computed(() => !!route.query.edit)
    const editingPostId = computed(() => route.query.edit)
    const uploadedUrls = ref([])

    const loadCategories = async () => {
      try {
        const response = await getCategories()
        if (response.code === 200) categories.value = response.data
      } catch (err) { console.error('获取分类失败:', err) }
    }

    const loadPostForEdit = async () => {
      if (!editingPostId.value) return
      try {
        const response = await getPost(editingPostId.value)
        if (response.code === 200) {
          const post = response.data
          form.value.categoryId = post.categoryId
          form.value.content = post.content
          form.value.image = post.image || ''
          if (post.image) {
            const files = post.image.split(',').filter(f => f.trim())
            uploadedUrls.value = files
            fileList.value = files.map((url, index) => ({
              name: url.split('/').pop(),
              url: url,
              uid: Date.now() + index,
              status: 'success'
            }))
          }
          try {
            const editCheck = await checkPostEditable(editingPostId.value)
            if (editCheck.code === 200 && !editCheck.data) {
              ElMessage.warning('帖子创建已超过10分钟，无法编辑')
              router.replace(`/post/${editingPostId.value}`)
            }
          } catch (err) { console.error('检查编辑权限失败:', err) }
        }
      } catch (err) {
        ElMessage.error('获取帖子信息失败')
        router.replace('/')
      }
    }

    const handleFileChange = (uploadFile, uploadFiles) => {
      fileList.value = uploadFiles
      if (uploadFile.raw && uploadFile.raw.size > 50 * 1024 * 1024) {
        ElMessage.error('文件大小不能超过50MB')
        return false
      }
    }

    const handleFileRemove = (file) => {
      if (file.url) {
        uploadedUrls.value = uploadedUrls.value.filter(u => u !== file.url)
      }
    }

    const handleExceed = () => { ElMessage.warning('最多上传5个文件') }

    const uploadFiles = async () => {
      const urls = [...uploadedUrls.value]
      const newFiles = fileList.value.filter(f => f.raw)
      for (const file of newFiles) {
        try {
          const response = await uploadFile(file.raw)
          if (response.code === 200) {
            urls.push(response.data)
          } else {
            ElMessage.warning(`文件 ${file.name} 上传失败`)
          }
        } catch (err) {
          ElMessage.warning(`文件 ${file.name} 上传失败`)
        }
      }
      return urls.filter(Boolean)
    }

    const handleSubmit = async () => {
      if (isSubmitting.value) return
      try {
        await formRef.value.validate()
      } catch { return }

      isSubmitting.value = true
      try {
        const imageUrls = await uploadFiles()
        form.value.image = imageUrls.join(',')

        let response
        if (isEditing.value) {
          response = await updatePost(editingPostId.value, form.value)
        } else {
          response = await createPost(form.value)
        }

        if (response.code === 200) {
          ElMessage.success(isEditing.value ? '帖子修改成功' : '帖子发布成功')
          router.push(isEditing.value ? `/post/${editingPostId.value}` : '/')
        } else {
          ElMessage.error(response.message || (isEditing.value ? '修改失败' : '发布失败'))
        }
      } catch (err) {
        ElMessage.error(err.message || '操作失败')
      } finally {
        isSubmitting.value = false
      }
    }

    const handleCancel = () => {
      if (isEditing.value) router.push(`/post/${editingPostId.value}`)
      else router.push('/')
    }

    onMounted(() => {
      loadCategories()
      if (isEditing.value) loadPostForEdit()
    })

    return {
      form, formRef, rules, categories, fileList, isSubmitting,
      isEditing, handleSubmit, handleCancel, handleFileChange,
      handleFileRemove, handleExceed
    }
  }
}
</script>

<style scoped>
.publish-container { max-width: 700px; margin: 0 auto; }

.publish-card {
  background-color: #fff; border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); padding: 30px;
}

.publish-title {
  font-size: 22px; font-weight: 700; margin: 0 0 24px;
  padding-bottom: 16px; border-bottom: 1px solid #f0f0f0; color: #303133;
}

.upload-area { width: 100%; }
.upload-tip { font-size: 12px; color: #909399; margin-top: 4px; }

.submit-btn { min-width: 120px; }

.edit-notice { margin-top: 16px; }

@media (max-width: 768px) {
  .publish-card { padding: 20px; }
  .publish-title { font-size: 18px; }
}

body.dark-mode-app .publish-card {
  background-color: #161b22 !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3) !important;
  color: #e5e7eb !important;
}

body.dark-mode-app .publish-title {
  color: #e5e7eb !important;
  border-bottom: 1px solid #2b3240 !important;
}

body.dark-mode-app .upload-tip {
  color: #9ca3af !important;
}

body.dark-mode-app .el-form-item__label {
  color: #e5e7eb !important;
}

body.dark-mode-app .el-input__wrapper {
  background: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .el-input__inner {
  color: #e5e7eb !important;
}

body.dark-mode-app .el-select .el-input__wrapper {
  background: #1f2937 !important;
  border-color: #374151 !important;
}

body.dark-mode-app .el-select .el-input__inner {
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

body.dark-mode-app .el-upload {
  color: #e5e7eb !important;
}

body.dark-mode-app .el-upload__tip {
  color: #9ca3af !important;
}

:deep(.dark .el-input__wrapper),
:deep(.dark .el-select__wrapper) {
  background-color: #2a2a2a;
  border: 1px solid #444;
  color: #ddd;
}

:deep(.dark .el-textarea__inner) {
  background-color: #2a2a2a;
  color: #ddd;
  border: 1px solid #444;
}

:deep(.dark textarea::placeholder),
:deep(.dark input::placeholder) {
  color: #888;
}

:deep(.dark .el-select__placeholder) {
  color: #ccc;
}

:deep(.dark .el-select__suffix) {
  color: #ccc;
}
</style>
