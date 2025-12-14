package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Carousel;
import java.util.List;

/**
 * 轮播图Service接口
 */
public interface CarouselService {
    /**
     * 新增轮播图
     * @param carousel 轮播图信息
     * @return 是否成功
     */
    boolean addCarousel(Carousel carousel);
    
    /**
     * 更新轮播图信息
     * @param carousel 轮播图信息
     * @return 是否成功
     */
    boolean updateCarousel(Carousel carousel);
    
    /**
     * 删除轮播图
     * @param id 轮播图ID
     * @return 是否成功
     */
    boolean deleteCarousel(Long id);
    
    /**
     * 根据ID获取轮播图信息
     * @param id 轮播图ID
     * @return 轮播图信息
     */
    Carousel getCarouselById(Long id);
    
    /**
     * 获取所有轮播图列表
     * @return 轮播图列表
     */
    List<Carousel> getAllCarousels();
    
    /**
     * 获取启用的轮播图列表（用于首页展示）
     * @return 启用的轮播图列表
     */
    List<Carousel> getActiveCarousels();
    
    /**
     * 分页查询轮播图
     * @param page 当前页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<Carousel> getCarouselsPage(int page, int pageSize);
}
