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

    // Default character set used for encoding/decoding
    private static String DEFAULT_CHAR_SET = "UTF-8";

    /**
     * Logs into a remote host via SSH.
     * @param ip IP address of the host to connect to
     * @param userName Username for authentication
     * @param password Password for authentication
     * @return Connection object if login is successful; null otherwise
     */
    public static Connection login(String ip, String userName, String password) {
        boolean isAuthenticated = false;
        Connection conn = null;
        long startTime = Calendar.getInstance().getTimeInMillis(); // Start time for login attempt

        try {
            conn = new Connection(ip); // Create a connection instance
            conn.connect(); // Connect to the host

            // Authenticate with provided username and password
            isAuthenticated = conn.authenticateWithPassword(userName, password);
            if (isAuthenticated) {
                // Connection authenticated successfully
            } else {
                // Authentication failed
            }
        } catch (IOException e) {
            // Handle connection error
            e.printStackTrace();
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        // Log the time taken for the login attempt (optional)
        return conn;
    }

    /**
     * Executes a shell command on a remote host via SSH.
     * @param conn Established SSH connection
     * @param cmd Command to execute on the remote host
     * @return The output of the executed command as a String
     */
    public static String execute(Connection conn, String cmd) {
        String result = "";
        Session session = null;
        try {
            // Check if the connection is authenticated before proceeding
            if (conn != null && conn.isAuthenticationComplete()) {
                session = conn.openSession();  // Open a session with the host
                session.execCommand(cmd);      // Execute the provided command

                // Capture standard output of the command
                result = processStdout(session.getStdout(), DEFAULT_CHAR_SET);

                // If there is no output, check the standard error for any error messages
                if (StringUtils.isBlank(result)) {
                    result = processStdout(session.getStderr(), DEFAULT_CHAR_SET);
                }
            } else {
                throw new IllegalStateException("You need to establish a connection first.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close(); // Close the session after execution
            }
        }
        return result;
    }

    /**
     * Processes the output stream from the executed command.
     * @param in InputStream from the command execution
     * @param charset Character set used for decoding the output
     * @return The command output as a plain text String
     */
    private static String processStdout(InputStream in, String charset) {
        InputStream stdout = new StreamGobbler(in); // Wrap InputStream in StreamGobbler to handle output efficiently
        StringBuffer buffer = new StringBuffer();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line + "\n"); // Append each line to the buffer
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error processing output: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error processing output: " + e.getMessage());
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * Establishes an SSH connection, executes commands to retrieve file information,
     * and parses the results into a 2D list.
     * @return A 2D ArrayList containing file information with each inner list representing a file's details (filename, size, and timestamp)
     */
    public static ArrayList<ArrayList<String>> startConnect() {
        // SSH connection details for the host
        String ip = "localhost";
        String userName = "root";
        String password = "Sky061104";

        // Establish an SSH connection
        Connection conn = SshUtil.login(ip, userName, password);

        // Commands to list filenames, sizes, and timestamps in a directory
        String cmd = "ls -t /usr/local/nginx/temp/records"; // Lists files by time in the specified directory
        String cmd2 = "cd /usr/local/nginx/temp/records && ls -lt | awk '{print $5}'"; // Gets file sizes
        String cmd3 = "cd /usr/local/nginx/temp/records && ls -lt | awk '{printf \"%s %s %s\\n\", $6,$7,$8}'"; // Gets file timestamps

        // Execute each command and capture the output
        String result = SshUtil.execute(conn, cmd);  // List of filenames
        String result2 = SshUtil.execute(conn, cmd2); // List of file sizes
        String result3 = SshUtil.execute(conn, cmd3); // List of file timestamps

        // Split command output by newline to create arrays of data
        String[] temp = result.split("\n");
        String[] temp2 = result2.split("\n");
        String[] temp3 = result3.split("\n");

        // 2D list to store details of each file (filename, size, timestamp)
        ArrayList<ArrayList<String>> lines = new ArrayList<>();

        // Loop through each file and compile its details into the list
        for (int i = 0; i < temp.length; i++) {
            ArrayList<String> line = new ArrayList<>();
            line.add(temp[i]);        // Add filename
            line.add(temp2[i + 1]);   // Add file size (skip first line if needed)
            line.add(temp3[i + 1]);   // Add timestamp (skip first line if needed)
            lines.add(line);          // Add compiled line to the main list
        }
        return lines;
    }
}
