package com.neu.tms.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neu.tms.pojo.TUser;
import com.neu.tms.utils.PersistentIdGenerator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.*;
import com.neu.tms.pojo.Role;


public class TUserDao {
    // 数据文件路径,做成常量。
    public static final File FILE_NAME = new File("data\\users.json");
    //实例化好一个json转换工具
    private final ObjectMapper om = new ObjectMapper();

    public TUserDao() {
        om.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * 添加用户（带自增ID，不会覆盖文件，支持多个用户）
     */
    public String addUser(TUser user) throws IOException {
        try {
            // 1. 先读取文件中已有的所有用户
            List<TUser> userList = findAll();
            // 2. 设置自增ID（重启不丢失）
            user.setId(PersistentIdGenerator.getInstance().nextId());
            // 4. 添加新用户到集合
            userList.add(user);
            // 5. 把整个集合重新写入文件（覆盖，因为是全量更新）
            om.writeValue(FILE_NAME, userList);
            return "添加成功";
        } catch (Exception e) {
            throw new RuntimeException("添加用户失败", e);
        }
    }

    /**
     * 查询所有用户（已补全 ）
     */
    public List<TUser> findAll() {
        // 文件不存在 → 返回空集合
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            // 读取文件 JSON 转 List<TUser>
            return om.readValue(FILE_NAME, new TypeReference<List<TUser>>() {});
        } catch (IOException e) {
            // 文件为空/格式错误 → 返回空集合
            return new ArrayList<>();
        }
    }
    /**
     * 根据用户名查询用户（精确匹配）
     */
    public TUser findByUserName(String userName) {
        List<TUser> userList = findAll();

        // 遍历所有用户，找到用户名相同的
        for (TUser user : userList) {
            if (user.getUsername().equals(userName)) {
                return user;
            }
        }

        // 没找到返回 null
        return null;
    }
    /**
     * 普通循环方式修改用户，不使用stream
     * @param newUser 待修改的用户对象，携带id标识
     * @return true修改成功，false用户不存在
     */
    public boolean updateUser(TUser newUser) {
        try {
            List<TUser> userList = findAll();
            boolean isUpdate = false;

            // 普通for循环遍历查找
            for (int i = 0; i < userList.size(); i++) {//循环下标
                TUser user = userList.get(i);//获取下标为i的原对象{}
                if (user.getId().equals(newUser.getId())) {
                    userList.set(i, newUser);//替换成新对象，关键代码
                    isUpdate = true;//标记有需要修改的
                    break;
                }
            }

            // 找到用户则写入文件保存修改
            if (isUpdate) {
                om.writeValue(FILE_NAME, userList);//关键代码，整体替换
            }
            return isUpdate;
        } catch (IOException e) {
            throw new RuntimeException("修改用户异常", e);
        }
    }

    /**
     * 根据ID删除用户
     * @param id 要删除的用户ID
     * @return true删除成功 false用户不存在
     */
    public boolean deleteById(int id) {
        try {
            List<TUser> userList = findAll();//全量读出
            boolean isDelete = false;

            // 普通for循环查找并删除
            for (int i = 0; i < userList.size(); i++) {
                TUser user = userList.get(i);//通过索引获取对象
                if (user.getId() == id) {
                    userList.remove(i);//移除指定id的对象
                    isDelete = true;
                    break;
                }
            }

            // 如果删除成功，重新写入文件
            if (isDelete) {
                om.writeValue(FILE_NAME, userList);//全量写入文件
            }
            return isDelete;
        } catch (IOException e) {
            throw new RuntimeException("删除用户失败", e);
        }
    }
    /**
     * 根据用户名查询用户（Stream 流式写法）
     */
    public TUser findByUserNameStream(String userName) {
        return findAll().stream()
                .filter(user -> user.getUsername().equals(userName))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 找到的用户对象，未找到返回null
     */
    public TUser findById(int id) {
        List<TUser> userList = findAll();
        
        for (TUser user : userList) {
            if (user.getId() != null && user.getId() == id) {
                return user;
            }
        }
        
        return null;
    }

    public TUser findByAdminByNameAndPassword(String userName, String password,int userType) {
        List<TUser> userList = findAll();

        for (TUser db_user : userList) {
            if (db_user.getUsername().equals(userName) && 
                db_user.getPassword().equals(password) && 
                db_user.getRoleId() != null && 
                db_user.getRoleId() == userType) {
                return db_user;
            }
        }

        return null;
    }

    /**
     * 根据用户名和密码查询健康管家（支持动态角色ID）
     */
    public TUser findByWorkerByNameAndPassword(String userName, String password) {
        List<TUser> userList = findAll();
        
        // 先查询健康管家的角色ID列表
        RoleDao roleDao = new RoleDao();
        List<Role> healthManagerRoles = roleDao.findAll().stream()
                .filter(r -> "健康管家".equals(r.getName()))
                .toList();
        
        if (healthManagerRoles.isEmpty()) {
            return null;
        }
        
        List<Integer> healthManagerRoleIds = healthManagerRoles.stream()
                .map(Role::getId)
                .toList();

        for (TUser db_user : userList) {
            if (db_user.getUsername() != null && db_user.getUsername().equals(userName) &&
                db_user.getPassword() != null && db_user.getPassword().equals(password) &&
                db_user.getRoleId() != null && 
                healthManagerRoleIds.contains(db_user.getRoleId())) {
                return db_user;
            }
        }

        return null;
    }
}