# API 명세

사용자, 강의, 수강 신청 API의 엔드포인트와 샘플 요청/응답을 정리한 문서입니다.

## 공통

- Base URL: `http://localhost:8080`
- 요청/응답 형식: `application/json`
- 인증/인가: 별도 인증 서버 없이 `X-USER-ID` 요청 헤더로 현재 사용자를 식별합니다.
- 날짜 형식: `yyyy-MM-dd`

### 공통 성공 응답

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": {}
}
```

### 공통 실패 응답

```json
{
  "meta": {
    "result": "FAIL",
    "errorCode": "Bad Request",
    "message": "잘못된 요청입니다."
  },
  "data": null
}
```

### 주요 에러 코드

| HTTP Status | errorCode | 설명 |
| --- | --- | --- |
| 400 | `Bad Request` | 요청 값이 잘못되었거나 상태 전이 조건을 만족하지 못한 경우 |
| 403 | `Forbidden` | 요청 사용자가 리소스에 대한 권한을 갖지 못한 경우 |
| 404 | `Not Found` | 사용자, 강의, 수강 신청을 찾을 수 없는 경우 |
| 409 | `Conflict` | 중복 회원 가입, 수강 정원 초과 등 충돌이 발생한 경우 |
| 500 | `Internal Server Error` | 서버 내부 오류 |

## API 목록

### 사용자(User)

| Method | Endpoint | 설명 | 인증 |
| --- | --- | --- | --- |
| `POST` | `/api/v1/users` | 회원 가입 | 불필요 |
| `GET` | `/api/v1/users/me` | 내 정보 조회 | `X-USER-ID` |

### 강의(Course)

| Method | Endpoint | 설명 | 인증 |
| --- | --- | --- | --- |
| `POST` | `/api/v1/courses` | 강의 등록 | `X-USER-ID` |
| `GET` | `/api/v1/courses` | 강의 목록 조회 | 불필요 |
| `GET` | `/api/v1/courses/{courseId}` | 강의 상세 조회 | 불필요 |
| `PATCH` | `/api/v1/courses/{courseId}/open` | 강의 모집 시작 | `X-USER-ID` |
| `PATCH` | `/api/v1/courses/{courseId}/close` | 강의 모집 마감 | `X-USER-ID` |

### 수강 신청(Enrollment)

| Method | Endpoint | 설명 | 인증 |
| --- | --- | --- | --- |
| `POST` | `/api/v1/enrollments` | 수강 신청 | `X-USER-ID` |
| `GET` | `/api/v1/enrollments/me` | 내 수강 신청 목록 조회 | `X-USER-ID` |
| `GET` | `/api/v1/enrollments/courses/{courseId}/students` | 강의별 수강생 목록 조회 | `X-USER-ID` |
| `PATCH` | `/api/v1/enrollments/{courseId}/payment` | 수강 신청 결제 확정 | `X-USER-ID` |
| `PATCH` | `/api/v1/enrollments/{courseId}/cancel` | 수강 신청 취소 | `X-USER-ID` |

## 사용자 API

### 회원 가입

`CREATOR` 또는 `STUDENT` 역할의 사용자를 생성합니다.

```http
POST /api/v1/users HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "loginId": "creator1",
  "userRole": "CREATOR"
}
```

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": {
    "id": 1,
    "loginId": "creator1",
    "userRole": "CREATOR"
  }
}
```

### 내 정보 조회

`X-USER-ID` 헤더에 해당하는 사용자 정보를 조회합니다.

```http
GET /api/v1/users/me HTTP/1.1
Host: localhost:8080
X-USER-ID: 1
```

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": {
    "id": 1,
    "loginId": "creator1",
    "userRole": "CREATOR"
  }
}
```

## 강의 API

### 강의 등록

`CREATOR` 역할의 사용자만 강의를 등록할 수 있습니다. 생성된 강의의 초기 상태는 `DRAFT`입니다.

```http
POST /api/v1/courses HTTP/1.1
Host: localhost:8080
Content-Type: application/json
X-USER-ID: 1

{
  "title": "자바 기초",
  "description": "자바 기초 문법을 학습합니다.",
  "price": 10000,
  "capacity": 30,
  "startDate": "2026-06-01",
  "endDate": "2026-06-30"
}
```

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": {
    "id": 1,
    "creatorId": 1,
    "title": "자바 기초",
    "description": "자바 기초 문법을 학습합니다.",
    "status": "DRAFT",
    "price": 10000,
    "capacity": 30,
    "startDate": "2026-06-01",
    "endDate": "2026-06-30",
    "applicants": null
  }
}
```

### 강의 목록 조회

상태별 강의 목록을 페이지네이션으로 조회합니다. `status`를 생략하면 `OPEN` 상태가 기본값입니다.

```http
GET /api/v1/courses?status=OPEN&page=0&size=10 HTTP/1.1
Host: localhost:8080
```

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": {
    "content": [
      {
        "id": 1,
        "creatorId": 1,
        "title": "자바 기초",
        "description": "자바 기초 문법을 학습합니다.",
        "status": "OPEN",
        "price": 10000,
        "capacity": 30,
        "startDate": "2026-06-01",
        "endDate": "2026-06-30",
        "applicants": null
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPage": 1
  }
}
```

### 강의 상세 조회

강의 상세 정보와 현재 신청 인원을 조회합니다. `applicants`는 `PENDING`, `CONFIRMED` 상태의 신청 수입니다.

```http
GET /api/v1/courses/1 HTTP/1.1
Host: localhost:8080
```

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": {
    "id": 1,
    "creatorId": 1,
    "title": "자바 기초",
    "description": "자바 기초 문법을 학습합니다.",
    "status": "OPEN",
    "price": 10000,
    "capacity": 30,
    "startDate": "2026-06-01",
    "endDate": "2026-06-30",
    "applicants": 3
  }
}
```

