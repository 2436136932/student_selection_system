<template>
  <div class="award-container">
    <!-- 评奖评优管理卡片式布局 -->
    <div class="award-management">
      <h2 class="page-title">评奖评优管理</h2>
      <div class="card-header" style="margin-bottom: 20px;">
          <el-button v-if="hasRole('admin')" type="primary" @click="handleAdd">
            <el-icon><plus /></el-icon> 新建奖项
          </el-button>
        </div>
      
      <!-- 奖项卡片列表 -->
      <div class="award-cards-container">
        <el-card 
          v-for="award in awards" 
          :key="award.awardId" 
          class="award-card"
          shadow="hover"
          :body-style="{ padding: '15px' }"
        >
          <div class="award-card-header">
            <h3 class="award-name">{{ award.awardName }}</h3>
            <el-tag
              :type="{
                '进行中': 'warning',
                '已完成': 'success',
                '未开始': 'info',
                '已关闭': 'danger'
              }[award.currentStatus || '待开始']"
              :effect="'light'"
              size="small"
            >
              {{ award.currentStatus || '待开始' }}
            </el-tag>
          </div>
          <div class="award-card-content">
            <div class="award-date">
              {{ award.startTime ? new Date(award.startTime).toLocaleDateString() : '未设置' }} 至 {{ award.endTime ? new Date(award.endTime).toLocaleDateString() : '未设置' }}
            </div>
            <div class="award-quota">
              名额: {{ award.quota || '不限' }}
            </div>
            <div class="award-requirement">
              最低要求: {{ award.requirement || '暂无要求' }}
            </div>
            <div class="award-actions">
                <!-- 根据状态显示不同操作按钮 -->
                <el-button 
                  type="info" 
                  size="small" 
                  @click="handleViewDetails(award)"
                >
                  查看奖项详情
                </el-button>
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="handleViewApplications(award)"
                >
                  查看申请
                </el-button>
                <el-button 
                  v-if="hasRole('admin') && award.status !== '已发布' && award.currentStatus !== '已关闭' && award.currentStatus !== '已完成'" 
                  type="success" 
                  size="small" 
                  @click="handlePublish(award)"
                >
                  发布奖项
                </el-button>
                <el-button 
                  v-if="award.currentStatus === '进行中' && hasRole('admin')" 
                  type="success" 
                  size="small" 
                  @click="handleEndSelection(award)"
                >
                  结束评选
                </el-button>
                <el-button 
                  v-if="award.currentStatus === '已完成' || award.currentStatus === '已关闭'" 
                  type="success" 
                  size="small" 
                  @click="handleViewResults(award)"
                >
                  查看结果
                </el-button>
                <el-button 
              v-if="hasRole('admin')" 
              type="info" 
              size="small" 
              @click="handleEdit(award)"
            >
              编辑
            </el-button>
              </div>
          </div>
        </el-card>
      </div>
      
      <!-- 奖项列表分页 -->
      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
      </div>
    </div>

    <!-- 评选流程管理 -->
    <div class="selection-process" style="margin-top: 40px;">
      <h2 class="page-title">评选流程管理</h2>
      <div class="process-table-container">
        <el-table
          :data="processTableData"
          style="width: 100%"
          border
          stripe
          :row-style="{height: '60px'}"
          :cell-style="{padding: '0 15px'}"
          :header-cell-style="{backgroundColor: '#fafafa', fontWeight: '600', fontSize: '14px'}"
        >
          <el-table-column prop="awardName" label="奖项名称" width="220">
            <template #default="scope">
              <span class="award-name">{{ scope.row.awardName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="currentStage" label="当前阶段" width="180">
            <template #default="scope">
              <el-tag
                :type="{
                  '学生申请': 'info',
                  '教师审批': 'warning',
                  '管理员审批': 'primary',
                  '结果公示': 'success',
                  '未开始': 'info'
                }[scope.row.currentStage || '未开始']"
                size="small"
                effect="light"
              >
                {{ scope.row.currentStage || '未开始' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="progress" label="进度" min-width="250">
            <template #default="scope">
              <div class="progress-container">
                <el-progress
                  :percentage="calculateProgress(scope.row)"
                  :stroke-width="8"
                  :color="{
                    100: '#67C23A',
                    75: '#409EFF',
                    50: '#E6A23C',
                    25: '#E6A23C',
                    0: '#909399'
                  }[calculateProgress(scope.row)]"
                  :show-text="false"
                ></el-progress>
                <span class="progress-text">{{ calculateProgress(scope.row) }}% 完成</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="right">
            <template #default="scope">
              <el-button 
                type="primary" 
                size="small" 
                @click="handleViewProcessDetails(scope.row)"
              >
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 评选流程分页 -->
        <div class="pagination">
          <el-pagination
            @size-change="handleProcessSizeChange"
            @current-change="handleProcessCurrentChange"
            :current-page="currentProcessPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="processPageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="processTotal"
          ></el-pagination>
        </div>
      </div>
    </div>

    <!-- 传统管理界面（保留原功能） -->
    <div class="traditional-management" style="margin-top: 40px;">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>奖项详细管理</span>
          </div>
        </template>
        <el-tabs v-model="activeTab" type="card">
          <el-tab-pane label="奖项管理" name="awards">
            <div class="card-body">
              <el-form :model="searchForm" :inline="true" class="search-form">
                <el-form-item label="奖项名称">
                  <el-input v-model="searchForm.awardName" placeholder="请输入奖项名称"></el-input>
                </el-form-item>
                <el-form-item label="奖项类型">
                  <el-input v-model="searchForm.awardType" placeholder="请输入奖项类型"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSearch">搜索</el-button>
                  <el-button @click="resetForm">重置</el-button>
                </el-form-item>
              </el-form>

              <el-table :data="awards" style="width: 100%" stripe v-loading="loading">
                <el-table-column prop="awardId" label="奖项ID" width="80"></el-table-column>
                <el-table-column prop="awardName" label="奖项名称" width="150"></el-table-column>
                <el-table-column prop="awardLevel" label="奖项级别" width="100"></el-table-column>
                <el-table-column prop="awardType" label="奖项类型" width="100"></el-table-column>
                <el-table-column prop="description" label="奖项描述" min-width="200"></el-table-column>
                <el-table-column prop="requirement" label="评奖要求" min-width="200"></el-table-column>
                <el-table-column label="操作" width="150" fixed="right" v-if="hasRole('admin')">
                  <template #default="scope">
                    <el-button size="small" type="primary" @click="handleEdit(scope.row)">
                      <el-icon><edit /></el-icon> 编辑
                    </el-button>
                    <el-button size="small" type="danger" @click="handleDelete(scope.row)">
                      <el-icon><delete /></el-icon> 删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>

              <div class="pagination">
                <el-pagination
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                  :current-page="currentPage"
                  :page-sizes="[10, 20, 50, 100]"
                  :page-size="pageSize"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="total"
                ></el-pagination>
              </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 新增/编辑奖项类型对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="奖项信息"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item v-if="form.awardId" label="奖项ID">
          <el-input v-model="form.awardId" placeholder="奖项ID" disabled></el-input>
        </el-form-item>
        <el-form-item label="奖项名称">
          <el-input v-model="form.awardName" placeholder="请输入奖项名称"></el-input>
        </el-form-item>
        <el-form-item label="奖项级别">
          <el-select v-model="form.awardLevel" placeholder="请选择奖项级别">
            <el-option label="国家级" value="国家级"></el-option>
            <el-option label="省级" value="省级"></el-option>
            <el-option label="校级" value="校级"></el-option>
            <el-option label="院级" value="院级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="奖项类型">
          <el-input v-model="form.awardType" placeholder="请输入奖项类型"></el-input>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%;"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%;"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="名额">
          <el-input-number v-model="form.quota" :min="0" :step="1" placeholder="请输入名额"></el-input-number>
        </el-form-item>
        <el-form-item label="奖项描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入奖项描述" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="评奖要求">
          <el-input v-model="form.requirement" type="textarea" placeholder="请输入评奖要求" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="未发布" value="未发布"></el-option>
            <el-option label="已发布" value="已发布"></el-option>
            <el-option label="已结束" value="已结束"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审批教师">
          <el-select 
            v-model="form.approvingTeacherId" 
            placeholder="请选择审批教师"
            @change="handleTeacherChange"
          >
            <el-option 
              v-for="teacher in teachers" 
              :key="teacher.id" 
              :label="teacher.name" 
              :value="teacher.id"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
          <el-button type="danger" @click="handleDelete(form)">删除</el-button>
        </span>
      </template>
    </el-dialog>



    <!-- 奖项详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`${currentDetailAward?.awardName} - 奖项详情`"
      width="600px"
    >
      <div class="award-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="奖项名称">{{ currentDetailAward?.awardName }}</el-descriptions-item>
          <el-descriptions-item label="奖项级别">
            <el-tag
              :type="{
                'national': 'success',
                'provincial': 'warning',
                'school': 'info',
                'department': 'primary'
              }[currentDetailAward?.awardLevel || 'school']"
            >
              {{ currentDetailAward?.awardLevel }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="奖项类型">{{ currentDetailAward?.awardType }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag
              :type="{
                '未发布': 'info',
                '已发布': 'success',
                '已结束': 'warning'
              }[currentDetailAward?.status || '未发布']"
            >
              {{ currentDetailAward?.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag
              :type="{
                '待开始': 'info',
                '进行中': 'warning',
                '已完成': 'success',
                '已关闭': 'danger'
              }[currentDetailAward?.currentStatus || '待开始']"
            >
              {{ currentDetailAward?.currentStatus }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="名额">{{ currentDetailAward?.quota || '不限' }}</el-descriptions-item>
          <el-descriptions-item label="开始时间" :span="2">
            {{ currentDetailAward?.startTime ? new Date(currentDetailAward?.startTime).toLocaleString() : '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="结束时间" :span="2">
            {{ currentDetailAward?.endTime ? new Date(currentDetailAward?.endTime).toLocaleString() : '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="评奖要求" :span="2">
            <div class="detail-content">{{ currentDetailAward?.requirement || '暂无要求' }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="奖项描述" :span="2">
            <div class="detail-content">{{ currentDetailAward?.description || '暂无描述' }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ currentDetailAward?.createdAt ? new Date(currentDetailAward?.createdAt).toLocaleString() : '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间" :span="2">
            {{ currentDetailAward?.updatedAt ? new Date(currentDetailAward?.updatedAt).toLocaleString() : '未设置' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button v-if="hasRole('admin')" type="primary" @click="handleEdit(currentDetailAward)">
            编辑
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 评选流程详情对话框 -->
    <el-dialog
      v-model="processDetailDialogVisible"
      :title="`${currentProcessDetail?.awardName} - 评选流程详情`"
      width="800px"
    >
      <div v-if="currentProcessDetail" class="process-detail-container">
        <!-- 奖项基本信息 -->
        <div class="process-basic-info">
          <h3>{{ currentProcessDetail.awardName }}</h3>
          <div class="process-status">
            <el-tag
              :type="{
                '未发布': 'info',
                '已发布': 'success',
                '已结束': 'warning'
              }[currentProcessDetail.status || '未发布']"
            >
              {{ currentProcessDetail.status }}
            </el-tag>
            <el-tag
              :type="{
                '待开始': 'info',
                '进行中': 'warning',
                '已完成': 'success',
                '已关闭': 'danger'
              }[currentProcessDetail.currentStatus || '待开始']"
            >
              {{ currentProcessDetail.currentStatus }}
            </el-tag>
          </div>
        </div>
        
        <!-- 整体进度 -->
        <div class="process-overall-progress">
          <h4>整体进度</h4>
          <div class="progress-container">
            <el-progress 
              :percentage="calculateProgress(currentProcessDetail)" 
              :color="getProgressColor(calculateProgress(currentProcessDetail))"
            ></el-progress>
            <span class="progress-text">{{ calculateProgress(currentProcessDetail) }}% 完成</span>
          </div>
        </div>
        
        <!-- 流程阶段详情 -->
        <div class="process-stages">
          <h4>流程阶段详情</h4>
          <el-timeline>
            <!-- 学生申请阶段 -->
            <el-timeline-item 
              :timestamp="formatStageTime(currentProcessDetail.startTime, currentProcessDetail.endTime)"
              :status="getStageStatus(currentProcessDetail, '学生申请')"
            >
              <div class="stage-content">
                <h5>学生申请</h5>
                <p>学生提交奖项申请的阶段</p>
                <div class="stage-status">
                  <el-progress 
                    :percentage="getStageProgress(currentProcessDetail, '学生申请')" 
                    :color="getProgressColor(getStageProgress(currentProcessDetail, '学生申请'))"
                    size="small"
                  ></el-progress>
                  <span>{{ getStageProgress(currentProcessDetail, '学生申请') }}%</span>
                </div>
              </div>
            </el-timeline-item>
            
            <!-- 教师审批阶段 -->
            <el-timeline-item 
              :timestamp="formatStageTime(currentProcessDetail.startTime, currentProcessDetail.endTime)"
              :status="getStageStatus(currentProcessDetail, '教师审批')"
            >
              <div class="stage-content">
                <h5>教师审批</h5>
                <p>教师审核学生申请的阶段</p>
                <div class="stage-status">
                  <el-progress 
                    :percentage="getStageProgress(currentProcessDetail, '教师审批')" 
                    :color="getProgressColor(getStageProgress(currentProcessDetail, '教师审批'))"
                    size="small"
                  ></el-progress>
                  <span>{{ getStageProgress(currentProcessDetail, '教师审批') }}%</span>
                </div>
              </div>
            </el-timeline-item>
            
            <!-- 管理员审批阶段 -->
            <el-timeline-item 
              :timestamp="formatStageTime(currentProcessDetail.startTime, currentProcessDetail.endTime)"
              :status="getStageStatus(currentProcessDetail, '管理员审批')"
            >
              <div class="stage-content">
                <h5>管理员审批</h5>
                <p>管理员最终审批的阶段</p>
                <div class="stage-status">
                  <el-progress 
                    :percentage="getStageProgress(currentProcessDetail, '管理员审批')" 
                    :color="getProgressColor(getStageProgress(currentProcessDetail, '管理员审批'))"
                    size="small"
                  ></el-progress>
                  <span>{{ getStageProgress(currentProcessDetail, '管理员审批') }}%</span>
                </div>
              </div>
            </el-timeline-item>
            
            <!-- 结果公示阶段 -->
            <el-timeline-item 
              :timestamp="formatStageTime(currentProcessDetail.startTime, currentProcessDetail.endTime)"
              :status="getStageStatus(currentProcessDetail, '结果公示')"
            >
              <div class="stage-content">
                <h5>结果公示</h5>
                <p>公示最终评选结果的阶段</p>
                <div class="stage-status">
                  <el-progress 
                    :percentage="getStageProgress(currentProcessDetail, '结果公示')" 
                    :color="getProgressColor(getStageProgress(currentProcessDetail, '结果公示'))"
                    size="small"
                  ></el-progress>
                  <span>{{ getStageProgress(currentProcessDetail, '结果公示') }}%</span>
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
        
        <!-- 流程统计信息 -->
        <div class="process-statistics">
          <h4>流程统计</h4>
          <div class="statistics-grid">
            <el-card shadow="hover" size="small">
              <template #header>
                <div class="card-header">
                  <span>总申请数</span>
                </div>
              </template>
              <div class="statistic-value">{{ getApplicationCount(currentProcessDetail) }}</div>
            </el-card>
            
            <el-card shadow="hover" size="small">
              <template #header>
                <div class="card-header">
                  <span>教师审核通过</span>
                </div>
              </template>
              <div class="statistic-value">{{ getTeacherApprovedCount(currentProcessDetail) }}</div>
            </el-card>
            
            <el-card shadow="hover" size="small">
              <template #header>
                <div class="card-header">
                  <span>管理员审核通过</span>
                </div>
              </template>
              <div class="statistic-value">{{ getAdminApprovedCount(currentProcessDetail) }}</div>
            </el-card>
            
            <el-card shadow="hover" size="small">
              <template #header>
                <div class="card-header">
                  <span>最终获奖数</span>
                </div>
              </template>
              <div class="statistic-value">{{ getFinalApprovedCount(currentProcessDetail) }}</div>
            </el-card>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="processDetailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleViewApplications(currentProcessDetail)">
            查看申请列表
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 学生申请列表对话框 -->
    <el-dialog
      v-model="applicationDialogVisible"
      :title="`${currentAward?.awardName} - 学生申请列表`"
      width="800px"
    >
      <el-form :model="applicationSearchForm" :inline="true" class="search-form">
        <el-form-item label="学生姓名">
          <el-input v-model="applicationSearchForm.studentName" placeholder="请输入学生姓名"></el-input>
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="applicationSearchForm.approvalStatus" placeholder="请选择审批状态">
            <el-option label="待教师审批" value="待教师审批"></el-option>
            <el-option label="教师已通过" value="教师已通过"></el-option>
            <el-option label="教师已拒绝" value="教师已拒绝"></el-option>
            <el-option label="待管理员审批" value="待管理员审批"></el-option>
            <el-option label="管理员已通过" value="管理员已通过"></el-option>
            <el-option label="管理员已拒绝" value="管理员已拒绝"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleApplicationSearch">搜索</el-button>
          <el-button @click="resetApplicationForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="studentApplications" style="width: 100%" stripe v-loading="applicationLoading">
        <el-table-column prop="id" label="申请ID" width="80"></el-table-column>
        <el-table-column prop="student.name" label="学生姓名" width="120"></el-table-column>
        <el-table-column prop="student.student_number" label="学号" width="150">
          <template #default="scope">{{ scope.row.student?.student_number || '-' }}</template>
        </el-table-column>
        <el-table-column prop="student.major" label="专业" width="150">
          <template #default="scope">{{ scope.row.student?.major || '-' }}</template>
        </el-table-column>
        <el-table-column prop="student.className" label="班级" width="120"></el-table-column>

        <el-table-column prop="applicationDate" label="申请日期" width="150">
          <template #default="scope">{{ scope.row.applicationDate ? new Date(scope.row.applicationDate).toLocaleString() : '-' }}</template>
        </el-table-column>
        <el-table-column prop="approvalStatus" label="审批状态" width="120">
          <template #default="scope">
            <el-tag
              :type="{
                '待教师审批': 'info',
                '教师已通过': 'primary',
                '教师已拒绝': 'danger',
                '待管理员审批': 'warning',
                '管理员已通过': 'success',
                '管理员已拒绝': 'danger'
              }[scope.row.approvalStatus || '待教师审批']"
            >
              {{ scope.row.approvalStatus || '待教师审批' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <!-- 教师审批按钮 -->
            <el-button
              v-if="hasRole('teacher') && scope.row.approvalStatus === '待教师审批'"
              type="success"
              size="small"
              @click="handleTeacherApproval(scope.row, true)"
            >
              审批通过
            </el-button>
            <el-button
              v-if="hasRole('teacher') && scope.row.approvalStatus === '待教师审批'"
              type="danger"
              size="small"
              @click="handleTeacherApproval(scope.row, false)"
              style="margin-left: 5px"
            >
              审批拒绝
            </el-button>
            <!-- 管理员二次审批按钮 -->
            <el-button
              v-if="hasRole('admin') && scope.row.approvalStatus === '待管理员审批'"
              type="success"
              size="small"
              @click="handleAdminApproval(scope.row, true)"
            >
              终审通过
            </el-button>
            <el-button
              v-if="hasRole('admin') && scope.row.approvalStatus === '待管理员审批'"
              type="danger"
              size="small"
              @click="handleAdminApproval(scope.row, false)"
              style="margin-left: 5px"
            >
              终审拒绝
            </el-button>
            <span v-else-if="scope.row.approvalStatus && (scope.row.approvalStatus.includes('已通过') || scope.row.approvalStatus.includes('已拒绝'))">
              已处理
            </span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 申请列表分页 -->
      <div class="pagination">
        <el-pagination
          @size-change="handleApplicationSizeChange"
          @current-change="handleApplicationCurrentChange"
          :current-page="applicationCurrentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="applicationPageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="applicationTotal"
        ></el-pagination>
      </div>
    </el-dialog>
  </div>
</div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { hasRole, getUserInfo } from '../utils/role'
import axios from 'axios'
import router from '../router'

export default {
  name: 'AwardView',
  setup() {
    // 奖项类型管理
    const dialogVisible = ref(false)
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const awards = ref([])
    const majors = ref([])
    const teachers = ref([])
    const activeTab = ref('awards')
    const form = reactive({
      awardId: null,
      awardName: '',
      awardLevel: '',
      awardType: '',
      startTime: '',
      endTime: '',
      quota: null,
      description: '',
      requirement: '',
      status: '未发布', // 添加状态字段
      approvingTeacherId: null,
      approvingTeacherName: ''
    })
    
    // 评选流程管理
    const selectionProcessList = ref([])
    const searchForm = reactive({
      awardName: '',
      awardType: ''
    })
    
    // 评选流程分页变量
    const currentProcessPage = ref(1)
    const processPageSize = ref(10)
    const processTotal = ref(0)
    
    // 评选流程表格显示数据
    const processTableData = ref([])



    // 学生申请列表管理
    const applicationDialogVisible = ref(false)
    const applicationLoading = ref(false)
    const applicationCurrentPage = ref(1)
    const applicationPageSize = ref(10)
    const applicationTotal = ref(0)
    const studentApplications = ref([])
    const currentAward = ref(null)
    const applicationSearchForm = reactive({
      studentName: '',
      approvalStatus: ''
    })

    // 奖项详情对话框
    const detailDialogVisible = ref(false)
    const currentDetailAward = ref(null)
    
    // 评选流程详情对话框
    const processDetailDialogVisible = ref(false)
    const currentProcessDetail = ref(null)



    // 获取奖项类型列表
    const getAwards = async () => {
      loading.value = true
      try {
        // 构建请求参数
        const params = {
          pageNum: currentPage.value,
          pageSize: pageSize.value,
          awardName: searchForm.awardName,
          awardType: searchForm.awardType
        }
        
        const response = await axios.get('/api/awards', { params })
        awards.value = response.data.records
        total.value = response.data.total
        // 更新评选流程列表
        await updateSelectionProcessList()
      } catch (error) {
        ElMessage.error('获取奖项列表失败')
        console.error('获取奖项列表失败:', error)
        console.error('错误详情:', error.response)
        awards.value = []
      } finally {
        loading.value = false
      }
    }

    // 获取专业列表
    const getMajors = async () => {
      try {
        const response = await axios.get('/api/majors')
        majors.value = response.data
      } catch (error) {
        ElMessage.error('获取专业列表失败')
        majors.value = []
      }
    }

    // 获取教师列表
    const getTeachers = async () => {
      try {
        const response = await axios.get('/api/teachers/all')
        teachers.value = response.data
      } catch (error) {
        ElMessage.error('获取教师列表失败')
        teachers.value = []
      }
    }

    // 处理教师选择变化
    const handleTeacherChange = (teacherId) => {
      const selectedTeacher = teachers.value.find(teacher => teacher.id === teacherId)
      if (selectedTeacher) {
        form.approvingTeacherName = selectedTeacher.name
      } else {
        form.approvingTeacherName = ''
      }
    }



    // 新增/编辑奖项
    const handleSubmit = async () => {
      try {
        if (form.awardId) {
          // 编辑
          await axios.put(`/api/awards/${form.awardId}`, form)
          ElMessage.success('奖项更新成功')
        } else {
          // 新增
          await axios.post('/api/awards', form)
          ElMessage.success('奖项新增成功')
        }
        dialogVisible.value = false
        getAwards()
        resetForm()
      } catch (error) {
        ElMessage.error('操作失败，请重试')
      }
    }

    // 编辑奖项类型
    const handleEdit = (row) => {
      // 深拷贝行数据，避免直接修改原数据
      const formData = JSON.parse(JSON.stringify(row))
      
      // 确保时间格式正确，与后端期望的yyyy-MM-dd HH:mm:ss格式一致
      if (formData.startTime) {
        formData.startTime = new Date(formData.startTime).toISOString().slice(0, 19).replace('T', ' ')
      }
      if (formData.endTime) {
        formData.endTime = new Date(formData.endTime).toISOString().slice(0, 19).replace('T', ' ')
      }
      
      Object.assign(form, formData)
      dialogVisible.value = true
    }

    // 删除奖项
    const handleDelete = async (row) => {
      try {
        await axios.delete(`/api/awards/${row.awardId}`)
        ElMessage.success('奖项删除成功')
        getAwards()
      } catch (error) {
        ElMessage.error('删除失败，请重试')
      }
    }

    // 搜索奖项类型
    const handleSearch = () => {
      currentPage.value = 1
      getAwards()
    }

    // 重置搜索表单
    const resetSearchForm = () => {
      searchForm.awardName = ''
      searchForm.awardType = ''
      currentPage.value = 1
      getAwards()
    }
    
    // 重置编辑表单
    const resetEditForm = () => {
      form.awardId = null
      form.awardName = ''
      form.awardLevel = ''
      form.awardType = ''
      form.startTime = ''
      form.endTime = ''
      form.quota = null
      form.description = ''
      form.requirement = ''
      form.status = '未发布'
      form.approvingTeacherId = null
      form.approvingTeacherName = ''
    }
    
    // 新建奖项
    const handleAdd = () => {
      resetEditForm()
      dialogVisible.value = true
    }
    
    // 重置表单（保持向后兼容）
    const resetForm = () => {
      resetSearchForm()
    }

    // 分页处理
    const handleSizeChange = (size) => {
      pageSize.value = size
      getAwards()
    }

    const handleCurrentChange = (current) => {
      currentPage.value = current
      getAwards()
    }





    // 获取学生申请列表
    const getStudentApplications = async (award) => {
      console.log('===== 开始获取学生申请列表 =====')
      console.log('当前award对象:', award)
      console.log('当前pageNum:', applicationCurrentPage.value)
      console.log('当前pageSize:', applicationPageSize.value)
      console.log('当前搜索条件:', applicationSearchForm)
      if (!award || !award.awardId) {
        console.error('award参数为空或awardId不存在')
        return
      }
      
      applicationLoading.value = true
      try {
        // 构建搜索参数
        const params = {
          pageNum: applicationCurrentPage.value,
          pageSize: applicationPageSize.value,
          awardId: parseInt(award.awardId) // 确保awardId是数字类型
        }
        console.log('请求参数:', params)
        
        // 只有当搜索条件不为空时才添加到参数中
        if (applicationSearchForm.studentName && (hasRole('admin') || hasRole('teacher'))) {
          params.studentName = applicationSearchForm.studentName
        }
        
        if (applicationSearchForm.approvalStatus) {
          // 将字符串状态转换为数字状态码
          const statusMap = {
            '待教师审批': 0,
            '教师已通过': 1,
            '教师已拒绝': 2,
            '待管理员审批': 3,
            '管理员已通过': 4,
            '管理员已拒绝': 5
          }
          params.status = statusMap[applicationSearchForm.approvalStatus] // 完整的状态映射
        }
        
        // 根据用户角色调用不同的API接口
        let response;
        if (hasRole('student')) {
          // 学生角色只能查看自己的申请
          const userInfo = getUserInfo();
          if (userInfo.studentNumber) {
            params.studentNumber = userInfo.studentNumber;
          }
        }
        
        // 使用新的搜索API获取申请列表
        response = await axios.get(`/api/student-award-applications/page/search`, {
          params
        })
        
        // 将后端返回的数字状态转换为字符串状态
        studentApplications.value = response.data.records.map(app => {
          let approvalStatus = '待教师审批'
          if (app.status === 1) {
            approvalStatus = '待管理员审批'
          } else if (app.status === 2) {
            approvalStatus = '教师已拒绝'
          } else if (app.status === 3) {
            approvalStatus = '管理员已通过'
          } else if (app.status === 4) {
            approvalStatus = '管理员已拒绝'
          }
          return {
            ...app,
            approvalStatus,
            applicationDate: app.applicationTime,
            // 确保学生信息正确映射
            student: {
              ...app.student
              // 直接使用后端返回的student对象，包含student_number字段
            }
          }
        })
        
        applicationTotal.value = response.data.total
      } catch (error) {
        console.error('获取学生申请列表失败:', error)
        console.error('错误详细信息:', JSON.stringify(error, null, 2))
        // 显示更详细的错误信息
        let errorMsg = '获取学生申请列表失败'
        if (error.response) {
            // 服务器返回了错误状态码
            errorMsg += `: ${error.response.status} - ${error.response.data?.message || error.response.statusText}`
            console.error('响应数据:', error.response.data)
            console.error('响应状态:', error.response.status)
            console.error('响应头:', error.response.headers)
        } else if (error.request) {
            // 请求发出但没有收到响应
            errorMsg += ': 服务器无响应'
            console.error('请求对象:', error.request)
        } else {
            // 请求配置时发生错误
            errorMsg += `: ${error.message}`
        }
        ElMessage.error(errorMsg)
        console.error('获取学生申请列表失败:', error)
      } finally {
        applicationLoading.value = false
      }
    }

    // 查看申请列表
    const handleViewApplications = (award) => {
      // 简化方法，只设置必要的状态
      console.log('查看申请按钮被点击，award对象:', award)
      
      // 1. 设置当前选中的奖项
      currentAward.value = award
      
      // 2. 重置搜索表单
      applicationSearchForm.studentName = ''
      applicationSearchForm.approvalStatus = ''
      
      // 3. 重置分页
      applicationCurrentPage.value = 1
      
      // 4. 强制设置对话框为可见
      console.log('设置applicationDialogVisible.value = true')
      applicationDialogVisible.value = true
      
      // 5. 获取申请列表
      getStudentApplications(award)
    }
    
    // 测试函数：用于快速测试查看申请功能
    window.testViewApplications = () => {
      console.log('测试查看申请功能...')
      const testAward = {
        awardId: 1,
        awardName: '测试奖项'
      }
      handleViewApplications(testAward)
    }

    // 教师审批
    const handleTeacherApproval = async (application, approve) => {
      const status = approve ? 1 : 2;
      const comments = approve ? '教师审批通过' : '教师审批拒绝';
      try {
        await axios.put(`/api/student-award-applications/${application.id}/teacher-approve`, null, {
          params: { status, comments }
        })
        ElMessage.success(approve ? '审批通过成功' : '审批拒绝成功')
        getStudentApplications(currentAward.value)
      } catch (error) {
        ElMessage.error('审批失败，请重试')
        console.error('教师审批失败:', error);
      }
    }

    // 管理员二次审批
    const handleAdminApproval = async (application, approve) => {
      const status = approve ? 1 : 2;
      const comments = approve ? '管理员终审通过' : '管理员终审拒绝';
      try {
        await axios.put(`/api/student-award-applications/${application.id}/admin-approve`, null, {
          params: { status, comments }
        })
        ElMessage.success(approve ? '终审通过成功' : '终审拒绝成功')
        getStudentApplications(currentAward.value)
      } catch (error) {
        ElMessage.error('审批失败，请重试')
        console.error('管理员审批失败:', error);
      }
    }

    // 搜索申请
    const handleApplicationSearch = () => {
      applicationCurrentPage.value = 1
      getStudentApplications(currentAward.value)
    }

    // 重置申请搜索表单
    const resetApplicationForm = () => {
      applicationSearchForm.studentName = ''
      applicationSearchForm.approvalStatus = ''
      applicationCurrentPage.value = 1
      getStudentApplications(currentAward.value)
    }

    // 申请列表分页处理
    const handleApplicationSizeChange = (size) => {
      applicationPageSize.value = size
      getStudentApplications(currentAward.value)
    }

    const handleApplicationCurrentChange = (current) => {
      applicationCurrentPage.value = current
      getStudentApplications(currentAward.value)
    }

    // 计算评选流程进度
    const calculateProgress = (award) => {
      // 根据奖项状态和当前阶段计算进度
      if (award.status === '已结束') {
        return 100
      } else if (award.status === '已发布') {
        switch (award.currentStage) {
          case '学生申请':
            return 25
          case '教师审批':
            return 50
          case '管理员审批':
            return 75
          default:
            return 0
        }
      } else {
        return 0
      }
    }

    // 获取进度颜色
    const getProgressColor = (progress) => {
      // 根据进度返回不同颜色
      if (progress === 0) {
        return '#909399' // 未开始 - 灰色
      } else if (progress < 100) {
        return '#67c23a' // 进行中 - 绿色
      } else {
        return '#e6a23c' // 已完成 - 橙色
      }
    }

    // 获取单个奖项的统计数据
    const getAwardStatistics = async (awardId) => {
      try {
        // 并行获取四个统计数据
        const [applicationCountData, finalApprovedCountData, teacherApprovedCountData, adminApprovedCountData] = await Promise.all([
          axios.get(`/api/student-award-applications/award/${awardId}/count`),
          axios.get(`/api/student-award-applications/award/${awardId}/approved-count`),
          axios.get(`/api/student-award-applications/award/${awardId}/teacher-approved-count`),
          axios.get(`/api/student-award-applications/award/${awardId}/admin-approved-count`)
        ]);
        
        return {
          applicationCount: applicationCountData.data,
          finalApprovedCount: finalApprovedCountData.data,
          teacherApprovedCount: teacherApprovedCountData.data,
          adminApprovedCount: adminApprovedCountData.data
        };
      } catch (error) {
        console.error('获取奖项统计数据失败:', error);
        return {
          applicationCount: 0,
          finalApprovedCount: 0,
          teacherApprovedCount: 0,
          adminApprovedCount: 0
        };
      }
    }

    // 更新评选流程列表
    const updateSelectionProcessList = async () => {
      // 计算每个奖项的当前阶段和进度
      const updatedAwards = await Promise.all(awards.value.map(async (award) => {
        // 根据奖项状态和时间计算当前阶段
        let currentStage = '未开始'
        let currentStatus = '待开始'
        const now = new Date()
        const endTime = award.endTime ? new Date(award.endTime) : null
        
        // 检查结束时间是否已到
        const isEndTimeReached = endTime != null && now > endTime
        
        // 根据奖项状态和结束时间设置当前状态
        if (award.status === '未发布') {
          currentStatus = '待开始'
          currentStage = '未开始'
        } else if (award.status === '已发布') {
          if (isEndTimeReached) {
            currentStatus = '已关闭'
            currentStage = '结果公示'
          } else {
            currentStatus = '进行中'
            
            // 获取实际的申请和审批数据
            const stats = await getAwardStatistics(award.awardId);
            
            // 根据实际数据确定当前阶段
            if (stats.applicationCount === 0) {
              // 没有学生申请，处于学生申请阶段
              currentStage = '学生申请'
            } else if (stats.teacherApprovedCount < stats.applicationCount) {
              // 有学生申请，但教师还没审批完，处于教师审批阶段
              currentStage = '教师审批'
            } else if (stats.adminApprovedCount < stats.teacherApprovedCount) {
              // 教师审批完，但管理员还没审批完，处于管理员审批阶段
              currentStage = '管理员审批'
            } else {
              // 管理员审批完，处于结果公示阶段
              currentStage = '结果公示'
            }
          }
        } else if (award.status === '已结束') {
          currentStatus = isEndTimeReached ? '已关闭' : '已完成'
          currentStage = '结果公示'
        }
        
        // 将当前阶段和状态保存到奖项对象中
        award.currentStage = currentStage
        award.currentStatus = currentStatus
        
        return award;
      }));
      
      // 更新评选流程列表
      selectionProcessList.value = updatedAwards
      
      // 更新总条数
      processTotal.value = selectionProcessList.value.length
      
      // 分页处理
      updateProcessTableData()
    }
    
    // 更新评选流程表格数据
    const updateProcessTableData = () => {
      const start = (currentProcessPage.value - 1) * processPageSize.value
      const end = start + processPageSize.value
      processTableData.value = selectionProcessList.value.slice(start, end)
    }
    
    // 评选流程分页方法
    const handleProcessSizeChange = (size) => {
      processPageSize.value = size
      currentProcessPage.value = 1
      updateProcessTableData()
    }
    
    const handleProcessCurrentChange = (current) => {
      currentProcessPage.value = current
      updateProcessTableData()
    }

    // 查看奖项详情
    const handleViewDetails = (award) => {
      console.log('handleViewDetails被调用，award对象:', award)
      console.log('调用前currentDetailAward:', currentDetailAward.value)
      console.log('调用前detailDialogVisible:', detailDialogVisible.value)
      
      currentDetailAward.value = award
      detailDialogVisible.value = true
      
      console.log('调用后currentDetailAward:', currentDetailAward.value)
      console.log('调用后detailDialogVisible:', detailDialogVisible.value)
    }

    // 用于存储异步获取的统计数据
    const applicationCount = ref(0)
    const finalApprovedCount = ref(0)
    const teacherApprovedCount = ref(0)
    const adminApprovedCount = ref(0)
    
    // 加载统计数据
      const loadStatisticsData = async (process) => {
        try {
          console.log('开始加载统计数据，process:', process);
          console.log('awardId:', process.awardId);
          // 并行获取四个统计数据
          const [applicationCountData, finalApprovedCountData, teacherApprovedCountData, adminApprovedCountData] = await Promise.all([
            axios.get(`/api/student-award-applications/award/${process.awardId}/count`).then(res => { console.log('总申请数接口返回:', res); return res; }),
            axios.get(`/api/student-award-applications/award/${process.awardId}/approved-count`).then(res => { console.log('最终获奖数接口返回:', res); return res; }),
            axios.get(`/api/student-award-applications/award/${process.awardId}/teacher-approved-count`).then(res => { console.log('教师审核通过数接口返回:', res); return res; }),
            axios.get(`/api/student-award-applications/award/${process.awardId}/admin-approved-count`).then(res => { console.log('管理员审核通过数接口返回:', res); return res; })
          ]);
          applicationCount.value = applicationCountData.data;
          finalApprovedCount.value = finalApprovedCountData.data;
          teacherApprovedCount.value = teacherApprovedCountData.data;
          adminApprovedCount.value = adminApprovedCountData.data;
        } catch (error) {
          console.error('加载统计数据失败:', error);
          console.error('错误详情:', error.response ? error.response.data : error.message);
          applicationCount.value = 0;
          finalApprovedCount.value = 0;
          teacherApprovedCount.value = 0;
          adminApprovedCount.value = 0;
        }
      }
      
      // 查看流程详情
    const handleViewProcessDetails = async (process) => {
      currentProcessDetail.value = process
      processDetailDialogVisible.value = true
      // 加载统计数据
      await loadStatisticsData(process)
    }
    
    // 格式化阶段时间
    const formatStageTime = (startTime, endTime) => {
      if (!startTime && !endTime) {
        return '时间未设置'
      }
      if (!startTime) {
        return `至 ${new Date(endTime).toLocaleDateString()}`
      }
      if (!endTime) {
        return `${new Date(startTime).toLocaleDateString()} 开始`
      }
      return `${new Date(startTime).toLocaleDateString()} 至 ${new Date(endTime).toLocaleDateString()}`
    }
    
    // 获取状态标签类型
    const getStatusTagType = (status) => {
      const typeMap = {
        '未发布': 'info',
        '已发布': 'success',
        '已结束': 'warning'
      }
      return typeMap[status] || 'info'
    }
    
    // 获取当前状态标签类型
    const getCurrentStatusTagType = (currentStatus) => {
      const typeMap = {
        '待开始': 'info',
        '进行中': 'warning',
        '已完成': 'success',
        '已关闭': 'danger'
      }
      return typeMap[currentStatus] || 'info'
    }
    
    // 获取阶段状态
    const getStageStatus = (process, stageName) => {
      const overallProgress = calculateProgress(process)
      if (overallProgress === 0) return 'wait'
      if (overallProgress === 100) return 'success'
      
      const stageMap = {
        '学生申请': 25,
        '教师审批': 50,
        '管理员审批': 75,
        '结果公示': 100
      }
      
      const stageProgress = stageMap[stageName] || 0
      if (overallProgress >= stageProgress) return 'success'
      if (overallProgress >= stageProgress - 25) return 'process'
      return 'wait'
    }
    
    // 获取阶段进度
    const getStageProgress = (process, stageName) => {
      const overallProgress = calculateProgress(process)
      if (overallProgress === 0) return 0
      if (overallProgress === 100) return 100
      
      const stageMap = {
        '学生申请': 25,
        '教师审批': 50,
        '管理员审批': 75,
        '结果公示': 100
      }
      
      const stageProgress = stageMap[stageName] || 0
      if (overallProgress >= stageProgress) return 100
      if (overallProgress >= stageProgress - 25) return 50
      return 0
    }
    
    // 获取总申请数
    const getApplicationCount = (process) => {
      return applicationCount.value;
    }
    
    // 获取教师审核通过数
    const getTeacherApprovedCount = (process) => {
      return teacherApprovedCount.value
    }
    
    // 获取管理员审核通过数
    const getAdminApprovedCount = (process) => {
      return adminApprovedCount.value
    }
    
    // 获取最终获奖数
    const getFinalApprovedCount = (process) => {
      return finalApprovedCount.value;
    }

    // 发布奖项
    const handlePublish = async (award) => {
      try {
        // 更新奖项状态为已发布，并设置当前阶段为学生申请
        await axios.put(`/api/awards/${award.awardId}`, { 
          ...award, 
          status: '已发布',
          currentStage: '学生申请',
          currentStatus: '进行中'
        })
        ElMessage.success('奖项发布成功')
        getAwards()
      } catch (error) {
        console.error('发布奖项失败:', error)
        ElMessage.error('发布失败，请重试')
      }
    }
    
    // 结束评选
    const handleEndSelection = async (award) => {
      try {
        // 更新奖项状态为已结束，当前状态为已完成
        await axios.put(`/api/awards/${award.awardId}`, { ...award, status: '已结束', currentStatus: '已完成' })
        ElMessage.success('结束评选成功')
        getAwards()
      } catch (error) {
        console.error('结束评选失败:', error)
        ElMessage.error('操作失败，请重试')
      }
    }

    // 查看评选结果
    const handleViewResults = (award) => {
      // 保存当前奖项ID到localStorage，供结果页面使用
      localStorage.setItem('currentAwardId', award.awardId)
      localStorage.setItem('currentAwardName', award.awardName)
      // 跳转到学生奖项申请页面查看结果
      router.push('/student-award-applications')
    }

    // 页面加载时获取数据


    onMounted(() => {
      getAwards()
      getMajors()
      getTeachers()
    })

    return {
      // 奖项类型管理
      dialogVisible,
      loading,
      currentPage,
      pageSize,
      total,
      awards,
      form,
      searchForm,
      activeTab,
      majors,
      teachers,
      
      // 评选流程管理
      selectionProcessList,
      // 异步统计数据
      applicationCount,
      finalApprovedCount,
      calculateProgress,
      processDetailDialogVisible,
      currentProcessDetail,
      
      // 奖项详情
      detailDialogVisible,
      currentDetailAward,

      // 角色判断
      hasRole,

      // 奖项类型管理方法
      getAwards,
      getTeachers,
      handleTeacherChange,
      handleAdd,
      handleSubmit,
      handleEdit,
      handleDelete,
      handleSearch,
      resetForm,
      resetEditForm,
      handleSizeChange,
      handleCurrentChange,
      handlePublish,
        handleEndSelection,
      
      // 评选流程管理方法
      getProgressColor,
      handleViewDetails,
      handleViewApplications,
      handleViewProcessDetails,
      formatStageTime,
      getStatusTagType,
      getCurrentStatusTagType,
      getStageStatus,
      getStageProgress,
      getApplicationCount,
      getTeacherApprovedCount,
      getAdminApprovedCount,
      getFinalApprovedCount,
      
      // 评选流程分页变量和方法
      currentProcessPage,
      processPageSize,
      processTotal,
      processTableData,
      handleProcessSizeChange,
      handleProcessCurrentChange,
      
      // 学生申请管理
  applicationDialogVisible,
  applicationCurrentPage,
  applicationPageSize,
  applicationLoading,
  applicationTotal,
  studentApplications,
  currentAward,
  applicationSearchForm,
  handleApplicationSearch,
  resetApplicationForm,
  handleApplicationSizeChange,
  handleApplicationCurrentChange,
  handleViewResults

    }
  }
}
</script>

<style scoped>
.award-container {
  padding: 20px;
}

/* 评奖评优管理界面样式 */
.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.no-data {
  text-align: center;
  padding: 20px;
  color: #909399;
}

.filter-section {
  margin-top: 30px;
}

.student-filter-form {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

/* 奖项卡片样式 */
.award-cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 30px;
}

.award-card {
  transition: all 0.3s ease;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.award-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.award-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.award-card-header .award-name {
  margin: 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.award-card-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.award-date {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.award-quota {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.award-requirement {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
  line-height: 1.5;
}

.award-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.award-actions .el-button {
  padding: 4px 12px;
  font-size: 12px;
}

/* 评选流程表格样式 */
.process-table-container {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.process-table-container .el-table {
  border: none;
}

.process-table-container .el-table__header-wrapper th {
  background-color: #fafafa;
  font-weight: 600;
  font-size: 14px;
  color: #303133;
}

.process-table-container .el-table__body-wrapper tr:hover > td {
  background-color: #f5f7fa;
}

/* 进度条样式 */
.progress-container {
  display: flex;
  align-items: center;
  width: 100%;
}

.progress-text {
  margin-left: 10px;
  font-size: 13px;
  color: #606266;
  min-width: 60px;
}

.el-progress {
  flex: 1;
}

@media (max-width: 768px) {
  .award-container {
    padding: 10px;
  }
  
  .search-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .award-cards-container {
    grid-template-columns: 1fr;
  }
}
</style>