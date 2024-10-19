package com.example.demo.controller;

import com.example.demo.utils.SshUtil;
import com.example.demo.utils.dataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StreamController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/live")
    public String[] getLiveUrls() {

        String[] urls = new String[4];
        // Query the database and map the result to a list of strings
        for(int i = 1; i <= 4; i ++){

            int finalI = i;
            String sql = "SELECT url" + finalI + " FROM liveUrl";

            urls[i - 1] = jdbcTemplate.queryForObject(sql, String.class);
            System.out.println(urls[i - 1]);

        }
        // Convert the list to an array and return
        return urls;
    }
//    @GetMapping("/changeurl")
//    public boolean changeUrl(String url) {
//
//    }
    @GetMapping("/fetchreplays")
    public ArrayList<ArrayList<String>> getFetchReplayUrls() {


        return dataUtil.getReplaysUrl();
    }
}
