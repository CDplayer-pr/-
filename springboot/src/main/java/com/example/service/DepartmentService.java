package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.LevelEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Department;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.*;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 社团信息表业务处理
 **/
@Service
public class DepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ApplyMapper applyMapper;
    @Resource
    private InformationMapper informationMapper;

    /**
     * 新增
     */
    public void add(Department department) {
        if (ObjectUtil.isNotEmpty(department.getUserId())) {
            Department dbDepartment = departmentMapper.selectByUserId(department.getUserId());
            if (ObjectUtil.isNotEmpty(dbDepartment)) {
                throw new CustomException(ResultCodeEnum.HEADER_ALREADY_ERROR);
            }
        }
        department.setTime(DateUtil.format(new Date(), "yyyy-MM-dd"));
        departmentMapper.insert(department);
//        Department info = departmentMapper.selectByUserId(department.getUserId());
//        // 往学生信息表里该学生的社团信息同步一条数据
//        User user = userMapper.selectById(department.getUserId());
//        user.setDepartmentId(info.getId());
//        userMapper.updateById(user);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        activityMapper.deleteBydepartmentId(id);
        applyMapper.deleteBydepartmentId(id);
        informationMapper.deleteBydepartmentId(id);
        departmentMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            departmentMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Department department) {
        if (ObjectUtil.isNotEmpty(department.getUserId())) {
            Department dbDepartment = departmentMapper.selectByUserId(department.getUserId());
            if (ObjectUtil.isNotEmpty(dbDepartment)) {
                if (!dbDepartment.getName().equals(department.getName())) {
                    throw new CustomException(ResultCodeEnum.HEADER_ALREADY_ERROR);
                }
            }
        }
        department.setTime(DateUtil.format(new Date(), "yyyy-MM-dd"));
        departmentMapper.updateById(department);
//        // 往学生信息表里该学生的社团信息同步一条数据
//        User user = userMapper.selectById(department.getUserId());
//        user.setDepartmentId(department.getId());
//        userMapper.updateById(user);
    }

    /**
     * 根据ID查询
     */
    public Department selectById(Integer id) {
        return departmentMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Department> selectAll(Department department) {
        return departmentMapper.selectAll(department);
    }

    /**
     * 分页查询
     */
    public PageInfo<Department> selectPage(Department department, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        System.out.printf(currentUser.getRole());
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            User user = userMapper.selectById(currentUser.getId());
            if (LevelEnum.HEADER.level.equals(user.getLevel())) {
                department.setUserId(user.getId());
            }
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Department> list = departmentMapper.selectAll(department);
        return PageInfo.of(list);
    }
}