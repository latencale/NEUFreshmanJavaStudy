
package com.neu.tms.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neu.tms.pojo.CustomerNurseItem;
import com.neu.tms.utils.PersistentIdGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerNurseItemDao {
    public static final File FILE_NAME = new File("data\\customerNurseItems.json");
    private final ObjectMapper om = new ObjectMapper();

    public CustomerNurseItemDao() {
        om.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * 添加客户护理项目
     */
    public String addCustomerNurseItem(CustomerNurseItem item) throws IOException {
        try {
            List<CustomerNurseItem> list = findAll();
            item.setId(PersistentIdGenerator.getInstance().nextId());
            item.setIsDeleted(0);
            if (item.getNurseNumber() == null) {
                item.setNurseNumber(1);
            }
            if (item.getBuyTime() == null) {
                item.setBuyTime(new Date());
            }
            list.add(item);
            om.writeValue(FILE_NAME, list);
            return "添加成功";
        } catch (Exception e) {
            throw new RuntimeException("添加客户护理项目失败", e);
        }
    }

    /**
     * 查询所有未删除的客户护理项目
     */
    public List<CustomerNurseItem> findAll() {
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            List<CustomerNurseItem> allItems = om.readValue(FILE_NAME, new TypeReference<List<CustomerNurseItem>>() {});
            return allItems.stream()
                    .filter(i -> i.getIsDeleted() == 0 || i.getIsDeleted() == null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据客户ID查询
     */
    public List<CustomerNurseItem> findByCustomerId(Integer customerId) {
        List<CustomerNurseItem> allItems = findAll();
        return allItems.stream()
                .filter(i -> i.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * 根据客户ID和护理项目ID查询
     */
    public CustomerNurseItem findByCustomerIdAndItemId(Integer customerId, Integer itemId) {
        List<CustomerNurseItem> items = findByCustomerId(customerId);
        return items.stream()
                .filter(i -> i.getItemId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 更新客户护理项目
     */
    public boolean updateCustomerNurseItem(CustomerNurseItem item) {
        try {
            List<CustomerNurseItem> list = findAll();
            boolean isUpdate = false;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(item.getId())) {
                    list.set(i, item);
                    isUpdate = true;
                    break;
                }
            }

            if (isUpdate) {
                om.writeValue(FILE_NAME, list);
            }
            return isUpdate;
        } catch (IOException e) {
            throw new RuntimeException("修改客户护理项目异常", e);
        }
    }

    /**
     * 逻辑删除
     */
    public boolean deleteById(Integer id) {
        try {
            List<CustomerNurseItem> list = findAll();
            boolean isDelete = false;

            for (CustomerNurseItem item : list) {
                if (item.getId().equals(id)) {
                    item.setIsDeleted(1);
                    isDelete = true;
                    break;
                }
            }

            if (isDelete) {
                om.writeValue(FILE_NAME, list);
            }
            return isDelete;
        } catch (IOException e) {
            throw new RuntimeException("删除客户护理项目失败", e);
        }
    }

    /**
     * 批量删除客户的护理项目
     */
    public boolean deleteByCustomerId(Integer customerId) {
        try {
            List<CustomerNurseItem> list = findAll();
            boolean isDelete = false;

            for (CustomerNurseItem item : list) {
                if (item.getCustomerId().equals(customerId)) {
                    item.setIsDeleted(1);
                    isDelete = true;
                }
            }

            if (isDelete) {
                om.writeValue(FILE_NAME, list);
            }
            return isDelete;
        } catch (IOException e) {
            throw new RuntimeException("删除客户护理项目失败", e);
        }
    }
}
