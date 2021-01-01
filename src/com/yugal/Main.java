package com.yugal;

import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Scanner;

public class Main {

    private static Scanner sc;
    private static String path;
    private static MasterObject master;

    public static void main(String[] args) throws IOException, ParseException {
        File directory = new File("C:\\Fireworks\\Datastore");
        if (!directory.exists()) directory.mkdirs();
        directory = new File("C:\\Fireworks\\MultipleThreads");
        if (!directory.exists()) directory.mkdirs();

        simulateMultipleThreads();

//        simulateLibrary();
    }

    private static void simulateLibrary() throws ParseException, IOException {
        sc = new Scanner(System.in);

        setPath();

        try {
            master = new MasterObject(path + "master.json");
        } catch (IOException e) {
            print("File is already locked by another process! Exiting");
            System.exit(10);
        }

        FileLock fl = master.enableLock();
        getInput();

        fl.release();
    }

    private static void simulateMultipleThreads() throws ParseException, IOException {
        MasterObject master;

        master = new MasterObject("C:\\Fireworks\\MultipleThreads\\" + "master.json");

        MasterObject finalMaster = master;

        Thread thread1 = new Thread(() -> {
            try {
                finalMaster.create100randomKeys();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                finalMaster.create100randomKeys();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
    }

    private static void getInput() throws IOException, ParseException {
        while (true) {
            String x = input("\nEnter a CRD operation:");

            if (x.contains("create")) {
                String[] splits = x.split("\\s");
                if (splits.length > 4 | splits.length < 2) {
                    print("Invalid command. Try Again!");
                    continue;
                }
                String key = splits[1];
                String value = splits[2];

                if (key.length() > 32) {
                    print("Length of key greater than 32 chars");
                    continue;
                }

                RandomJsonObject valueJson = new RandomJsonObject(value);

                if (valueJson.getSize() > 16 * 1024) {
                    print("Value Json object bigger than 16kb");
                    continue;
                }

                if (master.getSize() > 1024 * 1024 * 1024) {
                    print("data store bigger than 1 gb");
                    continue;
                }

                if (master.add(key, valueJson.json)) {
                    if (splits.length == 4) {
                        try {
                            master.timeToLive(key, Long.parseLong(splits[3]) * 1000);
                        } catch (NumberFormatException e) {
                            print("Enter a valid number of seconds");
                        }
                    }
                }

            } else if (x.contains("read")) {
                String[] splits = x.split("\\s");
                if (splits.length != 2) {
                    print("Invalid command. Try Again!");
                    continue;
                }
                String key = splits[1];

                print(master.read(key).toString());

            } else if (x.contains("delete")) {
                String[] splits = x.split("\\s");
                if (splits.length != 2) {
                    print("Invalid command. Try Again!");
                    continue;
                }
                String key = splits[1];

                master.delete(key);
            } else if (x.equals("list all")) {
                master.listAllKeys();
            } else if (x.equals("close")) {
                break;
            } else print("Invalid command!");
        }
    }

    private static void setPath() {
        path = input("\nEnter a location for the store(enter \"d\" if you want to use the default location)");
        if (path.equals("d")) path = "C:\\Fireworks\\Datastore";
        if (!new File(path).exists()) {
            print("Not a valid directory!");
            setPath();
        }
        path += "\\";
    }

    private static void print(Object s) {
        System.out.println(s);
    }

    private static String input(Object s) {
        System.out.println(s);
        return sc.nextLine();
    }

}
