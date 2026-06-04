
package com.neu.tms.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neu.tms.pojo.BackDown;
import com.neu.tms.pojo.Customer;
import com.neu.tms.utils.PersistentIdGenerator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BackDownDao {
    public static final File FILE_NAME = new File("data\\backdowns.json");
    private final ObjectMapper om = new ObjectMapper();

    public BackDownDao() {
        om.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * 添加退住登记
     */
    public String addBackDown(BackDown backDown) throws IOException {
        try {
            List<BackDown> list = findAll();
            backDown.setId(PersistentIdGenerator.getInstance().nextId());
            backDown.setIsDeleted(0);
            if (backDown.getAuditStatus() == null) {
                backDown.setAuditStatus(0); // 默认已提交
            }
            list.add(backDown);
            try (Writer writer = new OutputStreamWriter(
                    new java.io.FileOutputStream(FILE_NAME), StandardCharsets.UTF_8)) {
                om.writeValue(writer, list);
            }
            return "添加成功";
        } catch (Exception e) {
            throw new RuntimeException("添加退住登记失败", e);
        }
    }

    /**
     * 查询所有未删除的退住登记
     */
    public List<BackDown> findAll() {
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            List<BackDown> allBackDowns = om.readValue(FILE_NAME, new TypeReference<List<BackDown>>() {});
            return allBackDowns.stream()
                    .filter(b -> b.getIsDeleted() == 0 || b.getIsDeleted() == null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据客户ID查询
     */
    public List<BackDown> findByCustomerId(Integer customerId) {
        List<BackDown> allBackDowns = findAll();
        return allBackDowns.stream()
                .filter(b -> b.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询
     */
    public BackDown findById(Integer id) {
        List<BackDown> allBackDowns = findAll();
        return allBackDowns.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据客户ID列表和姓名模糊查询退住申请
     */
    public List<BackDown> findByCustomerIdsAndName(List<Integer> customerIds, String customerName) {
        List<BackDown> allBackDowns = findAll();
        
        if (customerIds == null || customerIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<BackDown> result = allBackDowns.stream()
                .filter(b -> customerIds.contains(b.getCustomerId()))
                .collect(Collectors.toList());
        
        if (customerName != null && !customerName.trim().isEmpty()) {
            CustomerDao customerDao = new CustomerDao();
            List<Customer> customers = customerDao.findByNameLike(customerName);
            List<Integer> matchedCustomerIds = customers.stream()
                    .map(Customer::getId)
                    .collect(Collectors.toList());
            
            result = result.stream()
                    .filter(b -> matchedCustomerIds.contains(b.getCustomerId()))
                    .collect(Collectors.toList());
        }
        
        return result;
    }

    /**
     * 更新退住登记
     */
    public boolean updateBackDown(BackDown backDown) {
        try {
            List<BackDown> list = findAll();
            boolean isUpdate = false;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(backDown.getId())) {
                    list.set(i, backDown);
                    isUpdate = true;
                    break;
                }
            }

            if (isUpdate) {
                try (Writer writer = new OutputStreamWriter(
                        new java.io.FileOutputStream(FILE_NAME), StandardCharsets.UTF_8)) {
                    om.writeValue(writer, list);
                }
            }
            return isUpdate;
        } catch (IOException e) {
            throw new RuntimeException("修改退住登记异常", e);
        }
    }

    /**
     * 逻辑删除
     */
    public boolean deleteById(Integer id) {
        try {
            List<BackDown> list = findAll();
            boolean isDelete = false;

            for (BackDown backDown : list) {
                if (backDown.getId().equals(id)) {
                    backDown.setIsDeleted(1);
                    isDelete = true;
                    break;
                }
            }

            if (isDelete) {
                try (Writer writer = new OutputStreamWriter(
                        new java.io.FileOutputStream(FILE_NAME), StandardCharsets.UTF_8)) {
                    om.writeValue(writer, list);
                }
            }
            return isDelete;
        } catch (IOException e) {
            throw new RuntimeException("删除退住登记失败", e);
        }
    }
}
