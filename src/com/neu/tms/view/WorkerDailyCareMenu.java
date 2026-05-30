
package com.neu.tms.view;

import com.neu.tms.dao.CustomerDao;
import com.neu.tms.dao.CustomerNurseItemDao;
import com.neu.tms.dao.NurseContentDao;
import com.neu.tms.dao.NurseRecordDao;
import com.neu.tms.pojo.Customer;
import com.neu.tms.pojo.CustomerNurseItem;
import com.neu.tms.pojo.NurseContent;
import com.neu.tms.pojo.NurseRecord;
import com.neu.tms.pojo.TUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class WorkerDailyCareMenu implements IMenu {
    private TUser currentUser;
    private CustomerDao customerDao = new CustomerDao();
    private CustomerNurseItemDao nurseItemDao = new CustomerNurseItemDao();
    private NurseRecordDao nurseRecordDao = new NurseRecordDao();
    private NurseContentDao nurseContentDao = new NurseContentDao();

    public WorkerDailyCareMenu(TUser user) {
        this.currentUser = user;
    }

    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========日常护理==========");
            System.out.println("1. 查询自己服务的客户信息列表");
            System.out.println("0. 返回上一级");
            System.out.println("===========================");
            System.out.print("请选择：");

            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        searchCustomers(sc);
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
     * 查询自己服务的客户信息列表
     */
    private void searchCustomers(Scanner sc) {
        System.out.print("请输入客户姓名（直接回车查询全部）：");
        sc.nextLine();
        String name = sc.nextLine();

        List<Customer> allMyCustomers = customerDao.findAll().stream()
                .filter(c -> c.getUserId() != null && c.getUserId().equals(currentUser.getId()))
                .toList();

        List<Customer> customers;
        if (name.trim().isEmpty()) {
            customers = allMyCustomers;
        } else {
            customers = allMyCustomers.stream()
                    .filter(c -> c.getCustomerName() != null && c.getCustomerName().contains(name))
                    .toList();
        }

        if (customers.isEmpty()) {
            System.out.println("未找到您服务的客户信息");
            return;
        }

        System.out.println("\n=== 我服务的客户列表 ===");
        System.out.printf("%-4s %-10s %-6s %-6s %-15s %-12s\n", 
                "ID", "姓名", "年龄", "性别", "联系电话", "房间号");
        System.out.println("----------------------------------------------------------------------------------------");

        for (Customer c : customers) {
            String sex = (c.getCustomerSex() != null && c.getCustomerSex() == 1) ? "男" : "女";
            System.out.printf("%-4d %-10s %-6d %-6s %-15s %-12s\n",
                    c.getId(),
                    c.getCustomerName(),
                    c.getCustomerAge(),
                    sex,
                    c.getContactTel(),
                    c.getRoomNo() != null ? c.getRoomNo() : "未分配");
        }

        System.out.println("\n共查询到 " + customers.size() + " 条记录");

        System.out.print("\n请选择客户进行日常护理（输入序号，0返回）：");
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
            showCustomerNurseItems(selectedCustomer, sc);
        } catch (Exception e) {
            System.out.println("输入无效，请输入数字");
            sc.next();
        }
    }

    /**
     * 显示客户的护理项目列表
     */
    private void showCustomerNurseItems(Customer customer, Scanner sc) {
        List<CustomerNurseItem> nurseItems = nurseItemDao.findByCustomerId(customer.getId());

        if (nurseItems.isEmpty()) {
            System.out.println("\n该客户暂无护理项目");
            return;
        }

        System.out.println("\n=== " + customer.getCustomerName() + " 的护理项目 ===");
        System.out.printf("%-4s %-15s %-8s %-12s %-12s %-10s\n", 
                "ID", "项目名称", "数量", "购买时间", "到期时间", "状态");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (int i = 0; i < nurseItems.size(); i++) {
            CustomerNurseItem item = nurseItems.get(i);
            String projectName = getProjectName(item.getItemId());
            String status = getItemStatus(item, now);
            
            System.out.printf("%-4d %-15s %-8d %-12s %-12s %-10s\n",
                    item.getId(),
                    projectName,
                    item.getNurseNumber(),
                    sdf.format(item.getBuyTime()),
                    sdf.format(item.getMaturityTime()),
                    status);
        }

        System.out.println("\n状态说明：");
        System.out.println("  - 正常：剩余次数 > 0 且未到期");
        System.out.println("  - 已用完：剩余次数 <= 0");
        System.out.println("  - 已到期：已到到期时间");

        System.out.print("\n请选择护理项目进行护理（输入序号，0返回）：");
        try {
            int itemIndex = sc.nextInt();
            if (itemIndex == 0) {
                return;
            }

            if (itemIndex < 1 || itemIndex > nurseItems.size()) {
                System.out.println("无效选择");
                return;
            }

            CustomerNurseItem selectedItem = nurseItems.get(itemIndex - 1);
            performNursing(customer, selectedItem, sc);
        } catch (Exception e) {
            System.out.println("输入无效，请输入数字");
            sc.next();
        }
    }

    /**
     * 获取项目状态
     */
    private String getItemStatus(CustomerNurseItem item, Date now) {
        boolean isExpired = item.getMaturityTime() != null && now.after(item.getMaturityTime());
        boolean isUsedUp = item.getNurseNumber() != null && item.getNurseNumber() <= 0;

        if (isExpired && isUsedUp) {
            return "已到期+已用完";
        } else if (isExpired) {
            return "已到期";
        } else if (isUsedUp) {
            return "已用完";
        } else {
            return "正常";
        }
    }

    /**
     * 执行护理操作
     */
    private void performNursing(Customer customer, CustomerNurseItem nurseItem, Scanner sc) {
        System.out.println("\n==========执行护理==========");
        
        String projectName = getProjectName(nurseItem.getItemId());
        System.out.println("客户：" + customer.getCustomerName());
        System.out.println("护理项目：" + projectName);
        System.out.println("剩余数量：" + nurseItem.getNurseNumber());
        System.out.println("到期时间：" + formatDate(nurseItem.getMaturityTime()));

        if (nurseItem.getNurseNumber() <= 0) {
            System.out.println("该项目剩余次数为0，无法进行护理");
            return;
        }

        Date now = new Date();
        if (nurseItem.getMaturityTime() != null && now.after(nurseItem.getMaturityTime())) {
            System.out.println("警告：该项目已到期！");
        }

        System.out.print("请输入护理数量：");
        try {
            int count = sc.nextInt();

            if (count <= 0) {
                System.out.println("护理数量必须大于0");
                return;
            }

            if (count > nurseItem.getNurseNumber()) {
                System.out.println("护理数量不能超过剩余数量（" + nurseItem.getNurseNumber() + "）");
                return;
            }

            System.out.print("请输入护理内容（直接回车使用默认内容）：");
            sc.nextLine();
            String content = sc.nextLine();
            if (content.isEmpty()) {
                content = "日常护理-" + projectName;
            }

            // 创建护理记录
            NurseRecord record = new NurseRecord();
            record.setCustomerId(customer.getId());
            record.setItemId(nurseItem.getItemId());
            record.setNursingTime(new Date());
            record.setNursingContent(content);
            record.setNursingCount(count);
            record.setUserId(currentUser.getId());

            String result = nurseRecordDao.addNurseRecord(record);

            // 更新客户护理项目数量
            nurseItem.setNurseNumber(nurseItem.getNurseNumber() - count);
            nurseItemDao.updateCustomerNurseItem(nurseItem);

            System.out.println("\n护理记录生成成功！");
            System.out.println("护理时间：" + formatDate(new Date()));
            System.out.println("护理数量：" + count);
            System.out.println("剩余数量：" + nurseItem.getNurseNumber());
        } catch (Exception e) {
            System.out.println("操作失败：" + e.getMessage());
            sc.next();
        }
    }

    /**
     * 获取护理项目名称
     */
    private String getProjectName(Integer itemId) {
        if (itemId == null) {
            return "未知项目";
        }
        NurseContent content = nurseContentDao.findById(itemId);
        return content != null ? content.getNursingName() : "未知项目";
    }

    /**
     * 格式化日期
     */
    private String formatDate(Date date) {
        if (date == null) {
            return "无";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
