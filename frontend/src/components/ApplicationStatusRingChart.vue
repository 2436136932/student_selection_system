<template>
  <div class="application-status-ring-chart">
    <div ref="chartRef" class="chart"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

// 图表引用
const chartRef = ref(null)

// 图表实例
let chartInstance = null

// 检查容器尺寸
const checkContainerSize = () => {
  if (chartRef.value) {
    const rect = chartRef.value.getBoundingClientRect()
    console.log('申请状态图表容器尺寸:', rect.width, rect.height)
    return rect.width > 0 && rect.height > 0
  }
  return false
}

// 绘制图表
const drawChart = async () => {
  try {
    console.log('开始绘制申请状态分布图表...')
    
    // 发送API请求获取数据
    console.log('开始请求申请状态分布数据...')
    const response = await axios.get('/api/statistics/application-status-distribution')
    console.log('申请状态分布API请求成功，响应:', response)
    
    const data = response.data.data
    console.log('获取到的申请状态分布数据:', data)
    
    // 处理空数据情况
    const chartData = Object.entries(data || {}).map(([name, value]) => ({
      name,
      value
    }))
    
    console.log('转换后的申请状态分布图表数据:', chartData)
    
    // 确保DOM已经更新，容器尺寸已经确定
    await nextTick()
    
    // 如果容器尺寸为0，等待一段时间后重试
    if (!checkContainerSize()) {
      console.log('申请状态图表容器尺寸为0，等待100ms后重试...')
      await new Promise(resolve => setTimeout(resolve, 100))
      
      // 再次检查容器尺寸
      if (!checkContainerSize()) {
        console.error('申请状态图表容器尺寸仍然为0，无法初始化图表')
        return
      }
    }
    
    // 初始化ECharts实例
    if (!chartInstance) {
      console.log('初始化申请状态分布图表实例...')
      chartInstance = echarts.init(chartRef.value)
    }
    
    // 设置图表选项
    const option = {
      title: {
        text: '申请状态分布',
        left: 'center',
        textStyle: {
          fontSize: 18,
          fontWeight: 'bold',
          color: '#2c3e50'
        }
      },
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#3498db',
        borderWidth: 1,
        borderRadius: 10,
        textStyle: {
          color: '#2c3e50'
        },
        padding: 12
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        textStyle: {
          color: '#2c3e50',
          fontSize: 14
        },
        itemWidth: 12,
        itemHeight: 12,
        itemGap: 15
      },
      series: [
        {
          name: '申请状态',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '55%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 12,
            borderColor: '#fff',
            borderWidth: 3,
            // 使用浅色透明渐变颜色
            color: function(params) {
              const colorList = [
                new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(46, 204, 113, 0.3)' },
                  { offset: 1, color: 'rgba(39, 174, 96, 0.3)' }
                ]),
                new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(243, 156, 18, 0.3)' },
                  { offset: 1, color: 'rgba(230, 126, 34, 0.3)' }
                ]),
                new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(231, 76, 60, 0.3)' },
                  { offset: 1, color: 'rgba(192, 57, 43, 0.3)' }
                ]),
                new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(149, 165, 166, 0.3)' },
                  { offset: 1, color: 'rgba(127, 140, 141, 0.3)' }
                ])
              ];
              return colorList[params.dataIndex % colorList.length];
            }
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 20,
              fontWeight: 'bold',
              color: '#2c3e50'
            },
            itemStyle: {
              shadowBlur: 20,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.3)'
            }
          },
          labelLine: {
            show: false
          },
          data: chartData.length > 0 ? chartData : [{ name: '无数据', value: 1 }],
          // 处理无数据情况
          silent: chartData.length === 0,
          animationType: 'scale',
          animationEasing: 'elasticOut',
          animationDelay: function(idx) {
            return Math.random() * 200;
          }
        }
      ]
    }
    
    console.log('设置申请状态分布图表选项:', option)
    chartInstance.setOption(option)
    console.log('申请状态分布图表绘制完成')
  } catch (error) {
    console.error('绘制申请状态分布图表失败:', error)
    console.error('错误详情:', error.response || error.message || error)
    
    // 确保DOM已经更新
    await nextTick()
    
    // 初始化图表并显示错误信息
    try {
      if (chartRef.value && !checkContainerSize()) {
        console.error('申请状态图表容器尺寸为0，无法显示错误信息')
        return
      }
      
      if (chartRef.value && !chartInstance) {
        chartInstance = echarts.init(chartRef.value)
      }
      
      if (chartInstance) {
        const errorOption = {
          title: {
            text: '申请状态分布',
            left: 'center'
          },
          series: [
            {
              name: '申请状态',
              type: 'pie',
              radius: ['40%', '70%'],
              data: [{ name: '加载失败', value: 1 }],
              label: {
                show: true,
                position: 'center',
                formatter: '数据加载失败',
                fontSize: 16
              }
            }
          ]
        }
        chartInstance.setOption(errorOption)
      }
    } catch (initError) {
      console.error('初始化错误图表失败:', initError)
    }
  }
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  if (chartInstance) {
    console.log('调整申请状态分布图表尺寸...')
    chartInstance.resize()
    console.log('申请状态分布图表尺寸调整完成')
  }
}

// 页面加载时初始化
onMounted(() => {
  console.log('申请状态分布图表组件挂载，开始绘制图表...')
  drawChart()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理资源
onUnmounted(() => {
  console.log('申请状态分布图表组件卸载，清理资源...')
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
    console.log('申请状态分布图表实例已销毁')
  }
})
</script>

<style scoped>
.application-status-ring-chart {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart {
  width: 100%;
  height: 100%;
  min-height: 300px;
}
</style>