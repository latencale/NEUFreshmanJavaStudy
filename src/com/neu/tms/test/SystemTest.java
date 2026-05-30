package com.neu.tms.test;

import com.neu.tms.dao.*;
import com.neu.tms.pojo.*;
import com.neu.tms.service.CustomerService;
import com.neu.tms.service.TUserService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SystemTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("       养老院管理系统 - 全功能测试       ");
        System.out.println("========================================\n");

        boolean allPassed = true;

        allPassed &= testUserManagement();
        allPassed &= testBedManagement();
        allPassed &= testCustomerManagement();
        allPassed &= testNurseLevelManagement();
        allPassed &= testNurseContentManagement();
        allPassed &= testOutwardManagement();
        allPassed &= testBackDownManagement();

        System.out.println("\n========================================");
        if (allPassed) {
            System.out.println("       所有测试通过！测试完成！       ");
        } else {
            System.out.println("       部分测试失败，请检查输出       ");
        }
        System.out.println("========================================");
    }

    private static boolean testUserManagement() {
        System.out.println("\n【测试1: 用户管理】");
        System.out.println("------------------------------------------");
        TUserService userService = new TUserService();
        TUserDao userDao = new TUserDao();

        try {
            System.out.println("1.1 查询所有用户...");
            List<TUser> users = userService.findAll();
            System.out.println("     查询到 " + users.size() + " 个用户");
            for (TUser u : users) {
                System.out.println("     - " + u.getNickname() + " | " + u.getUsername() + " | 角色ID:" + u.getRoleId());
            }

            System.out.println("\n1.2 测试管理员登录...");
            TUser admin = userService.findByAdminByNameAndPassword("admin", "admin");
            if (admin != null) {
                System.out.println("     管理员登录成功: " + admin.getNickname());
            } else {
                System.out.println("     管理员账号不存在，尝试创建...");
                TUser newAdmin = new TUser();
                newAdmin.setNickname("系统管理员");
                newAdmin.setUsername("admin");
                newAdmin.setPassword("admin");
                newAdmin.setRoleId(1);
                newAdmin.setSex(1);
                newAdmin.setCreateTime(new Date());
                String result = userService.addUser(newAdmin);
                System.out.println("     创建结果: " + result);
            }

            System.out.println("\n1.3 测试添加健康管家...");
            TUser worker = new TUser();
            worker.setNickname("测试护工");
            worker.setUsername("worker001");
            worker.setPassword("123456");
            worker.setRoleId(2);
            worker.setSex(1);
            worker.setPhoneNumber("13800138000");
            worker.setCreateTime(new Date());
            String addResult = userService.addUser(worker);
            System.out.println("     添加结果: " + addResult);

            System.out.println("\n1.4 测试条件查询用户...");
            List<TUser> foundUsers = userService.findAll();
            System.out.println("     共有 " + foundUsers.size() + " 个用户");

            System.out.println("\n1.5 测试修改用户...");
            TUser toUpdate = userService.findByUserName("worker001");
            if (toUpdate != null) {
                toUpdate.setPhoneNumber("13900139000");
                String updateResult = userService.updateUser(toUpdate);
                System.out.println("     修改结果: " + updateResult);
            } else {
                System.out.println("     未找到要修改的用户");
            }

            System.out.println("\n>>> 用户管理测试通过！");
            return true;
        } catch (Exception e) {
            System.out.println("     [错误] 用户管理测试异常: " + e.getMessage());
            return false;
        }
    }

    private static boolean testBedManagement() {
        System.out.println("\n【测试2: 床位管理】");
        System.out.println("------------------------------------------");
        BedDao bedDao = new BedDao();

        try {
            System.out.println("2.1 查询所有床位...");
            List<Bed> beds = bedDao.findAll();
            System.out.println("     查询到 " + beds.size() + " 个床位");

            System.out.println("\n2.2 测试添加床位...");
            Bed newBed = new Bed();
            newBed.setRoomNo(101);
            newBed.setBedNo("A");
            newBed.setBedStatus(1);
            newBed.setRemarks("测试床位");
            String addResult = bedDao.addBed(newBed);
            System.out.println("     添加结果: " + addResult);

            System.out.println("\n2.3 测试查询空闲床位...");
            List<Bed> availableBeds = bedDao.findAvailableBeds();
            System.out.println("     空闲床位数: " + availableBeds.size());

            System.out.println("\n2.4 测试按房间号查询...");
            List<Bed> room101Beds = bedDao.findByRoomNo(101);
            System.out.println("     101房间床位数: " + room101Beds.size());

            System.out.println("\n>>> 床位管理测试通过！");
            return true;
        } catch (Exception e) {
            System.out.println("     [错误] 床位管理测试异常: " + e.getMessage());
            return false;
        }
    }

    private static boolean testCustomerManagement() {
        System.out.println("\n【测试3: 客户管理】");
        System.out.println("------------------------------------------");
        CustomerDao customerDao = new CustomerDao();
        CustomerService customerService = new CustomerService();
        BedDao bedDao = new BedDao();

        try {
            System.out.println("3.1 查询所有客户...");
            List<Customer> customers = customerDao.findAll();
            System.out.println("     查询到 " + customers.size() + " 个客户");

            System.out.println("\n3.2 测试客户姓名模糊查询...");
            List<Customer> foundCustomers = customerDao.findByNameLike("张");
            System.out.println("     姓名含'张'的客户数: " + foundCustomers.size());

            System.out.println("\n3.3 测试添加客户...");
            Bed availableBed = bedDao.findAvailableBeds().stream().findFirst().orElse(null);
            if (availableBed != null) {
                Customer newCustomer = new Customer();
                newCustomer.setCustomerName("测试客户");
                newCustomer.setBirthday(SDF.parse("1960-05-01"));
                newCustomer.setCustomerAge(66);
                newCustomer.setCustomerSex(1);
                newCustomer.setIdCard("210102196005010001");
                newCustomer.setBloodType("A");
                newCustomer.setFamilyMember("儿子-张三");
                newCustomer.setContactTel("13900139001");
                newCustomer.setHeight("170");
                newCustomer.setWeight("65");
                newCustomer.setBuildingNo("606");
                newCustomer.setRoomNo("101");
                newCustomer.setBedId(availableBed.getId());
                newCustomer.setCheckinDate(new Date());
                newCustomer.setExpirationDate(SDF.parse("2027-05-28"));
                newCustomer.setPsychosomaticState("良好");
                newCustomer.setAttention("无过敏史");
                newCustomer.setIsDeleted(0);
                newCustomer.setUserId(-1);

                String addResult = customerService.addCustomer(newCustomer);
                System.out.println("     添加结果: " + addResult);
            } else {
                System.out.println("     暂无空闲床位，跳过添加客户测试");
            }

            System.out.println("\n3.4 测试修改客户信息...");
            List<Customer> allCustomers = customerService.findAll();
            if (!allCustomers.isEmpty()) {
                Customer toUpdate = allCustomers.get(0);
                toUpdate.setPsychosomaticState("非常好");
                String updateResult = customerService.updateCustomer(toUpdate);
                System.out.println("     修改结果: " + updateResult);
            }

            System.out.println("\n>>> 客户管理测试通过！");
            return true;
        } catch (Exception e) {
            System.out.println("     [错误] 客户管理测试异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testNurseLevelManagement() {
        System.out.println("\n【测试4: 护理级别管理】");
        System.out.println("------------------------------------------");
        NurseLevelDao nurseLevelDao = new NurseLevelDao();

        try {
            System.out.println("4.1 查询所有护理级别...");
            List<NurseLevel> levels = nurseLevelDao.findAll();
            System.out.println("     查询到 " + levels.size() + " 个护理级别");
            for (NurseLevel level : levels) {
                System.out.println("     - ID:" + level.getId() + " | " + level.getLevelName() + " | 状态:" + level.getLevelStatus());
            }

            System.out.println("\n4.2 测试添加护理级别...");
            NurseLevel newLevel = new NurseLevel();
            newLevel.setLevelName("一级护理");
            newLevel.setLevelStatus(1);
            String addResult = nurseLevelDao.addNurseLevel(newLevel);
            System.out.println("     添加结果: " + addResult);

            System.out.println("\n4.3 测试根据ID查询...");
            List<NurseLevel> allLevels = nurseLevelDao.findAll();
            if (!allLevels.isEmpty()) {
                NurseLevel found = nurseLevelDao.findById(allLevels.get(0).getId());
                System.out.println("     查询结果: " + (found != null ? found.getLevelName() : "未找到"));
            }

            System.out.println("\n4.4 测试更新护理级别...");
            List<NurseLevel> updatedLevels = nurseLevelDao.findAll();
            if (!updatedLevels.isEmpty()) {
                NurseLevel toUpdate = updatedLevels.get(0);
                toUpdate.setLevelStatus(2);
                boolean updateResult = nurseLevelDao.updateNurseLevel(toUpdate);
                System.out.println("     更新结果: " + (updateResult ? "成功" : "失败"));
            }

            System.out.println("\n>>> 护理级别管理测试通过！");
            return true;
        } catch (Exception e) {
            System.out.println("     [错误] 护理级别管理测试异常: " + e.getMessage());
            return false;
        }
    }

    private static boolean testNurseContentManagement() {
        System.out.println("\n【测试5: 护理项目管理】");
        System.out.println("------------------------------------------");
        NurseContentDao nurseContentDao = new NurseContentDao();

        try {
            System.out.println("5.1 查询所有护理项目...");
            List<NurseContent> contents = nurseContentDao.findAll();
            System.out.println("     查询到 " + contents.size() + " 个护理项目");
            for (NurseContent content : contents) {
                System.out.println("     - " + content.getNursingName() + " | 价格:" + content.getServicePrice());
            }

            System.out.println("\n5.2 测试按名称模糊查询...");
            List<NurseContent> foundContents = nurseContentDao.findByNameLike("护理");
            System.out.println("     含'护理'的项目数: " + foundContents.size());

            System.out.println("\n5.3 测试添加护理项目...");
            NurseContent newContent = new NurseContent();
            newContent.setSerialNumber("NC001");
            newContent.setNursingName("日常身体检查");
            newContent.setServicePrice("50元/次");
            newContent.setMessage("包括血压、体温等常规检查");
            newContent.setStatus(1);
            newContent.setExecutionCycle("每天");
            newContent.setExecutionTimes("1次");
            String addResult = nurseContentDao.addNurseContent(newContent);
            System.out.println("     添加结果: " + addResult);

            System.out.println("\n>>> 护理项目管理测试通过！");
            return true;
        } catch (Exception e) {
            System.out.println("     [错误] 护理项目管理测试异常: " + e.getMessage());
            return false;
        }
    }

    private static boolean testOutwardManagement() {
        System.out.println("\n【测试6: 外出申请管理】");
        System.out.println("------------------------------------------");
        OutwardDao outwardDao = new OutwardDao();
        CustomerDao customerDao = new CustomerDao();

        try {
            System.out.println("6.1 查询所有外出申请...");
            List<Outward> outawards = outwardDao.findAll();
            System.out.println("     查询到 " + outawards.size() + " 条外出申请记录");

            System.out.println("\n6.2 测试添加外出申请...");
            List<Customer> customers = customerDao.findAll();
            if (!customers.isEmpty()) {
                Outward newOutward = new Outward();
                newOutward.setCustomerId(customers.get(0).getId());
                newOutward.setOutgoingReason("就医检查");
                newOutward.setOutgoingTime(new Date());
                newOutward.setExpectedReturnTime(SDF.parse("2026-05-29"));
                newOutward.setEscorted("家属");
                newOutward.setRelation("儿子");
                newOutward.setEscortedTel("13800138000");
                newOutward.setAuditStatus(0);
                newOutward.setIsDeleted(0);

                String addResult = outwardDao.addOutward(newOutward);
                System.out.println("     添加结果: " + addResult);
            } else {
                System.out.println("     暂无客户，跳过外出申请测试");
            }

            System.out.println("\n6.3 测试根据客户ID查询外出记录...");
            if (!customers.isEmpty()) {
                List<Outward> customerOutwards = outwardDao.findByCustomerId(customers.get(0).getId());
                System.out.println("     客户外出记录数: " + customerOutwards.size());
            }

            System.out.println("\n>>> 外出申请管理测试通过！");
            return true;
        } catch (Exception e) {
            System.out.println("     [错误] 外出申请管理测试异常: " + e.getMessage());
            return false;
        }
    }

    private static boolean testBackDownManagement() {
        System.out.println("\n【测试7: 退住申请管理】");
        System.out.println("------------------------------------------");
        BackDownDao backDownDao = new BackDownDao();
        CustomerDao customerDao = new CustomerDao();

        try {
            System.out.println("7.1 查询所有退住申请...");
            List<BackDown> backDowns = backDownDao.findAll();
            System.out.println("     查询到 " + backDowns.size() + " 条退住申请记录");

            System.out.println("\n7.2 测试添加退住申请...");
            List<Customer> customers = customerDao.findAll();
            if (!customers.isEmpty()) {
                BackDown newBackDown = new BackDown();
                newBackDown.setCustomerId(customers.get(0).getId());
                newBackDown.setRetreatTime(SDF.parse("2026-06-01"));
                newBackDown.setRetreatType(1);
                newBackDown.setRetreatReason("回家居住");
                newBackDown.setAuditStatus(0);
                newBackDown.setIsDeleted(0);

                String addResult = backDownDao.addBackDown(newBackDown);
                System.out.println("     添加结果: " + addResult);
            } else {
                System.out.println("     暂无客户，跳过退住申请测试");
            }

            System.out.println("\n7.3 测试根据客户ID查询退住记录...");
            if (!customers.isEmpty()) {
                List<BackDown> customerBackDowns = backDownDao.findByCustomerId(customers.get(0).getId());
                System.out.println("     客户退住记录数: " + customerBackDowns.size());
            }

            System.out.println("\n7.4 测试审批退住申请...");
            List<BackDown> allBackDowns = backDownDao.findAll();
            if (!allBackDowns.isEmpty()) {
                BackDown toAudit = allBackDowns.get(0);
                toAudit.setAuditStatus(1);
                toAudit.setAuditPerson("管理员");
                toAudit.setAuditTime(new Date());
                boolean auditResult = backDownDao.updateBackDown(toAudit);
                System.out.println("     审批结果: " + (auditResult ? "通过" : "失败"));
            }

            System.out.println("\n>>> 退住申请管理测试通过！");
            return true;
        } catch (Exception e) {
            System.out.println("     [错误] 退住申请管理测试异常: " + e.getMessage());
            return false;
        }
    }
}