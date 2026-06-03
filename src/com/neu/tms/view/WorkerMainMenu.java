
package com.neu.tms.view;

import com.neu.tms.pojo.TUser;

import java.util.Scanner;

public class WorkerMainMenu implements IMenu {
    private TUser currentUser;

    public WorkerMainMenu(TUser user) {
        this.currentUser = user;
    }

    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========健康管家主菜单==========");
            System.out.println("当前用户：" + currentUser.getNickname());
            System.out.println("1. 日常护理");
            System.out.println("2. 服务对象护理记录");
            System.out.println("3. 外出申请");
            System.out.println("4. 退住申请");
            System.out.println("0. 退出登录");
            System.out.println("================================");
            System.out.print("请选择：");

            int result = sc.nextInt();
            switch (result) {
                case 1:
                    WorkerDailyCareMenu dailyCareMenu = new WorkerDailyCareMenu(currentUser);
                    dailyCareMenu.execute();
                    break;
                case 2:
                    WorkerNurseRecordMenu recordMenu = new WorkerNurseRecordMenu(currentUser);
                    recordMenu.execute();
                    break;
                case 3:
                    WorkerOutwardMenu outwardMenu = new WorkerOutwardMenu(currentUser);
                    outwardMenu.execute();
                    break;
                case 4:
                    WorkerBackDownMenu backDownMenu = new WorkerBackDownMenu(currentUser);
                    backDownMenu.execute();
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
