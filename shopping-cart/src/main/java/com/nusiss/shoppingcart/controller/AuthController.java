package com.nusiss.shoppingcart.controller;

import com.nusiss.shoppingcart.entity.User;
import com.nusiss.shoppingcart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * 用户认证控制器
 * @author SpringCA Team
 */
@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String showLoginPage(HttpSession session, Model model) {
        // 如果用户已登录，重定向到首页
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        return "auth/login";
    }
    
    /**
     * 处理用户登录
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        try {
            Optional<User> userOpt = userService.login(username, password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // 将用户信息存储到Session中
                session.setAttribute("currentUser", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                
                log.info("用户 {} 登录成功", username);
                redirectAttributes.addFlashAttribute("successMessage", "登录成功！欢迎回来，" + user.getFirstName());
                
                // 检查是否有重定向URL
                String redirectUrl = (String) session.getAttribute("redirectUrl");
                if (redirectUrl != null) {
                    session.removeAttribute("redirectUrl");
                    return "redirect:" + redirectUrl;
                }
                
                return "redirect:/products";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "用户名或密码错误");
                return "redirect:/auth/login";
            }
        } catch (Exception e) {
            log.error("登录过程中发生错误：{}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "登录失败，请稍后重试");
            return "redirect:/auth/login";
        }
    }
    
    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String showRegisterPage(HttpSession session, Model model) {
        // 如果用户已登录，重定向到首页
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        model.addAttribute("user", new User());
        return "auth/register";
    }
    
    /**
     * 处理用户注册
     */
    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                          @RequestParam String confirmPassword,
                          RedirectAttributes redirectAttributes) {
        
        try {
            // 验证密码确认
            if (!user.getPassword().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "两次输入的密码不一致");
                return "redirect:/auth/register";
            }
            
            // 验证用户名长度
            if (user.getUsername().length() < 3) {
                redirectAttributes.addFlashAttribute("errorMessage", "用户名长度至少为3个字符");
                return "redirect:/auth/register";
            }
            
            // 验证密码长度
            if (user.getPassword().length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage", "密码长度至少为6个字符");
                return "redirect:/auth/register";
            }
            
            // 尝试注册用户
            if (userService.register(user)) {
                log.info("用户 {} 注册成功", user.getUsername());
                redirectAttributes.addFlashAttribute("successMessage", "注册成功！请登录");
                return "redirect:/auth/login";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "注册失败：用户名或邮箱已存在");
                return "redirect:/auth/register";
            }
        } catch (Exception e) {
            log.error("注册过程中发生错误：{}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "注册失败，请稍后重试");
            return "redirect:/auth/register";
        }
    }
    
    /**
     * 用户注销
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");
        
        // 清除Session
        session.invalidate();
        
        log.info("用户 {} 注销成功", username);
        redirectAttributes.addFlashAttribute("successMessage", "您已成功注销");
        return "redirect:/";
    }
    
    /**
     * 检查用户名是否可用（AJAX接口）
     */
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        return userService.isUsernameAvailable(username);
    }
    
    /**
     * 检查邮箱是否可用（AJAX接口）
     */
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return userService.isEmailAvailable(email);
    }
}