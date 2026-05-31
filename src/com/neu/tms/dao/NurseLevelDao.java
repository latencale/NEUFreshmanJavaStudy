
package com.neu.tms.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neu.tms.pojo.NurseLevel;
import com.neu.tms.utils.PersistentIdGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NurseLevelDao {
    public static final File FILE_NAME = new File("data\\nurselevels.json");
    private final ObjectMapper om = new ObjectMapper();

    public NurseLevelDao() {
        om.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * 添加护理级别
     */
    public String addNurseLevel(NurseLevel nurseLevel) throws IOException {
        try {
            List<NurseLevel> list = findAll();
            nurseLevel.setId(PersistentIdGenerator.getInstance().nextId());
            nurseLevel.setIsDeleted(0);
            list.add(nurseLevel);
            om.writeValue(FILE_NAME, list);
            return "添加成功";
        } catch (Exception e) {
            throw new RuntimeException("添加护理级别失败", e);
        }
    }

    /**
     * 查询所有未删除的护理级别
     */
    public List<NurseLevel> findAll() {
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            List<NurseLevel> allLevels = om.readValue(FILE_NAME, new TypeReference<List<NurseLevel>>() {});
            return allLevels.stream()
                    .filter(l -> l.getIsDeleted() == 0 || l.getIsDeleted() == null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据状态查询
     */
    public List<NurseLevel> findByStatus(Integer status) {
        List<NurseLevel> allLevels = findAll();
        if (status == null) {
            return allLevels;
        }
        return allLevels.stream()
                .filter(l -> l.getLevelStatus().equals(status))
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询
     */
    public NurseLevel findById(Integer id) {
        List<NurseLevel> allLevels = findAll();
        return allLevels.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 更新护理级别
     */
    public boolean updateNurseLevel(NurseLevel nurseLevel) {
        try {
            List<NurseLevel> list = findAll();
            boolean isUpdate = false;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(nurseLevel.getId())) {
                    list.set(i, nurseLevel);
                    isUpdate = true;
                    break;
                }
            }

            if (isUpdate) {
                om.writeValue(FILE_NAME, list);
            }
            return isUpdate;
        } catch (IOException e) {
            throw new RuntimeException("修改护理级别异常", e);
        }
    }

    /**
     * 逻辑删除
     */
    public boolean deleteById(Integer id) {
        try {
            List<NurseLevel> list = findAll();
            boolean isDelete = false;

            for (NurseLevel level : list) {
                if (level.getId().equals(id)) {
                    level.setIsDeleted(1);
                    isDelete = true;
                    break;
                }
            }

            if (isDelete) {
                om.writeValue(FILE_NAME, list);
            }
            return isDelete;
        } catch (IOException e) {
            throw new RuntimeException("删除护理级别失败", e);
        }
    }
}
