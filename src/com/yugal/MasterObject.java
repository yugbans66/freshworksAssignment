package com.yugal;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.channels.FileLock;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MasterObject {
    private final Timer timer = new Timer();
    private final File file;
    private RandomAccessFile raf;
    private JSONObject json;

    public MasterObject(String filepath) throws IOException, ParseException {
        json = new JSONObject();
        file = new File(filepath);
        parse();
    }

    public FileLock enableLock() throws IOException {
        return raf.getChannel().lock();
    }

    public synchronized boolean add(String key, JSONObject value) throws IOException {
        if (!json.containsKey(key)) {
            json.put(key, value);
            save();
            return true;
        } else {
            System.out.println("Key is already there!. Try Again!");
            return false;
        }
    }

    public synchronized JSONObject read(String key) throws ParseException {
        JSONObject value = null;
        if (json.containsKey(key)) {
            JSONParser parser = new JSONParser();
            value = (JSONObject) parser.parse(json.get(key).toString());
        } else System.out.println("Not present in data store!");
        return value;
    }

    public synchronized void delete(String key) throws IOException, ParseException {
        if (json.containsKey(key)) {
            json.remove(key);
            System.out.println(key + " deleted!");
            save();
        } else System.out.println("Not present in data store!");
    }

    public synchronized void timeToLive(String key, long delay) {
        if (json.containsKey(key)) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        delete(key);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, delay);
        }
    }

    public synchronized void listAllKeys() {
        Set<String> keys = json.keySet();
        if (keys.size() == 0) {
            System.out.println("No keys in store");
            return;
        }
        for (String s : keys) System.out.println(s);
    }


    private synchronized void parse() throws IOException, ParseException {
        if (!file.exists()) {
            raf = new RandomAccessFile(file, "rw");
            save();
        } else {
            StringWriter buffer = new StringWriter();
            if (raf == null) raf = new RandomAccessFile(file, "rw");
            while (raf.getFilePointer() < raf.length()) {
                buffer.append(raf.readLine());
            }
            json = (JSONObject) new JSONParser().parse(buffer.toString());
        }
    }

    private synchronized void save() throws IOException {
        raf.setLength(0);
        raf.write(json.toString().getBytes());
    }

    public synchronized long getSize() {
        return file.length();
    }

    public synchronized void create100randomKeys() throws IOException {
        for (int i = 0; i < 100; i++) {
            json.put(i, i + 1);
            System.out.println(i + " key is added to datastore");
        }
        save();
    }
}
