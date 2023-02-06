//package com;
//
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.util.Random;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class MessageCreate {
//    /*
//    read user_info -> create message -> return to publisher
//     */
//    static String path = "D:\\develop\\site-data-gcp\\data\\analysis-data\\user_info.json";
//
//    public static void readJson() {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode json = objectMapper.readTree(Paths.get(path).toFile());
//            System.out.println(json.get(0).get("user_id").toString());
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
//
