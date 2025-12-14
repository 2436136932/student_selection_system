package com.example.studentselectionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentselectionsystem.entity.Carousel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarouselMapper extends BaseMapper<Carousel> {
    @Select("SELECT * FROM carousel WHERE status = 1 ORDER BY sort_order ASC, id ASC")
    List<Carousel> findActiveCarousels();
}
