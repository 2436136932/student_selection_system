package com.example.studentselectionsystem.controller;

import com.example.studentselectionsystem.entity.Carousel;
import com.example.studentselectionsystem.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * 轮播图Controller
 */
@RestController
@RequestMapping("/api/carousels")
public class CarouselController {
    
    @Autowired
    private CarouselService carouselService;

    /**
     * 获取所有轮播图（管理员用）
     * @return 轮播图列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Carousel>> getAllCarousels() {
        List<Carousel> carousels = carouselService.getAllCarousels();
        return ResponseEntity.ok(carousels);
    }

    /**
     * 获取启用的轮播图（首页用）
     * @return 启用的轮播图列表
     */
    @GetMapping("/active")
    public ResponseEntity<List<Carousel>> getActiveCarousels() {
        List<Carousel> carousels = carouselService.getActiveCarousels();
        return ResponseEntity.ok(carousels);
    }

    /**
     * 根据ID获取轮播图信息
     * @param id 轮播图ID
     * @return 轮播图信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Carousel> getCarouselById(@PathVariable Long id) {
        Carousel carousel = carouselService.getCarouselById(id);
        if (carousel != null) {
            return ResponseEntity.ok(carousel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 新增轮播图
     * @param carousel 轮播图信息
     * @return 响应结果
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addCarousel(@RequestBody Carousel carousel) {
        boolean success = carouselService.addCarousel(carousel);
        if (success) {
            return ResponseEntity.ok("新增轮播图成功");
        } else {
            return ResponseEntity.badRequest().body("新增轮播图失败");
        }
    }

    /**
     * 更新轮播图信息
     * @param id 轮播图ID
     * @param carousel 轮播图信息
     * @return 响应结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateCarousel(@PathVariable Long id, @RequestBody Carousel carousel) {
        // 确保路径参数和请求体中的ID一致
        carousel.setId(id);
        boolean success = carouselService.updateCarousel(carousel);
        if (success) {
            return ResponseEntity.ok("更新轮播图成功");
        } else {
            return ResponseEntity.badRequest().body("更新轮播图失败");
        }
    }

    /**
     * 删除轮播图
     * @param id 轮播图ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCarousel(@PathVariable Long id) {
        boolean success = carouselService.deleteCarousel(id);
        if (success) {
            return ResponseEntity.ok("删除轮播图成功");
        } else {
            return ResponseEntity.badRequest().body("删除轮播图失败");
        }
    }

    /**
     * 上传轮播图图片
     * @param file 上传的图片文件
     * @return 上传后的图片URL
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("请选择要上传的图片");
        }

        try {
            // 设置图片存储路径 - 使用配置文件中的路径
            String uploadDir = System.getProperty("file.storage.dir", "/www/wwwroot/student-selection-system/uploads/");
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一的文件名
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            Path filePath = Paths.get(uploadDir + uniqueFileName);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 返回图片URL
            String imageUrl = "/uploads/" + uniqueFileName;
            return ResponseEntity.ok(imageUrl);
        } catch (IOException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("图片上传失败");
        }
    }
}
