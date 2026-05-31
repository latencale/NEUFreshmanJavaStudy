
package com.neu.tms.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neu.tms.pojo.NurseRecord;
import com.neu.tms.utils.PersistentIdGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NurseRecordDao {
    public static final File FILE_NAME = new File("data\\nurseRecords.json");
    private final ObjectMapper om = new ObjectMapper();

    public NurseRecordDao() {
        om.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * 添加护理记录
     */
    public String addNurseRecord(NurseRecord record) throws IOException {
        try {
            List<NurseRecord> list = findAll();
            record.setId(PersistentIdGenerator.getInstance().nextId());
            record.setIsDeleted(0);
            list.add(record);
            om.writeValue(FILE_NAME, list);
            return "添加成功";
        } catch (Exception e) {
            throw new RuntimeException("添加护理记录失败", e);
        }
    }

    /**
     * 查询所有未删除的护理记录
     */
    public List<NurseRecord> findAll() {
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            List<NurseRecord> allRecords = om.readValue(FILE_NAME, new TypeReference<List<NurseRecord>>() {});
            return allRecords.stream()
                    .filter(r -> r.getIsDeleted() == 0 || r.getIsDeleted() == null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据客户ID查询
     */
    public List<NurseRecord> findByCustomerId(Integer customerId) {
        List<NurseRecord> allRecords = findAll();
        return allRecords.stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID查询（健康管家查询自己的护理记录）
     */
    public List<NurseRecord> findByUserId(Integer userId) {
        List<NurseRecord> allRecords = findAll();
        return allRecords.stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * 根据客户ID和用户ID查询
     */
    public List<NurseRecord> findByCustomerIdAndUserId(Integer customerId, Integer userId) {
        List<NurseRecord> allRecords = findAll();
        return allRecords.stream()
                .filter(r -> r.getCustomerId().equals(customerId) && 
                        r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * 更新护理记录
     */
    public boolean updateNurseRecord(NurseRecord record) {
        try {
            List<NurseRecord> list = findAll();
            boolean isUpdate = false;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(record.getId())) {
                    list.set(i, record);
                    isUpdate = true;
                    break;
                }
            }

            if (isUpdate) {
                om.writeValue(FILE_NAME, list);
            }
            return isUpdate;
        } catch (IOException e) {
            throw new RuntimeException("修改护理记录异常", e);
        }
    }

    /**
     * 逻辑删除
     */
    public boolean deleteById(Integer id) {
        try {
            List<NurseRecord> list = findAll();
            boolean isDelete = false;

            for (NurseRecord record : list) {
                if (record.getId().equals(id)) {
                    record.setIsDeleted(1);
                    isDelete = true;
                    break;
                }
            }

            if (isDelete) {
                om.writeValue(FILE_NAME, list);
            }
            return isDelete;
        } catch (IOException e) {
            throw new RuntimeException("删除护理记录失败", e);
        }
    }
}
