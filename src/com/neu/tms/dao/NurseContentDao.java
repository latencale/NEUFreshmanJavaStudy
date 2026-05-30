package com.neu.tms.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.tms.pojo.NurseContent;
import com.neu.tms.utils.PersistentIdGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NurseContentDao {
    public static final File FILE_NAME = new File("data\\nursecontents.json");
    private final ObjectMapper om = new ObjectMapper();

    /**
     * 添加护理项目
     */
    public String addNurseContent(NurseContent nurseContent) throws IOException {
        try {
            List<NurseContent> list = findAll();
            nurseContent.setId(PersistentIdGenerator.getInstance().nextId());
            nurseContent.setIsDeleted(0);
            list.add(nurseContent);
            om.writeValue(FILE_NAME, list);
            return "添加成功";
        } catch (Exception e) {
            throw new RuntimeException("添加护理项目失败", e);
        }
    }

    /**
     * 查询所有未删除的护理项目
     */
    public List<NurseContent> findAll() {
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            List<NurseContent> allContents = om.readValue(FILE_NAME, new TypeReference<List<NurseContent>>() {});
            return allContents.stream()
                    .filter(c -> c.getIsDeleted() == 0 || c.getIsDeleted() == null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据名称模糊查询
     */
    public List<NurseContent> findByNameLike(String name) {
        List<NurseContent> allContents = findAll();
        if (name == null || name.trim().isEmpty()) {
            return allContents;
        }
        return allContents.stream()
                .filter(c -> c.getNursingName() != null && 
                        c.getNursingName().contains(name))
                .collect(Collectors.toList());
    }

    /**
     * 根据状态查询
     */
    public List<NurseContent> findByStatus(Integer status) {
        List<NurseContent> allContents = findAll();
        if (status == null) {
            return allContents;
        }
        return allContents.stream()
                .filter(c -> c.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    /**
     * 多条件组合查询
     */
    public List<NurseContent> findByConditions(String name, Integer status) {
        List<NurseContent> result = findAll();
        
        if (name != null && !name.trim().isEmpty()) {
            result = result.stream()
                    .filter(c -> c.getNursingName() != null && 
                            c.getNursingName().contains(name))
                    .collect(Collectors.toList());
        }
        
        if (status != null) {
            result = result.stream()
                    .filter(c -> c.getStatus().equals(status))
                    .collect(Collectors.toList());
        }
        
        return result;
    }

    /**
     * 根据ID查询
     */
    public NurseContent findById(Integer id) {
        List<NurseContent> allContents = findAll();
        return allContents.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 更新护理项目
     */
    public boolean updateNurseContent(NurseContent nurseContent) {
        try {
            List<NurseContent> list = findAll();
            boolean isUpdate = false;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(nurseContent.getId())) {
                    list.set(i, nurseContent);
                    isUpdate = true;
                    break;
                }
            }

            if (isUpdate) {
                om.writeValue(FILE_NAME, list);
            }
            return isUpdate;
        } catch (IOException e) {
            throw new RuntimeException("修改护理项目异常", e);
        }
    }

    /**
     * 逻辑删除
     */
    public boolean deleteById(Integer id) {
        try {
            List<NurseContent> list = findAll();
            boolean isDelete = false;

            for (NurseContent content : list) {
                if (content.getId().equals(id)) {
                    content.setIsDeleted(1);
                    isDelete = true;
                    break;
                }
            }

            if (isDelete) {
                om.writeValue(FILE_NAME, list);
            }
            return isDelete;
        } catch (IOException e) {
            throw new RuntimeException("删除护理项目失败", e);
        }
    }
}
