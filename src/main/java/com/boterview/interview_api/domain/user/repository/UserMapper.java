package com.boterview.interview_api.domain.user.repository;

import com.boterview.interview_api.domain.user.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

        @Insert("INSERT INTO `user` (user_id, email, password, name, created_at, oauth) " +
                        "VALUES (#{userId}, #{email}, #{password}, #{name}, #{createdAt}, #{oauth})")
        void insert(User user);

        @Update("UPDATE `user` SET email = #{email}, password = #{password}, name = #{name}, oauth = #{oauth} " +
                        "WHERE user_id = #{userId}")
        void update(User user);

        @Select("SELECT * FROM `user` WHERE email = #{email}")
        Optional<User> findByEmail(String email);

        @Update("UPDATE `user` SET password = #{password} WHERE user_id = #{userId}")
        void updatePassword(@Param("userId") String userId, @Param("password") String password);

        @Select("SELECT * FROM `user` WHERE user_id = #{userId}")
        Optional<User> findById(String userId);

        @Delete("DELETE FROM `user` WHERE user_id = #{userId}")
        void delete(String userId);


}
