# 테스트 목록

이 문서는 현재 프로젝트의 테스트를 도메인별, 테스트 유형별로 정리합니다.

## 테스트 유형 기준

| 유형 | 기준 | 예시 |
| --- | --- | --- |
| 단위 테스트 | Entity, Service의 비즈니스 규칙을 mock 또는 순수 객체 중심으로 검증 | `xxEntityTest`, `xxServiceTest` |
| 통합 테스트 | Spring Context와 실제 Repository, 테스트 DB를 사용해 도메인 서비스 흐름을 검증 | `xxServiceIT` |
| E2E 테스트 | HTTP 요청부터 Controller, Facade, Service, Repository까지 전체 API 흐름을 검증 | `xxE2ETest` |


## 사용자(User)

### 단위 테스트

| 테스트 클래스 | 검증 내용 |
| --- | --- |
| `UserEntityTest` | 사용자 생성 시 로그인 ID와 역할이 설정되는지 검증 |
| `UserEntityTest` | 사용자 역할이 없으면 예외가 발생하는지 검증 |
| `UserServiceTest` | 이미 존재하는 로그인 ID로 가입 시 `CONFLICT` 예외가 발생하는지 검증 |
| `UserServiceTest` | 사용자 생성 시 사용자 정보가 반환되는지 검증 |

### 통합 테스트

| 테스트 클래스 | 검증 내용 |
| --- | --- |
| `UserServiceIT` | 이미 가입된 ID로 회원 가입 시 실패하는지 검증 |
| `UserServiceIT` | 회원 가입 시 사용자 정보가 저장되고 반환되는지 검증 |
| `UserServiceIT` | 존재하는 ID로 사용자 조회 시 사용자 정보가 반환되는지 검증 |
| `UserServiceIT` | 존재하지 않는 ID로 사용자 조회 시 빈 `Optional`이 반환되는지 검증 |

### E2E 테스트

| 테스트 클래스 | 검증 내용 |
| --- | --- |
| `UserV1ApiE2ETest` | `POST /api/v1/users` 회원 가입 성공 응답 검증 |
| `UserV1ApiE2ETest` | 회원 가입 시 역할이 없으면 `400 Bad Request` 응답을 반환하는지 검증 |
| `UserV1ApiE2ETest` | `GET /api/v1/users/me` 내 정보 조회 성공 응답 검증 |
| `UserV1ApiE2ETest` | 존재하지 않는 사용자 ID 조회 시 `404 Not Found` 응답을 반환하는지 검증 |

## 강의(Course)

### 단위 테스트

| 테스트 클래스 | 검증 내용 |
| --- | --- |
| `CourseEntityTest` | 강의 생성 시 상태가 `DRAFT`로 설정되는지 검증 |
| `CourseEntityTest` | 강의 가격이 음수이면 예외가 발생하는지 검증 |
| `CourseEntityTest` | 강의 오픈 시 상태가 `OPEN`으로 변경되는지 검증 |
| `CourseEntityTest` | 강의 마감 시 상태가 `CLOSED`로 변경되는지 검증 |
| `CourseServiceTest` | 강의 생성 시 강의 정보가 반환되는지 검증 |
| `CourseServiceTest` | 강의 단건 조회 시 존재하는 강의 정보가 반환되는지 검증 |
| `CourseServiceTest` | 강의 목록 조회 시 기본 `OPEN` 상태 목록이 반환되는지 검증 |
| `CourseServiceTest` | 강의 목록 조회 시 지정한 상태의 목록이 반환되는지 검증 |
| `CourseServiceTest` | 강의 수 조회 시 기본 `OPEN` 상태의 강의 수가 반환되는지 검증 |
| `CourseServiceTest` | 강의 생성자가 본인의 강의를 오픈할 수 있는지 검증 |
| `CourseServiceTest` | 다른 생성자의 강의를 오픈할 수 없는지 검증 |
| `CourseServiceTest` | 강의 생성자가 본인의 강의를 마감할 수 있는지 검증 |
| `CourseServiceTest` | 다른 생성자의 강의를 마감할 수 없는지 검증 |

