package com.nusiss.shoppingcart.service;

import com.nusiss.shoppingcart.entity.User;
import com.nusiss.shoppingcart.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户业务逻辑层
 * @author SpringCA Team
 */
@Service
@Transactional(readOnly = true)
public class UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @return 用户对象（如果验证成功）
     */
    public Optional<User> login(String username, String password) {
        try {
            // 简单的密码验证（实际项目中应该使用加密）
            Optional<User> userOpt = userRepository.findByUsernameAndPassword(username, password);
            if (userOpt.isPresent()) {
                log.info("用户 {} 登录成功", username);
                return userOpt;
            } else {
                log.warn("用户 {} 登录失败：用户名或密码错误", username);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("用户登录过程中发生错误：{}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * 用户注册
     * @param user 用户对象
     * @return 注册是否成功
     */
    @Transactional
    public boolean register(User user) {
        try {
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(user.getUsername())) {
                log.warn("注册失败：用户名 {} 已存在", user.getUsername());
                return false;
            }
            
            // 检查邮箱是否已存在
            if (userRepository.existsByEmail(user.getEmail())) {
                log.warn("注册失败：邮箱 {} 已存在", user.getEmail());
                return false;
            }
            
            // 保存用户
            userRepository.save(user);
            log.info("用户 {} 注册成功", user.getUsername());
            return true;
        } catch (Exception e) {
            log.error("用户注册过程中发生错误：{}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户对象
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户对象
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 是否可用
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    /**
     * 检查邮箱是否可用
     * @param email 邮箱
     * @return 是否可用
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新是否成功
     */
    @Transactional
    public boolean updateUser(User user) {
        try {
            userRepository.save(user);
            log.info("用户 {} 信息更新成功", user.getUsername());
            return true;
        } catch (Exception e) {
            log.error("更新用户信息时发生错误：{}", e.getMessage());
            return false;
        }
    }
}