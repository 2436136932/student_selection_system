package com.example.studentselectionsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentselectionsystem.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 教师信息数据访问接口
 */
@Mapper
public interface TeacherRepository extends BaseMapper<Teacher> {

    /**
     * 根据工号查找教师
     * @param teacherNumber 工号
     * @return 教师信息
     */
    @Select("SELECT * FROM teachers WHERE teacher_number = #{teacherNumber}")
    Optional<Teacher> selectByTeacherNumber(@Param("teacherNumber") String teacherNumber);

    /**
     * 根据用户ID查找教师
     * @param userId 用户ID
     * @return 教师信息
     */
    @Select("SELECT * FROM teachers WHERE user_id = #{userId}")
    Optional<Teacher> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据姓名查找教师
     * @param name 姓名
     * @return 教师列表
     */
    @Select("SELECT * FROM teachers WHERE name = #{name}")
    List<Teacher> selectByName(@Param("name") String name);

    /**
     * 根据姓名模糊查找教师
     * @param name 姓名
     * @return 教师列表
     */
    @Select("SELECT * FROM teachers WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<Teacher> selectByNameContaining(@Param("name") String name);

    /**
     * 根据部门查找教师
     * @param department 部门
     * @return 教师列表
     */
    @Select("SELECT * FROM teachers WHERE department = #{department}")
    List<Teacher> selectByDepartment(@Param("department") String department);

    /**
     * 判断工号是否存在
     * @param teacherNumber 工号
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM teachers WHERE teacher_number = #{teacherNumber}")
    boolean existsByTeacherNumber(@Param("teacherNumber") String teacherNumber);
}
