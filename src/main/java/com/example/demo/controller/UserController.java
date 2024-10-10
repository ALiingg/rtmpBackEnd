package com.example.demo.controller;


import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.service.serviceImpl.UserServiceImpl;
import com.example.demo.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.utils.randomUUID;


@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @PostMapping("/login")
    public Result<User> loginController(@RequestParam String uname, @RequestParam String password){
        User user = userService.loginService(uname, password);
        if(user!=null){
            String token = randomUUID.getUUID();
            user.setToken(token);
            userService.updateToken(user);
            System.out.println(token);
            return Result.success(user,"登录成功！");
        }else{
            return Result.error("123","账号或密码错误！");
        }
    }

    @PostMapping("/register")
    public Result<User> registController(@RequestBody User newUser){
        User user = userService.registService(newUser);
        if(user!=null){
            return Result.success(user,"注册成功！");
        }else{
            return Result.error("456","用户名已存在！");
        }
    }
    @PostMapping("/tokenlogin")
    public Result<User> tokenLoginController(@RequestBody String token){
        User user = userServiceImpl.tokenService(token);
        System.out.println(token);
        if(user!=null){
            return Result.success(user, "token valid");
        } else {
            return Result.error("456","token invalid");
        }
    }
}