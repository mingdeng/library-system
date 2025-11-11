<template>
  <div class="user-management-container">
    <div class="header-actions">
      <h2 class="page-title">用户管理</h2>
      <div class="actions">
        <el-button type="primary" @click="handleAdd">新增用户</el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-section">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户..."
        style="width: 300px"
        clearable
        @keyup.enter="handleSearch"
      />
      <el-select v-model="selectedRole" placeholder="角色" style="width: 150px; margin-left: 10px" clearable>
        <el-option label="全部" value="" />
        <el-option label="读者" value="READER" />
        <el-option label="管理员" value="LIBRARIAN" />
        <el-option label="系统管理员" value="ADMIN" />
      </el-select>
      <el-select v-model="selectedStatus" placeholder="状态" style="width: 150px; margin-left: 10px" clearable>
        <el-option label="全部" value="" />
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleSearch" style="margin-left: 10px">搜索</el-button>
    </div>

    <!-- 用户表格 -->
    <el-table :data="userList" v-loading="loading" stripe border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="phone" label="电话" width="150" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="role" label="角色" width="120">
        <template #default="{ row }">
          <el-tag :type="getRoleType(row.role)">
            {{ getRoleText(row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
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
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="!!userForm.id" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!userForm.id">
          <el-input v-model="userForm.password" type="password" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" style="width: 100%">
            <el-option label="读者" value="READER" />
            <el-option label="管理员" value="LIBRARIAN" />
            <el-option label="系统管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import { getUserList, addUser, updateUser, deleteUser } from '@/api/user'

const loading = ref(false)
const submitting = ref(false)
const userList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const selectedRole = ref('')
const selectedStatus = ref('')

const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const userFormRef = ref(null)
const userForm = reactive({
  id: null,
  username: '',
  realName: '',
  password: '',
  phone: '',
  email: '',
  role: 'READER',
  status: 1
})

const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
      role: selectedRole.value || undefined,
      status: selectedStatus.value !== '' ? selectedStatus.value : undefined
    })
    userList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadUsers()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增用户'
  Object.assign(userForm, {
    id: null,
    username: '',
    realName: '',
    password: '',
    phone: '',
    email: '',
    role: 'READER',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  Object.assign(userForm, { ...row, password: '' })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '确认删除', {
      type: 'warning'
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (userForm.id) {
          await updateUser(userForm.id, userForm)
          ElMessage.success('更新成功')
        } else {
          await addUser(userForm)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadUsers()
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
  userFormRef.value?.resetFields()
}

// 获取角色类型
const getRoleType = (role) => {
  const map = {
    'READER': 'info',
    'LIBRARIAN': 'warning',
    'ADMIN': 'danger'
  }
  return map[role] || 'info'
}

// 获取角色文本
const getRoleText = (role) => {
  const map = {
    'READER': '读者',
    'LIBRARIAN': '管理员',
    'ADMIN': '系统管理员'
  }
  return map[role] || role
}

// 分页
const handleSizeChange = () => {
  loadUsers()
}

const handlePageChange = () => {
  loadUsers()
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-management-container {
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

