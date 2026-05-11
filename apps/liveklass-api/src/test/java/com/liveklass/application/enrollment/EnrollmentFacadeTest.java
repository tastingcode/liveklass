package com.liveklass.application.enrollment;

import com.liveklass.domain.course.CourseCommand;
import com.liveklass.domain.course.CourseEntity;
import com.liveklass.domain.course.CourseRepository;
import com.liveklass.domain.enrollment.EnrollmentCommand;
import com.liveklass.domain.enrollment.EnrollmentEntity;
import com.liveklass.domain.enrollment.EnrollmentRepository;
import com.liveklass.domain.enrollment.EnrollmentService;
import com.liveklass.domain.user.UserCommand;
import com.liveklass.domain.user.UserEntity;
import com.liveklass.domain.user.UserRepository;
import com.liveklass.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class EnrollmentFacadeTest {
	private static final int CAPACITY = 5;
	private static final int ALREADY_ENROLLED_COUNT = 4;
	private static final int CONCURRENT_REQUEST_COUNT = 30;

	@Autowired
	private EnrollmentFacade enrollmentFacade;

	@Autowired
	private EnrollmentService enrollmentService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	@Autowired
	private DatabaseCleanUp databaseCleanUp;

	@AfterEach
	void tearDown() {
		databaseCleanUp.truncateAllTables();
	}

	@DisplayName("수강 정원 5명 강의에 4명이 신청한 상태에서 30명이 동시에 신청해도 최종 신청 인원은 5명을 넘지 않는다.")
	@Test
	void 수강_신청_시_동시성을_제어한다() throws InterruptedException {
		//given
		UserEntity creator = saveUser("creator", "CREATOR");
		CourseEntity course = courseRepository.save(openCourse(creator.getId(), CAPACITY));
		saveAlreadyEnrolledStudents(course);
		List<UserEntity> students = saveStudents(CONCURRENT_REQUEST_COUNT);

		ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_REQUEST_COUNT);
		CountDownLatch readyLatch = new CountDownLatch(CONCURRENT_REQUEST_COUNT);
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch doneLatch = new CountDownLatch(CONCURRENT_REQUEST_COUNT);
		AtomicInteger successCount = new AtomicInteger();
		ConcurrentLinkedQueue<Throwable> failures = new ConcurrentLinkedQueue<>();

		try {
			for (UserEntity student : students) {
				executorService.submit(() -> {
					readyLatch.countDown();
					try {
						startLatch.await();
						enrollmentFacade.enroll(new EnrollmentCriteria.Enroll(course.getId(), student.getId()));
						successCount.incrementAndGet();
					} catch (Throwable throwable) {
						failures.add(throwable);
					} finally {
						doneLatch.countDown();
					}
				});
			}

			assertThat(readyLatch.await(5, TimeUnit.SECONDS)).isTrue();

			//when
			startLatch.countDown();

			//then
			assertThat(doneLatch.await(20, TimeUnit.SECONDS)).isTrue();
			assertAll(
					() -> assertThat(enrollmentService.getApplicantsCount(course.getId())).isEqualTo(CAPACITY),
					() -> assertThat(successCount.get()).isEqualTo(1),
					() -> assertThat(failures).hasSize(CONCURRENT_REQUEST_COUNT - 1)
			);
		} finally {
			executorService.shutdownNow();
		}
	}

	private UserEntity saveUser(String loginId, String userRole) {
		return userRepository.save(UserEntity.from(new UserCommand.Create(loginId, userRole)));
	}

	private List<UserEntity> saveStudents(int count) {
		List<UserEntity> students = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			students.add(saveUser("student-" + i, "STUDENT"));
		}
		return students;
	}

	private CourseEntity openCourse(Long creatorId, int capacity) {
		CourseEntity course = CourseEntity.from(new CourseCommand.Create(
				creatorId,
				"자바 기초",
				"강의 설명",
				1000,
				capacity,
				LocalDate.now(),
				LocalDate.now().plusDays(30)
		));
		course.open();
		return course;
	}

	private void saveAlreadyEnrolledStudents(CourseEntity course) {
		for (int i = 0; i < ALREADY_ENROLLED_COUNT; i++) {
			UserEntity student = saveUser("already-enrolled-student-" + i, "STUDENT");
			enrollmentRepository.save(EnrollmentEntity.from(new EnrollmentCommand.Create(
					course.getId(),
					student.getId(),
					course.getCapacity()
			)));
		}
	}
}
