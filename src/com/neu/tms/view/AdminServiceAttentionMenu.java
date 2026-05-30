package com.neu.tms.view;

import com.neu.tms.dao.CustomerDao;
import com.neu.tms.dao.CustomerNurseItemDao;
import com.neu.tms.dao.NurseContentDao;
import com.neu.tms.pojo.Customer;
import com.neu.tms.pojo.CustomerNurseItem;
import com.neu.tms.pojo.NurseContent;

import java.text.SimpleDateFormat;
import java.util.*;

public class AdminServiceAttentionMenu implements IMenu {
    private CustomerDao customerDao = new CustomerDao();
    private CustomerNurseItemDao customerNurseItemDao = new CustomerNurseItemDao();
    private NurseContentDao nurseContentDao = new NurseContentDao();

    @Override
    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========服务关注==========");
            System.out.println("1. 查询客户信息列表");
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
        System.out.printf("%-4s %-10s %-6s %-6s %-15s\n", 
                "ID", "姓名", "年龄", "性别", "联系电话");
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

        System.out.println("\n共查询到 " + customers.size() + " 条记录");

        System.out.print("\n请选择客户进行操作（输入序号，0返回）：");
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
            manageCustomerServiceItems(selectedCustomer, sc);
        } catch (Exception e) {
            System.out.println("输入无效，请输入数字");
            sc.next();
        }
    }

    /**
     * 管理客户的服务项目
     */
    private void manageCustomerServiceItems(Customer customer, Scanner sc) {
        while (true) {
            System.out.println("\n==========客户：" + customer.getCustomerName() + " - 服务项目管理==========");
            System.out.println("1. 查询已购买的护理服务项目列表");
            System.out.println("2. 购买护理项目");
            System.out.println("0. 返回上一级");
            System.out.println("========================================================================");
            System.out.print("请选择：");

            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        viewAndManageServiceItems(customer, sc);
                        break;
                    case 2:
                        purchaseNurseItem(customer, sc);
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
     * 查看并管理客户已购买的护理服务项目
     */
    private void viewAndManageServiceItems(Customer customer, Scanner sc) {
        List<CustomerNurseItem> serviceItems = customerNurseItemDao.findByCustomerId(customer.getId());

        if (serviceItems.isEmpty()) {
            System.out.println("\n该客户暂无已购买的护理服务项目");
            return;
        }

        System.out.println("\n=== " + customer.getCustomerName() + " 的护理服务项目列表 ===");
        System.out.printf("%-4s %-15s %-8s %-12s %-12s %-10s\n", 
                "ID", "项目名称", "数量", "购买时间", "到期时间", "状态");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (int i = 0; i < serviceItems.size(); i++) {
            CustomerNurseItem item = serviceItems.get(i);
            String projectName = getProjectName(item.getItemId());
            String status = getServiceStatus(item, now);
            
            System.out.printf("%-4d %-15s %-8d %-12s %-12s %-10s\n",
                    item.getId(),
                    projectName,
                    item.getNurseNumber(),
                    sdf.format(item.getBuyTime()),
                    sdf.format(item.getMaturityTime()),
                    status);
        }

        System.out.println("\n共 " + serviceItems.size() + " 个项目");
        System.out.println("\n状态说明：");
        System.out.println("  - 数量正常：剩余次数 > 0 且未到期");
        System.out.println("  - 到期：已到到期时间");
        System.out.println("  - 欠费：剩余次数 <= 0");
        System.out.println("  - 未到期：未到到期时间且次数充足");

        System.out.print("\n请选择要操作的项目（输入序号进行续费或移除，0返回）：");
        try {
            int itemIndex = sc.nextInt();
            if (itemIndex == 0) {
                return;
            }

            if (itemIndex < 1 || itemIndex > serviceItems.size()) {
                System.out.println("无效选择");
                return;
            }

            CustomerNurseItem selectedItem = serviceItems.get(itemIndex - 1);
            performItemOperation(selectedItem, sc);
        } catch (Exception e) {
            System.out.println("输入无效，请输入数字");
            sc.next();
        }
    }

    /**
     * 获取服务状态
     */
    private String getServiceStatus(CustomerNurseItem item, Date now) {
        boolean isExpired = item.getMaturityTime() != null && now.after(item.getMaturityTime());
        boolean isLowCount = item.getNurseNumber() != null && item.getNurseNumber() <= 0;

        if (isExpired && isLowCount) {
            return "到期+欠费";
        } else if (isExpired) {
            return "到期";
        } else if (isLowCount) {
            return "欠费";
        } else {
            return "数量正常";
        }
    }

    /**
     * 执行项目操作（续费或移除）
     */
    private void performItemOperation(CustomerNurseItem item, Scanner sc) {
        System.out.println("\n==========项目操作==========");
        System.out.println("1. 续费");
        System.out.println("2. 移除");
        System.out.println("0. 返回");
        System.out.println("===========================");
        System.out.print("请选择：");

        try {
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    renewService(item, sc);
                    break;
                case 2:
                    removeService(item, sc);
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

    /**
     * 续费服务
     */
    private void renewService(CustomerNurseItem item, Scanner sc) {
        System.out.println("\n==========续费操作==========");
        
        String projectName = getProjectName(item.getItemId());
        System.out.println("项目名称：" + projectName);
        System.out.println("当前数量：" + item.getNurseNumber());
        System.out.println("当前到期时间：" + formatDate(item.getMaturityTime()));

        System.out.print("\n请输入新增数量：");
        int addNumber = sc.nextInt();
        
        if (addNumber <= 0) {
            System.out.println("新增数量必须大于0");
            return;
        }

        System.out.print("请输入新的到期时间（格式：yyyy-MM-dd，直接回车保持原日期）：");
        sc.nextLine();
        String dateStr = sc.nextLine();

        Date newMaturityTime = item.getMaturityTime();
        if (!dateStr.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                newMaturityTime = sdf.parse(dateStr);
            } catch (Exception e) {
                System.out.println("日期格式错误，请使用 yyyy-MM-dd 格式");
                return;
            }
        }

        int originalNumber = item.getNurseNumber();
        item.setNurseNumber(originalNumber + addNumber);
        item.setMaturityTime(newMaturityTime);

        System.out.println("\n确认续费信息：");
        System.out.println("项目名称：" + projectName);
        System.out.println("原有数量：" + originalNumber);
        System.out.println("新增数量：" + addNumber);
        System.out.println("总数量：" + item.getNurseNumber());
        System.out.println("新到期时间：" + formatDate(newMaturityTime));
        System.out.print("\n确认续费？（y/n）：");
        String confirm = sc.nextLine();

        if ("y".equalsIgnoreCase(confirm)) {
            boolean result = customerNurseItemDao.updateCustomerNurseItem(item);
            System.out.println(result ? "续费成功" : "续费失败");
        } else {
            System.out.println("已取消续费");
        }
    }

    /**
     * 移除服务
     */
    private void removeService(CustomerNurseItem item, Scanner sc) {
        System.out.println("\n==========移除操作==========");
        
        String projectName = getProjectName(item.getItemId());
        System.out.println("警告：此操作将永久移除该服务项目！");
        System.out.println("项目名称：" + projectName);
        System.out.println("当前数量：" + item.getNurseNumber());
        System.out.println("到期时间：" + formatDate(item.getMaturityTime()));
        System.out.println("\n移除后客户将不再享有对应的服务，请谨慎操作！");

        System.out.print("\n确认要移除此服务项目吗？（输入 y 确认）：");
        sc.nextLine();
        String confirm = sc.nextLine();

        if ("y".equalsIgnoreCase(confirm)) {
            boolean result = customerNurseItemDao.deleteById(item.getId());
            System.out.println(result ? "移除成功" : "移除失败");
        } else {
            System.out.println("已取消移除");
        }
    }

    /**
     * 购买护理项目
     */
    private void purchaseNurseItem(Customer customer, Scanner sc) {
        System.out.println("\n==========购买护理项目==========");
        
        System.out.print("请输入护理项目名称（直接回车查询全部）：");
        sc.nextLine();
        String itemName = sc.nextLine();

        List<NurseContent> allProjects;
        if (itemName.trim().isEmpty()) {
            allProjects = nurseContentDao.findAll();
        } else {
            allProjects = nurseContentDao.findByNameLike(itemName);
        }

        if (allProjects.isEmpty()) {
            System.out.println("未找到护理项目");
            return;
        }

        List<CustomerNurseItem> existingItems = customerNurseItemDao.findByCustomerId(customer.getId());
        Set<Integer> existingItemIds = new HashSet<>();
        for (CustomerNurseItem existing : existingItems) {
            existingItemIds.add(existing.getItemId());
        }

        List<NurseContent> availableProjects = new ArrayList<>();
        for (NurseContent project : allProjects) {
            if (!existingItemIds.contains(project.getId())) {
                availableProjects.add(project);
            }
        }

        if (availableProjects.isEmpty()) {
            System.out.println("该客户已拥有所有可用的护理项目，无需重复购买");
            return;
        }

        System.out.println("\n=== 可购买的护理项目列表 ===");
        System.out.printf("%-4s %-15s %-12s %-15s\n", "ID", "项目名称", "服务价格", "执行周期");
        System.out.println("--------------------------------------------------------------");

        for (NurseContent project : availableProjects) {
            System.out.printf("%-4d %-15s %-12s %-15s\n",
                    project.getId(),
                    project.getNursingName(),
                    project.getServicePrice(),
                    project.getExecutionCycle());
        }

        System.out.println("\n共 " + availableProjects.size() + " 个可购买项目");

        System.out.print("\n请选择要购买的项目（输入序号，0返回）：");
        try {
            int projectIndex = sc.nextInt();
            if (projectIndex == 0) {
                return;
            }

            if (projectIndex < 1 || projectIndex > availableProjects.size()) {
                System.out.println("无效选择");
                return;
            }

            NurseContent selectedProject = availableProjects.get(projectIndex - 1);

            System.out.println("\n==========添加到已选护理项目==========");
            System.out.println("项目名称：" + selectedProject.getNursingName());
            System.out.println("服务价格：" + selectedProject.getServicePrice());

            System.out.print("请输入数量（默认1）：");
            int quantity = 1;
            String quantityInput = sc.next();
            if (!quantityInput.trim().isEmpty()) {
                try {
                    quantity = Integer.parseInt(quantityInput);
                    if (quantity <= 0) {
                        System.out.println("数量必须大于0，使用默认值1");
                        quantity = 1;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("输入无效，使用默认值1");
                }
            }

            System.out.print("请输入服务到期时间（格式：yyyy-MM-dd，直接回车使用当前日期）：");
            sc.nextLine();
            String dateStr = sc.nextLine();

            Date maturityTime;
            if (dateStr.trim().isEmpty()) {
                maturityTime = new Date();
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setLenient(false);
                    maturityTime = sdf.parse(dateStr);
                } catch (Exception e) {
                    System.out.println("日期格式错误，使用当前日期");
                    maturityTime = new Date();
                }
            }

            CustomerNurseItem newItem = new CustomerNurseItem();
            newItem.setItemId(selectedProject.getId());
            newItem.setCustomerId(customer.getId());
            newItem.setLevelId(customer.getLevelId());
            newItem.setNurseNumber(quantity);
            newItem.setBuyTime(new Date());
            newItem.setMaturityTime(maturityTime);

            System.out.println("\n确认购买信息：");
            System.out.println("客户：" + customer.getCustomerName());
            System.out.println("项目：" + selectedProject.getNursingName());
            System.out.println("数量：" + quantity);
            System.out.println("购买时间：" + formatDate(new Date()));
            System.out.println("到期时间：" + formatDate(maturityTime));
            System.out.print("\n确认购买？（y/n）：");
            String confirm = sc.nextLine();

            if ("y".equalsIgnoreCase(confirm)) {
                String result = customerNurseItemDao.addCustomerNurseItem(newItem);
                System.out.println(result);
            } else {
                System.out.println("已取消购买");
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
