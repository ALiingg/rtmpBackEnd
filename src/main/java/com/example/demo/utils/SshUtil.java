package com.example.demo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;


public class SshUtil {
    private static String DEFAULT_CHAR_SET = "UTF-8";
//    private static String tipStr = "=======================%s=======================";
//    private static String splitStr = "=====================================================";

    /**
     * 登录主机
     * @return
     *      登录成功返回true，否则返回false
     */
    public static Connection login(String ip, String userName, String password){
        boolean isAuthenticated = false;
        Connection conn = null;
        long startTime = Calendar.getInstance().getTimeInMillis();
        try {
            conn = new Connection(ip);
            conn.connect(); // 连接主机

            isAuthenticated = conn.authenticateWithPassword(userName, password); // 认证
            if(isAuthenticated){
//                System.out.println(String.format(tipStr, "认证成功"));
            } else {
//                System.out.println(String.format(tipStr, "认证失败"));
            }
        } catch (IOException e) {
//            System.err.println(String.format(tipStr, "登录失败"));
            e.printStackTrace();
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
//        System.out.println("登录用时: " + (endTime - startTime)/1000.0 + "s\n" + splitStr);
        return conn;
    }

    /**
     * 远程执行shell脚本或者命令
     * @param cmd
     *      即将执行的命令
     * @return
     *      命令执行完后返回的结果值
     */
    public static String execute(Connection conn, String cmd) {
        String result = "";
        Session session = null;
        try {
            if (conn != null && conn.isAuthenticationComplete()) {
                session = conn.openSession();  // Open a session
                session.execCommand(cmd);      // Execute the command
                result = processStdout(session.getStdout(), DEFAULT_CHAR_SET);

                if (StringUtils.isBlank(result)) {
                    result = processStdout(session.getStderr(), DEFAULT_CHAR_SET); // Check for errors
                }
            } else {
                throw new IllegalStateException("You need to establish a connection first.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return result;
    }

    /**
     * 解析脚本执行返回的结果集
     * @param in 输入流对象
     * @param charset 编码
     * @return
     *       以纯文本的格式返回
     */
    private static String processStdout(InputStream in, String charset){
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line = null;
            while((line = br.readLine()) != null){
                buffer.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("解析脚本出错：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("解析脚本出错：" + e.getMessage());
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static ArrayList<ArrayList<String>> startConnect(){
        String ip = "180.159.15.171";   // 此处根据实际情况，换成自己需要访问的主机IP
        String userName = "root";
        String password = "Sky061104";
        Connection conn =  SshUtil.login(ip, userName, password);

        String cmd = "ls /usr/local/nginx/temp/records";
        String cmd2 = "cd /usr/local/nginx/temp/records && ls -l | awk '{print $5}'";
        String result = SshUtil.execute(conn, cmd);
        String result2 = SshUtil.execute(conn, cmd2);
        System.out.println(result2);
//        System.out.println();
        String[] temp = result.split("\n");
        String[] temp2 = result2.split("\n");
//        for (int i = 0; i < temp2.length; i ++) {
//            System.out.println(temp2[i]);
//        }
        ArrayList<ArrayList<String>> lines = new ArrayList<>();

        for (int i = 0; i < temp.length; i++) {
            ArrayList<String> line = new ArrayList<>();

            line.add(temp[i]);
            line.add(temp2[i + 1]);

            lines.add(line);

        }
        return lines;
    }
}