### 강의 모집 시작

강의 소유자인 `CREATOR`만 강의를 `OPEN` 상태로 변경할 수 있습니다.

```http
PATCH /api/v1/courses/1/open HTTP/1.1
Host: localhost:8080
X-USER-ID: 1
```

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": {
    "id": 1,
    "creatorId": 1,
    "title": "자바 기초",
    "description": "자바 기초 문법을 학습합니다.",
    "status": "OPEN",
    "price": 10000,
    "capacity": 30,
    "startDate": "2026-06-01",
    "endDate": "2026-06-30",
    "applicants": null
  }
}
```

### 강의 모집 마감

강의 소유자인 `CREATOR`만 강의를 `CLOSED` 상태로 변경할 수 있습니다.

```http
PATCH /api/v1/courses/1/close HTTP/1.1
Host: localhost:8080
X-USER-ID: 1
```

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": {
    "id": 1,
    "creatorId": 1,
    "title": "자바 기초",
    "description": "자바 기초 문법을 학습합니다.",
    "status": "CLOSED",
    "price": 10000,
    "capacity": 30,
    "startDate": "2026-06-01",
    "endDate": "2026-06-30",
    "applicants": null
  }
}
```

## 수강 신청 API

### 수강 신청

`STUDENT` 역할의 사용자만 `OPEN` 상태의 강의에 신청할 수 있습니다. 신청 직후 상태는 `PENDING`입니다.

```http
POST /api/v1/enrollments HTTP/1.1
Host: localhost:8080
Content-Type: application/json
X-USER-ID: 2

{
  "courseId": 1
}
```

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": {
    "id": 1,
    "courseId": 1,
    "userId": 2,
    "status": "PENDING",
    "confirmedDate": null
  }
}
```

```json
{
  "meta": {
    "result": "FAIL",
    "errorCode": "Conflict",
    "message": "수강 정원이 초과되었습니다."
  },
  "data": null
}
```

### 내 수강 신청 목록 조회

현재 수강생의 수강 신청 목록을 조회합니다.

```http
GET /api/v1/enrollments/me HTTP/1.1
Host: localhost:8080
X-USER-ID: 2
```

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": [
    {
      "id": 1,
      "courseId": 1,
      "userId": 2,
      "status": "PENDING",
      "confirmedDate": null
    }
  ]
}
```

### 강의별 수강생 목록 조회

강의 소유자인 `CREATOR`만 조회할 수 있습니다. 응답에는 `CONFIRMED` 상태의 수강 신청만 포함됩니다.

```http
GET /api/v1/enrollments/courses/1/students?page=0&size=10 HTTP/1.1
Host: localhost:8080
X-USER-ID: 1
```

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": {
    "content": [
      {
        "id": 1,
        "courseId": 1,
        "userId": 2,
        "status": "CONFIRMED",
        "confirmedDate": "2026-05-11"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPage": 1
  }
}
```

### 수강 신청 결제 확정

결제 대기 상태(`PENDING`)의 수강 신청을 결제 완료 상태(`CONFIRMED`)로 변경합니다.

```http
PATCH /api/v1/enrollments/1/payment HTTP/1.1
Host: localhost:8080
X-USER-ID: 2
```

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": {
    "id": 1,
    "courseId": 1,
    "userId": 2,
    "status": "CONFIRMED",
    "confirmedDate": "2026-05-11"
  }
}
```

### 수강 신청 취소

수강 신청을 `CANCELLED` 상태로 변경합니다. `CONFIRMED` 상태의 신청은 결제 확정일 기준 7일 이내에만 취소할 수 있습니다.

```http
PATCH /api/v1/enrollments/1/cancel HTTP/1.1
Host: localhost:8080
X-USER-ID: 2
```

```json
{
  "meta": {
    "result": "SUCCESS",
    "errorCode": null,
    "message": null
  },
  "data": {
    "id": 1,
    "courseId": 1,
    "userId": 2,
    "status": "CANCELLED",
    "confirmedDate": "2026-05-11"
  }
}
```

## 대표 실패 예시

### 강사가 아닌 사용자가 강의 등록을 시도한 경우

```json
{
  "meta": {
    "result": "FAIL",
    "errorCode": "Forbidden",
    "message": "강사만 강의를 등록할 수 있습니다."
  },
  "data": null
}
```

### 모집 중이 아닌 강의에 수강 신청한 경우

```json
{
  "meta": {
    "result": "FAIL",
    "errorCode": "Bad Request",
    "message": "모집 중인 강의만 수강 신청할 수 있습니다."
  },
  "data": null
}
```

### 수강 정원이 초과된 경우

```json
{
  "meta": {
    "result": "FAIL",
    "errorCode": "Conflict",
    "message": "수강 정원이 초과되었습니다."
  },
  "data": null
}
```

### 존재하지 않는 강의를 조회한 경우

```json
{
  "meta": {
    "result": "FAIL",
    "errorCode": "Not Found",
    "message": "강의를 찾을 수 없습니다: 999"
  },
  "data": null
}
```
