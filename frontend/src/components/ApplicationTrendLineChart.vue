<template>
  <div class="application-trend-line-chart">
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
    console.log('奖项申请趋势图表容器尺寸:', rect.width, rect.height)
    return rect.width > 0 && rect.height > 0
  }
  return false
}

// 绘制图表
const drawChart = async () => {
  try {
    console.log('开始绘制奖项申请趋势图表...')
    
    // 发送API请求获取数据
    console.log('开始请求奖项申请趋势数据...')
    const response = await axios.get('/api/statistics/application-trend')
    console.log('奖项申请趋势API请求成功，响应:', response)
    
    const data = response.data.data
    console.log('获取到的奖项申请趋势数据:', data)
    
    // 处理空数据情况
    let categories = data ? Object.keys(data) : []
    let values = data ? Object.values(data) : []
    
    // 如果没有数据，添加默认的空数据提示
    if (categories.length === 0 || values.length === 0) {
      categories = ['暂无数据']
      values = [0]
    }
    
    console.log('奖项申请趋势图表分类:', categories)
    console.log('奖项申请趋势图表数值:', values)
    
    // 确保DOM已经更新，容器尺寸已经确定
    await nextTick()
    
    // 如果容器尺寸为0，等待一段时间后重试
    if (!checkContainerSize()) {
      console.log('奖项申请趋势图表容器尺寸为0，等待100ms后重试...')
      await new Promise(resolve => setTimeout(resolve, 100))
      
      // 再次检查容器尺寸
      if (!checkContainerSize()) {
        console.error('奖项申请趋势图表容器尺寸仍然为0，无法初始化图表')
        return
      }
    }
    
    // 初始化ECharts实例
    if (!chartInstance) {
      console.log('初始化奖项申请趋势图表实例...')
      chartInstance = echarts.init(chartRef.value)
    }
    
    // 设置图表选项
    const option = {
      title: {
        text: '奖项申请趋势',
        left: 'center',
        textStyle: {
          fontSize: 18,
          fontWeight: 'bold',
          color: '#2c3e50'
        }
      },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: 'rgba(243, 156, 18, 0.3)',
        borderWidth: 1,
        borderRadius: 10,
        textStyle: {
          color: '#2c3e50'
        },
        padding: 12,
        axisPointer: {
          type: 'line',
          lineStyle: {
            color: 'rgba(243, 156, 18, 0.3)',
            width: 2
          }
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: '15%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: categories.length > 0 ? categories : ['暂无数据'],
        axisLabel: {
          color: '#2c3e50',
          fontSize: 13
        },
        axisLine: {
          lineStyle: {
            color: '#e0e0e0'
          }
        },
        axisTick: {
          show: false
        },
        splitLine: {
          show: false
        }
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          color: '#2c3e50',
          fontSize: 13
        },
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0',
            type: 'dashed'
          }
        }
      },
      series: [
        {
          name: '申请数量',
          type: 'line',
          stack: 'Total',
          data: values.length > 0 ? values : [0],
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          itemStyle: {
            // 使用浅色透明渐变颜色
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(243, 156, 18, 0.3)' },
              { offset: 1, color: 'rgba(230, 126, 34, 0.3)' }
            ])
          },
          lineStyle: {
            width: 4,
            // 使用浅色透明渐变颜色
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: 'rgba(243, 156, 18, 0.3)' },
              { offset: 1, color: 'rgba(230, 126, 34, 0.3)' }
            ])
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: 'rgba(243, 156, 18, 0.2)'
              },
              {
                offset: 1,
                color: 'rgba(243, 156, 18, 0.05)'
              }
            ])
          },
          emphasis: {
            itemStyle: {
              borderColor: '#fff',
              borderWidth: 3,
              // 高亮渐变颜色
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(230, 126, 34, 0.4)' },
                { offset: 1, color: 'rgba(243, 156, 18, 0.4)' }
              ])
            },
            symbolSize: 12
          },
          label: {
            show: false
          },
          animationType: 'scale',
          animationEasing: 'elasticOut',
          animationDelay: function(idx) {
            return idx * 30;
          }
        }
      ]
    }
    
    console.log('设置奖项申请趋势图表选项:', option)
    chartInstance.setOption(option)
    console.log('奖项申请趋势图表绘制完成')
  } catch (error) {
    console.error('绘制奖项申请趋势图表失败:', error)
    console.error('错误详情:', error.response || error.message || error)
    
    // 确保DOM已经更新
    await nextTick()
    
    // 初始化图表并显示错误信息
    try {
      if (chartRef.value && !checkContainerSize()) {
        console.error('奖项申请趋势图表容器尺寸为0，无法显示错误信息')
        return
      }
      
      if (chartRef.value && !chartInstance) {
        chartInstance = echarts.init(chartRef.value)
      }
      
      if (chartInstance) {
        const errorOption = {
          title: {
            text: '奖项申请趋势',
            left: 'center'
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: ['暂无数据']
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '申请数量',
              type: 'line',
              stack: 'Total',
              data: [0],
              smooth: true,
              itemStyle: {
                color: '#fac858'
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: 'rgba(250, 200, 88, 0.8)'
                  },
                  {
                    offset: 1,
                    color: 'rgba(250, 200, 88, 0.2)'
                  }
                ])
              },
              label: {
                show: true,
                position: 'center',
                formatter: '数据加载失败',
                fontSize: 14
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
    console.log('调整奖项申请趋势图表尺寸...')
    chartInstance.resize()
    console.log('奖项申请趋势图表尺寸调整完成')
  }
}

// 页面加载时初始化
onMounted(() => {
  console.log('奖项申请趋势图表组件挂载，开始绘制图表...')
  drawChart()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理资源
onUnmounted(() => {
  console.log('奖项申请趋势图表组件卸载，清理资源...')
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
    console.log('奖项申请趋势图表实例已销毁')
  }
})
</script>

<style scoped>
.application-trend-line-chart {
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