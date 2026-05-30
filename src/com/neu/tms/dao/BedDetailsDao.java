
package com.neu.tms.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.tms.pojo.BedDetails;
import com.neu.tms.utils.PersistentIdGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BedDetailsDao {
    public static final File FILE_NAME = new File("data\\beddetails.json");
    private final ObjectMapper om = new ObjectMapper();

    /**
     * 添加床位详情
     */
    public String addBedDetails(BedDetails bedDetails) throws IOException {
        try {
            List<BedDetails> list = findAll();
            bedDetails.setId(PersistentIdGenerator.getInstance().nextId());
            bedDetails.setIsDeleted(0);
            list.add(bedDetails);
            om.writeValue(FILE_NAME, list);
            return "添加成功";
        } catch (Exception e) {
            throw new RuntimeException("添加床位详情失败", e);
        }
    }

    /**
     * 查询所有未删除的床位详情
     */
    public List<BedDetails> findAll() {
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            List<BedDetails> allDetails = om.readValue(FILE_NAME, new TypeReference<List<BedDetails>>() {});
            return allDetails.stream()
                    .filter(d -> d.getIsDeleted() == 0 || d.getIsDeleted() == null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据客户ID查询
     */
    public List<BedDetails> findByCustomerId(Integer customerId) {
        List<BedDetails> allDetails = findAll();
        return allDetails.stream()
                .filter(d -> d.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * 查询客户正在使用的床位详情
     */
    public BedDetails findCurrentByCustomerId(Integer customerId) {
        List<BedDetails> details = findByCustomerId(customerId);
        Date now = new Date();
        
        return details.stream()
                .filter(d -> d.getStartDate() != null && 
                        !d.getStartDate().after(now) &&
                        (d.getEndDate() == null || d.getEndDate().after(now)))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据床位ID查询
     */
    public List<BedDetails> findByBedId(Integer bedId) {
        List<BedDetails> allDetails = findAll();
        return allDetails.stream()
                .filter(d -> d.getBedId().equals(bedId))
                .collect(Collectors.toList());
    }

    /**
     * 更新床位详情
     */
    public boolean updateBedDetails(BedDetails bedDetails) {
        try {
            List<BedDetails> list = findAll();
            boolean isUpdate = false;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(bedDetails.getId())) {
                    list.set(i, bedDetails);
                    isUpdate = true;
                    break;
                }
            }

            if (isUpdate) {
                om.writeValue(FILE_NAME, list);
            }
            return isUpdate;
        } catch (IOException e) {
            throw new RuntimeException("修改床位详情异常", e);
        }
    }

    /**
     * 逻辑删除指定客户的所有床位详情
     */
    public boolean hideByCustomerId(Integer customerId) {
        try {
            List<BedDetails> list = findAll();
            boolean isUpdate = false;

            for (BedDetails detail : list) {
                if (detail.getCustomerId().equals(customerId)) {
                    detail.setIsDeleted(1);
                    isUpdate = true;
                }
            }

            if (isUpdate) {
                om.writeValue(FILE_NAME, list);
            }
            return isUpdate;
        } catch (IOException e) {
            throw new RuntimeException("隐藏床位详情失败", e);
        }
    }

    /**
     * 根据客户姓名和状态查询
     */
    public List<BedDetails> findByCustomerNameAndStatus(String customerName, Integer status) {
        List<BedDetails> allDetails = findAll();
        // 这里需要关联查询Customer，简化处理
        return allDetails;
    }
}
