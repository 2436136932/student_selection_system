package com.example.studentselectionsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentselectionsystem.entity.Major;
import com.example.studentselectionsystem.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 学生信息数据访问接口
 */
@Mapper
public interface StudentRepository extends BaseMapper<Student> {

    /**
     * 根据学号查找学生
     * @param studentNumber 学号
     * @return 学生信息
     */
    @Select("SELECT * FROM students WHERE student_number = #{studentNumber}")
    Optional<Student> selectByStudentId(@Param("studentNumber") String studentNumber);

    /**
     * 根据姓名查找学生
     * @param name 姓名
     * @return 学生列表
     */
    @Select("SELECT * FROM students WHERE name = #{name}")
    List<Student> selectByName(@Param("name") String name);

    /**
     * 根据姓名模糊查找学生
     * @param name 姓名
     * @return 学生列表
     */
    @Select("SELECT * FROM students WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<Student> selectByNameContaining(@Param("name") String name);

    /**
     * 根据专业查找学生
     * @param major 专业
     * @return 学生列表
     */
    @Select("SELECT * FROM students WHERE major = #{major.name}")
    List<Student> selectByMajor(@Param("major") Major major);

    /**
     * 根据专业名称查找学生
     * @param majorName 专业名称
     * @return 学生列表
     */
    @Select("SELECT * FROM students WHERE major = #{majorName}")
    List<Student> selectByMajorName(@Param("majorName") String majorName);

    /**
     * 根据性别查找学生
     * @param gender 性别
     * @return 学生列表
     */
    @Select("SELECT * FROM students WHERE gender = #{gender}")
    List<Student> selectByGender(@Param("gender") String gender);

    /**
     * 判断学号是否存在
     * @param studentNumber 学号
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM students WHERE student_number = #{studentNumber}")
    boolean existsByStudentId(@Param("studentNumber") String studentNumber);

    /**
     * 获取学生及其专业信息
     * @param id 学生ID
     * @return 学生信息
     */
    @Select("SELECT s.*, m.department FROM students s LEFT JOIN majors m ON s.major = m.name WHERE s.id = #{id}")
    Optional<Student> selectByIdWithMajor(@Param("id") Long id);

    /**
     * 获取所有学生及其专业信息
     * @return 学生列表
     */
    @Select("SELECT s.*, m.department FROM students s LEFT JOIN majors m ON s.major = m.name")
    List<Student> selectAllWithMajor();

    /**
     * 根据用户ID查找学生
     * @param userId 用户ID
     * @return 学生信息
     */
    @Select("SELECT * FROM students WHERE user_id = #{userId}")
    Optional<Student> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 分页和搜索获取学生及其院系信息
     * @param studentNumber 学号
     * @param name 姓名
     * @param className 班级
     * @return 学生列表
     */
    @Select("SELECT s.*, m.department FROM students s LEFT JOIN majors m ON s.major = m.name WHERE (#{studentNumber} IS NULL OR s.student_number LIKE CONCAT('%', #{studentNumber}, '%')) AND (#{name} IS NULL OR s.name LIKE CONCAT('%', #{name}, '%')) AND (#{className} IS NULL OR s.class_name LIKE CONCAT('%', #{className}, '%'))")
    List<Student> selectStudentsWithDepartment(@Param("studentNumber") String studentNumber, @Param("name") String name, @Param("className") String className);

}