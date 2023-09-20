package com.quant.core.repository;

import com.quant.core.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, String> {

    Long countByEmail(String email);
}
