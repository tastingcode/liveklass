# 테스트 실행 방법

이 문서는 로컬 환경에서 LiveKlass API를 실행하고 테스트하는 방법을 정리합니다.

## 사전 준비

- Java 21
- Docker Desktop 또는 Docker Engine
- Gradle Wrapper 사용 가능 환경

## 로컬 MySQL 실행

수동 API 테스트를 위해 Docker Compose로 MySQL 8.0을 실행합니다.

```bash
docker compose -f docker/infra-compose.yml up -d
```

실행되는 MySQL 설정은 다음과 같습니다.

| 항목 | 값 |
| --- | --- |
| Host | `localhost` |
| Port | `3306` |
| Database | `liveklass` |
| Username | `application` |
| Password | `application` |
| Root Password | `root` |

컨테이너 상태는 다음 명령으로 확인할 수 있습니다.

```bash
docker compose -f docker/infra-compose.yml ps
```

테스트가 끝난 뒤 로컬 DB 컨테이너를 중지하려면 다음 명령을 사용합니다.

```bash
docker compose -f docker/infra-compose.yml down
```

볼륨까지 삭제해 데이터를 초기화하려면 다음 명령을 사용합니다.

```bash
docker compose -f docker/infra-compose.yml down -v
```

## 애플리케이션 실행

로컬 프로필은 기본값으로 활성화되어 있으며, `jpa.yml`의 `local` 설정에 따라 `localhost:3306/liveklass` MySQL에 연결합니다.

```bash
./gradlew :apps:liveklass-api:bootRun
```

애플리케이션이 실행되면 기본 API 주소는 다음과 같습니다.

```text
http://localhost:8080
```

Swagger UI는 다음 주소에서 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
```

## HTTP 파일로 수동 테스트

프로젝트의 `http` 디렉터리에는 API 수동 테스트용 요청 파일이 있습니다.

| 파일 | 설명 |
| --- | --- |
| `http/user-v1.http` | 사용자 회원 가입, 내 정보 조회 |
| `http/course-v1.http` | 강의 등록, 오픈, 목록 조회, 상세 조회 |
| `http/enrollment-v1.http` | 수강 신청, 내 신청 목록 조회, 결제 확정, 취소, 수강생 목록 조회 |
| `http/http-client.env.json` | HTTP Client 환경 변수 |

`http/http-client.env.json`에는 다음과 같이 로컬 API 주소가 정의되어 있습니다.

```json
{
  "local": {
    "liveklass-api": "http://localhost:8080"
  }
}
```

수동 테스트는 다음 순서로 진행하면 좋습니다.

1. `docker compose -f docker/infra-compose.yml up -d`로 MySQL을 실행합니다.
2. `./gradlew :apps:liveklass-api:bootRun`으로 애플리케이션을 실행합니다.
3. `http/user-v1.http`에서 `CREATOR`, `STUDENT` 사용자를 생성합니다.
4. `http/course-v1.http`에서 `CREATOR`의 `X-USER-ID`로 강의를 등록하고 오픈합니다.
5. `http/enrollment-v1.http`에서 `STUDENT`의 `X-USER-ID`로 수강 신청, 결제 확정, 취소를 확인합니다.

## Gradle 자동화 테스트 실행

전체 테스트는 다음 명령으로 실행합니다.

```bash
./gradlew test
```

API 모듈 테스트만 실행하려면 다음 명령을 사용할 수 있습니다.

```bash
./gradlew :apps:liveklass-api:test
```

특정 테스트 클래스만 실행하려면 다음처럼 `--tests` 옵션을 사용합니다.

```bash
./gradlew :apps:liveklass-api:test --tests "com.liveklass.domain.course.CourseEntityTest"
```

## 자동화 테스트와 Docker Compose의 차이

Gradle 자동화 테스트는 `test` 프로필로 실행되며, DB 통합 테스트와 E2E 테스트는 Testcontainers 기반 테스트 MySQL을 사용합니다.  
따라서 자동화 테스트에는 Docker 데몬이 필요하지만, `docker/infra-compose.yml`로 띄운 로컬 MySQL 컨테이너가 반드시 필요하지는 않습니다.  
Docker Compose로 실행한 MySQL은 애플리케이션을 직접 띄운 뒤 `http` 파일이나 Swagger UI로 수동 테스트할 때 사용합니다.

## 테스트 리포트

테스트 실행 후 Gradle 테스트 리포트는 다음 경로에서 확인할 수 있습니다.

```text
apps/liveklass-api/build/reports/tests/test/index.html
```

JaCoCo 리포트가 필요하면 다음 명령을 실행합니다.

```bash
./gradlew :apps:liveklass-api:jacocoTestReport
```

JaCoCo XML 리포트는 다음 경로에 생성됩니다.

```text
apps/liveklass-api/build/reports/jacoco/test/jacocoTestReport.xml
```