### 통합 테스트

| 테스트 클래스 | 검증 내용 |
| --- | --- |
| `CourseServiceIT` | 강의 생성 시 강의 정보가 저장되고 반환되는지 검증 |
| `CourseServiceIT` | 강의 단건 조회 시 존재하는 강의 정보가 반환되는지 검증 |
| `CourseServiceIT` | 존재하지 않는 강의 조회 시 빈 `Optional`이 반환되는지 검증 |
| `CourseServiceIT` | 강의 목록 조회 시 기본 `OPEN` 상태 목록과 개수가 반환되는지 검증 |
| `CourseServiceIT` | 강의 목록 조회 시 지정한 상태의 목록과 개수가 반환되는지 검증 |
| `CourseServiceIT` | 강의 생성자가 본인의 강의를 오픈할 수 있는지 검증 |
| `CourseServiceIT` | 강의 생성자가 본인의 강의를 마감할 수 있는지 검증 |
| `CourseServiceIT` | 다른 생성자의 강의를 마감할 수 없는지 검증 |

### E2E 테스트

| 테스트 클래스 | 검증 내용 |
| --- | --- |
| `CourseV1ApiE2ETest` | `POST /api/v1/courses` 강의 등록 성공 응답 검증 |
| `CourseV1ApiE2ETest` | `CREATOR`가 아닌 사용자가 강의 등록 시 `403 Forbidden` 응답을 반환하는지 검증 |
| `CourseV1ApiE2ETest` | `GET /api/v1/courses` 상태값이 없으면 `OPEN` 상태 강의 목록을 반환하는지 검증 |
| `CourseV1ApiE2ETest` | `GET /api/v1/courses` 상태값이 주어지면 해당 상태 강의 목록을 반환하는지 검증 |
| `CourseV1ApiE2ETest` | `GET /api/v1/courses/{courseId}` 강의 상세 조회 성공 응답과 신청 인원 반환을 검증 |
| `CourseV1ApiE2ETest` | 존재하지 않는 강의 상세 조회 시 `404 Not Found` 응답을 반환하는지 검증 |
| `CourseV1ApiE2ETest` | `PATCH /api/v1/courses/{courseId}/open` 강의 오픈 성공 응답 검증 |
| `CourseV1ApiE2ETest` | 다른 사용자의 강의 오픈 시 `403 Forbidden` 응답을 반환하는지 검증 |
| `CourseV1ApiE2ETest` | `PATCH /api/v1/courses/{courseId}/close` 강의 마감 성공 응답 검증 |
| `CourseV1ApiE2ETest` | 다른 사용자의 강의 마감 시 `403 Forbidden` 응답을 반환하는지 검증 |

## 수강 신청(Enrollment)

### 단위 테스트

| 테스트 클래스 | 검증 내용 |
| --- | --- |
| `EnrollmentEntityTest` | 수강 신청 생성 시 상태가 `PENDING`으로 설정되는지 검증 |
| `EnrollmentEntityTest` | 강의 ID가 양수가 아니면 예외가 발생하는지 검증 |
| `EnrollmentEntityTest` | `PENDING` 상태 신청이 `CONFIRMED`로 변경되는지 검증 |
| `EnrollmentEntityTest` | 수강 신청 확정 시 확정일이 저장되는지 검증 |
| `EnrollmentEntityTest` | `PENDING`이 아닌 신청은 확정할 수 없는지 검증 |
| `EnrollmentEntityTest` | `PENDING` 상태 신청 취소 시 `CANCELLED`로 변경되는지 검증 |
| `EnrollmentEntityTest` | 이미 취소된 신청은 다시 취소할 수 없는지 검증 |
| `EnrollmentEntityTest` | `CONFIRMED` 상태 신청은 취소 가능 기간 내에 취소할 수 있는지 검증 |
| `EnrollmentEntityTest` | `CONFIRMED` 상태 신청은 취소 가능 기간이 지나면 취소할 수 없는지 검증 |
| `EnrollmentServiceTest` | 정원이 남아 있으면 수강 신청 정보가 반환되는지 검증 |
| `EnrollmentServiceTest` | 강의 정원이 초과되면 `CONFLICT` 예외가 발생하는지 검증 |
| `EnrollmentServiceTest` | 수강 신청 인원 조회 시 `PENDING`, `CONFIRMED` 상태만 집계하는지 검증 |
| `EnrollmentServiceTest` | 결제 대기 상태의 신청이 결제 확정 처리되는지 검증 |
| `EnrollmentServiceTest` | 결제 확정 대상 수강 신청이 없으면 `NOT_FOUND` 예외가 발생하는지 검증 |
| `EnrollmentServiceTest` | 수강 신청 취소가 처리되는지 검증 |
| `EnrollmentServiceTest` | 취소 대상 수강 신청이 없으면 `NOT_FOUND` 예외가 발생하는지 검증 |
| `EnrollmentServiceTest` | 내 수강 신청 목록 조회 시 사용자 ID에 해당하는 신청 목록이 반환되는지 검증 |

