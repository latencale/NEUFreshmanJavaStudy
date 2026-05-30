package com.neu.tms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.tms.pojo.TUser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JSONStudy01 {

    public static void main(String[] args) throws IOException {
        File file01 = new File("E:\\NEU\\shixun\\shixun1\\data\\User.txt");
        FileWriter fw = null;
        BufferedWriter bw = null;

        try{//目的：把一个管理员存到文件里
            //1。实例化一个对象，类型为TUser
            TUser admin01 = new TUser();
            admin01.setId(1);
            admin01.setUsername("admin");
            admin01.setPassword("admin");

            //TUser admin02 = new TUser(2, "admin", "admin");

            //2.把对象装换成json字符串格式
            ObjectMapper om = new ObjectMapper();
            //调用工具类的writeValueAsString object->String (lib文件夹里的工具)
            String str = om.writeValueAsString(admin01);

            //3.调用 Java文件相关方法，存到文件里
            //用字符流相关的类FileWriter BufferedWriter
            //fw = new FileWriter(file01,true);//需要追加的时候，在加true
            fw = new FileWriter(file01);
            bw = new BufferedWriter(fw);
            bw.write(str);


            //目的：读出数据
            //1.从文件把对象读出来


            //2，把对象装换成java格式
            TUser userRead = om.readValue(str, TUser.class);
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            //一定会执行的代码，在这里做清理和关流的工作
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
