package com.neu.tms.view;

import com.neu.tms.pojo.TUser;
import com.neu.tms.service.TUserService;

import java.util.Scanner;

public class AdminUpdateMenu implements IMenu {
    @Override
    public void execute() {
        Scanner sc = new Scanner(System.in);
        TUserService service = new TUserService();

        System.out.println("==================修改员工信息===============");
        
        System.out.print("请输入要修改的员工ID：");
        int id = sc.nextInt();

        TUser existingUser = service.findById(id);
        if (existingUser == null) {
            System.out.println("未找到该员工信息！");
            return;
        }

        System.out.println("当前员工信息：");
        System.out.println("姓名：" + (existingUser.getNickname() != null ? existingUser.getNickname() : "未设置"));
        System.out.println("账号：" + (existingUser.getUsername() != null ? existingUser.getUsername() : "未设置"));
        System.out.println("角色：" + (existingUser.getRoleId() != null ? (existingUser.getRoleId() == 1 ? "管理员" : "健康管家") : "未设置"));

        System.out.println("\n请输入新信息（直接回车保持不变）：");

        System.out.print("请输入真实姓名（当前：" + existingUser.getNickname() + "）：");
        String nicknameInput = sc.nextLine(); // 消耗上一行的换行符
        nicknameInput = sc.nextLine();
        String nickname = nicknameInput.isEmpty() ? existingUser.getNickname() : nicknameInput;

        System.out.print("请输入系统账号（当前：" + existingUser.getUsername() + "）：");
        String usernameInput = sc.nextLine();
        String username = usernameInput.isEmpty() ? existingUser.getUsername() : usernameInput;

        System.out.print("请输入密码（当前：" + existingUser.getPassword() + "）：");
        String passwordInput = sc.nextLine();
        String password = passwordInput.isEmpty() ? existingUser.getPassword() : passwordInput;

        System.out.print("请输入性别（0-女 1-男，当前：" + existingUser.getSex() + "）：");
        String sexInput = sc.nextLine();
        Integer sex = sexInput.isEmpty() ? existingUser.getSex() : Integer.parseInt(sexInput);

        System.out.print("请输入邮箱（当前：" + existingUser.getEmail() + "）：");
        String emailInput = sc.nextLine();
        String email = emailInput.isEmpty() ? existingUser.getEmail() : emailInput;

        System.out.print("请输入电话号码（当前：" + existingUser.getPhoneNumber() + "）：");
        String phoneInput = sc.nextLine();
        String phoneNumber = phoneInput.isEmpty() ? existingUser.getPhoneNumber() : phoneInput;

        System.out.print("请选择角色（1-管理员 2-健康管家，当前：" + existingUser.getRoleId() + "）：");
        String roleIdInput = sc.nextLine();
        Integer roleId = roleIdInput.isEmpty() ? existingUser.getRoleId() : Integer.parseInt(roleIdInput);

        // 构建更新后的用户对象
        TUser user = new TUser();
        user.setId(existingUser.getId());
        user.setNickname(nickname);
        user.setUsername(username);
        user.setPassword(password);
        user.setSex(sex);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setRoleId(roleId);
        user.setCreateTime(existingUser.getCreateTime());
        user.setCreateBy(existingUser.getCreateBy());
        user.setIsDeleted(existingUser.getIsDeleted());
        user.setUpdateTime(existingUser.getUpdateTime());
        user.setUpdateBy(existingUser.getUpdateBy());

        String result = service.updateUser(user);
        System.out.println(result);
    }
}
