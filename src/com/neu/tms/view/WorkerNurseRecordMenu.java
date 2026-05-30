package com.neu.tms.view;

import com.neu.tms.dao.CustomerDao;
import com.neu.tms.dao.NurseContentDao;
import com.neu.tms.dao.NurseRecordDao;
import com.neu.tms.pojo.Customer;
import com.neu.tms.pojo.NurseContent;
import com.neu.tms.pojo.NurseRecord;
import com.neu.tms.pojo.TUser;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class WorkerNurseRecordMenu implements IMenu {
    private TUser currentUser;
    private CustomerDao customerDao = new CustomerDao();
    private NurseRecordDao nurseRecordDao = new NurseRecordDao();
    private NurseContentDao nurseContentDao = new NurseContentDao();

    public WorkerNurseRecordMenu(TUser user) {
        this.currentUser = user;
    }

    @Override
    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========服务对象护理记录==========");
            System.out.println("1. 查询自己服务的客户信息列表");
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
     * 查询自己服务的客户信息列表
     */
    private void searchCustomers(Scanner sc) {
        System.out.print("请输入客户姓名（直接回车查询全部）：");
        sc.nextLine();
        String name = sc.nextLine();

        // 只查询当前健康管家服务的客户
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

        System.out.print("\n请选择客户查看护理记录（输入序号，0返回）：");
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
            showNurseRecords(selectedCustomer, sc);
        } catch (Exception e) {
            System.out.println("输入无效，请输入数字");
            sc.next();
        }
    }

    /**
     * 显示客户的护理记录
     */
    private void showNurseRecords(Customer customer, Scanner sc) {
        // 查询该客户的所有护理记录（包括已隐藏的）
        List<NurseRecord> allRecords = nurseRecordDao.findByCustomerId(customer.getId());

        if (allRecords.isEmpty()) {
            System.out.println("\n该客户暂无护理记录");
            return;
        }

        System.out.println("\n=== " + customer.getCustomerName() + " 的护理记录 ===");
        System.out.printf("%-4s %-15s %-18s %-8s %-20s %-10s\n", 
                "ID", "项目名称", "护理时间", "数量", "护理内容", "状态");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (int i = 0; i < allRecords.size(); i++) {
            NurseRecord record = allRecords.get(i);
            String projectName = getProjectName(record.getItemId());
            String status = record.getIsDeleted() != null && record.getIsDeleted() == 1 ? "已隐藏" : "正常";
            
            System.out.printf("%-4d %-15s %-18s %-8d %-20s %-10s\n",
                    record.getId(),
                    projectName,
                    sdf.format(record.getNursingTime()),
                    record.getNursingCount(),
                    record.getNursingContent().length() > 20 ? 
                        record.getNursingContent().substring(0, 20) + "..." : record.getNursingContent(),
                    status);
        }

        System.out.println("\n共 " + allRecords.size() + " 条记录");

        System.out.print("\n是否要移除(隐藏)护理记录？(y/n)：");
        String choice = sc.next();
        if ("y".equalsIgnoreCase(choice)) {
            removeNurseRecord(allRecords, sc);
        }
    }

    /**
     * 移除(隐藏)护理记录
     */
    private void removeNurseRecord(List<NurseRecord> records, Scanner sc) {
        System.out.print("请选择要移除的记录序号（输入0取消）：");
        try {
            int recordIndex = sc.nextInt();
            if (recordIndex == 0) {
                System.out.println("已取消操作");
                return;
            }

            if (recordIndex < 1 || recordIndex > records.size()) {
                System.out.println("无效选择");
                return;
            }

            NurseRecord selectedRecord = records.get(recordIndex - 1);
            
            if (selectedRecord.getIsDeleted() != null && selectedRecord.getIsDeleted() == 1) {
                System.out.println("该记录已被隐藏");
                return;
            }

            System.out.println("\n确认移除以下护理记录？");
            System.out.println("护理时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(selectedRecord.getNursingTime()));
            System.out.println("项目名称：" + getProjectName(selectedRecord.getItemId()));
            System.out.println("护理内容：" + selectedRecord.getNursingContent());
            
            System.out.print("\n此操作不可恢复，是否继续？(y/n)：");
            String confirm = sc.next();
            
            if ("y".equalsIgnoreCase(confirm)) {
                // 逻辑删除：设置 isDeleted = 1
                selectedRecord.setIsDeleted(1);
                boolean result = nurseRecordDao.updateNurseRecord(selectedRecord);
                
                if (result) {
                    System.out.println("✓ 护理记录已隐藏");
                } else {
                    System.out.println("✗ 操作失败");
                }
            } else {
                System.out.println("已取消操作");
            }
        } catch (Exception e) {
            System.out.println("输入无效，请输入数字");
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
}
