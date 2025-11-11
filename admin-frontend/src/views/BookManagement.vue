<template>
  <div class="book-management-container">
    <div class="header-actions">
      <h2 class="page-title">图书管理</h2>
      <div class="actions">
        <el-button type="primary" @click="handleAdd">新增图书</el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-section">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索图书..."
        style="width: 300px"
        clearable
        @keyup.enter="handleSearch"
      />
      <el-select v-model="selectedCategory" placeholder="分类" style="width: 150px; margin-left: 10px" clearable>
        <el-option label="全部" value="" />
        <el-option label="文学" value="文学" />
        <el-option label="科技" value="科技" />
        <el-option label="历史" value="历史" />
      </el-select>
      <el-select v-model="selectedStatus" placeholder="状态" style="width: 150px; margin-left: 10px" clearable>
        <el-option label="全部" value="" />
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleSearch" style="margin-left: 10px">搜索</el-button>
    </div>

    <!-- 图书表格 -->
    <el-table :data="bookList" v-loading="loading" stripe border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="书名" min-width="200" />
      <el-table-column prop="author" label="作者" width="150" />
      <el-table-column prop="isbn" label="ISBN" width="150" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="totalQuantity" label="总数量" width="100" />
      <el-table-column prop="availableQuantity" label="可借数量" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="bookForm" :rules="bookRules" ref="bookFormRef" label-width="100px">
        <el-form-item label="书名" prop="title">
          <el-input v-model="bookForm.title" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="bookForm.publisher" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="bookForm.category" />
        </el-form-item>
        <el-form-item label="总数量" prop="totalQuantity">
          <el-input-number v-model="bookForm.totalQuantity" :min="1" />
        </el-form-item>
        <el-form-item label="可借数量" prop="availableQuantity">
          <el-input-number v-model="bookForm.availableQuantity" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="bookForm.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="简介" prop="summary">
          <el-input v-model="bookForm.summary" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="封面" prop="coverUrl">
          <el-upload
            :http-request="handleUpload"
            :before-upload="checkFileUpload"
            :on-remove="onRemove"
            :file-list="fileList"
            list-type="picture"
          >
            <el-button size="small">选择图片</el-button>
          </el-upload>
          <div v-if="bookForm.coverUrl" style="margin-top:10px">
            <img :src="bookForm.coverUrl" alt="封面" style="max-width:120px; max-height:160px; border:1px solid #ebeef5; border-radius:4px" />
          </div>
          <div v-if="uploadingCover" style="margin-top:10px">上传中...</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBookList, addBook, updateBook, deleteBook, uploadCover } from '@/api/book'
import { de } from 'element-plus/es/locales.mjs'

const loading = ref(false)
const submitting = ref(false)
const bookList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const selectedCategory = ref('')
const selectedStatus = ref('')

const dialogVisible = ref(false)
const dialogTitle = ref('新增图书')
const bookFormRef = ref(null)
const bookForm = reactive({
  id: null,
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  category: '',
  totalQuantity: 1,
  availableQuantity: 1,
  status: 1,
  summary: '',
  coverUrl: ''
})

const uploadingCover = ref(false)
const fileList = ref([])

// 1. (可选) 上传前的校验
const checkFileUpload = (file) => {
  console.log('1. 正在校验文件:', file)
  const isLt2M = file.size / 1024 / 1024 < 2; // 举例：限制2MB
  if (!isLt2M) {
    ElMessage.error('文件大小不能超过 2MB!');
    return false; // 校验不通过，阻止上传
  }
  return true; // 校验通过
}

// 2. 自定义上传逻辑
// (注意：这个函数会接收一个包含 file 的对象)
const handleUpload = async (options) => {
  const file = options.file
  console.log('2. 准备上传文件:', file)

  uploadingCover.value = true
  try {
    // debugger
    // 传入 bookId（如果是新增还没有 id，则传空字符串，后端需支持或在后端先创建图书再上传）
    const res = await uploadCover(file, bookForm.id || '') // 调用你自己的 API，字段名为 bookId 和 cover
    const url = res?.data?.url || res?.data?.path || null
    if (url) {
      bookForm.coverUrl = url
      ElMessage.success('封面上传成功')
      // 注意：使用 http-request 时，需要手动管理 fileList
      fileList.value = [{ name: file.name, url }]
    } else {
      ElMessage.error('上传返回异常')
      // 可选：上传失败时从 fileList 移除
      fileList.value = []
    }
  } catch (err) {
    // ------------------------------------
    // ▼▼▼ 添加这两行 ▼▼▼
    // console.error('上传失败，错误详情:', err); 
    // console.dir(err); // 使用 console.dir 展开对象
    // ------------------------------------

    ElMessage.error('上传失败')
    fileList.value = [] // 发生错误时移除
  } finally {
    uploadingCover.value = false
  }
  
  // 使用 http-request 时，不需要返回任何东西
  // (你已经在函数内处理了所有逻辑)
}

const onRemove = (file) => {
  // 清除封面
  bookForm.coverUrl = ''
  fileList.value = []
}

const bookRules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }]
}

// 加载图书列表
const loadBooks = async () => {
  loading.value = true
  try {
    const res = await getBookList({
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
      category: selectedCategory.value || undefined,
      status: selectedStatus.value !== '' ? selectedStatus.value : undefined
    })
    bookList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载图书列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadBooks()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增图书'
  Object.assign(bookForm, {
    id: null,
    title: '',
    author: '',
    isbn: '',
    publisher: '',
    category: '',
    totalQuantity: 1,
    availableQuantity: 1,
    status: 1,
    summary: '',
    coverUrl: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑图书'
  Object.assign(bookForm, { ...row })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这本图书吗？', '确认删除', {
      type: 'warning'
    })
    await deleteBook(row.id)
    ElMessage.success('删除成功')
    loadBooks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交
const handleSubmit = async () => {
  if (!bookFormRef.value) return
  
  await bookFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (bookForm.id) {
          await updateBook(bookForm.id, bookForm)
          ElMessage.success('更新成功')
        } else {
          await addBook(bookForm)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadBooks()
      } catch (error) {
        ElMessage.error('操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 对话框关闭
const handleDialogClose = () => {
  bookFormRef.value?.resetFields()
  // 清理上传状态
  fileList.value = []
  uploadingCover.value = false
}

// 分页
const handleSizeChange = () => {
  loadBooks()
}

const handlePageChange = () => {
  loadBooks()
}

onMounted(() => {
  loadBooks()
})
</script>

<style scoped>
.book-management-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  color: #303133;
}

.search-section {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>

