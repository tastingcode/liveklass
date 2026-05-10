package com.liveklass.domain.user;


import com.liveklass.domain.BaseEntity;
import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {
	@Column(nullable = false, unique = true, length = 100)
	private String loginId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserRole userRole;

	public static UserEntity from(UserCommand.Create command){
		UserEntity user = new UserEntity();
		user.loginId = command.loginId();
		user.userRole = command.toUserRole();
		user.guard();
		return user;
	}

	@Override
	protected void guard() {
		if (loginId == null || loginId.isBlank()) {
			throw new CoreException(ErrorType.BAD_REQUEST, "로그인 ID는 필수입니다.");
		}
		if (userRole == null ) {
			throw new CoreException(ErrorType.BAD_REQUEST, "사용자 역할은 필수입니다.");
		}
	}





}
