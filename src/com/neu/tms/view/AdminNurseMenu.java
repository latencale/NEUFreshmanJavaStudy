package com.neu.tms.view;

import com.neu.tms.dao.*;
import com.neu.tms.pojo.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class AdminNurseMenu implements IMenu {
    private NurseLevelDao nurseLevelDao = new NurseLevelDao();
    private NurseContentDao nurseContentDao = new NurseContentDao();
    private CustomerDao customerDao = new CustomerDao();
    private CustomerNurseItemDao customerNurseItemDao = new CustomerNurseItemDao();
    private NurseRecordDao nurseRecordDao = new NurseRecordDao();
    private NurseLevelItemDao nurseLevelItemDao = new NurseLevelItemDao();
    private TUserDao userDao = new TUserDao();

    @Override
    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========护理管理==========");
            System.out.println("1. 护理级别管理");
            System.out.println("2. 护理项目管理");
            System.out.println("3. 客户护理设置");
            System.out.println("4. 设置服务对象");
            System.out.println("0. 返回上一级");
            System.out.println("===========================");
            System.out.print("请选择：");

            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        manageNurseLevel(sc);
                        break;
                    case 2:
                        manageNurseContent(sc);
                        break;
                    case 3:
                        setCustomerNurse(sc);
                        break;
                    case 4:
                        assignServiceObjects(sc);
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
     * 设置服务对象（健康管家分配客户）
     */
    private void assignServiceObjects(Scanner sc) {
        while (true) {
            System.out.println("\n==========设置服务对象==========");
            System.out.println("1. 查询健康管家");
            System.out.println("2. 设置服务对象");
            System.out.println("0. 返回上一级");
            System.out.println("================================");
            System.out.print("请选择：");

            try {
                int choice = sc.nextInt();
                sc.nextLine(); // 消耗换行符
                switch (choice) {
                    case 1:
                        listHealthManagers(sc);
                        break;
                    case 2:
                        assignCustomerToManager(sc);
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("输入有误，请重新输入");
                }
            } catch (Exception e) {
                System.out.println("输入无效，请输入数字选项");
                sc.next(); // 清除非法输入
            }
        }
    }

    /**
     * 查询健康管家列表
     */
    private void listHealthManagers(Scanner sc) {
        System.out.println("\n==========查询健康管家==========");
        System.out.print("请输入管家姓名（直接回车查询全部）：");
        String managerName = sc.nextLine();
        
        RoleDao roleDao = new RoleDao();
        List<Role> healthManagerRoles = roleDao.findAll().stream()
                .filter(r -> "健康管家".equals(r.getName()))
                .toList();
        
        if (healthManagerRoles.isEmpty()) {
            System.out.println("系统中没有健康管家角色");
            return;
        }
        
        List<Integer> healthManagerRoleIds = healthManagerRoles.stream()
                .map(Role::getId)
                .toList();
        
        List<TUser> allManagers = userDao.findAll();
        List<TUser> managers = new ArrayList<>();
        
        for (TUser user : allManagers) {
            if (user.getRoleId() != null && healthManagerRoleIds.contains(user.getRoleId())) {
                if (managerName.isEmpty() || 
                        (user.getNickname() != null && user.getNickname().contains(managerName))) {
                    managers.add(user);
                }
            }
        }

        if (managers.isEmpty()) {
            System.out.println("未找到健康管家");
            return;
        }

        System.out.println("\n=== 健康管家列表 ===");
        System.out.printf("%-4s %-10s %-15s %-6s %-15s\n", "ID", "姓名", "账号", "性别", "联系电话");
        System.out.println("----------------------------------------------------------------------------------------");
        
        for (TUser manager : managers) {
            String sex = (manager.getSex() != null && manager.getSex() == 1) ? "男" : "女";
            System.out.printf("%-4d %-10s %-15s %-6s %-15s\n",
                    manager.getId(),
                    manager.getNickname() != null ? manager.getNickname() : "未设置",
                    manager.getUsername(),
                    sex,
                    manager.getPhoneNumber());
        }
        
        System.out.println("\n共查询到 " + managers.size() + " 条记录");
    }

    /**
     * 设置服务对象（给客户分配管家或从管家移除客户）
     */
    private void assignCustomerToManager(Scanner sc) {
        System.out.println("\n==========设置服务对象==========");
        
        System.out.print("请输入健康管家姓名：");
        String managerName = sc.next();
        sc.nextLine(); // 消耗换行符
        
        RoleDao roleDao = new RoleDao();
        List<Role> healthManagerRoles = roleDao.findAll().stream()
                .filter(r -> "健康管家".equals(r.getName()))
                .toList();
        
        if (healthManagerRoles.isEmpty()) {
            System.out.println("系统中没有健康管家角色");
            return;
        }
        
        List<Integer> healthManagerRoleIds = healthManagerRoles.stream()
                .map(Role::getId)
                .toList();
        
        List<TUser> allManagers = userDao.findAll();
        List<TUser> managers = new ArrayList<>();
        
        for (TUser user : allManagers) {
            if (user.getRoleId() != null && healthManagerRoleIds.contains(user.getRoleId())) {
                if (user.getNickname() != null && user.getNickname().contains(managerName)) {
                    managers.add(user);
                }
            }
        }
        
        if (managers.isEmpty()) {
            System.out.println("未找到该健康管家");
            return;
        }

        System.out.println("\n=== 匹配的健康管家 ===");
        for (int i = 0; i < managers.size(); i++) {
            TUser m = managers.get(i);
            System.out.println((i + 1) + ". " + m.getNickname() + " | 账号：" + m.getUsername());
        }

        System.out.print("\n请选择健康管家（输入序号）：");
        try {
            int managerIndex = sc.nextInt();
            sc.nextLine(); // 消耗换行符
            if (managerIndex < 1 || managerIndex > managers.size()) {
                System.out.println("无效选择");
                return;
            }

            TUser selectedManager = managers.get(managerIndex - 1);
            
            while (true) {
                System.out.println("\n==========健康管家：" + selectedManager.getNickname() + "==========");
                System.out.println("1. 添加服务客户（查询无管家客户）");
                System.out.println("2. 查看当前服务客户列表");
                System.out.println("3. 移除服务客户");
                System.out.println("0. 返回上一级");
                System.out.println("==========================================");
                System.out.print("请选择：");

                try {
                    int choice = sc.nextInt();
                    sc.nextLine(); // 消耗换行符
                    switch (choice) {
                        case 1:
                            addCustomerToManager(sc, selectedManager);
                            break;
                        case 2:
                            viewManagerCustomers(selectedManager);
                            break;
                        case 3:
                            removeCustomerFromManager(sc, selectedManager);
                            break;
                        case 0:
                            return;
                        default:
                            System.out.println("输入有误，请重新输入");
                    }
                } catch (Exception e) {
                    System.out.println("输入无效，请输入数字选项");
                    sc.next(); // 清除非法输入
                }
            }
        } catch (Exception e) {
            System.out.println("输入无效，请输入数字");
            sc.next(); // 清除非法输入
        }
    }

    /**
     * 添加客户到管家的服务列表
     */
    private void addCustomerToManager(Scanner sc, TUser manager) {
        System.out.println("\n==========添加服务客户==========");
        System.out.print("请输入客户姓名（直接回车查询全部无管家客户）：");
        String customerName = sc.nextLine();
        
        List<Customer> allCustomers = customerDao.findAll();
        List<Customer> unassignedCustomers = new ArrayList<>();
        
        for (Customer customer : allCustomers) {
            if (customer.getUserId() == null || customer.getUserId() == -1) {
                if (customerName.isEmpty() || 
                        (customer.getCustomerName() != null && customer.getCustomerName().contains(customerName))) {
                    unassignedCustomers.add(customer);
                }
            }
        }

        if (unassignedCustomers.isEmpty()) {
            System.out.println("暂无无管家的客户");
            return;
        }

        System.out.println("\n=== 无管家的客户列表 ===");
        System.out.printf("%-4s %-10s %-6s %-6s %-15s\n", "ID", "姓名", "年龄", "性别", "联系电话");
        System.out.println("----------------------------------------------------------------------------------------");
        
        for (int i = 0; i < unassignedCustomers.size(); i++) {
            Customer c = unassignedCustomers.get(i);
            String sex = (c.getCustomerSex() != null && c.getCustomerSex() == 1) ? "男" : "女";
            System.out.printf("%-4d %-10s %-6d %-6s %-15s\n",
                    c.getId(),
                    c.getCustomerName(),
                    c.getCustomerAge(),
                    sex,
                    c.getContactTel());
        }

        System.out.print("\n请选择要添加的客户（输入序号，0返回）：");
        try {
            int customerIndex = sc.nextInt();
            sc.nextLine(); // 消耗换行符
            if (customerIndex == 0) {
                return;
            }
            
            if (customerIndex < 1 || customerIndex > unassignedCustomers.size()) {
                System.out.println("无效选择");
                return;
            }

            Customer selectedCustomer = unassignedCustomers.get(customerIndex - 1);
            
            System.out.print("\n确认要将 " + selectedCustomer.getCustomerName() + " 分配给 " + manager.getNickname() + " 吗？（y/n）：");
            String confirm = sc.nextLine();
            
            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("已取消分配");
                return;
            }

            selectedCustomer.setUserId(manager.getId());
            boolean result = customerDao.updateCustomer(selectedCustomer);
            
            if (result) {
                System.out.println("分配成功！");
            } else {
                System.out.println("分配失败");
            }
        } catch (Exception e) {
            System.out.println("输入无效，请输入数字");
            sc.next(); // 清除非法输入
        }
    }

    /**
     * 获取管家的服务客户列表（提取为公共方法）
     */
    private List<Customer> getManagerCustomers(TUser manager) {
        List<Customer> allCustomers = customerDao.findAll();
        List<Customer> assignedCustomers = new ArrayList<>();
        
        for (Customer customer : allCustomers) {
            if (customer.getUserId() != null && customer.getUserId().equals(manager.getId())) {
                assignedCustomers.add(customer);
            }
        }
        return assignedCustomers;
    }

    /**
     * 查看管家当前的服务客户列表
     */
    private void viewManagerCustomers(TUser manager) {
        List<Customer> assignedCustomers = getManagerCustomers(manager);

        if (assignedCustomers.isEmpty()) {
            System.out.println("\n该管家暂无服务客户");
            return;
        }

        System.out.println("\n=== " + manager.getNickname() + " 的服务客户列表 ===");
        System.out.printf("%-4s %-10s %-6s %-6s %-15s %-12s\n", "ID", "姓名", "年龄", "性别", "联系电话", "护理级别");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        
        for (Customer c : assignedCustomers) {
            String sex = (c.getCustomerSex() != null && c.getCustomerSex() == 1) ? "男" : "女";
            String levelInfo = getNurseLevelInfo(c.getLevelId());
            System.out.printf("%-4d %-10s %-6d %-6s %-15s %-12s\n",
                    c.getId(),
                    c.getCustomerName(),
                    c.getCustomerAge(),
                    sex,
                    c.getContactTel(),
                    levelInfo);
        }
        
        System.out.println("\n共 " + assignedCustomers.size() + " 个服务客户");
    }

    /**
     * 从管家的服务列表中移除客户
     */
    private void removeCustomerFromManager(Scanner sc, TUser manager) {
        List<Customer> assignedCustomers = getManagerCustomers(manager);

        if (assignedCustomers.isEmpty()) {
            System.out.println("该管家暂无服务客户");
            return;
        }

        System.out.println("\n=== 当前服务客户列表 ===");
        for (int i = 0; i < assignedCustomers.size(); i++) {
            Customer c = assignedCustomers.get(i);
            System.out.println((i + 1) + ". " + c.getCustomerName() + 
                    " | 年龄：" + c.getCustomerAge() + 
                    " | 电话：" + c.getContactTel());
        }

        System.out.print("\n请选择要移除的客户（输入序号，0返回）：");
        try {
            int customerIndex = sc.nextInt();
            sc.nextLine(); // 消耗换行符
            if (customerIndex == 0) {
                return;
            }
            
            if (customerIndex < 1 || customerIndex > assignedCustomers.size()) {
                System.out.println("无效选择");
                return;
            }

            Customer selectedCustomer = assignedCustomers.get(customerIndex - 1);
            
            System.out.println("\n警告：移除操作不会影响到该客户的护理记录信息");
            System.out.print("确认要将 " + selectedCustomer.getCustomerName() + " 从服务列表中移除吗？（y/n）：");
            String confirm = sc.nextLine();
            
            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("已取消移除");
                return;
            }

            selectedCustomer.setUserId(-1);
            boolean result = customerDao.updateCustomer(selectedCustomer);
            
            if (result) {
                System.out.println("移除成功！该客户现在可以重新分配给其他管家");
            } else {
                System.out.println("移除失败");
            }
        } catch (Exception e) {
            System.out.println("输入无效，请输入数字");
            sc.next(); // 清除非法输入
        }
    }

    /**
     * 获取护理级别信息
     * @param levelId 级别ID
     * @return 级别名称
     */
    private String getNurseLevelInfo(Integer levelId) {
        if (levelId == null) {
            return "未设置";
        }
        NurseLevelDao nurseLevelDao = new NurseLevelDao();
        NurseLevel level = nurseLevelDao.findById(levelId);
        return level != null ? level.getLevelName() : "未知";
    }

    /**
     * 护理级别管理
     */
    private void manageNurseLevel(Scanner sc) {
        while (true) {
            System.out.println("\n==========护理级别管理==========");
            System.out.println("1. 添加护理级别");
            System.out.println("2. 查询所有护理级别");
            System.out.println("3. 修改护理级别");
            System.out.println("4. 删除护理级别");
            System.out.println("0. 返回上一级");
            System.out.println("===============================");
            System.out.print("请选择：");

            try {
                int choice = sc.nextInt();
                sc.nextLine();
                switch (choice) {
                    case 1:
                        addNurseLevel(sc);
                        break;
                    case 2: listNurseLevels();
                        break;
                    case 3:
                        updateNurseLevel(sc);
                        break;
                    case 4:
                        deleteNurseLevel(sc);
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

    private void addNurseLevel(Scanner sc) {
        System.out.println("\n==========添加护理级别==========");
        System.out.print("请输入护理级别名称：");
        String levelName = sc.nextLine();
        System.out.print("请输入级别状态（1-启用，0-禁用）：");
        int status = sc.nextInt();
        sc.nextLine();

        NurseLevel level = new NurseLevel();
        level.setLevelName(levelName);
        level.setLevelStatus(status);
        level.setIsDeleted(0);

        try {
            String result = nurseLevelDao.addNurseLevel(level);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("添加失败：" + e.getMessage());
        }
    }

    private void listNurseLevels() {
        System.out.println("\n==========护理级别列表==========");
        List<NurseLevel> levels = nurseLevelDao.findAll();
        if (levels.isEmpty()) {
            System.out.println("暂无护理级别");
            return;
        }

        System.out.printf("%-4s %-20s %-10s\n", "ID", "级别名称", "状态");
        System.out.println("----------------------------------------");
        for (NurseLevel level : levels) {
            String status = level.getLevelStatus() == 1 ? "启用" : "禁用";
            System.out.printf("%-4d %-20s %-10s\n",
                    level.getId(),
                    level.getLevelName(),
                    status);
        }
    }

    private void updateNurseLevel(Scanner sc) {
        System.out.println("\n==========修改护理级别==========");
        listNurseLevels();
        System.out.print("请输入要修改的级别ID：");
        int id = sc.nextInt();
        sc.nextLine();

        NurseLevel level = nurseLevelDao.findById(id);
        if (level == null) {
            System.out.println("未找到该护理级别");
            return;
        }

        System.out.print("请输入新的级别名称（直接回车保持原值）：");
        String name = sc.nextLine();
        if (!name.isEmpty()) {
            level.setLevelName(name);
        }

        System.out.print("请输入状态（1-启用，0-禁用，直接回车保持原值）：");
        String statusStr = sc.nextLine();
        if (!statusStr.isEmpty()) {
            level.setLevelStatus(Integer.parseInt(statusStr));
        }

        boolean result = nurseLevelDao.updateNurseLevel(level);
        System.out.println(result ? "修改成功" : "修改失败");
    }

    private void deleteNurseLevel(Scanner sc) {
        System.out.println("\n==========删除护理级别==========");
        listNurseLevels();
        System.out.print("请输入要删除的级别ID：");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("确认删除？（y/n）：");
        String confirm = sc.nextLine();
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("已取消删除");
            return;
        }

        boolean result = nurseLevelDao.deleteById(id);
        System.out.println(result ? "删除成功" : "删除失败");
    }

    /**
     * 护理项目管理
     */
    private void manageNurseContent(Scanner sc) {
        while (true) {
            System.out.println("\n==========护理项目管理==========");
            System.out.println("1. 添加护理项目");
            System.out.println("2. 查询所有护理项目");
            System.out.println("3. 修改护理项目");
            System.out.println("4. 删除护理项目");
            System.out.println("0. 返回上一级");
            System.out.println("===============================");
            System.out.print("请选择：");

            try {
                int choice = sc.nextInt();
                sc.nextLine();
                switch (choice) {
                    case 1:
                        addNurseContent(sc);
                        break;
                    case 2:
                        listNurseContents();
                        break;
                    case 3:
                        updateNurseContent(sc);
                        break;
                    case 4:
                        deleteNurseContent(sc);
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

    private void addNurseContent(Scanner sc) {
        System.out.println("\n==========添加护理项目==========");
        System.out.print("请输入项目编号：");
        String serialNumber = sc.nextLine();
        System.out.print("请输入护理项目名称：");
        String nursingName = sc.nextLine();
        System.out.print("请输入服务价格：");
        String servicePrice = sc.nextLine();
        System.out.print("请输入项目说明：");
        String message = sc.nextLine();
        System.out.print("请输入状态（1-启用，0-禁用）：");
        int status = sc.nextInt();
        sc.nextLine();
        System.out.print("请输入执行周期：");
        String executionCycle = sc.nextLine();
        System.out.print("请输入执行次数：");
        String executionTimes = sc.nextLine();

        NurseContent content = new NurseContent();
        content.setSerialNumber(serialNumber);
        content.setNursingName(nursingName);
        content.setServicePrice(servicePrice);
        content.setMessage(message);
        content.setStatus(status);
        content.setExecutionCycle(executionCycle);
        content.setExecutionTimes(executionTimes);
        content.setIsDeleted(0);

        try {
            String result = nurseContentDao.addNurseContent(content);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("添加失败：" + e.getMessage());
        }
    }

    private void listNurseContents() {
        System.out.println("\n==========护理项目列表==========");
        List<NurseContent> contents = nurseContentDao.findAll();
        if (contents.isEmpty()) {
            System.out.println("暂无护理项目");
            return;
        }

        System.out.printf("%-4s %-20s %-10s %-10s\n", "ID", "项目名称", "价格", "状态");
        System.out.println("------------------------------------------------");
        for (NurseContent content : contents) {
            String status = content.getStatus() == 1 ? "启用" : "禁用";
            System.out.printf("%-4d %-20s %-10s %-10s\n",
                    content.getId(),
                    content.getNursingName(),
                    content.getServicePrice(),
                    status);
        }
    }

    private void updateNurseContent(Scanner sc) {
        System.out.println("\n==========修改护理项目==========");
        listNurseContents();
        System.out.print("请输入要修改的项目ID：");
        int id = sc.nextInt();
        sc.nextLine();

        NurseContent content = nurseContentDao.findById(id);
        if (content == null) {
            System.out.println("未找到该护理项目");
            return;
        }

        System.out.print("请输入新的项目名称（直接回车保持原值）：");
        String name = sc.nextLine();
        if (!name.isEmpty()) {
            content.setNursingName(name);
        }

        System.out.print("请输入新的服务价格（直接回车保持原值）：");
        String price = sc.nextLine();
        if (!price.isEmpty()) {
            content.setServicePrice(price);
        }

        System.out.print("请输入状态（1-启用，0-禁用，直接回车保持原值）：");
        String statusStr = sc.nextLine();
        if (!statusStr.isEmpty()) {
            content.setStatus(Integer.parseInt(statusStr));
        }

        boolean result = nurseContentDao.updateNurseContent(content);
        System.out.println(result ? "修改成功" : "修改失败");
    }

    private void deleteNurseContent(Scanner sc) {
        System.out.println("\n==========删除护理项目==========");
        listNurseContents();
        System.out.print("请输入要删除的项目ID：");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("确认删除？（y/n）：");
        String confirm = sc.nextLine();
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("已取消删除");
            return;
        }

        boolean result = nurseContentDao.deleteById(id);
        System.out.println(result ? "删除成功" : "删除失败");
    }

    /**
     * 客户护理设置
     */
    private void setCustomerNurse(Scanner sc) {
        while (true) {
            System.out.println("\n==========客户护理设置==========");
            System.out.println("1. 查询客户列表");
            System.out.println("2. 为客户设置护理级别");
            System.out.println("3. 为客户添加护理项目");
            System.out.println("4. 查看客户护理项目");
            System.out.println("0. 返回上一级");
            System.out.println("===============================");
            System.out.print("请选择：");

            try {
                int choice = sc.nextInt();
                sc.nextLine();
                switch (choice) {
                    case 1:
                        listCustomersForNurse();
                        break;
                    case 2:
                        setCustomerLevel(sc);
                        break;
                    case 3:
                        addCustomerNurseItem(sc);
                        break;
                    case 4:
                        viewCustomerNurseItems(sc);
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

    private void listCustomersForNurse() {
        System.out.println("\n==========客户列表==========");
        List<Customer> customers = customerDao.findAll();
        if (customers.isEmpty()) {
            System.out.println("暂无客户");
            return;
        }

        System.out.printf("%-4s %-10s %-6s %-15s %-12s\n", "ID", "姓名", "年龄", "联系电话", "护理级别");
        System.out.println("------------------------------------------------------------------------");
        for (Customer c : customers) {
            String levelInfo = getNurseLevelInfo(c.getLevelId());
            System.out.printf("%-4d %-10s %-6d %-15s %-12s\n",
                    c.getId(),
                    c.getCustomerName(),
                    c.getCustomerAge(),
                    c.getContactTel(),
                    levelInfo);
        }
    }

    private void setCustomerLevel(Scanner sc) {
        System.out.println("\n==========设置护理级别==========");
        listCustomersForNurse();
        System.out.print("请输入客户ID：");
        int customerId = sc.nextInt();
        sc.nextLine();

        Customer customer = customerDao.findById(customerId);
        if (customer == null) {
            System.out.println("未找到该客户");
            return;
        }

        System.out.println("\n==========可选护理级别==========");
        List<NurseLevel> levels = nurseLevelDao.findByStatus(1);
        if (levels.isEmpty()) {
            System.out.println("暂无可选护理级别");
            return;
        }

        System.out.printf("%-4s %-20s\n", "ID", "级别名称");
        System.out.println("----------------------------------------");
        for (NurseLevel level : levels) {
            System.out.printf("%-4d %-20s\n", level.getId(), level.getLevelName());
        }

        System.out.print("请选择护理级别ID：");
        int levelId = sc.nextInt();
        sc.nextLine();

        customer.setLevelId(levelId);
        boolean result = customerDao.updateCustomer(customer);
        System.out.println(result ? "设置成功" : "设置失败");
    }

    private void addCustomerNurseItem(Scanner sc) {
        System.out.println("\n==========添加护理项目==========");
        listCustomersForNurse();
        System.out.print("请输入客户ID：");
        int customerId = sc.nextInt();
        sc.nextLine();

        Customer customer = customerDao.findById(customerId);
        if (customer == null) {
            System.out.println("未找到该客户");
            return;
        }

        System.out.println("\n==========可选护理项目==========");
        List<NurseContent> contents = nurseContentDao.findByStatus(1);
        if (contents.isEmpty()) {
            System.out.println("暂无可选护理项目");
            return;
        }

        System.out.printf("%-4s %-20s %-10s\n", "ID", "项目名称", "价格");
        System.out.println("----------------------------------------");
        for (NurseContent content : contents) {
            System.out.printf("%-4d %-20s %-10s\n",
                    content.getId(),
                    content.getNursingName(),
                    content.getServicePrice());
        }

        System.out.print("请选择护理项目ID：");
        int contentId = sc.nextInt();
        sc.nextLine();

        System.out.print("请输入护理次数：");
        int count = sc.nextInt();
        sc.nextLine();

        CustomerNurseItem item = new CustomerNurseItem();
        item.setCustomerId(customerId);
        item.setItemId(contentId);
        item.setNurseNumber(count);
        item.setBuyTime(new Date());
        item.setMaturityTime(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
        item.setIsDeleted(0);

        try {
            String result = customerNurseItemDao.addCustomerNurseItem(item);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("添加失败：" + e.getMessage());
        }
    }

    private void viewCustomerNurseItems(Scanner sc) {
        System.out.println("\n==========查看护理项目==========");
        listCustomersForNurse();
        System.out.print("请输入客户ID：");
        int customerId = sc.nextInt();
        sc.nextLine();

        List<CustomerNurseItem> items = customerNurseItemDao.findByCustomerId(customerId);
        if (items.isEmpty()) {
            System.out.println("该客户暂无护理项目");
            return;
        }

        System.out.println("\n=== 客户护理项目列表 ===");
        System.out.printf("%-4s %-20s %-8s %-10s\n", "ID", "项目名称", "次数", "状态");
        System.out.println("--------------------------------------------------------");
        for (CustomerNurseItem item : items) {
            String projectName = getNurseContentName(item.getItemId());
            String status = item.getIsDeleted() == 0 ? "有效" : "已删除";
            System.out.printf("%-4d %-20s %-8d %-10s\n",
                    item.getId(),
                    projectName,
                    item.getNurseNumber(),
                    status);
        }
    }

    private String getNurseContentName(Integer contentId) {
        if (contentId == null) {
            return "未知";
        }
        NurseContent content = nurseContentDao.findById(contentId);
        return content != null ? content.getNursingName() : "未知";
    }
}