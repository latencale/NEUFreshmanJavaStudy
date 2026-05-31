package com.neu.tms.service;

import com.neu.tms.dao.BedDao;
import com.neu.tms.dao.BedDetailsDao;
import com.neu.tms.dao.CustomerDao;
import com.neu.tms.pojo.Bed;
import com.neu.tms.pojo.Customer;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerService {
    private CustomerDao customerDao = new CustomerDao();
    private BedDao bedDao = new BedDao();
    private BedDetailsDao bedDetailsDao = new BedDetailsDao();

    /**
     * 添加客户
     */
    public String addCustomer(Customer customer) {
        try {
            // 验证合同到期时间不能小于入住时间
            if (customer.getExpirationDate() != null && customer.getCheckinDate() != null) {
                if (customer.getExpirationDate().before(customer.getCheckinDate())) {
                    return "合同到期时间不能小于入住时间";
                }
            }
            
            // 验证床位是否空闲
            if (customer.getBedId() != null) {
                Bed bed = bedDao.findById(customer.getBedId());
                if (bed == null) {
                    // 检查是否是已删除的床位
                    List<Bed> allBeds = bedDao.findAllIncludingDeleted();
                    Bed deletedBed = allBeds.stream()
                            .filter(b -> b.getId().equals(customer.getBedId()))
                            .findFirst()
                            .orElse(null);
                    if (deletedBed != null && deletedBed.getIsDeleted() != null && deletedBed.getIsDeleted() == 1) {
                        return "床位ID " + customer.getBedId() + " 已被删除，请选择其他床位";
                    }
                    return "床位ID " + customer.getBedId() + " 不存在";
                }
                if (bed.getBedStatus() != 1) {
                    String statusStr = bed.getBedStatus() == 2 ? "有人" : "外出";
                    return "床位不可用（当前状态：" + statusStr + "）";
                }
            }

            String result = customerDao.addCustomer(customer);
            
            // 如果添加了床位信息，更新床位状态为有人
            if (customer.getBedId() != null) {
                Bed bed = bedDao.findById(customer.getBedId());
                bed.setBedStatus(2); // 2：有人
                bedDao.updateBed(bed);
            }
            
            return result;
        } catch (Exception e) {
            return "添加失败：" + e.getMessage();
        }
    }

    /**
     * 查询所有客户
     */
    public List<Customer> findAll() {
        return customerDao.findAll();
    }

    /**
     * 根据姓名模糊查询
     */
    public List<Customer> findByNameLike(String name) {
        return customerDao.findByNameLike(name);
    }

    /**
     * 更新客户信息
     */
    public String updateCustomer(Customer customer) {
        try {
            // 验证合同到期时间
            if (customer.getExpirationDate() != null && customer.getCheckinDate() != null) {
                if (customer.getExpirationDate().before(customer.getCheckinDate())) {
                    return "合同到期时间不能小于入住时间";
                }
            }

            boolean result = customerDao.updateCustomer(customer);
            return result ? "修改成功" : "修改失败";
        } catch (Exception e) {
            return "修改失败：" + e.getMessage();
        }
    }

    /**
     * 删除客户（逻辑删除）
     */
    public String deleteCustomer(Integer customerId) {
        try {
            Customer customer = customerDao.findById(customerId);
            if (customer == null) {
                return "客户不存在";
            }

            // 逻辑删除客户
            customerDao.deleteById(customerId);

            // 修改床位状态为空闲
            if (customer.getBedId() != null) {
                Bed bed = bedDao.findById(customer.getBedId());
                if (bed != null) {
                    bed.setBedStatus(1); // 1：空闲
                    bedDao.updateBed(bed);
                }
            }

            // 逻辑删除床位使用详情
            bedDetailsDao.hideByCustomerId(customerId);

            return "删除成功";
        } catch (Exception e) {
            return "删除失败：" + e.getMessage();
        }
    }

    /**
     * 根据ID查询客户
     */
    public Customer findById(Integer id) {
        return customerDao.findById(id);
    }

    /**
     * 根据条件查询客户
     * @param name 客户姓名（模糊查询），为空则不限制
     * @param elderlyType 老人类型：0-全部，1-自理老人，2-护理老人，null则不限制
     * @return 符合条件的客户列表
     */
    public List<Customer> findByConditions(String name, Integer elderlyType) {
        List<Customer> allCustomers = customerDao.findAll();
        List<Customer> result = allCustomers;
        
        // 按姓名过滤
        if (name != null && !name.trim().isEmpty()) {
            result = result.stream()
                    .filter(c -> c.getCustomerName() != null && 
                            c.getCustomerName().contains(name))
                    .collect(Collectors.toList());
        }
        
        // 按老人类型过滤
        if (elderlyType != null && elderlyType != 0) {
            if (elderlyType == 1) {
                // 自理老人：levelId 为 null 或 0
                result = result.stream()
                        .filter(c -> c.getLevelId() == null || c.getLevelId() == 0)
                        .collect(Collectors.toList());
            } else if (elderlyType == 2) {
                // 护理老人：levelId 不为 null 且不为 0
                result = result.stream()
                        .filter(c -> c.getLevelId() != null && c.getLevelId() != 0)
                        .collect(Collectors.toList());
            }
        }
        
        return result;
    }
}
