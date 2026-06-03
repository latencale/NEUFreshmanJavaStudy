package com.neu.tms.view;

import java.util.Scanner;

public class AdminMainMenu {
    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========系统管理员主菜单==========");
            System.out.println("【用户管理】");
            System.out.println("1. 用户管理（增删改查）");
            System.out.println("【客户管理】");
            System.out.println("2. 客户管理（入住/查询/修改/删除/审批）");
            System.out.println("【床位管理】");
            System.out.println("3. 床位管理（示意图/管理/调换/添加）");
            System.out.println("【护理管理】");
            System.out.println("4. 护理管理（级别/项目/客户设置/记录）");
            System.out.println("【健康管家管理】");
            System.out.println("5. 健康管家管理（设置对象/查看/分配/移除/服务关注）");
            System.out.println("【系统】");
            System.out.println("0. 退出登录");
            System.out.println("====================================");
            System.out.print("请选择：");

            int result = sc.nextInt();
            switch (result) {
                case 1:
                    AdminUserMenu userMenu = new AdminUserMenu();
                    userMenu.execute();
                    break;
                case 2:
                    AdminCustomerMenu customerMenu = new AdminCustomerMenu();
                    customerMenu.execute();
                    break;
                case 3:
                    AdminBedMenu bedMenu = new AdminBedMenu();
                    bedMenu.execute();
                    break;
                case 4:
                    AdminNurseMenu nurseMenu = new AdminNurseMenu();
                    nurseMenu.execute();
                    break;
                case 5:
                    AdminHealthManagerMenu healthMenu = new AdminHealthManagerMenu();
                    healthMenu.execute();
                    break;
                case 0:
                    System.out.println("退出登录成功");
                    System.out.println("\n返回主菜单...\n");
                    MainMenu.main(new String[]{});
                    return;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }
}