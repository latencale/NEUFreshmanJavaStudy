package com.neu.tms.view;

import com.neu.tms.pojo.TUser;
import com.neu.tms.service.TUserService;
import com.neu.tms.utils.SessionManager;

import java.util.Scanner;

public class AdminLoginMenu implements  IMenu{
    public void execute(){
        while(true){
        System.out.println("==================系统管理员登录===============");
        Scanner sc=new Scanner(System.in);
        System.out.println("请输入账号");
         String userName=sc.next();
        System.out.println("请输入密码");
        String password=sc.next();
        
        if(checkInitialAdmin(userName, password)){
            TUser initialUser = new TUser();
            initialUser.setUsername(userName);
            initialUser.setNickname(userName);
            initialUser.setRoleId(1);
            SessionManager.setCurrentUser(initialUser);
            AdminMainMenu amm=new AdminMainMenu();
            amm.execute();
            break;
        }else{
            TUserService service=new TUserService();
            TUser db_user=service.findByAdminByNameAndPassword(userName,password);
            if(db_user!=null){
                SessionManager.setCurrentUser(db_user);
                AdminMainMenu amm=new AdminMainMenu();
                amm.execute();
                break;
            }else{
                System.out.println("用户名或密码错误，请重新输入");
                continue;
            }
        }
    }

    }
    
    /**
     * 检查是否为初始管理员账号（admin、admin1、admin2）
     * @param userName 用户名
     * @param password 密码
     * @return true-是初始管理员且密码正确，false-不是或密码错误
     */
    private boolean checkInitialAdmin(String userName, String password) {
        if ("admin".equalsIgnoreCase(userName) && "admin".equals(password)) {
            return true;
        }
        if ("admin1".equalsIgnoreCase(userName) && "admin1".equals(password)) {
            return true;
        }
        if ("admin2".equalsIgnoreCase(userName) && "admin2".equals(password)) {
            return true;
        }
        return false;
    }
}
