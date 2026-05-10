package com.liveklass.domain.course;

import com.liveklass.domain.BaseEntity;
import com.liveklass.support.error.CoreException;
import com.liveklass.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "course")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseEntity extends BaseEntity {

	@Column(nullable = false)
	private Long creatorId;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CourseStatus status;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private int capacity;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	public static CourseEntity from(CourseCommand.Create command){
		CourseEntity course = new CourseEntity();
		course.creatorId = command.userId();
		course.title = command.title();
		course.description = command.description();
		course.status = CourseStatus.DRAFT;
		course.price = command.price();
		course.capacity = command.capacity();
		course.startDate = command.startDate();
		course.endDate = command.endDate();
		course.guard();
		return course;
	}

	@Override
	protected void guard() {
		if (creatorId == null || creatorId <= 0) {
			throw new CoreException(ErrorType.BAD_REQUEST, "크리에이터 ID는 양수여야 합니다.");
		}
		if (title == null || title.isBlank()) {
			throw new CoreException(ErrorType.BAD_REQUEST, "강의 제목은 필수입니다.");
		}
		if (description == null || description.isBlank()) {
			throw new CoreException(ErrorType.BAD_REQUEST, "강의 설명은 필수입니다.");
		}
		if (price < 0) {
			throw new CoreException(ErrorType.BAD_REQUEST, "가격은 0 이상이어야 합니다.");
		}
		if (capacity <= 0) {
			throw new CoreException(ErrorType.BAD_REQUEST, "정원은 1명 이상이어야 합니다.");
		}
		if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
			throw new CoreException(ErrorType.BAD_REQUEST, "수강 종료일은 시작일과 같거나 이후여야 합니다.");
		}
		if (status == null) {
			throw new CoreException(ErrorType.BAD_REQUEST, "강의 상태는 필수입니다.");
		}
	}

	public void open(){
		this.status = CourseStatus.OPEN;
	}

	public void close(){
		this.status = CourseStatus.CLOSED;
	}
}
