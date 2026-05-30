package com.neu.tms.view;

import com.neu.tms.dao.CustomerDao;
import com.neu.tms.dao.NurseContentDao;
import com.neu.tms.dao.NurseLevelDao;
import com.neu.tms.dao.NurseRecordDao;
import com.neu.tms.pojo.Customer;
import com.neu.tms.pojo.NurseContent;
import com.neu.tms.pojo.NurseLevel;
import com.neu.tms.pojo.NurseRecord;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class AdminNurseRecordMenu implements IMenu {
    private CustomerDao customerDao = new CustomerDao();
    private NurseRecordDao nurseRecordDao = new NurseRecordDao();
    private NurseContentDao nurseContentDao = new NurseContentDao();

    @Override
    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========护理记录管理==========");
            System.out.println("1. 查询客户信息列表");
            System.out.println("0. 返回上一级");
            System.out.println("===============================");
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
     * 查询客户信息列表
     */
    private void searchCustomers(Scanner sc) {
        System.out.print("请输入客户姓名（直接回车查询全部）：");
        sc.nextLine();
        String name = sc.nextLine();

        List<Customer> customers;
        if (name.trim().isEmpty()) {
            customers = customerDao.findAll();
        } else {
            customers = customerDao.findByNameLike(name);
        }

        if (customers.isEmpty()) {
            System.out.println("未找到客户信息");
            return;
        }

        System.out.println("\n=== 客户信息列表 ===");
        System.out.printf("%-4s %-10s %-6s %-6s %-15s %-12s\n", 
                "ID", "姓名", "年龄", "性别", "联系电话", "护理级别");
        System.out.println("----------------------------------------------------------------------------------------");

        for (Customer c : customers) {
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

        System.out.println("\n共查询到 " + customers.size() + " 条记录");

        System.out.print("\n请选择客户查看详情（输入序号，0返回）：");
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
            showAndManageNurseRecords(selectedCustomer, sc);
        } catch (Exception e) {
            System.out.println("输入无效，请输入数字");
            sc.next();
        }
    }

    /**
     * 显示并管理客户的护理记录
     */
    private void showAndManageNurseRecords(Customer customer, Scanner sc) {
        List<NurseRecord> records = nurseRecordDao.findByCustomerId(customer.getId());

        if (records.isEmpty()) {
            System.out.println("\n该客户暂无护理记录");
            return;
        }

        System.out.println("\n=== " + customer.getCustomerName() + " 的护理记录 ===");
        System.out.printf("%-4s %-20s %-15s %-8s %-30s\n", 
                "ID", "护理时间", "项目名称", "数量", "护理内容");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < records.size(); i++) {
            NurseRecord record = records.get(i);
            String projectName = getProjectName(record.getItemId());
            String timeStr = sdf.format(record.getNursingTime());
            
            System.out.printf("%-4d %-20s %-15s %-8d %-30s\n",
                    record.getId(),
                    timeStr,
                    projectName,
                    record.getNursingCount(),
                    record.getNursingContent() != null ? record.getNursingContent() : "");
        }

        System.out.println("\n共 " + records.size() + " 条记录");

        System.out.print("\n是否要移除某条记录？（输入序号，0返回）：");
        try {
            int recordIndex = sc.nextInt();
            if (recordIndex == 0) {
                return;
            }

            if (recordIndex < 1 || recordIndex > records.size()) {
                System.out.println("无效选择");
                return;
            }

            NurseRecord selectedRecord = records.get(recordIndex - 1);

            System.out.print("\n确认要移除此护理记录吗？此操作将隐藏该记录（y/n）：");
            sc.nextLine();
            String confirm = sc.nextLine();
            
            if ("y".equalsIgnoreCase(confirm)) {
                boolean result = nurseRecordDao.deleteById(selectedRecord.getId());
                if (result) {
                    System.out.println("移除成功");
                } else {
                    System.out.println("移除失败");
                }
            } else {
                System.out.println("已取消移除");
            }
        } catch (Exception e) {
            System.out.println("输入无效，请输入数字");
            sc.next();
        }
    }

    /**
     * 获取护理级别信息
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
     * 获取护理项目名称
     */
    private String getProjectName(Integer itemId) {
        if (itemId == null) {
            return "未知项目";
        }
        NurseContent content = nurseContentDao.findById(itemId);
        return content != null ? content.getNursingName() : "未知项目";
    }
}
