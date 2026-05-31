package com.neu.tms.view;

import com.neu.tms.dao.*;
import com.neu.tms.pojo.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class AdminHealthManagerMenu implements IMenu {
    private TUserDao userDao = new TUserDao();
    private CustomerDao customerDao = new CustomerDao();
    private CustomerNurseItemDao customerNurseItemDao = new CustomerNurseItemDao();
    private NurseRecordDao nurseRecordDao = new NurseRecordDao();

    @Override
    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========健康管家管理==========");
            System.out.println("1. 设置服务对象");
            System.out.println("2. 服务关注");
            System.out.println("0. 返回上一级");
            System.out.println("===============================");
            System.out.print("请选择：");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    setServiceObjects(sc);
                    break;
                case 2:
                    serviceAttention(sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }

    /**
     * 设置服务对象（为护工分配客户）
     */
    private void setServiceObjects(Scanner sc) {
        while (true) {
            System.out.println("\n==========设置服务对象==========");
            System.out.println("1. 查看护工服务对象");
            System.out.println("2. 为护工分配客户");
            System.out.println("3. 为护工移除客户");
            System.out.println("0. 返回上一级");
            System.out.println("================================");
            System.out.print("请选择：");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    viewWorkerServiceObjects(sc);
                    break;
                case 2:
                    assignCustomerToWorker(sc);
                    break;
                case 3:
                    removeCustomerFromWorker(sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }

    private void viewWorkerServiceObjects(Scanner sc) {
        System.out.println("\n==========查看护工服务对象==========");
        
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
        
        List<TUser> workers = userDao.findAll().stream()
                .filter(u -> u.getRoleId() != null && healthManagerRoleIds.contains(u.getRoleId()))
                .toList();

        if (workers.isEmpty()) {
            System.out.println("暂无健康管家数据");
            return;
        }

        System.out.println("\n=== 健康管家列表 ===");
        for (int i = 0; i < workers.size(); i++) {
            TUser worker = workers.get(i);
            System.out.println((i + 1) + ". " + worker.getNickname() + 
                    " | 账号：" + worker.getUsername() + 
                    " | 电话：" + worker.getPhoneNumber());
        }

        System.out.print("\n请选择要查看的护工（输入序号）：");
        int workerIndex = sc.nextInt();
        if (workerIndex < 1 || workerIndex > workers.size()) {
            System.out.println("无效选择");
            return;
        }

        TUser selectedWorker = workers.get(workerIndex - 1);
        
        List<Customer> allCustomers = customerDao.findAll();
        List<Customer> serviceCustomers = allCustomers.stream()
                .filter(c -> c.getUserId() != null && c.getUserId().equals(selectedWorker.getId()))
                .toList();

        System.out.println("\n=== " + selectedWorker.getNickname() + " 的服务对象 ===");
        if (serviceCustomers.isEmpty()) {
            System.out.println("该护工暂无服务对象");
            return;
        }

        System.out.printf("%-6s %-10s %-6s %-15s %-10s\n", "ID", "姓名", "年龄", "联系电话", "房间号");
        System.out.println("--------------------------------------------------------------");
        
        for (Customer customer : serviceCustomers) {
            System.out.printf("%-6d %-10s %-6d %-15s %-10s\n",
                    customer.getId(),
                    customer.getCustomerName(),
                    customer.getCustomerAge(),
                    customer.getContactTel(),
                    customer.getRoomNo());
        }

        System.out.println("\n总计：" + serviceCustomers.size() + " 位客户");
    }

    private void assignCustomerToWorker(Scanner sc) {
        System.out.println("\n==========为护工分配客户==========");
        
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
        
        List<TUser> workers = userDao.findAll().stream()
                .filter(u -> u.getRoleId() != null && healthManagerRoleIds.contains(u.getRoleId()))
                .toList();

        if (workers.isEmpty()) {
            System.out.println("暂无健康管家数据");
            return;
        }

        System.out.println("\n=== 健康管家列表 ===");
        for (int i = 0; i < workers.size(); i++) {
            System.out.println((i + 1) + ". " + workers.get(i).getNickname());
        }

        System.out.print("\n请选择护工（输入序号）：");
        int workerIndex = sc.nextInt();
        TUser selectedWorker = workers.get(workerIndex - 1);

        System.out.print("\n请输入要分配的客户姓名：");
        String customerName = sc.next();
        
        List<Customer> customers = customerDao.findByNameLike(customerName);
        if (customers.isEmpty()) {
            System.out.println("未找到该客户");
            return;
        }

        System.out.println("\n=== 匹配的客户 ===");
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            String currentWorker = "无";
            if (c.getUserId() != null) {
                TUser worker = userDao.findById(c.getUserId());
                if (worker != null) {
                    currentWorker = worker.getNickname();
                }
            }
            System.out.println((i + 1) + ". " + c.getCustomerName() + 
                    " | 当前护工：" + currentWorker);
        }

        System.out.print("\n请选择客户（输入序号）：");
        int customerIndex = sc.nextInt();
        if (customerIndex < 1 || customerIndex > customers.size()) {
            System.out.println("无效选择");
            return;
        }

        Customer selectedCustomer = customers.get(customerIndex - 1);
        
        System.out.println("\n确认分配信息：");
        System.out.println("护工：" + selectedWorker.getNickname());
        System.out.println("客户：" + selectedCustomer.getCustomerName());
        System.out.print("\n确认分配？（y/n）：");
        String confirm = sc.next();
        
        if ("y".equalsIgnoreCase(confirm)) {
            selectedCustomer.setUserId(selectedWorker.getId());
            boolean result = customerDao.updateCustomer(selectedCustomer);
            System.out.println(result ? "分配成功" : "分配失败");
        } else {
            System.out.println("已取消分配");
        }
    }

    private void removeCustomerFromWorker(Scanner sc) {
        System.out.println("\n==========为护工移除客户==========");
        
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
        
        List<TUser> workers = userDao.findAll().stream()
                .filter(u -> u.getRoleId() != null && healthManagerRoleIds.contains(u.getRoleId()))
                .toList();

        if (workers.isEmpty()) {
            System.out.println("暂无健康管家数据");
            return;
        }

        System.out.println("\n=== 健康管家列表 ===");
        for (int i = 0; i < workers.size(); i++) {
            System.out.println((i + 1) + ". " + workers.get(i).getNickname());
        }

        System.out.print("\n请选择护工（输入序号）：");
        int workerIndex = sc.nextInt();
        TUser selectedWorker = workers.get(workerIndex - 1);

        List<Customer> allCustomers = customerDao.findAll();
        List<Customer> serviceCustomers = allCustomers.stream()
                .filter(c -> c.getUserId() != null && c.getUserId().equals(selectedWorker.getId()))
                .toList();

        if (serviceCustomers.isEmpty()) {
            System.out.println("该护工暂无服务对象");
            return;
        }

        System.out.println("\n=== " + selectedWorker.getNickname() + " 的服务对象 ===");
        for (int i = 0; i < serviceCustomers.size(); i++) {
            System.out.println((i + 1) + ". " + serviceCustomers.get(i).getCustomerName());
        }

        System.out.print("\n请选择要移除的客户（输入序号）：");
        int customerIndex = sc.nextInt();
        if (customerIndex < 1 || customerIndex > serviceCustomers.size()) {
            System.out.println("无效选择");
            return;
        }

        Customer selectedCustomer = serviceCustomers.get(customerIndex - 1);
        
        System.out.println("\n确认移除信息：");
        System.out.println("护工：" + selectedWorker.getNickname());
        System.out.println("客户：" + selectedCustomer.getCustomerName());
        System.out.print("\n确认移除？（y/n）：");
        String confirm = sc.next();
        
        if ("y".equalsIgnoreCase(confirm)) {
            selectedCustomer.setUserId(null);
            boolean result = customerDao.updateCustomer(selectedCustomer);
            System.out.println(result ? "移除成功" : "移除失败");
        } else {
            System.out.println("已取消移除");
        }
    }

    /**
     * 服务关注
     */
    private void serviceAttention(Scanner sc) {
        while (true) {
            System.out.println("\n==========服务关注==========");
            System.out.println("1. 查看服务缴费情况");
            System.out.println("2. 查看服务过期提醒");
            System.out.println("3. 续费提醒管理");
            System.out.println("0. 返回上一级");
            System.out.println("===========================");
            System.out.print("请选择：");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    viewServicePayment(sc);
                    break;
                case 2:
                    viewServiceExpiration(sc);
                    break;
                case 3:
                    manageRenewalReminder(sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }

    private void viewServicePayment(Scanner sc) {
        System.out.println("\n==========查看服务缴费情况==========");
        
        List<Customer> customers = customerDao.findAll();
        
        if (customers.isEmpty()) {
            System.out.println("暂无客户数据");
            return;
        }

        System.out.println("\n=== 客户服务缴费情况 ===");
        System.out.printf("%-6s %-10s %-15s %-15s %-15s\n", 
                "ID", "姓名", "入住时间", "合同到期", "剩余天数");
        System.out.println("------------------------------------------------------------------------");
        
        Date now = new Date();
        for (Customer customer : customers) {
            String remainingDays = "无合同";
            if (customer.getExpirationDate() != null) {
                long diff = customer.getExpirationDate().getTime() - now.getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                remainingDays = String.valueOf(days);
            }
            
            System.out.printf("%-6d %-10s %-15s %-15s %-15s\n",
                    customer.getId(),
                    customer.getCustomerName(),
                    formatDate(customer.getCheckinDate()),
                    formatDate(customer.getExpirationDate()),
                    remainingDays);
        }
    }

    private void viewServiceExpiration(Scanner sc) {
        System.out.println("\n==========查看服务过期提醒==========");
        
        List<Customer> customers = customerDao.findAll();
        Date now = new Date();
        
        System.out.println("\n=== 即将过期的服务（30天内） ===");
        System.out.printf("%-6s %-10s %-15s %-10s\n", "ID", "姓名", "到期时间", "剩余天数");
        System.out.println("--------------------------------------------------------------");
        
        boolean hasExpiring = false;
        for (Customer customer : customers) {
            if (customer.getExpirationDate() != null) {
                long diff = customer.getExpirationDate().getTime() - now.getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                
                if (days >= 0 && days <= 30) {
                    hasExpiring = true;
                    System.out.printf("%-6d %-10s %-15s %-10d\n",
                            customer.getId(),
                            customer.getCustomerName(),
                            formatDate(customer.getExpirationDate()),
                            days);
                }
            }
        }
        
        if (!hasExpiring) {
            System.out.println("暂无即将过期的服务");
        }

        System.out.println("\n=== 已过期的服务 ===");
        System.out.printf("%-6s %-10s %-15s %-10s\n", "ID", "姓名", "到期时间", "超期天数");
        System.out.println("--------------------------------------------------------------");
        
        boolean hasExpired = false;
        for (Customer customer : customers) {
            if (customer.getExpirationDate() != null) {
                long diff = now.getTime() - customer.getExpirationDate().getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                
                if (days > 0) {
                    hasExpired = true;
                    System.out.printf("%-6d %-10s %-15s %-10d\n",
                            customer.getId(),
                            customer.getCustomerName(),
                            formatDate(customer.getExpirationDate()),
                            days);
                }
            }
        }
        
        if (!hasExpired) {
            System.out.println("暂无已过期的服务");
        }
    }

    private void manageRenewalReminder(Scanner sc) {
        System.out.println("\n==========续费提醒管理==========");
        
        List<Customer> customers = customerDao.findAll();
        Date now = new Date();
        
        System.out.println("\n=== 需要续费提醒的客户 ===");
        
        List<Customer> needReminder = new ArrayList<>();
        for (Customer customer : customers) {
            if (customer.getExpirationDate() != null) {
                long diff = customer.getExpirationDate().getTime() - now.getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                
                if (days >= 0 && days <= 15) {
                    needReminder.add(customer);
                }
            }
        }

        if (needReminder.isEmpty()) {
            System.out.println("暂无需要续费提醒的客户");
            return;
        }

        System.out.printf("%-6s %-10s %-15s %-10s %-15s\n", 
                "ID", "姓名", "到期时间", "剩余天数", "联系电话");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (Customer customer : needReminder) {
            long diff = customer.getExpirationDate().getTime() - now.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            
            System.out.printf("%-6d %-10s %-15s %-10d %-15s\n",
                    customer.getId(),
                    customer.getCustomerName(),
                    formatDate(customer.getExpirationDate()),
                    days,
                    customer.getContactTel());
        }

        System.out.println("\n提示：请及时联系以上客户进行续费");
    }

    private String formatDate(Date date) {
        if (date == null) return "无";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
