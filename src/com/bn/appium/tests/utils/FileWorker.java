package com.bn.appium.tests.utils;

import com.bn.appium.tests.manager.TestManager;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nikolai on 14.01.14.
 */
public class FileWorker {
    private String fileName;
    private File currentFile;
    private String testName = null;

    public FileWorker(String fileName) {
        this.fileName = fileName;
    }

    public FileWorker() {
        this.fileName = MainConstants.FILE_NAME_RANDOM_TESTS;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void writeLog(ItemLog itemLog){
        BufferedWriter bw = null;
        currentFile = new File(fileName);
        try {
            if(!currentFile.exists()){
                currentFile.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(currentFile.getAbsoluteFile(), true));
            int number = getLineNumberFile();
            number++;
            String log = itemLog.toString();
            bw.write(log);
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
        String name = itemLog.getDate() + "_" + itemLog.getTime() + "_" + (itemLog.getTestResult() ? "pass" : "fail") +
                "_" + itemLog.getTestName() + "_" + itemLog.getTestAction();
        TestManager.sleep(MainConstants.SCREENSHOT_TIMEOUT);
        TestManager.captureScreenshot(name);
    }

    public Date write (String text) {
        BufferedWriter bw = null;
        currentFile = new File(fileName);
        Date date = new Date();
        try {
            if(!currentFile.exists()){
                currentFile.createNewFile();
            }

            bw = new BufferedWriter(new FileWriter(currentFile.getAbsoluteFile(), true));

            int number = getLineNumberFile();
            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String log = String.format(MainConstants.RANDOM_TEST_LOG, ++number, simpleTimeFormat.format(date),
                    simpleDateFormat.format(date), (testName==null ? "" : testName), text);

            bw.write(log);
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
        return date;
    }

    private int getLineNumberFile() throws IOException {
        LineNumberReader  lnr = new LineNumberReader(new FileReader(new File(fileName)));
        lnr.skip(Long.MAX_VALUE);
        return lnr.getLineNumber();
    }
}
