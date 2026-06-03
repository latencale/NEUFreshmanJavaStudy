package com.neu.tms.service;

import com.neu.tms.dao.CustomerDao;
import com.neu.tms.dao.TUserDao;
import com.neu.tms.pojo.Customer;
import com.neu.tms.pojo.TUser;

import java.io.IOException;
import java.util.List;

public class TUserService {
    //DAO层专注持久化
    TUserDao userDao=new TUserDao();
    CustomerDao customerDao = new CustomerDao();

    public TUserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(TUserDao userDao) {
        this.userDao = userDao;
    }

    /**
     *
     *
     * @param user 用户
     * @return 成功/失败的字符串
     */
    public String addUser(TUser user){
        System.out.println("传过来的要添加的用户"+user);
        System.out.println("稍候将存到文件中");
        //1.需要验证用户名、密码、用户类型是否是合法的（后续常用完成）
        //2.需要验证用户名是否重复（后续常用完成）
        //3.如何验证通过，调用 dao层进行存储
        String result= null;
        try {
            result = userDao.addUser(user);
        } catch (IOException e) {
            result="添加失败";

        }
        //返回存储结果
        return result;
    }

    public List<TUser> findAll() {
        List<TUser> list=userDao.findAll();
        return list;
    }

    public TUser findByUserName(String inputUserName) {
        return userDao.findByUserName(inputUserName);
    }

    public String updateUser(TUser user) {
        //@TODO 验证
        boolean db_result= userDao.updateUser(user);
        if(db_result){
            return "修改成功";
        }else{
            return "修改失败";
        }
    }

    public String delById(int id) {
        try {
            // 先查询该用户是否存在
            TUser user = userDao.findById(id);
            if (user == null) {
                return "用户不存在";
            }

            // 如果该用户是健康管家，需要解除其服务的所有客户关联
            if (user.getRoleId() != null && user.getRoleId() == 2) {
                List<Customer> allCustomers = customerDao.findAll();
                boolean hasUpdates = false;
                
                for (Customer customer : allCustomers) {
                    if (customer.getUserId() != null && customer.getUserId().equals(id)) {
                        // 解除客户与该健康管家的关联
                        customer.setUserId(null);
                        customerDao.updateCustomer(customer);
                        hasUpdates = true;
                    }
                }
                
                if (hasUpdates) {
                    System.out.println("已解除该健康管家服务的 " + 
                            allCustomers.stream()
                                .filter(c -> c.getUserId() == null || !c.getUserId().equals(id))
                                .count() + " 个客户关联");
                }
            }

            // 执行删除操作
            boolean db_result = userDao.deleteById(id);
            if (db_result) {
                return "删除成功";
            } else {
                return "删除失败";
            }
        } catch (Exception e) {
            return "删除失败：" + e.getMessage();
        }
    }

    public TUser findByAdminByNameAndPassword(String userName, String password) {
        return userDao.findByAdminByNameAndPassword(userName,password,1);
    }

    public TUser findByWorkerByNameAndPassword(String userName, String password) {
        return userDao.findByWorkerByNameAndPassword(userName, password);
    }

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象，未找到返回null
     */
    public TUser findById(int id) {
        return userDao.findById(id);
    }
}
