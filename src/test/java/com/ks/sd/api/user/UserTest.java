package com.ks.sd.api.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ks.sd.api.user.dto.UserSaveRequest;
import com.ks.sd.api.user.repository.UserRepository;

@SpringBootTest
public class UserTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void save() {
        String userId = "simkyuseung";
        String userNm = "";
        String password = "";

        // request 받은 DTO
        UserSaveRequest userSaveRequest = new UserSaveRequest(userId, userNm, password);

        // 비밀번호 초기화
        userSaveRequest.setPassword("1234");

        // DB 저장
        userRepository.save(userSaveRequest.toEntity());
    }
}
