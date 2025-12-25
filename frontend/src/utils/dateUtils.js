/**
 * 日期时间工具类
 */

/**
 * 格式化日期时间
 * @param {Date|string|number} date - 日期对象、字符串或时间戳
 * @param {string} format - 格式化字符串，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return '';

  // 转换为Date对象
  const d = new Date(date);
  
  // 检查是否为有效日期
  if (isNaN(d.getTime())) return '';

  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');

  // 替换格式化字符串
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}

/**
 * 格式化日期（仅日期部分）
 * @param {Date|string|number} date - 日期对象、字符串或时间戳
 * @returns {string} 格式化后的日期字符串，格式为 'YYYY-MM-DD'
 */
export function formatDateOnly(date) {
  return formatDate(date, 'YYYY-MM-DD');
}

/**
 * 格式化时间（仅时间部分）
 * @param {Date|string|number} date - 日期对象、字符串或时间戳
 * @returns {string} 格式化后的时间字符串，格式为 'HH:mm:ss'
 */
export function formatTimeOnly(date) {
  return formatDate(date, 'HH:mm:ss');
}

/**
 * 计算两个日期之间的天数差
 * @param {Date|string|number} startDate - 开始日期
 * @param {Date|string|number} endDate - 结束日期
 * @returns {number} 天数差
 */
export function getDaysDiff(startDate, endDate) {
  const start = new Date(startDate);
  const end = new Date(endDate);
  const diffTime = Math.abs(end - start);
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
}

/**
 * 判断日期是否在指定范围内
 * @param {Date|string|number} date - 要判断的日期
 * @param {Date|string|number} startDate - 开始日期
 * @param {Date|string|number} endDate - 结束日期
 * @returns {boolean} 是否在范围内
 */
export function isDateInRange(date, startDate, endDate) {
  const d = new Date(date);
  const start = new Date(startDate);
  const end = new Date(endDate);
  return d >= start && d <= end;
}
