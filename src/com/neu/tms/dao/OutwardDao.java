
package com.neu.tms.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.tms.pojo.Customer;
import com.neu.tms.pojo.Outward;
import com.neu.tms.utils.PersistentIdGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OutwardDao {
    public static final File FILE_NAME = new File("data\\outwards.json");
    private final ObjectMapper om = new ObjectMapper();

    /**
     * 添加外出登记
     */
    public String addOutward(Outward outward) throws IOException {
        try {
            List<Outward> list = findAll();
            outward.setId(PersistentIdGenerator.getInstance().nextId());
            outward.setIsDeleted(0);
            if (outward.getAuditStatus() == null) {
                outward.setAuditStatus(0); // 默认已提交
            }
            list.add(outward);
            om.writeValue(FILE_NAME, list);
            return "添加成功";
        } catch (Exception e) {
            throw new RuntimeException("添加外出登记失败", e);
        }
    }

    /**
     * 查询所有未删除的外出登记
     */
    public List<Outward> findAll() {
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            List<Outward> allOutwards = om.readValue(FILE_NAME, new TypeReference<List<Outward>>() {});
            return allOutwards.stream()
                    .filter(o -> o.getIsDeleted() == 0 || o.getIsDeleted() == null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据客户ID查询
     */
    public List<Outward> findByCustomerId(Integer customerId) {
        List<Outward> allOutwards = findAll();
        return allOutwards.stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * 根据客户姓名模糊查询
     */
    public List<Outward> findByCustomerNameLike(String customerName) {
        List<Outward> allOutwards = findAll();
        if (customerName == null || customerName.trim().isEmpty()) {
            return allOutwards;
        }
        return allOutwards.stream()
                .filter(o -> o.getCustomerId() != null)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询
     */
    public Outward findById(Integer id) {
        List<Outward> allOutwards = findAll();
        return allOutwards.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 更新外出登记
     */
    public boolean updateOutward(Outward outward) {
        try {
            List<Outward> list = findAll();
            boolean isUpdate = false;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(outward.getId())) {
                    list.set(i, outward);
                    isUpdate = true;
                    break;
                }
            }

            if (isUpdate) {
                om.writeValue(FILE_NAME, list);
            }
            return isUpdate;
        } catch (IOException e) {
            throw new RuntimeException("修改外出登记异常", e);
        }
    }

    /**
     * 逻辑删除
     */
    public boolean deleteById(Integer id) {
        try {
            List<Outward> list = findAll();
            boolean isDelete = false;

            for (Outward outward : list) {
                if (outward.getId().equals(id)) {
                    outward.setIsDeleted(1);
                    isDelete = true;
                    break;
                }
            }

            if (isDelete) {
                om.writeValue(FILE_NAME, list);
            }
            return isDelete;
        } catch (IOException e) {
            throw new RuntimeException("删除外出登记失败", e);
        }
    }

    /**
     * 根据客户ID列表和姓名模糊查询外出申请
     */
    public List<Outward> findByCustomerIdsAndName(List<Integer> customerIds, String customerName) {
        List<Outward> allOutwards = findAll();
        
        if (customerIds == null || customerIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Outward> result = allOutwards.stream()
                .filter(o -> customerIds.contains(o.getCustomerId()))
                .collect(Collectors.toList());
        
        if (customerName != null && !customerName.trim().isEmpty()) {
            CustomerDao customerDao = new CustomerDao();
            List<Customer> customers = customerDao.findByNameLike(customerName);
            List<Integer> matchedCustomerIds = customers.stream()
                    .map(Customer::getId)
                    .collect(Collectors.toList());
            
            result = result.stream()
                    .filter(o -> matchedCustomerIds.contains(o.getCustomerId()))
                    .collect(Collectors.toList());
        }
        
        return result;
    }
}
