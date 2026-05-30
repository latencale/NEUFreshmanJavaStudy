package com.neu.tms.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 修复版：持久化整数ID生成器
 * 解决空文件、数字格式异常问题
 */
public class PersistentIdGenerator {
    // 存储最后ID的文件路径
    private static final String ID_FILE = "data\\last-id.txt";
    private int currentId;
    private final ReentrantLock lock = new ReentrantLock();

    // 单例
    //2.在类内部先实例化一个出来
    private static final PersistentIdGenerator INSTANCE = new PersistentIdGenerator();
    //1.将构造方法变成私有的
    private PersistentIdGenerator() {
        this.currentId = loadLastIdFromFile();
    }
    //2.做一个公有
    public static PersistentIdGenerator getInstance() {
        return INSTANCE;
    }

    public int nextId() {
        lock.lock();
        try {
            currentId++;
            saveIdToFile(currentId);
            return currentId;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 安全加载ID（修复空文件、格式错误）
     */
    private int loadLastIdFromFile() {
        File file = new File(ID_FILE);
        if (!file.exists()) {
            return 0;
        }

        try {
            String content = Files.readString(file.toPath()).trim();

            // 空内容 → 返回0
            if (content.isEmpty()) {
                return 0;
            }

            // 安全解析
            return Integer.parseInt(content);

        } catch (Exception e) {
            // 解析失败 → 从0开始，不抛错
            return 0;
        }
    }

    /**
     * 保存最新ID
     */
    private void saveIdToFile(long id) {
        try (FileWriter writer = new FileWriter(ID_FILE)) {
            writer.write(String.valueOf(id));
        } catch (IOException e) {
            throw new RuntimeException("保存ID失败", e);
        }
    }

    // 测试
    public static void main(String[] args) {
        PersistentIdGenerator generator = PersistentIdGenerator.getInstance();
        for (int i = 0; i < 5; i++) {
            System.out.println("生成ID: " + generator.nextId());
        }
    }
}