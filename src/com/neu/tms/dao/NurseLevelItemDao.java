package com.neu.tms.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neu.tms.pojo.NurseLevelItem;
import com.neu.tms.utils.PersistentIdGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NurseLevelItemDao {
    public static final File FILE_NAME = new File("data\\nurselevelitems.json");
    private final ObjectMapper om = new ObjectMapper();

    public NurseLevelItemDao() {
        om.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * 添加护理级别与项目关联
     */
    public String addNurseLevelItem(NurseLevelItem item) throws IOException {
        try {
            List<NurseLevelItem> list = findAll();
            item.setId(PersistentIdGenerator.getInstance().nextId());
            list.add(item);
            om.writeValue(FILE_NAME, list);
            return "添加成功";
        } catch (Exception e) {
            throw new RuntimeException("添加关联失败", e);
        }
    }

    /**
     * 查询所有关联
     */
    public List<NurseLevelItem> findAll() {
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            return om.readValue(FILE_NAME, new TypeReference<List<NurseLevelItem>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据护理级别ID查询关联的项目
     */
    public List<NurseLevelItem> findByLevelId(Integer levelId) {
        List<NurseLevelItem> allItems = findAll();
        return allItems.stream()
                .filter(item -> item.getLevelId().equals(levelId))
                .collect(Collectors.toList());
    }

    /**
     * 根据项目ID查询关联的护理级别
     */
    public List<NurseLevelItem> findByItemId(Integer itemId) {
        List<NurseLevelItem> allItems = findAll();
        return allItems.stream()
                .filter(item -> item.getItemId().equals(itemId))
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询关联记录
     */
    public NurseLevelItem findById(Integer id) {
        List<NurseLevelItem> allItems = findAll();
        return allItems.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 删除关联（根据ID）
     */
    public boolean deleteById(Integer id) {
        try {
            List<NurseLevelItem> list = findAll();
            boolean isDelete = false;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(id)) {
                    list.remove(i);
                    isDelete = true;
                    break;
                }
            }

            if (isDelete) {
                om.writeValue(FILE_NAME, list);
            }
            return isDelete;
        } catch (IOException e) {
            throw new RuntimeException("删除关联失败", e);
        }
    }

    /**
     * 删除指定级别下的指定项目关联
     */
    public boolean deleteByLevelIdAndItemId(Integer levelId, Integer itemId) {
        try {
            List<NurseLevelItem> list = findAll();
            boolean isDelete = false;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getLevelId().equals(levelId) && 
                        list.get(i).getItemId().equals(itemId)) {
                    list.remove(i);
                    isDelete = true;
                    break;
                }
            }

            if (isDelete) {
                om.writeValue(FILE_NAME, list);
            }
            return isDelete;
        } catch (IOException e) {
            throw new RuntimeException("删除关联失败", e);
        }
    }
}
