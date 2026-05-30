package com.neu.tms.view;

import java.util.Scanner;

public class MainMenu {

    public static void main(String[] args) {
        System.out.println("=======欢迎登陆系统=======");
        System.out.println("1------管理员登陆");
        System.out.println("2------护工登陆");
        System.out.println("3------退出");
        //sout==System.out.println();

        while(true) {
            Scanner sc = new Scanner(System.in);
            //获取用户输入
            int result = sc.nextInt();//用result变量接收用户输入

            switch (result) {
                case 1:
                    System.out.println(("管理员登陆"));
                    AdminLoginMenu alm = new AdminLoginMenu();
                    alm.execute();
                    break;
                case 2:
                    System.out.println("护工登陆");
                    WorkerLoginMenu wlm = new WorkerLoginMenu();
                    wlm.execute();
                    break;
                case 3:
                    System.exit(1);
                default:
                    System.out.println("输入有误，请重新输入");

            }
        }
    }
}
