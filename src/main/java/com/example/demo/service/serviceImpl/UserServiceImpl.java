package com.example.demo.service.serviceImpl;

import com.example.demo.domain.User;
import com.example.demo.repository.UserDao;
import com.example.demo.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Resource

    private UserDao userDao;
    @Override
    public User loginService(String uname, String password) {
        User user = userDao.findByUnameAndPassword(uname, password);
        if (user == null) {
            user.setPassword("");
        }
        return user;
    }

    @Override
    public User registService(User user) {
        if(userDao.findByUname(user.getUsername()) != null) {
            return null;
        } else {
            User newUser = userDao.save(user);
//            if (newUser != null) {
//                newUser.setPassword("");
//            }
            return newUser;
        }
    }

    @Override
    public User updateToken(User user) {

        User newUser = userDao.save(user);
        return newUser;
    }
    public User tokenService(String token) {
        User user = userDao.findByToken(token);
        return user;
    }

}
