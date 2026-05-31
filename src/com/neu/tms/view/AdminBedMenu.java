package com.neu.tms.view;

import com.neu.tms.dao.BackDownDao;
import com.neu.tms.dao.BedDao;
import com.neu.tms.dao.BedDetailsDao;
import com.neu.tms.dao.CustomerDao;
import com.neu.tms.pojo.BackDown;
import com.neu.tms.pojo.Bed;
import com.neu.tms.pojo.BedDetails;
import com.neu.tms.pojo.Customer;
import com.neu.tms.service.CustomerService;
import com.neu.tms.utils.PersistentIdGenerator;

import java.text.SimpleDateFormat;
import java.util.*;

public class AdminBedMenu implements IMenu {
    private BedDao bedDao = new BedDao();
    private BedDetailsDao bedDetailsDao = new BedDetailsDao();
    private CustomerDao customerDao = new CustomerDao();
    private BackDownDao backDownDao = new BackDownDao();
    private CustomerService customerService = new CustomerService();

    @Override
    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========床位管理==========");
            System.out.println("1. 床位示意图");
            System.out.println("2. 床位管理");
            System.out.println("3. 床位调换");
            System.out.println("4. 添加床位");
            System.out.println("0. 返回上一级");
            System.out.println("===========================");
            System.out.print("请选择：");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    showBedDiagram();
                    break;
                case 2:
                    manageBed(sc);
                    break;
                case 3:
                    swapBed(sc);
                    break;
                case 4:
                    addBed(sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }

    /**
     * 床位示意图
     */
    private void showBedDiagram() {
        System.out.println("\n==========床位示意图==========");
        
        List<Bed> allBeds = bedDao.findAll();
        
        if (allBeds.isEmpty()) {
            System.out.println("暂无床位数据");
            return;
        }

        int[] stats = bedDao.getBedStatistics();
        System.out.println("床位统计：总计 " + stats[0] + " | 空闲 " + stats[1] + " | 有人 " + stats[2] + " | 外出 " + stats[3]);
        System.out.println("\n图例：[空闲] [有人] [外出]");
        System.out.println("===========================================");

        Scanner sc = new Scanner(System.in);
        System.out.print("请输入楼层号（直接回车显示第1层）：");
        String floorInput = sc.nextLine();
        
        Integer floor = 1;
        if (!floorInput.isEmpty()) {
            try {
                floor = Integer.parseInt(floorInput);
            } catch (NumberFormatException e) {
                System.out.println("输入的楼层号无效，默认显示第1层");
                floor = 1;
            }
        }

        Map<Integer, List<Bed>> roomMap = new TreeMap<>();
        for (Bed bed : allBeds) {
            if (bed.getRoomNo() != null) {
                Integer roomFloor = getFloorFromRoomNo(bed.getRoomNo());
                if (roomFloor.equals(floor)) {
                    roomMap.computeIfAbsent(bed.getRoomNo(), k -> new ArrayList<>()).add(bed);
                }
            }
        }

        if (roomMap.isEmpty()) {
            System.out.println("第 " + floor + " 层暂无房间数据");
            return;
        }

        System.out.println("\n【第 " + floor + " 层】");
        System.out.println("===========================================");
        
        for (Map.Entry<Integer, List<Bed>> entry : roomMap.entrySet()) {
            Integer roomNo = entry.getKey();
            List<Bed> beds = entry.getValue();
            
            System.out.println("\n【房间 " + roomNo + "】");
            for (Bed bed : beds) {
                String status = "";
                switch (bed.getBedStatus()) {
                    case 1: status = "[空闲]"; break;
                    case 2: status = "[有人]"; break;
                    case 3: status = "[外出]"; break;
                    default: status = "[未知]";
                }
                
                String customerInfo = "";
                if (bed.getBedStatus() == 2 || bed.getBedStatus() == 3) {
                    Customer customer = findCustomerByBedId(bed.getId());
                    if (customer != null) {
                        customerInfo = " - " + customer.getCustomerName();
                    }
                }
                
                System.out.println("  床位" + bed.getBedNo() + " " + status + customerInfo);
            }
        }
    }

