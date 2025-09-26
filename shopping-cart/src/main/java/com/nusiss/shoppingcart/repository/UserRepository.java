package com.nusiss.shoppingcart.repository;

import com.nusiss.shoppingcart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 * @author SpringCA Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户对象
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据用户名和密码查找用户（用于登录验证）
     * @param username 用户名
     * @param password 密码
     * @return 用户对象
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.password = :password AND u.active = true")
    Optional<User> findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 查找所有活跃用户
     * @return 活跃用户列表
     */
    @Query("SELECT u FROM User u WHERE u.active = true")
    java.util.List<User> findAllActiveUsers();
}