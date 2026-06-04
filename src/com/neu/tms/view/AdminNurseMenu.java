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

    @Override
    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========护理管理==========");
            System.out.println("1. 护理级别管理");
            System.out.println("2. 护理项目管理");
            System.out.println("3. 客户护理设置");
            System.out.println("4. 护理记录管理");
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
                        manageNurseRecord(sc);
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

    /**
     * 护理记录管理
     */
    private void manageNurseRecord(Scanner sc) {
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
                        searchCustomersForRecord(sc);
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
     * 查询客户信息列表（护理记录用）
     */
    private void searchCustomersForRecord(Scanner sc) {
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