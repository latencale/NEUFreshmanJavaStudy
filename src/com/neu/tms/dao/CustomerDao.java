package com.neu.tms.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.tms.pojo.Customer;
import com.neu.tms.utils.PersistentIdGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDao {
    public static final File FILE_NAME = new File("data\\customers.json");
    private final ObjectMapper om = new ObjectMapper();

    /**
     * 添加客户
     */
    public String addCustomer(Customer customer) throws IOException {
        try {
            List<Customer> customerList = findAll();
            customer.setId(PersistentIdGenerator.getInstance().nextId());
            customer.setIsDeleted(0);
            customerList.add(customer);
            om.writeValue(FILE_NAME, customerList);
            return "添加成功";
        } catch (Exception e) {
            throw new RuntimeException("添加客户失败", e);
        }
    }

    /**
     * 查询所有未删除的客户
     */
    public List<Customer> findAll() {
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            List<Customer> allCustomers = om.readValue(FILE_NAME, new TypeReference<List<Customer>>() {});
            return allCustomers.stream()
                    .filter(c -> c.getIsDeleted() == 0 || c.getIsDeleted() == null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据姓名模糊查询
     */
    public List<Customer> findByNameLike(String name) {
        List<Customer> allCustomers = findAll();
        if (name == null || name.trim().isEmpty()) {
            return allCustomers;
        }
        return allCustomers.stream()
                .filter(c -> c.getCustomerName() != null && 
                        c.getCustomerName().contains(name))
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询
     */
    public Customer findById(Integer id) {
        List<Customer> allCustomers = findAll();
        return allCustomers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 更新客户信息
     */
    public boolean updateCustomer(Customer customer) {
        try {
            List<Customer> customerList = findAll();
            boolean isUpdate = false;

            for (int i = 0; i < customerList.size(); i++) {
                if (customerList.get(i).getId().equals(customer.getId())) {
                    customerList.set(i, customer);
                    isUpdate = true;
                    break;
                }
            }

            if (isUpdate) {
                om.writeValue(FILE_NAME, customerList);
            }
            return isUpdate;
        } catch (IOException e) {
            throw new RuntimeException("修改客户异常", e);
        }
    }

    /**
     * 逻辑删除客户
     */
    public boolean deleteById(Integer id) {
        try {
            List<Customer> customerList = findAll();
            boolean isDelete = false;

            for (Customer customer : customerList) {
                if (customer.getId().equals(id)) {
                    customer.setIsDeleted(1);
                    isDelete = true;
                    break;
                }
            }

            if (isDelete) {
                om.writeValue(FILE_NAME, customerList);
            }
            return isDelete;
        } catch (IOException e) {
            throw new RuntimeException("删除客户失败", e);
        }
    }

    /**
     * 查询所有客户（包括已删除的）
     */
    public List<Customer> findAllIncludingDeleted() {
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            return om.readValue(FILE_NAME, new TypeReference<List<Customer>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
