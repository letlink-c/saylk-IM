package com.saylk.jwt.controller;

import com.saylk.jwt.pojo.User;
import com.saylk.jwt.util.JwtUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
public class LoginController {
    /**
     * token测试用例
     */
    private final String USERNAME = "admin";
    private final String PASSWORD = "123";

    @Resource
    private JwtUtil jwtUtil;

    /**
     * 用户认证通过后，签发token
     * @param user
     * @return
     */

    @PostMapping("/login")
    public User login(@RequestBody User user){
        if(USERNAME.equals(user.getUsername()) && PASSWORD.equals(user.getPassword())){
            user.setToken(new JwtUtil().createToken(user.getUsername()));
            return user;
        }
        return null;
    }

    /**
     * 前端将token字符串封装在 header: authorization中
     * 直接解析token，如果抛出异常，则认证失败
     * @return
     */

    @PostMapping("/parseLogin")
    public String parseLogin(){
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("authorization");
        System.out.println(token);
        if(token != null){
            try {
                jwtUtil.parseToken(token);
                return "true";
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return "false";
    }
}
