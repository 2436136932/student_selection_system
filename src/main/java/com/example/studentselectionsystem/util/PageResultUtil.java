package com.example.studentselectionsystem.util;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 分页结果工具类，用于统一转换分页结果格式
 */
public class PageResultUtil {

    /**
     * 将MyBatis-Plus的IPage对象转换为前端期望的格式
     * @param page MyBatis-Plus的分页结果
     * @param <T> 数据类型
     * @return 前端期望的分页结果格式
     */
    public static <T> PageResult<T> convertToPageResult(IPage<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 前端期望的分页结果格式
     * @param <T> 数据类型
     */
    public static class PageResult<T> {
        private final List<T> records;
        private final long total;

        public PageResult(List<T> records, long total) {
            this.records = records;
            this.total = total;
        }

        public List<T> getRecords() {
            return records;
        }

        public long getTotal() {
            return total;
        }
    }
}
