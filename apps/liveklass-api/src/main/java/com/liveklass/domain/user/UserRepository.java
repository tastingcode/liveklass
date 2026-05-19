package com.liveklass.domain.user;

import java.util.Optional;

public interface UserRepository {
	UserEntity save(UserEntity user);

	boolean existsByLoginId(String loginId);

	Optional<UserEntity> findById(Long userId);

	Optional<UserEntity> findByLoginId(String loginId);
}
