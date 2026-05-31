package com.neu.tms.view;

import com.neu.tms.dao.RoleDao;
import com.neu.tms.pojo.Role;
import com.neu.tms.pojo.TUser;
import com.neu.tms.service.TUserService;
import com.neu.tms.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class AdminUserMenu implements IMenu {
    @Override
    public void execute() {
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n==========用户管理==========");
            System.out.println("1. 添加员工");
            System.out.println("2. 查询所有员工");
            System.out.println("3. 条件查询员工");
            System.out.println("4. 修改员工");
            System.out.println("5. 删除员工");
            System.out.println("6. 基础数据维护");
            System.out.println("0. 返回上级菜单");
            System.out.println("============================");
            System.out.print("请选择：");
            
            try {
                int choice = sc.nextInt();
                
                switch (choice) {
                    case 1:
                        addEmployee(sc);
                        break;
                    case 2:
                        searchAllEmployees();
                        break;
                    case 3:
                        searchEmployeesByParams(sc);
                        break;
                    case 4:
                        updateEmployee(sc);
                        break;
                    case 5:
                        deleteEmployee(sc);
                        break;
                    case 6:
                        basicDataMaintenance(sc);
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("输入有误，请重新输入");
                }
            } catch (Exception e) {
                System.out.println("输入无效，请输入数字选项");
                sc.next();
            }
        }
    }

    /**
     * 基础数据维护
     */
    private void basicDataMaintenance(Scanner sc) {
        while (true) {
            System.out.println("\n==========基础数据维护==========");
            System.out.println("1. 查询系统用户列表");
            System.out.println("2. 查询角色列表");
            System.out.println("3. 配置用户角色");
            System.out.println("4. 维护用户基础信息");
            System.out.println("0. 返回上一级");
            System.out.println("===============================");
            System.out.print("请选择：");

            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        viewUserList();
                        break;
                    case 2:
                        viewRoleList();
                        break;
                    case 3:
                        configureUserRole(sc);
                        break;
                    case 4:
                        maintainUserInfo(sc);
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("输入有误，请重新输入");
                }
            } catch (Exception e) {
                System.out.println("输入无效，请输入数字选项");
                sc.next();
            }
        }
    }

    /**
     * 查询系统用户列表
     */
    private void viewUserList() {
        System.out.println("\n==========系统用户列表==========");
        TUserService us = new TUserService();
        List<TUser> list = us.findAll();

        if (list == null || list.isEmpty()) {
            System.out.println("暂无用户信息");
            return;
        }

        System.out.printf("%-4s %-10s %-15s %-6s %-15s %-12s %-10s\n", 
                "ID", "姓名", "账号", "性别", "联系电话", "角色", "创建人");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        for (TUser user : list) {
            String sex = (user.getSex() != null && user.getSex() == 1) ? "男" : "女";
            String roleName = getRoleName(user.getRoleId());
            String createBy = user.getCreateBy() != null ? user.getCreateBy() : "系统";
            
            System.out.printf("%-4d %-10s %-15s %-6s %-15s %-12s %-10s\n",
                    user.getId(),
                    user.getNickname() != null ? user.getNickname() : "未设置",
                    user.getUsername(),
                    sex,
                    user.getPhoneNumber() != null ? user.getPhoneNumber() : "未设置",
                    roleName,
                    createBy);
        }

        System.out.println("\n共查询到 " + list.size() + " 条记录");
    }

    /**
     * 查询角色列表
     */
    private void viewRoleList() {
        System.out.println("\n==========角色列表==========");
        RoleDao roleDao = new RoleDao();
        List<Role> roles = roleDao.findAll();

        if (roles.isEmpty()) {
            System.out.println("暂无角色信息");
            return;
        }

        System.out.printf("%-4s %-15s %-20s\n", "ID", "角色名称", "创建时间");
        System.out.println("----------------------------------------------");

        for (Role role : roles) {
            System.out.printf("%-4d %-15s %-20s\n",
                    role.getId(),
                    role.getName(),
                    formatDate(role.getCreateTime()));
        }

        System.out.println("\n共查询到 " + roles.size() + " 条记录");
    }

    /**
     * 配置用户角色
     */
    private void configureUserRole(Scanner sc) {
        System.out.println("\n==========配置用户角色==========");
        
        TUserService userService = new TUserService();
        List<TUser> users = userService.findAll();

        if (users.isEmpty()) {
            System.out.println("暂无用户数据");
            return;
        }

        System.out.println("\n=== 用户列表 ===");
        System.out.printf("%-4s %-10s %-15s %-12s\n", "ID", "姓名", "账号", "当前角色");
        System.out.println("--------------------------------------------------------------");

        for (TUser user : users) {
            String roleName = getRoleName(user.getRoleId());
            System.out.printf("%-4d %-10s %-15s %-12s\n",
                    user.getId(),
                    user.getNickname() != null ? user.getNickname() : "未设置",
                    user.getUsername(),
                    roleName);
        }

        System.out.print("\n请选择要配置的用户（输入ID）：");
        int userId = sc.nextInt();

        TUser selectedUser = userService.findById(userId);
        if (selectedUser == null) {
            System.out.println("未找到该用户");
            return;
        }

        RoleDao roleDao = new RoleDao();
        List<Role> roles = roleDao.findAll();

        if (roles.isEmpty()) {
            System.out.println("暂无可用角色");
            return;
        }

        System.out.println("\n=== 可选角色 ===");
        for (int i = 0; i < roles.size(); i++) {
            Role role = roles.get(i);
            String currentMark = (selectedUser.getRoleId() != null && selectedUser.getRoleId().equals(role.getId())) ? " [当前]" : "";
            System.out.println((i + 1) + ". " + role.getName() + currentMark);
        }

        System.out.print("\n请选择角色（输入序号）：");
        int roleIndex = sc.nextInt();

        if (roleIndex < 1 || roleIndex > roles.size()) {
            System.out.println("无效选择");
            return;
        }

        Role selectedRole = roles.get(roleIndex - 1);

        System.out.println("\n确认配置信息：");
        System.out.println("用户：" + selectedUser.getNickname());
        System.out.println("原角色：" + getRoleName(selectedUser.getRoleId()));
        System.out.println("新角色：" + selectedRole.getName());
        System.out.print("\n确认配置？（y/n）：");
        String confirm = sc.next();

        if ("y".equalsIgnoreCase(confirm)) {
            selectedUser.setRoleId(selectedRole.getId());
            selectedUser.setUpdateTime(new Date());
            selectedUser.setUpdateBy(SessionManager.getCurrentUserId());
            
            String result = userService.updateUser(selectedUser);
            System.out.println(result);
        } else {
            System.out.println("已取消配置");
        }
    }

    /**
     * 维护用户基础信息
     */
    private void maintainUserInfo(Scanner sc) {
        System.out.println("\n==========维护用户基础信息==========");
        
        TUserService userService = new TUserService();
        List<TUser> users = userService.findAll();

        if (users.isEmpty()) {
            System.out.println("暂无用户数据");
            return;
        }

        System.out.println("\n=== 用户列表 ===");
        System.out.printf("%-4s %-10s %-15s %-12s\n", "ID", "姓名", "账号", "角色");
        System.out.println("--------------------------------------------------------------");

        for (TUser user : users) {
            String roleName = getRoleName(user.getRoleId());
            System.out.printf("%-4d %-10s %-15s %-12s\n",
                    user.getId(),
                    user.getNickname() != null ? user.getNickname() : "未设置",
                    user.getUsername(),
                    roleName);
        }

        System.out.print("\n请选择要维护的用户（输入ID）：");
        int userId = sc.nextInt();

        TUser selectedUser = userService.findById(userId);
        if (selectedUser == null) {
            System.out.println("未找到该用户");
            return;
        }

        System.out.println("\n当前用户信息：");
        System.out.println("姓名：" + (selectedUser.getNickname() != null ? selectedUser.getNickname() : "未设置"));
        System.out.println("账号：" + selectedUser.getUsername());
        System.out.println("性别：" + (selectedUser.getSex() != null ? (selectedUser.getSex() == 1 ? "男" : "女") : "未设置"));
        System.out.println("邮箱：" + (selectedUser.getEmail() != null ? selectedUser.getEmail() : "未设置"));
        System.out.println("电话：" + (selectedUser.getPhoneNumber() != null ? selectedUser.getPhoneNumber() : "未设置"));
        System.out.println("角色：" + getRoleName(selectedUser.getRoleId()));

        System.out.println("\n请输入新信息（直接回车保持不变）：");

        sc.nextLine(); // 消耗换行符

        System.out.print("请输入真实姓名（当前：" + selectedUser.getNickname() + "）：");
        String nicknameInput = sc.nextLine();
        String nickname = nicknameInput.isEmpty() ? selectedUser.getNickname() : nicknameInput;

        System.out.print("请输入性别（0-女 1-男，当前：" + selectedUser.getSex() + "）：");
        String sexInput = sc.nextLine();
        Integer sex = sexInput.isEmpty() ? selectedUser.getSex() : Integer.parseInt(sexInput);

        System.out.print("请输入邮箱（当前：" + selectedUser.getEmail() + "，可为空）：");
        String emailInput = sc.nextLine();
        String email = emailInput.isEmpty() ? selectedUser.getEmail() : (emailInput.equals("null") ? null : emailInput);

        System.out.print("请输入电话号码（当前：" + selectedUser.getPhoneNumber() + "）：");
        String phoneInput = sc.nextLine();
        String phoneNumber = phoneInput.isEmpty() ? selectedUser.getPhoneNumber() : phoneInput;

        // 构建更新后的用户对象
        TUser updatedUser = new TUser();
        updatedUser.setId(selectedUser.getId());
        updatedUser.setNickname(nickname);
        updatedUser.setUsername(selectedUser.getUsername());
        updatedUser.setPassword(selectedUser.getPassword());
        updatedUser.setSex(sex);
        updatedUser.setEmail(email);
        updatedUser.setPhoneNumber(phoneNumber);
        updatedUser.setRoleId(selectedUser.getRoleId());
        updatedUser.setCreateTime(selectedUser.getCreateTime());
        updatedUser.setCreateBy(selectedUser.getCreateBy());
        updatedUser.setIsDeleted(selectedUser.getIsDeleted());
        updatedUser.setUpdateTime(new Date());
        updatedUser.setUpdateBy(SessionManager.getCurrentUserId());

        System.out.println("\n确认修改信息：");
        System.out.println("姓名：" + nickname);
        System.out.println("性别：" + (sex != null ? (sex == 1 ? "男" : "女") : "未设置"));
        System.out.println("邮箱：" + (email != null ? email : "未设置"));
        System.out.println("电话：" + phoneNumber);
        System.out.print("\n确认修改？（y/n）：");
        String confirm = sc.next();

        if ("y".equalsIgnoreCase(confirm)) {
            String result = userService.updateUser(updatedUser);
            System.out.println(result);
        } else {
            System.out.println("已取消修改");
        }
    }

    /**
     * 获取角色名称
     */
    private String getRoleName(Integer roleId) {
        if (roleId == null) {
            return "未设置";
        }
        RoleDao roleDao = new RoleDao();
        Role role = roleDao.findById(roleId);
        return role != null ? role.getName() : "未知";
    }

    /**
     * 格式化日期
     */
    private String formatDate(Date date) {
        if (date == null) {
            return "无";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 添加员工
     */
    private void addEmployee(Scanner sc) {
        System.out.println("\n==================添加系统用户===============");

        System.out.print("请输入真实姓名：");
        String nickname = sc.next();

        System.out.print("请输入系统账号：");
        String userName = sc.next();

        System.out.print("请输入电话号码：");
        String phoneNumber = sc.next();

        System.out.print("请输入密码（直接回车使用手机号后6位）：");
        sc.nextLine();
        String password = sc.nextLine();
        
        String finalPassword;
        if (password.isEmpty()) {
            if (phoneNumber.length() >= 6) {
                finalPassword = phoneNumber.substring(phoneNumber.length() - 6);
                System.out.println("使用默认密码（手机号后6位）：" + finalPassword);
            } else {
                System.out.println("手机号长度不足，请手动输入密码：");
                finalPassword = sc.nextLine();
            }
        } else {
            finalPassword = password;
        }

        System.out.print("请输入性别（0-女 1-男）：");
        Integer sex = sc.nextInt();

        System.out.print("请输入邮箱（可为空，直接回车跳过）：");
        String email = sc.next();

        RoleDao roleDao = new RoleDao();
        List<Role> roles = roleDao.findAll();
        
        if (roles.isEmpty()) {
            System.out.println("暂无可用角色，请先创建角色");
            return;
        }

        System.out.println("\n=== 可选角色 ===");
        for (int i = 0; i < roles.size(); i++) {
            Role role = roles.get(i);
            System.out.println((i + 1) + ". " + role.getName());
        }
        
        System.out.print("\n请选择角色（输入序号）：");
        int roleIndex = sc.nextInt();
        
        if (roleIndex < 1 || roleIndex > roles.size()) {
            System.out.println("无效选择");
            return;
        }
        
        Role selectedRole = roles.get(roleIndex - 1);

        TUser user = new TUser();
        user.setNickname(nickname);
        user.setUsername(userName);
        user.setPassword(finalPassword);
        user.setSex(sex);
        user.setEmail(email.isEmpty() ? null : email);
        user.setPhoneNumber(phoneNumber);
        user.setRoleId(selectedRole.getId());
        user.setCreateTime(new Date());
        user.setCreateBy(SessionManager.getCurrentUserName());
        user.setIsDeleted(0);

        TUserService us = new TUserService();
        String result = us.addUser(user);
        System.out.println(result);
    }

    /**
     * 查询所有员工
     */
    private void searchAllEmployees() {
        System.out.println("\n==================查询所有员工===============");
        TUserService us = new TUserService();
        List<TUser> list = us.findAll();

        if (list == null || list.isEmpty()) {
            System.out.println("暂无员工信息");
            return;
        }

        System.out.printf("%-4s %-10s %-15s %-6s %-15s %-12s\n", 
                "ID", "姓名", "账号", "性别", "联系电话", "角色");
        System.out.println("------------------------------------------------------------------------------------");

        for (TUser user : list) {
            String sex = (user.getSex() != null && user.getSex() == 1) ? "男" : "女";
            String roleName = getRoleName(user.getRoleId());
            
            System.out.printf("%-4d %-10s %-15s %-6s %-15s %-12s\n",
                    user.getId(),
                    user.getNickname() != null ? user.getNickname() : "未设置",
                    user.getUsername(),
                    sex,
                    user.getPhoneNumber() != null ? user.getPhoneNumber() : "未设置",
                    roleName);
        }

        System.out.println("\n共查询到 " + list.size() + " 条记录");
    }

    /**
     * 条件查询员工
     */
    private void searchEmployeesByParams(Scanner input) {
        System.out.println("\n==================条件查询用户===============");
        TUserService service = new TUserService();

        System.out.print("请输入用户名（直接回车跳过）：");
        input.nextLine();
        String userName = input.nextLine();

        RoleDao roleDao = new RoleDao();
        List<Role> roles = roleDao.findAll();
        
        System.out.println("\n=== 可选角色 ===");
        for (Role role : roles) {
            System.out.println(role.getId() + " - " + role.getName());
        }
        
        System.out.print("请输入角色ID（直接回车跳过）：");
        String roleIdStr = input.nextLine();
        Integer roleId = roleIdStr.isEmpty() ? null : Integer.parseInt(roleIdStr);

        List<TUser> resultList = service.findAll();

        if (!userName.isEmpty()) {
            resultList = resultList.stream()
                    .filter(u -> u.getUsername() != null && u.getUsername().contains(userName))
                    .collect(Collectors.toList());
        }

        if (roleId != null) {
            resultList = resultList.stream()
                    .filter(u -> u.getRoleId() != null && u.getRoleId().equals(roleId))
                    .collect(Collectors.toList());
        }

        if (resultList.isEmpty()) {
            System.out.println("暂无结果");
        } else {
            System.out.println("\n=== 查询结果 ===");
            System.out.printf("%-4s %-10s %-15s %-6s %-15s %-12s\n", 
                    "ID", "姓名", "账号", "性别", "联系电话", "角色");
            System.out.println("------------------------------------------------------------------------------------");
            
            for (TUser user : resultList) {
                String sex = (user.getSex() != null && user.getSex() == 1) ? "男" : "女";
                String roleName = getRoleName(user.getRoleId());
                
                System.out.printf("%-4d %-10s %-15s %-6s %-15s %-12s\n",
                        user.getId(),
                        user.getNickname() != null ? user.getNickname() : "未设置",
                        user.getUsername(),
                        sex,
                        user.getPhoneNumber() != null ? user.getPhoneNumber() : "未设置",
                        roleName);
            }
            
            System.out.println("\n共查询到 " + resultList.size() + " 条记录");
        }
    }

    /**
     * 修改员工
     */
    private void updateEmployee(Scanner sc) {
        System.out.println("\n==================修改员工信息===============");
        TUserService service = new TUserService();

        System.out.print("请输入要修改的员工ID：");
        int id = sc.nextInt();

        TUser existingUser = service.findById(id);
        if (existingUser == null) {
            System.out.println("未找到该员工信息！");
            return;
        }

        System.out.println("\n当前员工信息：");
        System.out.println("姓名：" + (existingUser.getNickname() != null ? existingUser.getNickname() : "未设置"));
        System.out.println("账号：" + (existingUser.getUsername() != null ? existingUser.getUsername() : "未设置"));
        System.out.println("性别：" + (existingUser.getSex() != null ? (existingUser.getSex() == 1 ? "男" : "女") : "未设置"));
        System.out.println("邮箱：" + (existingUser.getEmail() != null ? existingUser.getEmail() : "未设置"));
        System.out.println("电话：" + (existingUser.getPhoneNumber() != null ? existingUser.getPhoneNumber() : "未设置"));
        System.out.println("角色：" + getRoleName(existingUser.getRoleId()));

        System.out.println("\n请输入新信息（直接回车保持不变）：");

        sc.nextLine();

        System.out.print("请输入真实姓名（当前：" + existingUser.getNickname() + "）：");
        String nicknameInput = sc.nextLine();
        String nickname = nicknameInput.isEmpty() ? existingUser.getNickname() : nicknameInput;

        System.out.print("请输入系统账号（当前：" + existingUser.getUsername() + "）：");
        String usernameInput = sc.nextLine();
        String username = usernameInput.isEmpty() ? existingUser.getUsername() : usernameInput;

        System.out.print("请输入密码（当前：******，直接回车保持不变）：");
        String passwordInput = sc.nextLine();
        String password = passwordInput.isEmpty() ? existingUser.getPassword() : passwordInput;

        System.out.print("请输入性别（0-女 1-男，当前：" + existingUser.getSex() + "）：");
        String sexInput = sc.nextLine();
        Integer sex = sexInput.isEmpty() ? existingUser.getSex() : Integer.parseInt(sexInput);

        System.out.print("请输入邮箱（当前：" + existingUser.getEmail() + "，可为空）：");
        String emailInput = sc.nextLine();
        String email = emailInput.isEmpty() ? existingUser.getEmail() : (emailInput.equals("null") ? null : emailInput);

        System.out.print("请输入电话号码（当前：" + existingUser.getPhoneNumber() + "）：");
        String phoneInput = sc.nextLine();
        String phoneNumber = phoneInput.isEmpty() ? existingUser.getPhoneNumber() : phoneInput;

        RoleDao roleDao = new RoleDao();
        List<Role> roles = roleDao.findAll();
        
        System.out.println("\n=== 可选角色 ===");
        for (int i = 0; i < roles.size(); i++) {
            Role role = roles.get(i);
            String currentMark = (existingUser.getRoleId() != null && existingUser.getRoleId().equals(role.getId())) ? " [当前]" : "";
            System.out.println((i + 1) + ". " + role.getName() + currentMark);
        }
        
        System.out.print("\n请选择角色（输入序号，直接回车保持不变）：");
        String roleIdInput = sc.nextLine();
        Integer roleId = roleIdInput.isEmpty() ? existingUser.getRoleId() : null;
        
        if (roleId == null) {
            int roleIndex = Integer.parseInt(roleIdInput);
            if (roleIndex < 1 || roleIndex > roles.size()) {
                System.out.println("无效选择，保持原角色");
                roleId = existingUser.getRoleId();
            } else {
                roleId = roles.get(roleIndex - 1).getId();
            }
        }

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
        user.setUpdateTime(new Date());
        user.setUpdateBy(SessionManager.getCurrentUserId());

        System.out.println("\n确认修改信息：");
        System.out.println("姓名：" + nickname);
        System.out.println("账号：" + username);
        System.out.println("性别：" + (sex != null ? (sex == 1 ? "男" : "女") : "未设置"));
        System.out.println("邮箱：" + (email != null ? email : "未设置"));
        System.out.println("电话：" + phoneNumber);
        System.out.println("角色：" + getRoleName(roleId));
        System.out.print("\n确认修改？（y/n）：");
        String confirm = sc.next();

        if ("y".equalsIgnoreCase(confirm)) {
            String result = service.updateUser(user);
            System.out.println(result);
        } else {
            System.out.println("已取消修改");
        }
    }

    /**
     * 删除员工
     */
    private void deleteEmployee(Scanner sc) {
        System.out.println("\n==================删除系统用户===============");
        System.out.print("请输入要删除的用户ID：");
        int id = sc.nextInt();

        TUserService service = new TUserService();
        TUser user = service.findById(id);

        if (user == null) {
            System.out.println("未找到该用户！");
            return;
        }

        System.out.println("\n当前用户信息：");
        System.out.println("姓名：" + (user.getNickname() != null ? user.getNickname() : "未设置"));
        System.out.println("账号：" + user.getUsername());
        System.out.println("角色：" + getRoleName(user.getRoleId()));

        System.out.print("\n确认要删除该用户吗？此操作不可恢复（y/n）：");
        String confirm = sc.next();

        if ("y".equalsIgnoreCase(confirm)) {
            String result = service.delById(id);
            System.out.println(result);
        } else {
            System.out.println("已取消删除操作");
        }
    }
}
