package com.neu.tms.view;

import com.neu.tms.dao.BackDownDao;
import com.neu.tms.dao.OutwardDao;
import com.neu.tms.pojo.BackDown;
import com.neu.tms.pojo.Customer;
import com.neu.tms.pojo.Outward;
import com.neu.tms.service.CustomerService;
import com.neu.tms.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class AdminCustomerMenu implements IMenu {
    private CustomerService customerService = new CustomerService();
    private BackDownDao backDownDao = new BackDownDao();
    private OutwardDao outwardDao = new OutwardDao();

    @Override
    public void execute() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==========客户管理==========");
            System.out.println("1. 入住登记");
            System.out.println("2. 查询客户");
            System.out.println("3. 修改客户");
            System.out.println("4. 删除客户");
            System.out.println("5. 退住审批");
            System.out.println("6. 外出审批");
            System.out.println("0. 返回上一级");
            System.out.println("===========================");
            System.out.print("请选择：");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    addCustomer(sc);
                    break;
                case 2:
                    searchCustomers(sc);
                    break;
                case 3:
                    updateCustomer(sc);
                    break;
                case 4:
                    deleteCustomer(sc);
                    break;
                case 5:
                    auditBackDown(sc);
                    break;
                case 6:
                    auditOutward(sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }

    private void addCustomer(Scanner sc) {
        System.out.println("\n==========入住登记==========");
        
        com.neu.tms.dao.BedDao bedDao = new com.neu.tms.dao.BedDao();
        List<com.neu.tms.pojo.Bed> availableBeds = bedDao.findAvailableBeds();
        
        if (availableBeds.isEmpty()) {
            System.out.println("当前没有空闲床位，无法办理入住！");
            return;
        }
        
        System.out.println("\n=== 当前可用床位 ===");
        System.out.printf("%-10s %-10s %-10s\n", "床位ID", "房间号", "床位号");
        System.out.println("------------------------------");
        for (com.neu.tms.pojo.Bed bed : availableBeds) {
            System.out.printf("%-10d %-10s %-10s\n", 
                    bed.getId(), 
                    bed.getRoomNo().toString(),
                    bed.getBedNo());
        }
        System.out.println();
        
        sc.nextLine();
        System.out.print("请输入客户姓名：");
        String customerName = sc.nextLine();

        // 身份证号输入（验证失败时重新输入）
        String idCard = null;
        while (idCard == null) {
            System.out.print("请输入身份证号：");
            idCard = sc.nextLine();
            
            if (!validateIdCard(idCard)) {
                System.out.println("身份证号格式不正确，请重新输入！");
                idCard = null;
            }
        }

        // 从身份证号提取出生日期和性别
        Date birthday = parseBirthdayFromIdCard(idCard);
        Integer customerSex = parseSexFromIdCard(idCard);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("从身份证号解析出出生日期：" + sdf.format(birthday));

        Integer customerAge = calculateAge(birthday);
        System.out.println("根据出生日期计算年龄：" + customerAge + " 岁");

        String sexStr = customerSex == 1 ? "男" : "女";
        System.out.println("从身份证号解析出性别：" + sexStr);

        System.out.print("请输入血型：");
        String bloodType = sc.nextLine();

        System.out.print("请输入家属成员：");
        String familyMember = sc.nextLine();

        System.out.print("请输入联系电话：");
        String contactTel = sc.nextLine();

        System.out.print("请输入身高（cm）：");
        String height = sc.nextLine();

        System.out.print("请输入体重（kg）：");
        String weight = sc.nextLine();

        System.out.println("楼栋固定为：606");
        String buildingNo = "606";

        // 显示空闲房间列表
        System.out.println("\n=== 当前空闲房间列表 ===");
        List<String> availableRooms = availableBeds.stream()
                .map(bed -> bed.getRoomNo().toString())
                .distinct()
                .sorted()
                .toList();
        
        if (availableRooms.isEmpty()) {
            System.out.println("当前没有空闲房间！");
            return;
        }
        
        System.out.println("可用房间号：");
        for (int i = 0; i < availableRooms.size(); i++) {
            System.out.println((i + 1) + ". " + availableRooms.get(i));
        }
        
        System.out.print("\n请输入房间号（从上面列表中选择）：");
        String roomNo = sc.nextLine();
        
        // 验证房间号是否在可用列表中
        if (!availableRooms.contains(roomNo)) {
            System.out.println("房间号无效，请从可用房间列表中选择！");
            return;
        }

        // 显示该房间的空闲床位
        System.out.println("\n=== " + roomNo + " 房间的空闲床位 ===");
        List<com.neu.tms.pojo.Bed> roomBeds = availableBeds.stream()
                .filter(bed -> bed.getRoomNo().toString().equals(roomNo))
                .toList();
        
        System.out.printf("%-10s %-10s\n", "床位ID", "床位号");
        System.out.println("------------------");
        for (com.neu.tms.pojo.Bed bed : roomBeds) {
            System.out.printf("%-10d %-10s\n", bed.getId(), bed.getBedNo());
        }
        
        System.out.print("\n请输入床位号（从上面列表中选择）：");
        Integer bedId = Integer.parseInt(sc.nextLine());
        
        // 验证床位ID是否在该房间的可用床位列表中
        boolean bedValid = roomBeds.stream()
                .anyMatch(bed -> bed.getId().equals(bedId));
        
        if (!bedValid) {
            System.out.println("床位ID无效，请从该房间的可用床位列表中选择！");
            return;
        }

        System.out.print("请输入入住时间（yyyy-MM-dd）：");
        Date checkinDate = null;
        try {
            String checkinDateStr = sc.nextLine();
                    SimpleDateFormat sdfCheckin = new SimpleDateFormat("yyyy-MM-dd");
                    checkinDate = sdfCheckin.parse(checkinDateStr);
            checkinDate = sdf.parse(checkinDateStr);
        } catch (ParseException e) {
            System.out.println("日期格式错误");
            return;
        }

        System.out.print("请输入合同到期时间（yyyy-MM-dd）：");
        Date expirationDate = null;
        try {
            String expirationDateStr = sc.nextLine();
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            expirationDate = sdf2.parse(expirationDateStr);
        } catch (ParseException e) {
            System.out.println("日期格式错误");
            return;
        }

        if (expirationDate != null && checkinDate != null && expirationDate.before(checkinDate)) {
            System.out.println("合同到期时间不能小于入住时间");
            return;
        }

        System.out.print("请输入身心状态：");
        String psychosomaticState = sc.nextLine();

        System.out.print("请输入注意事项：");
        String attention = sc.nextLine();

        Customer customer = new Customer();
        customer.setCustomerName(customerName);
        customer.setBirthday(birthday);
        customer.setCustomerAge(customerAge);
        customer.setCustomerSex(customerSex);
        customer.setIdCard(idCard);
        customer.setBloodType(bloodType);
        customer.setFamilyMember(familyMember);
        customer.setContactTel(contactTel);
        customer.setHeight(height);
        customer.setWeight(weight);
        customer.setBuildingNo(buildingNo);
        customer.setRoomNo(roomNo);
        customer.setBedId(bedId);
        customer.setCheckinDate(checkinDate);
        customer.setExpirationDate(expirationDate);
        customer.setPsychosomaticState(psychosomaticState);
        customer.setAttention(attention);
        customer.setIsDeleted(0);
        customer.setUserId(-1);
        customer.setLevelId(null);

        String result = customerService.addCustomer(customer);
        System.out.println(result);
    }

    private void searchCustomers(Scanner sc) {
        System.out.println("\n==========查询客户==========");
        System.out.println("老人类型说明：");
        System.out.println("1. 自理老人：没有配置护理项目的客户");
        System.out.println("2. 护理老人：配置有护理项目的客户");
        System.out.println();
        
        System.out.print("请输入客户姓名（直接回车跳过）：");
        sc.nextLine();
        String name = sc.nextLine();
        
        System.out.print("请选择老人类型（0-全部 1-自理老人 2-护理老人，直接回车查全部）：");
        String typeInput = sc.nextLine();
        Integer elderlyType = typeInput.isEmpty() ? 0 : Integer.parseInt(typeInput);

        List<Customer> customers;
        
        if (name.isEmpty() && (elderlyType == 0 || elderlyType == null)) {
            customers = customerService.findAll();
        } else {
            customers = customerService.findByConditions(name, elderlyType);
        }

        if (customers.isEmpty()) {
            System.out.println("未找到客户信息");
            return;
        }

        System.out.println("\n=== 客户列表 ===");
        System.out.printf("%-4s %-10s %-6s %-6s %-15s %-12s %-10s %-10s %-15s\n", 
                "ID", "姓名", "年龄", "性别", "联系电话", "老人类型", "楼号", "房间", "床位ID");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        
        for (Customer c : customers) {
            String sex = (c.getCustomerSex() != null && c.getCustomerSex() == 1) ? "男" : "女";
            String elderlyTypeStr = getElderlyType(c);
            System.out.printf("%-4d %-10s %-6s %-6s %-15s %-12s %-10s %-10s %-15s\n",
                    c.getId() != null ? c.getId() : 0,
                    c.getCustomerName() != null ? c.getCustomerName() : "未知",
                    c.getCustomerAge() != null ? c.getCustomerAge().toString() : "未知",
                    sex,
                    c.getContactTel() != null ? c.getContactTel() : "未知",
                    elderlyTypeStr,
                    c.getBuildingNo() != null ? c.getBuildingNo() : "未知",
                    c.getRoomNo() != null ? c.getRoomNo() : "未知",
                    c.getBedId() != null ? c.getBedId().toString() : "未分配");
        }
        
        System.out.println("\n共查询到 " + customers.size() + " 条记录");
    }

    /**
     * 修改客户信息
     * 根据客户ID查找并显示当前信息，允许用户选择性更新字段（直接回车保持不变）
     *
     * @param sc 扫描器对象，用于接收用户输入
     */
    private void updateCustomer(Scanner sc) {
        System.out.println("\n==========修改客户==========");
        System.out.print("请输入客户ID：");
        Integer id = sc.nextInt();

        Customer customer = customerService.findById(id);
        if (customer == null) {
            System.out.println("未找到该客户");
            return;
        }

        System.out.println("当前客户信息：");
        System.out.println("姓名：" + customer.getCustomerName());
        System.out.println("年龄：" + customer.getCustomerAge());
        System.out.println("联系电话：" + customer.getContactTel());
        System.out.println("房间：" + customer.getBuildingNo() + "-" + customer.getRoomNo());
        System.out.println("床位ID：" + customer.getBedId());

        sc.nextLine();
        System.out.println("\n请输入新信息（直接回车保持不变）：");
        
        System.out.print("请输入客户姓名（当前：" + customer.getCustomerName() + "）：");
        String nameInput = sc.nextLine();
        String customerName = nameInput.isEmpty() ? customer.getCustomerName() : nameInput;

        System.out.print("请输入出生日期（yyyy-MM-dd，当前：" + formatDate(customer.getBirthday()) + "）：");
        String birthdayInput = sc.nextLine();
        Date birthday = customer.getBirthday();
        Integer customerAge = customer.getCustomerAge();
        if (!birthdayInput.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                birthday = sdf.parse(birthdayInput);
                customerAge = calculateAge(birthday);
            } catch (ParseException e) {
                System.out.println("日期格式错误，保持原值");
            }
        }

        System.out.print("请输入性别（0-女 1-男，当前：" + customer.getCustomerSex() + "）：");
        String sexInput = sc.nextLine();
        Integer customerSex = sexInput.isEmpty() ? customer.getCustomerSex() : Integer.parseInt(sexInput);

        System.out.print("请输入身份证号（当前：" + customer.getIdCard() + "）：");
        String idCardInput = sc.nextLine();
        String idCard = idCardInput.isEmpty() ? customer.getIdCard() : idCardInput;

        System.out.print("请输入血型（当前：" + customer.getBloodType() + "）：");
        String bloodTypeInput = sc.nextLine();
        String bloodType = bloodTypeInput.isEmpty() ? customer.getBloodType() : bloodTypeInput;

        System.out.print("请输入家属成员（当前：" + customer.getFamilyMember() + "）：");
        String familyMemberInput = sc.nextLine();
        String familyMember = familyMemberInput.isEmpty() ? customer.getFamilyMember() : familyMemberInput;

        System.out.print("请输入联系电话（当前：" + customer.getContactTel() + "）：");
        String telInput = sc.nextLine();
        String contactTel = telInput.isEmpty() ? customer.getContactTel() : telInput;

        System.out.print("请输入身高（cm，当前：" + customer.getHeight() + "）：");
        String heightInput = sc.nextLine();
        String height = heightInput.isEmpty() ? customer.getHeight() : heightInput;

        System.out.print("请输入体重（kg，当前：" + customer.getWeight() + "）：");
        String weightInput = sc.nextLine();
        String weight = weightInput.isEmpty() ? customer.getWeight() : weightInput;

        System.out.print("请输入房间号（当前：" + customer.getRoomNo() + "）：");
        String roomNoInput = sc.nextLine();
        String roomNo = roomNoInput.isEmpty() ? customer.getRoomNo() : roomNoInput;

        System.out.print("请输入床位号（当前：" + customer.getBedId() + "）：");
        String bedIdInput = sc.nextLine();
        Integer bedId = bedIdInput.isEmpty() ? customer.getBedId() : Integer.parseInt(bedIdInput);

        System.out.print("请输入入住时间（yyyy-MM-dd，当前：" + formatDate(customer.getCheckinDate()) + "）：");
        String checkinDateInput = sc.nextLine();
        Date checkinDate = customer.getCheckinDate();
        if (!checkinDateInput.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                checkinDate = sdf.parse(checkinDateInput);
            } catch (ParseException e) {
                System.out.println("日期格式错误，保持原值");
            }
        }

        System.out.print("请输入合同到期时间（yyyy-MM-dd，当前：" + formatDate(customer.getExpirationDate()) + "）：");
        String expirationDateInput = sc.nextLine();
        Date expirationDate = customer.getExpirationDate();
        if (!expirationDateInput.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                expirationDate = sdf.parse(expirationDateInput);
            } catch (ParseException e) {
                System.out.println("日期格式错误，保持原值");
            }
        }

        if (expirationDate != null && checkinDate != null && expirationDate.before(checkinDate)) {
            System.out.println("合同到期时间不能小于入住时间，保持原值");
            expirationDate = customer.getExpirationDate();
        }

        System.out.print("请输入身心状态（当前：" + customer.getPsychosomaticState() + "）：");
        String psychosomaticStateInput = sc.nextLine();
        String psychosomaticState = psychosomaticStateInput.isEmpty() ? customer.getPsychosomaticState() : psychosomaticStateInput;

        System.out.print("请输入注意事项（当前：" + customer.getAttention() + "）：");
        String attentionInput = sc.nextLine();
        String attention = attentionInput.isEmpty() ? customer.getAttention() : attentionInput;

        customer.setCustomerName(customerName);
        customer.setBirthday(birthday);
        customer.setCustomerAge(customerAge);
        customer.setCustomerSex(customerSex);
        customer.setIdCard(idCard);
        customer.setBloodType(bloodType);
        customer.setFamilyMember(familyMember);
        customer.setContactTel(contactTel);
        customer.setHeight(height);
        customer.setWeight(weight);
        customer.setRoomNo(roomNo);
        customer.setBedId(bedId);
        customer.setCheckinDate(checkinDate);
        customer.setExpirationDate(expirationDate);
        customer.setPsychosomaticState(psychosomaticState);
        customer.setAttention(attention);

        String result = customerService.updateCustomer(customer);
        System.out.println(result);
    }

    private void deleteCustomer(Scanner sc) {
        System.out.println("\n==========删除客户==========");
        System.out.print("请输入客户ID：");
        Integer id = sc.nextInt();

        Customer customer = customerService.findById(id);
        if (customer == null) {
            System.out.println("未找到该客户");
            return;
        }

        System.out.println("当前客户信息：");
        System.out.println("姓名：" + customer.getCustomerName());
        System.out.println("年龄：" + customer.getCustomerAge());

        System.out.print("\n确认要删除该客户吗？（y/n）：");
        String confirm = sc.next();
        
        if ("y".equalsIgnoreCase(confirm)) {
            String result = customerService.deleteCustomer(id);
            System.out.println(result);
        } else {
            System.out.println("已取消删除操作");
        }
    }

    private void auditBackDown(Scanner sc) {
        System.out.println("\n==========退住审批==========");
        
        System.out.print("请输入客户姓名进行查询（直接回车查询全部）：");
        sc.nextLine();
        String customerName = sc.nextLine();
        
        List<BackDown> allBackDowns = backDownDao.findAll();
        List<BackDown> backDowns;
        
        if (customerName.isEmpty()) {
            backDowns = allBackDowns;
        } else {
            backDowns = filterBackDownsByCustomerName(allBackDowns, customerName);
        }
        
        if (backDowns.isEmpty()) {
            System.out.println("暂无退住申请记录");
            return;
        }

        System.out.println("\n=== 退住申请列表 ===");
        for (int i = 0; i < backDowns.size(); i++) {
            BackDown b = backDowns.get(i);
            String type = getRetreatTypeStr(b.getRetreatType());
            String status = getAuditStatusStr(b.getAuditStatus());
            String customerInfo = getCustomerInfoById(b.getCustomerId());
            
            System.out.println((i + 1) + ". ID：" + b.getId() + 
                    " | 客户ID：" + b.getCustomerId() + 
                    " | 客户姓名：" + customerInfo +
                    " | 退住类型：" + type + 
                    " | 退住原因：" + b.getRetreatReason() +
                    " | 退住时间：" + formatDate(b.getRetreatTime()) + 
                    " | 审批状态：" + status);
        }

        System.out.print("\n请选择要审批的申请（输入序号，0返回）：");
        int choice = sc.nextInt();
        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > backDowns.size()) {
            System.out.println("无效选择");
            return;
        }

        BackDown selected = backDowns.get(choice - 1);
        
        if (selected.getAuditStatus() != 0) {
            System.out.println("该申请已经审批过了");
            return;
        }

        System.out.println("\n审批操作：1-同意 2-拒绝");
        System.out.print("请选择：");
        int auditChoice = sc.nextInt();

        if (auditChoice == 1) {
            selected.setAuditStatus(1);
            System.out.println("已同意该退住申请");
            
            Customer customer = customerService.findById(selected.getCustomerId());
            if (customer != null) {
                // 1. 从健康管家的服务列表中移除（清空userId）
                if (customer.getUserId() != null) {
                    customer.setUserId(null);
                    customerService.updateCustomer(customer);
                    System.out.println("已从健康管家的服务客户列表中移除该客户");
                }
                
                // 2. 逻辑删除客户
                customer.setIsDeleted(1);
                customerService.updateCustomer(customer);
                System.out.println("已将客户状态设置为已退住");
                
                // 3. 处理床位状态
                if (selected.getRetreatType() == 0 || selected.getRetreatType() == 1) {
                    if (customer.getBedId() != null) {
                        com.neu.tms.dao.BedDao bedDao = new com.neu.tms.dao.BedDao();
                        com.neu.tms.pojo.Bed bed = bedDao.findById(customer.getBedId());
                        if (bed != null) {
                            bed.setBedStatus(1);
                            bedDao.updateBed(bed);
                            System.out.println("已将床位 " + bed.getBedNo() + " 状态设置为空闲");
                        }
                    }
                } else if (selected.getRetreatType() == 2) {
                    System.out.println("保留床位类型，不修改床位状态");
                }
            }
        } else if (auditChoice == 2) {
            selected.setAuditStatus(2);
            System.out.println("已拒绝该退住申请");
        } else {
            System.out.println("无效选择");
            return;
        }

        selected.setAuditPerson(com.neu.tms.utils.SessionManager.getCurrentUserName());
        selected.setAuditTime(new Date());
        
        backDownDao.updateBackDown(selected);
        System.out.println("审批完成！");
    }

    private void auditOutward(Scanner sc) {
        System.out.println("\n==========外出审批==========");
        
        System.out.print("请输入客户姓名进行查询（直接回车查询全部）：");
        sc.nextLine();
        String customerName = sc.nextLine();
        
        List<Outward> allOutwards = outwardDao.findAll();
        List<Outward> outwards;
        
        if (customerName.isEmpty()) {
            outwards = allOutwards;
        } else {
            outwards = filterOutwardsByCustomerName(allOutwards, customerName);
        }
        
        if (outwards.isEmpty()) {
            System.out.println("暂无外出申请记录");
            return;
        }

        System.out.println("\n=== 外出申请列表 ===");
        for (int i = 0; i < outwards.size(); i++) {
            Outward o = outwards.get(i);
            String status = getAuditStatusStr(o.getAuditStatus());
            String customerInfo = getCustomerInfoById(o.getCustomerId());
            
            System.out.println((i + 1) + ". ID：" + o.getId() + 
                    " | 客户ID：" + o.getCustomerId() + 
                    " | 客户姓名：" + customerInfo +
                    " | 外出事由：" + o.getOutgoingReason() +
                    " | 外出时间：" + formatDate(o.getOutgoingTime()) + 
                    " | 预计返回：" + formatDate(o.getExpectedReturnTime()) +
                    " | 陪同人：" + o.getEscorted() +
                    " | 关系：" + o.getRelation() +
                    " | 联系电话：" + o.getEscortedTel() +
                    " | 审批状态：" + status);
        }

        System.out.print("\n请选择要审批的申请（输入序号，0返回）：");
        int choice = sc.nextInt();
        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > outwards.size()) {
            System.out.println("无效选择");
            return;
        }

        Outward selected = outwards.get(choice - 1);
        
        if (selected.getAuditStatus() != 0) {
            System.out.println("该申请已经审批过了");
            return;
        }

        System.out.println("\n审批操作：1-同意 2-拒绝");
        System.out.print("请选择：");
        int auditChoice = sc.nextInt();

        if (auditChoice == 1) {
            selected.setAuditStatus(1);
            System.out.println("已同意该外出申请");
            
            Customer customer = customerService.findById(selected.getCustomerId());
            if (customer != null && customer.getBedId() != null) {
                com.neu.tms.dao.BedDao bedDao = new com.neu.tms.dao.BedDao();
                com.neu.tms.pojo.Bed bed = bedDao.findById(customer.getBedId());
                if (bed != null) {
                    bed.setBedStatus(3);
                    bedDao.updateBed(bed);
                    System.out.println("已将床位 " + bed.getBedNo() + " 状态设置为外出");
                }
            }
        } else if (auditChoice == 2) {
            selected.setAuditStatus(2);
            System.out.println("已拒绝该外出申请");
        } else {
            System.out.println("无效选择");
            return;
        }

        selected.setAuditPerson(com.neu.tms.utils.SessionManager.getCurrentUserName());
        selected.setAuditTime(new Date());
        
        outwardDao.updateOutward(selected);
        System.out.println("审批完成！");
    }

    /**
     * 根据客户姓名过滤外出记录
     * @param outwards 所有外出记录
     * @param customerName 客户姓名（模糊匹配）
     * @return 过滤后的外出记录
     */
    private List<Outward> filterOutwardsByCustomerName(List<Outward> outwards, String customerName) {
        List<Outward> result = new ArrayList<>();
        for (Outward outward : outwards) {
            Customer customer = customerService.findById(outward.getCustomerId());
            if (customer != null && customer.getCustomerName() != null && 
                    customer.getCustomerName().contains(customerName)) {
                result.add(outward);
            }
        }
        return result;
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

    /**
     * 获取退住类型描述
     * @param retreatType 退住类型编码
     * @return 退住类型字符串
     */
    private String getRetreatTypeStr(Integer retreatType) {
        if (retreatType == null) {
            return "未知";
        }
        switch (retreatType) {
            case 0: return "正常退住";
            case 1: return "死亡退住";
            case 2: return "保留床位";
            default: return "未知";
        }
    }

    /**
     * 获取审批状态描述
     * @param auditStatus 审批状态编码
     * @return 审批状态字符串
     */
    private String getAuditStatusStr(Integer auditStatus) {
        if (auditStatus == null) {
            return "未知";
        }
        switch (auditStatus) {
            case 0: return "待审批";
            case 1: return "已通过";
            case 2: return "已拒绝";
            default: return "未知";
        }
    }

    /**
     * 根据客户ID获取客户信息
     * @param customerId 客户ID
     * @return 客户姓名，未找到返回"未知客户"
     */
    private String getCustomerInfoById(Integer customerId) {
        Customer customer = customerService.findById(customerId);
        if (customer != null) {
            return customer.getCustomerName();
        }
        return "未知客户(ID:" + customerId + ")";
    }

    /**
     * 获取老人类型描述
     * @param customer 客户对象
     * @return 老人类型字符串
     */
    private String getElderlyType(Customer customer) {
        if (customer.getLevelId() == null || customer.getLevelId() == 0) {
            return "自理老人";
        } else {
            return "护理老人";
        }
    }

    /**
     * 根据出生日期计算年龄
     * @param birthday 出生日期
     * @return 年龄
     */
    private Integer calculateAge(Date birthday) {
        if (birthday == null) {
            return 0;
        }
        java.util.Calendar birthCal = java.util.Calendar.getInstance();
        birthCal.setTime(birthday);
        
        java.util.Calendar nowCal = java.util.Calendar.getInstance();
        
        int age = nowCal.get(java.util.Calendar.YEAR) - birthCal.get(java.util.Calendar.YEAR);
        
        if (nowCal.get(java.util.Calendar.DAY_OF_YEAR) < birthCal.get(java.util.Calendar.DAY_OF_YEAR)) {
            age--;
        }
        
        return age;
    }

    /**
     * 格式化日期
     * @param date 日期对象
     * @return 格式化后的字符串
     */
    private String formatDate(Date date) {
        if (date == null) {
            return "未设置";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
  
    /**
     * 从身份证号解析出生日期
     * @param idCard 身份证号
     * @return 出生日期，如果解析失败返回null
     */
    private Date parseBirthdayFromIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return null;
        }
        
        String cleanIdCard = idCard.trim().toUpperCase();
        
        try {
            String birthStr;
            if (cleanIdCard.length() == 18) {
                // 18位身份证：第7-14位是出生日期（YYYYMMDD）
                birthStr = cleanIdCard.substring(6, 14);
            } else if (cleanIdCard.length() == 15) {
                // 15位身份证：第7-12位是出生日期（YYMMDD），需要补全年份
                birthStr = "19" + cleanIdCard.substring(6, 12);
            } else {
                return null;
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.parse(birthStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从身份证号解析性别
     * @param idCard 身份证号
     * @return 性别（1-男，0-女），如果解析失败返回null
     */
    private Integer parseSexFromIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return null;
        }
        
        String cleanIdCard = idCard.trim().toUpperCase();
        
        try {
            int sexIndex;
            if (cleanIdCard.length() == 18) {
                // 18位身份证：第17位是性别码
                sexIndex = 16;
            } else if (cleanIdCard.length() == 15) {
                // 15位身份证：第15位是性别码
                sexIndex = 14;
            } else {
                return null;
            }
            
            char sexChar = cleanIdCard.charAt(sexIndex);
            int sexCode = Character.getNumericValue(sexChar);
            
            // 奇数为男，偶数为女
            return (sexCode % 2 == 1) ? 1 : 0;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证身份证号是否合规
     * @param idCard 身份证号
     * @return true表示合规，false表示不合规
     */
    private boolean validateIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            System.out.println("错误：身份证号不能为空");
            return false;
        }
        
        String cleanIdCard = idCard.trim().toUpperCase();
        
        // 检查位数（15位或18位）
        if (cleanIdCard.length() != 15 && cleanIdCard.length() != 18) {
            System.out.println("错误：身份证号位数不正确");
            return false;
        }
        
        // 18位身份证验证
        if (cleanIdCard.length() == 18) {
            // 前17位必须是数字
            String numPart = cleanIdCard.substring(0, 17);
            if (!numPart.matches("\\d{17}")) {
                System.out.println("错误：身份证号前17位必须是数字");
                return false;
            }
            
            // 第18位必须是数字或X
            char lastChar = cleanIdCard.charAt(17);
            if (!Character.isDigit(lastChar) && lastChar != 'X') {
                System.out.println("错误：身份证号第18位必须是数字或X");
                return false;
            }
            
            // 验证出生日期
            String birthStr = cleanIdCard.substring(6, 14);
            if (!validateDate(birthStr)) {
                System.out.println("错误：身份证号中的出生日期不正确");
                return false;
            }
            
            // 验证校验码
            if (!validateChecksum(cleanIdCard)) {
                System.out.println("错误：身份证号校验码不正确");
                return false;
            }
        } else {
            // 15位身份证验证
            // 必须全部是数字
            if (!cleanIdCard.matches("\\d{15}")) {
                System.out.println("错误：15位身份证号必须全部是数字");
                return false;
            }
            
            // 验证出生日期（15位身份证年份是2位，补19前缀）
            String birthStr = "19" + cleanIdCard.substring(6, 12);
            if (!validateDate(birthStr)) {
                System.out.println("错误：身份证号中的出生日期不正确");
                return false;
            }
        }
        
        return true;
    }

    /**
     * 验证日期格式是否正确（YYYYMMDD格式）
     */
    private boolean validateDate(String dateStr) {
        if (dateStr.length() != 8) {
            return false;
        }
        
        try {
            int year = Integer.parseInt(dateStr.substring(0, 4));
            int month = Integer.parseInt(dateStr.substring(4, 6));
            int day = Integer.parseInt(dateStr.substring(6, 8));
            
            // 年份范围检查（1900-2099）
            if (year < 1900 || year > 2099) {
                return false;
            }
            
            // 月份检查
            if (month < 1 || month > 12) {
                return false;
            }
            
            // 日期检查
            int[] daysInMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            if (day < 1 || day > daysInMonth[month - 1]) {
                return false;
            }
            
            // 闰年检查（2月29日）
            if (month == 2 && day == 29) {
                if (!isLeapYear(year)) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否为闰年
     */
    private boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    /**
     * 验证18位身份证校验码
     */
    private boolean validateChecksum(String idCard) {
        if (idCard.length() != 18) {
            return false;
        }
        
        // 加权因子
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        // 校验码对应表
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard.charAt(i) - '0') * weights[i];
        }
        
        int remainder = sum % 11;
        char expectedCheckCode = checkCodes[remainder];
        char actualCheckCode = idCard.charAt(17);
        
        return expectedCheckCode == actualCheckCode;
    }
}
