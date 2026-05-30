
package com.neu.tms.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.tms.pojo.Bed;
import com.neu.tms.utils.PersistentIdGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BedDao {
    public static final File FILE_NAME = new File("data\\beds.json");
    private final ObjectMapper om = new ObjectMapper();

    /**
     * 添加床位
     */
    public String addBed(Bed bed) throws IOException {
        try {
            List<Bed> bedList = findAll();
            bed.setId(PersistentIdGenerator.getInstance().nextId());
            bedList.add(bed);
            om.writeValue(FILE_NAME, bedList);
            return "添加成功";
        } catch (Exception e) {
            throw new RuntimeException("添加床位失败", e);
        }
    }

    /**
     * 查询所有床位
     */
    public List<Bed> findAll() {
        if (!FILE_NAME.exists()) {
            return new ArrayList<>();
        }
        try {
            return om.readValue(FILE_NAME, new TypeReference<List<Bed>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据ID查询
     */
    public Bed findById(Integer id) {
        List<Bed> allBeds = findAll();
        return allBeds.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据房间号查询床位列表
     */
    public List<Bed> findByRoomNo(Integer roomNo) {
        List<Bed> allBeds = findAll();
        return allBeds.stream()
                .filter(b -> b.getRoomNo().equals(roomNo))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 查询空闲床位
     */
    public List<Bed> findAvailableBeds() {
        List<Bed> allBeds = findAll();
        return allBeds.stream()
                .filter(b -> b.getBedStatus() == 1)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据房间号查询空闲床位
     */
    public List<Bed> findAvailableBedsByRoom(Integer roomNo) {
        List<Bed> allBeds = findAll();
        return allBeds.stream()
                .filter(b -> b.getRoomNo().equals(roomNo) && b.getBedStatus() == 1)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 更新床位
     */
    public boolean updateBed(Bed bed) {
        try {
            List<Bed> bedList = findAll();
            boolean isUpdate = false;

            for (int i = 0; i < bedList.size(); i++) {
                if (bedList.get(i).getId().equals(bed.getId())) {
                    bedList.set(i, bed);
                    isUpdate = true;
                    break;
                }
            }

            if (isUpdate) {
                om.writeValue(FILE_NAME, bedList);
            }
            return isUpdate;
        } catch (IOException e) {
            throw new RuntimeException("修改床位异常", e);
        }
    }

    /**
     * 统计床位信息
     */
    public int[] getBedStatistics() {
        List<Bed> allBeds = findAll();
        int total = allBeds.size();
        int available = 0;
        int occupied = 0;
        int outward = 0;

        for (Bed bed : allBeds) {
            switch (bed.getBedStatus()) {
                case 1: available++; break;
                case 2: occupied++; break;
                case 3: outward++; break;
            }
        }

        return new int[]{total, available, occupied, outward};
    }
}