### 통합 테스트

| 테스트 클래스 | 검증 내용 |
| --- | --- |
| `EnrollmentServiceIT` | 수강 신청 시 수강 신청 정보가 저장되고 반환되는지 검증 |
| `EnrollmentServiceIT` | 정원이 초과된 강의에 신청하면 실패하는지 검증 |
| `EnrollmentServiceIT` | 취소된 수강 신청은 정원 계산에서 제외되는지 검증 |
| `EnrollmentServiceIT` | 결제 대기 상태의 신청이 결제 확정 처리되는지 검증 |
| `EnrollmentServiceIT` | 결제 확정 대상 수강 신청이 없으면 `NOT_FOUND` 예외가 발생하는지 검증 |
| `EnrollmentServiceIT` | 수강 신청 취소 시 상태가 `CANCELLED`로 변경되는지 검증 |
| `EnrollmentServiceIT` | 취소 대상 수강 신청이 없으면 `NOT_FOUND` 예외가 발생하는지 검증 |
| `EnrollmentServiceIT` | 내 수강 신청 목록 조회 시 사용자 ID에 해당하는 신청 목록이 반환되는지 검증 |
| `EnrollmentServiceIT` | 수강 신청 인원 조회 시 `PENDING`, `CONFIRMED` 상태만 집계하는지 검증 |
| `EnrollmentFacadeTest` | 수강 정원 5명 강의에 4명이 신청한 상태에서 30명이 동시에 신청해도 최종 신청 인원이 5명을 넘지 않는지 검증 |

### E2E 테스트

| 테스트 클래스 | 검증 내용 |
| --- | --- |
| `EnrollmentV1ApiE2ETest` | `POST /api/v1/enrollments` 수강 신청 성공 응답 검증 |
| `EnrollmentV1ApiE2ETest` | 정원이 초과된 강의 신청 시 `409 Conflict` 응답을 반환하는지 검증 |
| `EnrollmentV1ApiE2ETest` | `PATCH /api/v1/enrollments/{courseId}/payment` 결제 확정 성공 응답 검증 |
| `EnrollmentV1ApiE2ETest` | 결제 확정 대상 신청이 없으면 `404 Not Found` 응답을 반환하는지 검증 |
| `EnrollmentV1ApiE2ETest` | `PATCH /api/v1/enrollments/{courseId}/cancel` 수강 취소 성공 응답 검증 |
| `EnrollmentV1ApiE2ETest` | 취소 대상 신청이 없으면 `404 Not Found` 응답을 반환하는지 검증 |
| `EnrollmentV1ApiE2ETest` | `GET /api/v1/enrollments/me` 내 수강 신청 목록 조회 성공 응답 검증 |
| `EnrollmentV1ApiE2ETest` | `GET /api/v1/enrollments/courses/{courseId}/students` 강의 수강생 목록 조회 성공 응답 검증 |
| `EnrollmentV1ApiE2ETest` | 다른 강사의 강의 수강생 목록 조회 시 `403 Forbidden` 응답을 반환하는지 검증 |