    /**
     * 床位管理
     */
    private void manageBed(Scanner sc) {
        while (true) {
            System.out.println("\n==========床位管理==========");
            System.out.println("1. 查询所有床位");
            System.out.println("2. 查询空闲床位");
            System.out.println("3. 按房间查询床位");
            System.out.println("4. 查看床位详情");
            System.out.println("5. 修改床位状态");
            System.out.println("6. 删除床位");
            System.out.println("0. 返回上一级");
            System.out.println("===========================");
            System.out.print("请选择：");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    listAllBeds();
                    break;
                case 2:
                    listAvailableBeds();
                    break;
                case 3:
                    listBedsByRoom(sc);
                    break;
                case 4:
                    viewBedDetails(sc);
                    break;
                case 5:
                    updateBedStatus(sc);
                    break;
                case 6:
                    deleteBed(sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }

    /**
     * 列出所有床位
     */
    private void listAllBeds() {
        List<Bed> beds = bedDao.findAll();
        
        if (beds.isEmpty()) {
            System.out.println("暂无床位数据");
            return;
        }

        System.out.println("\n=== 所有床位列表 ===");
        System.out.printf("%-6s %-10s %-8s %-10s %-15s\n", "ID", "房间号", "床位号", "状态", "备注");
        System.out.println("--------------------------------------------------------------");
        
        for (Bed bed : beds) {
            String status = "";
            switch (bed.getBedStatus()) {
                case 1: status = "空闲"; break;
                case 2: status = "有人"; break;
                case 3: status = "外出"; break;
                default: status = "未知";
            }
            
            System.out.printf("%-6d %-10d %-8s %-10s %-15s\n",
                    bed.getId(),
                    bed.getRoomNo(),
                    bed.getBedNo(),
                    status,
                    bed.getRemarks() != null ? bed.getRemarks() : "");
        }
    }

    /**
     * 列出空闲床位
     */
    private void listAvailableBeds() {
        List<Bed> beds = bedDao.findAvailableBeds();
        
        if (beds.isEmpty()) {
            System.out.println("暂无空闲床位");
            return;
        }

        System.out.println("\n=== 空闲床位列表 ===");
        System.out.printf("%-6s %-10s %-8s\n", "ID", "房间号", "床位号");
        System.out.println("------------------------------------");
        
        for (Bed bed : beds) {
            System.out.printf("%-6d %-10d %-8s\n",
                    bed.getId(),
                    bed.getRoomNo(),
                    bed.getBedNo());
        }
    }

    /**
     * 按房间查询床位
     */
    private void listBedsByRoom(Scanner sc) {
        System.out.print("请输入房间号：");
        Integer roomNo = sc.nextInt();
        
        List<Bed> beds = bedDao.findByRoomNo(roomNo);
        
        if (beds.isEmpty()) {
            System.out.println("该房间暂无床位");
            return;
        }

        System.out.println("\n=== 房间 " + roomNo + " 的床位 ===");
        System.out.printf("%-6s %-8s %-10s %-15s\n", "ID", "床位号", "状态", "备注");
        System.out.println("----------------------------------------------------");
        
        for (Bed bed : beds) {
            String status = "";
            switch (bed.getBedStatus()) {
                case 1: status = "空闲"; break;
                case 2: status = "有人"; break;
                case 3: status = "外出"; break;
                default: status = "未知";
            }
            
            System.out.printf("%-6d %-8s %-10s %-15s\n",
                    bed.getId(),
                    bed.getBedNo(),
                    status,
                    bed.getRemarks() != null ? bed.getRemarks() : "");
        }
    }

    /**
     * 查看床位详情
     */
    private void viewBedDetails(Scanner sc) {
        System.out.print("请输入床位ID：");
        Integer bedId = sc.nextInt();
        
        Bed bed = bedDao.findById(bedId);
        if (bed == null) {
            System.out.println("未找到该床位");
            return;
        }

        String status = "";
        switch (bed.getBedStatus()) {
            case 1: status = "空闲"; break;
            case 2: status = "有人"; break;
            case 3: status = "外出"; break;
            default: status = "未知";
        }

        System.out.println("\n=== 床位详情 ===");
        System.out.println("床位ID：" + bed.getId());
        System.out.println("房间号：" + bed.getRoomNo());
        System.out.println("床位号：" + bed.getBedNo());
        System.out.println("状态：" + status);
        System.out.println("备注：" + (bed.getRemarks() != null ? bed.getRemarks() : "无"));

        if (bed.getBedStatus() == 2 || bed.getBedStatus() == 3) {
            Customer customer = findCustomerByBedId(bedId);
            if (customer != null) {
                System.out.println("\n--- 入住客户信息 ---");
                System.out.println("姓名：" + customer.getCustomerName());
                System.out.println("年龄：" + customer.getCustomerAge());
                System.out.println("联系电话：" + customer.getContactTel());
                System.out.println("入住时间：" + formatDate(customer.getCheckinDate()));
            }

            List<BedDetails> details = bedDetailsDao.findByBedId(bedId);
            if (!details.isEmpty()) {
                System.out.println("\n--- 床位使用记录 ---");
                for (BedDetails detail : details) {
                    System.out.println("开始时间：" + formatDate(detail.getStartDate()) + 
                            " | 结束时间：" + formatDate(detail.getEndDate()) +
                            " | 备注：" + detail.getBedDetails());
                }
            }
        }
    }

    /**
     * 修改床位状态
     */
    private void updateBedStatus(Scanner sc) {
        System.out.print("请输入床位ID：");
        Integer bedId = sc.nextInt();
        
        Bed bed = bedDao.findById(bedId);
        if (bed == null) {
            System.out.println("未找到该床位");
            return;
        }

        System.out.println("当前状态：" + getBedStatusText(bed.getBedStatus()));
        System.out.println("\n请选择新状态：");
        System.out.println("1. 空闲");
        System.out.println("2. 有人");
        System.out.println("3. 外出");
        System.out.print("请选择：");
        
        int newStatus = sc.nextInt();
        if (newStatus < 1 || newStatus > 3) {
            System.out.println("无效的状态值");
            return;
        }

        bed.setBedStatus(newStatus);
        boolean result = bedDao.updateBed(bed);
        
        if (result) {
            System.out.println("床位状态更新成功");
        } else {
            System.out.println("床位状态更新失败");
        }
    }

    /**
     * 删除床位
     */
    private void deleteBed(Scanner sc) {
        System.out.println("\n==========删除床位==========");
        
        System.out.print("请输入要删除的床位ID：");
        Integer bedId = sc.nextInt();
        
        Bed bed = bedDao.findById(bedId);
        if (bed == null) {
            System.out.println("未找到该床位");
            return;
        }

        System.out.println("\n=== 床位信息 ===");
        System.out.println("床位ID：" + bed.getId());
        System.out.println("房间号：" + bed.getRoomNo());
        System.out.println("床位号：" + bed.getBedNo());
        System.out.println("状态：" + getBedStatusText(bed.getBedStatus()));
        System.out.println("备注：" + (bed.getRemarks() != null ? bed.getRemarks() : "无"));

        if (bed.getBedStatus() == 2) {
            Customer customer = findCustomerByBedId(bedId);
            if (customer != null) {
                System.out.println("\n警告：该床位正在被使用！");
                System.out.println("入住客户：" + customer.getCustomerName());
                System.out.println("请先办理客户退住或调换床位后再删除。");
                System.out.println("\n是否继续删除？（y/n）：");
                String confirm = sc.next();
                if (!"y".equalsIgnoreCase(confirm)) {
                    System.out.println("已取消删除");
                    return;
                }
            }
        }

        System.out.print("\n确认删除该床位？（y/n）：");
        String confirm = sc.next();
        
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("已取消删除");
            return;
        }

        try {
            boolean result = bedDao.deleteById(bedId);
            if (result) {
                System.out.println("床位删除成功！");
            } else {
                System.out.println("床位删除失败");
            }
        } catch (Exception e) {
            System.out.println("删除床位失败：" + e.getMessage());
        }
    }

    /**
     * 床位调换
     */
    private void swapBed(Scanner sc) {
        System.out.println("\n==========床位调换==========");
        
        System.out.print("请输入客户姓名：");
        String customerName = sc.next();
        
        List<Customer> customers = customerDao.findByNameLike(customerName);
        if (customers.isEmpty()) {
            System.out.println("未找到该客户");
            return;
        }

        System.out.println("\n=== 匹配的客户 ===");
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            System.out.println((i + 1) + ". " + c.getCustomerName() + 
                    " | 当前房间：" + c.getRoomNo() + 
                    " | 当前床位：" + c.getBedId());
        }

        System.out.print("\n请选择客户（输入序号）：");
        int customerIndex = sc.nextInt();
        if (customerIndex < 1 || customerIndex > customers.size()) {
            System.out.println("无效选择");
            return;
        }

        Customer selectedCustomer = customers.get(customerIndex - 1);
        
        if (selectedCustomer.getBedId() == null) {
            System.out.println("该客户没有分配床位");
            return;
        }

        Bed currentBed = bedDao.findById(selectedCustomer.getBedId());
        System.out.println("\n当前床位信息：");
        System.out.println("房间号：" + currentBed.getRoomNo());
        System.out.println("床位号：" + currentBed.getBedNo());

        System.out.println("\n=== 可调配的空闲床位 ===");
        List<Bed> availableBeds = bedDao.findAvailableBeds();
        
        if (availableBeds.isEmpty()) {
            System.out.println("暂无空闲床位");
            return;
        }

        for (int i = 0; i < availableBeds.size(); i++) {
            Bed b = availableBeds.get(i);
            System.out.println((i + 1) + ". 房间：" + b.getRoomNo() + 
                    " | 床位号：" + b.getBedNo() + 
                    " | 床位ID：" + b.getId());
        }

        System.out.print("\n请选择目标床位（输入序号）：");
        int bedIndex = sc.nextInt();
        if (bedIndex < 1 || bedIndex > availableBeds.size()) {
            System.out.println("无效选择");
            return;
        }

        Bed targetBed = availableBeds.get(bedIndex - 1);
        
        System.out.println("\n确认调换信息：");
        System.out.println("客户：" + selectedCustomer.getCustomerName());
        System.out.println("从：" + currentBed.getRoomNo() + "房间 " + currentBed.getBedNo() + "床位");
        System.out.println("到：" + targetBed.getRoomNo() + "房间 " + targetBed.getBedNo() + "床位");
        System.out.print("\n确认调换？（y/n）：");
        String confirm = sc.next();
        
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("已取消调换");
            return;
        }

