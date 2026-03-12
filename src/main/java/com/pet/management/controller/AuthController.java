package com.pet.management.controller;

import com.pet.management.entity.User;
import com.pet.management.service.UserService;
import com.pet.management.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody(required = false) Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();

        logger.info("收到登录请求，credentials: {}", credentials);

        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            User user = userService.findByUsername(username);
            if (Objects.isNull(user)) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return response;
            }

            // [DEBUG] 打印提交的密码和数据库里的密码
            logger.info("加密的密码: [{}]", passwordEncoder.encode(password));
            logger.info("提交的明文密码: [{}]", password);
            logger.info("数据库密文密码: [{}]", user.getPassword());

            // 手动验证一下
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            boolean matches = encoder.matches(password, user.getPassword());
            logger.info("BCryptPasswordEncoder.matches 结果: {}", matches);

            // 如果不想抛错，可以临时注释掉 authenticationManager 的检查或依赖手动结果
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            // 生成真正的 JWT Token
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            response.put("success", true);
            response.put("token", token);
            response.put("user", user);

        } catch (org.springframework.security.core.AuthenticationException e) {
            logger.error("认证失败：error={}", e.getMessage());
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        } catch (Exception e) {
            logger.error("登录过程发生异常：error={}", e.getMessage());
            logger.error("详细错误堆栈", e);
            response.put("success", false);
            response.put("message", "登录失败：" + e.getMessage());
        }

        return response;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        Map<String, Object> response = new HashMap<>();
        // 无状态 JWT 模式下，服务端无需操作，客户端清除 Token 即可
        response.put("success", true);
        response.put("message", "已登出");
        return response;
    }

    @GetMapping("/profile")
    public Map<String, Object> getProfile() {
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user != null) {
            response.put("success", true);
            response.put("user", user);
        } else {
            response.put("success", false);
            response.put("message", "用户不存在");
        }
        return response;
    }
}