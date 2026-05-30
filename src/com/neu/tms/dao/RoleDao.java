package com.neu.tms.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.tms.pojo.Role;
import com.neu.tms.utils.PersistentIdGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RoleDao {
    public static final File FILE_NAME = new File("data\\roles.json");
    private final ObjectMapper om = new ObjectMapper();

    /**
     * 初始化默认角色数据
     */
    public void initDefaultRoles() {
        if (!FILE_NAME.exists()) {
            try {
                List<Role> defaultRoles = new ArrayList<>();
                
                Role adminRole = new Role();
                adminRole.setId(PersistentIdGenerator.getInstance().nextId());
                adminRole.setName("管理员");
                adminRole.setCreateTime(new Date());
                adminRole.setIsDeleted(0);
                defaultRoles.add(adminRole);

                Role workerRole = new Role();
                workerRole.setId(PersistentIdGenerator.getInstance().nextId());
                workerRole.setName("健康管家");
                workerRole.setCreateTime(new Date());
                workerRole.setIsDeleted(0);
                defaultRoles.add(workerRole);

                om.writeValue(FILE_NAME, defaultRoles);
            } catch (IOException e) {
                System.out.println("初始化角色数据失败：" + e.getMessage());
            }
        }
    }

    /**
     * 查询所有未删除的角色
     */
    public List<Role> findAll() {
        initDefaultRoles();
        
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            List<Role> allRoles = om.readValue(FILE_NAME, new TypeReference<List<Role>>() {});
            return allRoles.stream()
                    .filter(r -> r.getIsDeleted() == 0 || r.getIsDeleted() == null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据ID查询角色
     */
    public Role findById(Integer id) {
        List<Role> allRoles = findAll();
        return allRoles.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 添加角色
     */
    public String addRole(Role role) throws IOException {
        try {
            List<Role> list = findAll();
            role.setId(PersistentIdGenerator.getInstance().nextId());
            role.setIsDeleted(0);
            role.setCreateTime(new Date());
            list.add(role);
            om.writeValue(FILE_NAME, list);
            return "添加成功";
        } catch (Exception e) {
            throw new RuntimeException("添加角色失败", e);
        }
    }

    /**
     * 更新角色
     */
    public boolean updateRole(Role role) {
        try {
            List<Role> list = findAll();
            boolean isUpdate = false;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(role.getId())) {
                    list.set(i, role);
                    isUpdate = true;
                    break;
                }
            }

            if (isUpdate) {
                om.writeValue(FILE_NAME, list);
            }
            return isUpdate;
        } catch (IOException e) {
            throw new RuntimeException("修改角色异常", e);
        }
    }

    /**
     * 逻辑删除角色
     */
    public boolean deleteById(Integer id) {
        try {
            List<Role> list = findAll();
            boolean isDelete = false;

            for (Role role : list) {
                if (role.getId().equals(id)) {
                    role.setIsDeleted(1);
                    isDelete = true;
                    break;
                }
            }

            if (isDelete) {
                om.writeValue(FILE_NAME, list);
            }
            return isDelete;
        } catch (IOException e) {
            throw new RuntimeException("删除角色失败", e);
        }
    }
}
