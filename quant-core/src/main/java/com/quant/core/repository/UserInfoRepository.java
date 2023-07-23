package com.quant.core.repository;

import com.quant.core.entity.CorpInfo;
import com.quant.core.entity.UserInfo;
import com.quant.core.enums.CorpState;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserInfoRepository extends JpaRepository<UserInfo, String> {


}