        try {
            Date now = new Date();
            
            BedDetails oldDetail = bedDetailsDao.findCurrentByCustomerId(selectedCustomer.getId());
            if (oldDetail != null) {
                oldDetail.setEndDate(now);
                bedDetailsDao.updateBedDetails(oldDetail);
            }

            currentBed.setBedStatus(1);
            bedDao.updateBed(currentBed);

            selectedCustomer.setBedId(targetBed.getId());
            selectedCustomer.setRoomNo(String.valueOf(targetBed.getRoomNo()));
            customerDao.updateCustomer(selectedCustomer);

            targetBed.setBedStatus(2);
            bedDao.updateBed(targetBed);

            BedDetails newDetail = new BedDetails();
            newDetail.setCustomerId(selectedCustomer.getId());
            newDetail.setBedId(targetBed.getId());
            newDetail.setStartDate(now);
            newDetail.setEndDate(null);
            newDetail.setBedDetails("床位调换");
            newDetail.setIsDeleted(0);
            
            bedDetailsDao.addBedDetails(newDetail);

            System.out.println("床位调换成功！");
        } catch (Exception e) {
            System.out.println("床位调换失败：" + e.getMessage());
        }
    }

    /**
     * 添加床位
     */
    private void addBed(Scanner sc) {
        System.out.println("\n==========添加床位==========");
        
        System.out.print("请输入房间号：");
        Integer roomNo = sc.nextInt();
        
        System.out.print("请输入床位号（如：A、B、C）：");
        String bedNo = sc.next();
        
        System.out.print("请输入备注（可选，直接回车跳过）：");
        sc.nextLine();
        String remarks = sc.nextLine();
        
        Bed bed = new Bed();
        bed.setRoomNo(roomNo);
        bed.setBedNo(bedNo);
        bed.setBedStatus(1);
        bed.setRemarks(remarks.isEmpty() ? null : remarks);
        
        try {
            String result = bedDao.addBed(bed);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("添加床位失败：" + e.getMessage());
        }
    }

    private Customer findCustomerByBedId(Integer bedId) {
        List<Customer> customers = customerDao.findAll();
        for (Customer c : customers) {
            if (c.getBedId() != null && c.getBedId().equals(bedId)) {
                return c;
            }
        }
        return null;
    }

    private String getBedStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "空闲";
            case 2: return "有人";
            case 3: return "外出";
            default: return "未知";
        }
    }

    private String formatDate(Date date) {
        if (date == null) return "无";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 从房间号提取楼层号
     * 例如：101 -> 1, 201 -> 2, 606 -> 6
     * @param roomNo 房间号
     * @return 楼层号
     */
    private Integer getFloorFromRoomNo(Integer roomNo) {
        if (roomNo == null) {
            return 0;
        }
        return roomNo / 100;
    }

    /**
     * 根据客户姓名过滤退住记录
     * @param backDowns 所有退住记录
     * @param customerName 客户姓名（模糊匹配）
     * @return 过滤后的退住记录
     */
    private List<BackDown> filterBackDownsByCustomerName(List<BackDown> backDowns, String customerName) {
        List<BackDown> result = new ArrayList<>();
        for (BackDown backDown : backDowns) {
            Customer customer = customerService.findById(backDown.getCustomerId());
            if (customer != null && customer.getCustomerName() != null && 
                    customer.getCustomerName().contains(customerName)) {
                result.add(backDown);
            }
        }
        return result;
    }
}
