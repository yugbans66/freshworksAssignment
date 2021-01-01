package com.yugal;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class RandomJsonObject {
    public JSONObject json;

    public RandomJsonObject(String value) {
        json = new JSONObject();
        for (int i = 0; i < 100; i++) {
            json.put(randString(), value);
            json.put(randString(), value);
        }
    }

    private String randString() {
        String randomChars = "qwertyuiopasdfghjklkzxcvbnm.1234567890";
        StringBuilder str = new StringBuilder();
        Random rnd = new Random();
        while (str.length() < 8) {
            int index = (int) (rnd.nextFloat() * randomChars.length());
            str.append(randomChars.charAt(index));
        }
        return str.toString();
    }

    public long getSize() {
        PrintWriter out;
        String path = "C:\\Users\\asus\\Desktop\\project\\" + "temp.json";
        try {
            out = new PrintWriter(path);
            out.print(json.toJSONString());
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File file = new File(path);

        return file.length();
    }
}
