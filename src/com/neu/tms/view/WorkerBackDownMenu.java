package com.neu.tms.view;

import com.neu.tms.dao.CustomerDao;
import com.neu.tms.pojo.BackDown;
import com.neu.tms.pojo.Customer;
import com.neu.tms.pojo.TUser;
import com.neu.tms.dao.BackDownDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class WorkerBackDownMenu implements IMenu {
    private TUser currentUser;
    private CustomerDao customerDao = new CustomerDao();
    private BackDownDao backDownDao = new BackDownDao();

    public WorkerBackDownMenu(TUser user) {
        this.currentUser = user;
    }

    @Override
    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========退住申请==========");
            System.out.println("1. 为客户添加退住办理申请");
            System.out.println("2. 查询客户退住申请信息列表");
            System.out.println("0. 返回上一级");
            System.out.println("========================");
            System.out.print("请选择：");

            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        addBackDownApplication(sc);
                        break;
                    case 2:
                        searchBackDownRecords(sc);
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
     * 为客户添加退住办理申请
     */
    private void addBackDownApplication(Scanner sc) {
        System.out.println("\n==========选择客户==========");
        System.out.print("请输入客户姓名（直接回车查询全部）：");
        sc.nextLine();
        String name = sc.nextLine();

        // 只查询当前健康管家服务的客户
        List<Customer> myCustomers = customerDao.findAll().stream()
                .filter(c -> c.getUserId() != null && c.getUserId().equals(currentUser.getId()))
                .toList();

        List<Customer> customers;
        if (name.trim().isEmpty()) {
            customers = myCustomers;
        } else {
            customers = myCustomers.stream()
                    .filter(c -> c.getCustomerName() != null && c.getCustomerName().contains(name))
                    .toList();
        }

        if (customers.isEmpty()) {
            System.out.println("未找到您服务的客户信息");
            return;
        }

        System.out.println("\n=== 我服务的客户列表 ===");
        System.out.printf("%-4s %-10s %-6s %-6s %-15s\n", "ID", "姓名", "年龄", "性别", "联系电话");
        System.out.println("--------------------------------------------------------------");

        for (Customer c : customers) {
            String sex = (c.getCustomerSex() != null && c.getCustomerSex() == 1) ? "男" : "女";
            System.out.printf("%-4d %-10s %-6d %-6s %-15s\n",
                    c.getId(),
                    c.getCustomerName(),
                    c.getCustomerAge(),
                    sex,
                    c.getContactTel());
        }

        System.out.print("\n请选择客户（输入序号，0返回）：");
        try {
            int customerIndex = sc.nextInt();
            if (customerIndex == 0) {
                return;
            }

            if (customerIndex < 1 || customerIndex > customers.size()) {
                System.out.println("无效选择");
                return;
            }

            Customer selectedCustomer = customers.get(customerIndex - 1);

            System.out.println("\n==========退住登记==========");
            System.out.println("客户：" + selectedCustomer.getCustomerName());
            
            System.out.println("请选择退住类型：");
            System.out.println("1. 正常退住");
            System.out.println("2. 异常退住");
            System.out.print("请选择（输入数字）：");
            int typeChoice = sc.nextInt();
            Integer backType = typeChoice == 1 ? 1 : 2; // 1:正常退住, 2:异常退住

            sc.nextLine();
            System.out.print("请输入退住原因：");
            String reason = sc.nextLine();
            if (reason.isEmpty()) {
                System.out.println("退住原因不能为空");
                return;
            }

            System.out.print("请输入退住时间（格式：yyyy-MM-dd，直接回车使用当前日期）：");
            String backTimeStr = sc.nextLine();
            
            Date backTime;
            if (backTimeStr.isEmpty()) {
                backTime = new Date();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                backTime = sdf.parse(backTimeStr);
            }

            BackDown backDown = new BackDown();
            backDown.setCustomerId(selectedCustomer.getId());
            // 使用remarks记录申请人信息
            backDown.setRemarks("申请人: " + currentUser.getNickname() + "(" + currentUser.getUsername() + ")");
            backDown.setRetreatType(backType);
            backDown.setRetreatReason(reason);
            backDown.setRetreatTime(backTime);
            backDown.setAuditStatus(0); // 0:已提交（默认状态）

            String result = backDownDao.addBackDown(backDown);
            System.out.println("\n" + result);
        } catch (Exception e) {
            System.out.println("操作失败：" + e.getMessage());
            sc.next();
        }
    }

    /**
     * 查询客户退住申请信息列表
     */
    private void searchBackDownRecords(Scanner sc) {
        System.out.print("请输入客户姓名（直接回车查询全部）：");
        sc.nextLine();
        String name = sc.nextLine();

        // 获取当前健康管家服务的客户ID列表
        List<Customer> myCustomers = customerDao.findAll().stream()
                .filter(c -> c.getUserId() != null && c.getUserId().equals(currentUser.getId()))
                .toList();

        List<Integer> myCustomerIds = myCustomers.stream()
                .map(Customer::getId)
                .toList();

        List<BackDown> backDowns = backDownDao.findByCustomerIdsAndName(myCustomerIds, name);

        if (backDowns.isEmpty()) {
            System.out.println("未找到退住申请记录");
            return;
        }

        System.out.println("\n=== 退住申请列表 ===");
        System.out.printf("%-4s %-10s %-12s %-15s %-12s %-15s\n", 
                "ID", "客户姓名", "退住类型", "退住时间", "状态", "退住原因");
        System.out.println("---------------------------------------------------------------------------------------------");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (BackDown b : backDowns) {
            Customer customer = customerDao.findById(b.getCustomerId());
            String customerName = customer != null ? customer.getCustomerName() : "未知";
            String backTypeText = b.getRetreatType() != null && b.getRetreatType() == 1 ? "正常退住" : "异常退住";
            String statusText = getStatusText(b.getAuditStatus());
            
            System.out.printf("%-4d %-10s %-12s %-15s %-12s %-15s\n",
                    b.getId(),
                    customerName,
                    backTypeText,
                    sdf.format(b.getRetreatTime()),
                    statusText,
                    b.getRetreatReason().length() > 15 ? b.getRetreatReason().substring(0, 15) + "..." : b.getRetreatReason());
        }

        System.out.println("\n共 " + backDowns.size() + " 条记录");
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer auditStatus) {
        if (auditStatus == null) {
            return "已提交";
        }
        switch (auditStatus) {
            case 0:
                return "已提交";
            case 1:
                return "已批准";
            case 2:
                return "已拒绝";
            case 3:
                return "已完成";
            default:
                return "未知";
        }
    }
}
