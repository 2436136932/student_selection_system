package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.Carousel;
import com.example.studentselectionsystem.mapper.CarouselMapper;
import com.example.studentselectionsystem.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图Service实现类
 */
@Service
public class CarouselServiceImpl implements CarouselService {
    
    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public boolean addCarousel(Carousel carousel) {
        return carouselMapper.insert(carousel) > 0;
    }

    @Override
    public boolean updateCarousel(Carousel carousel) {
        return carouselMapper.updateById(carousel) > 0;
    }

    @Override
    public boolean deleteCarousel(Long id) {
        return carouselMapper.deleteById(id) > 0;
    }

    @Override
    public Carousel getCarouselById(Long id) {
        return carouselMapper.selectById(id);
    }

    @Override
    public List<Carousel> getAllCarousels() {
        QueryWrapper<Carousel> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort_order").orderByAsc("id");
        return carouselMapper.selectList(queryWrapper);
    }

    @Override
    public List<Carousel> getActiveCarousels() {
        return carouselMapper.findActiveCarousels();
    }

    @Override
    public IPage<Carousel> getCarouselsPage(int page, int pageSize) {
        QueryWrapper<Carousel> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort_order").orderByAsc("id");
        IPage<Carousel> carouselPage = new Page<>(page, pageSize);
        return carouselMapper.selectPage(carouselPage, queryWrapper);
    }
}
