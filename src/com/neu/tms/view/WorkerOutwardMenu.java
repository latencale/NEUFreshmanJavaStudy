package com.neu.tms.view;

import com.neu.tms.dao.CustomerDao;
import com.neu.tms.dao.OutwardDao;
import com.neu.tms.pojo.Customer;
import com.neu.tms.pojo.Outward;
import com.neu.tms.pojo.TUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class WorkerOutwardMenu implements IMenu {
    private TUser currentUser;
    private CustomerDao customerDao = new CustomerDao();
    private OutwardDao outwardDao = new OutwardDao();

    public WorkerOutwardMenu(TUser user) {
        this.currentUser = user;
    }

    @Override
    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========外出申请==========");
            System.out.println("1. 为客户添加外出登记申请");
            System.out.println("2. 查询客户外出申请信息列表");
            System.out.println("0. 返回上一级");
            System.out.println("========================");
            System.out.print("请选择：");

            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        addOutwardApplication(sc);
                        break;
                    case 2:
                        searchOutwardRecords(sc);
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
     * 为客户添加外出登记申请
     */
    private void addOutwardApplication(Scanner sc) {
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

            System.out.println("\n==========外出登记==========");
            System.out.println("客户：" + selectedCustomer.getCustomerName());
            
            sc.nextLine(); // 清除残留的换行符
            
            System.out.print("请输入外出事由：");
            String reason = sc.nextLine();
            if (reason.isEmpty()) {
                System.out.println("外出事由不能为空");
                return;
            }

            System.out.print("请输入外出时间（格式：yyyy-MM-dd HH:mm）：");
            String outTimeStr = sc.nextLine();
            
            System.out.print("请输入预计回院时间（格式：yyyy-MM-dd HH:mm）：");
            String expectedReturnTimeStr = sc.nextLine();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date outTime = sdf.parse(outTimeStr);
            Date expectedReturnTime = sdf.parse(expectedReturnTimeStr);

            if (expectedReturnTime.before(outTime)) {
                System.out.println("预计回院时间不能早于外出时间");
                return;
            }

            Outward outward = new Outward();
            outward.setCustomerId(selectedCustomer.getId());
            outward.setRemarks("申请人: " + currentUser.getNickname() + "(" + currentUser.getUsername() + ")");
            outward.setOutgoingReason(reason);
            outward.setOutgoingTime(outTime);
            outward.setExpectedReturnTime(expectedReturnTime);
            outward.setAuditStatus(0);
            outward.setCreateTime(new Date());

            String result = outwardDao.addOutward(outward);
            System.out.println("\n" + result);
        } catch (Exception e) {
            System.out.println("操作失败：" + e.getMessage());
            sc.next();
        }
    }

    /**
     * 查询客户外出申请信息列表
     */
    private void searchOutwardRecords(Scanner sc) {
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

        List<Outward> outwards = outwardDao.findByCustomerIdsAndName(myCustomerIds, name);

        if (outwards.isEmpty()) {
            System.out.println("未找到外出申请记录");
            return;
        }

        System.out.println("\n=== 外出申请列表 ===");
        System.out.printf("%-4s %-10s %-18s %-18s %-18s %-10s %-10s\n", 
                "ID", "客户姓名", "外出时间", "预计回院", "实际回院", "状态", "事由");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (Outward o : outwards) {
            Customer customer = customerDao.findById(o.getCustomerId());
            String customerName = customer != null ? customer.getCustomerName() : "未知";
            String actualReturnTime = o.getActualReturnTime() != null ? sdf.format(o.getActualReturnTime()) : "未回院";
            String status = getStatusText(o.getAuditStatus());
            
            System.out.printf("%-4d %-10s %-18s %-18s %-18s %-10s %-10s\n",
                    o.getId(),
                    customerName,
                    sdf.format(o.getOutgoingTime()),
                    sdf.format(o.getExpectedReturnTime()),
                    actualReturnTime,
                    status,
                    o.getOutgoingReason().length() > 10 ? o.getOutgoingReason().substring(0, 10) + "..." : o.getOutgoingReason());
        }

        System.out.println("\n共 " + outwards.size() + " 条记录");

        System.out.print("\n是否为外出的客户登记回院时间？(y/n)：");
        String choice = sc.next();
        if ("y".equalsIgnoreCase(choice)) {
            registerReturnTime(outwards, sc);
        }
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
                return "已回院";
            default:
                return "未知";
        }
    }

    /**
     * 登记回院时间
     */
    private void registerReturnTime(List<Outward> outwards, Scanner sc) {
        System.out.print("请选择要登记回院的记录序号（输入0取消）：");
        try {
            int recordIndex = sc.nextInt();
            if (recordIndex == 0) {
                System.out.println("已取消操作");
                return;
            }

            if (recordIndex < 1 || recordIndex > outwards.size()) {
                System.out.println("无效选择");
                return;
            }

            Outward selectedOutward = outwards.get(recordIndex - 1);

            if (selectedOutward.getActualReturnTime() != null) {
                System.out.println("该记录已登记回院时间");
                return;
            }

            System.out.print("请输入实际回院时间（格式：yyyy-MM-dd HH:mm，直接回车使用当前时间）：");
            sc.nextLine();
            String returnTimeStr = sc.nextLine();
            
            Date actualReturnTime;
            if (returnTimeStr.isEmpty()) {
                actualReturnTime = new Date();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                actualReturnTime = sdf.parse(returnTimeStr);
            }

            selectedOutward.setActualReturnTime(actualReturnTime);
            selectedOutward.setAuditStatus(3); // 3:已回院
            
            boolean result = outwardDao.updateOutward(selectedOutward);
            
            if (result) {
                System.out.println("✓ 回院时间登记成功");
            } else {
                System.out.println("✗ 操作失败");
            }
        } catch (Exception e) {
            System.out.println("操作失败：" + e.getMessage());
            sc.next();
        }
    }
}
