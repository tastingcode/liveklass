package com.liveklass.infrastructure.user;

import com.liveklass.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
	boolean existsByLoginId(String loginId);

}
