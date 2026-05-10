package com.liveklass.infrastructure.user;

import com.liveklass.domain.user.UserEntity;
import com.liveklass.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
	private final UserJpaRepository userJpaRepository;

	@Override
	public UserEntity save(UserEntity user) {
		return userJpaRepository.save(user);
	}

	@Override
	public boolean existsByLoginId(String loginId) {
		return userJpaRepository.existsByLoginId(loginId);
	}

	@Override
	public Optional<UserEntity> findById(Long userId) {
		return userJpaRepository.findById(userId);
	}
}
