
package com.neu.tms.view;

import com.neu.tms.pojo.TUser;
import com.neu.tms.service.TUserService;

import java.util.Scanner;

public class WorkerLoginMenu implements IMenu {
    public void execute() {
        while (true) {
            System.out.println("==================健康管家登录===============");
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入账号");
            String userName = sc.next();
            System.out.println("请输入密码");
            String password = sc.next();

            TUserService service = new TUserService();
            TUser db_user = service.findByWorkerByNameAndPassword(userName, password);
            
            if (db_user != null) {
                System.out.println("登录成功！欢迎，" + db_user.getNickname());
                WorkerMainMenu wmm = new WorkerMainMenu(db_user);
                wmm.execute();
                break;
            } else {
                System.out.println("用户名或密码错误，请重新输入");
                System.out.println("按1返回主菜单，其他键继续输入");
                String choice = sc.next();
                if ("1".equals(choice)) {
                    break;
                }
            }
        }
    }
}
