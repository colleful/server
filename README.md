# 🌈 Colleful

## 개요

전북대학교 과팅 매칭 플랫폼 Colleful의 API 서버.  
College와 Colorful의 합성어로, 다채로운 대학 생활을 제공해 주겠다는 의미이다.

## API 문서

* [API.md](/API.md)
* [Swagger](http://203.254.143.247:8080/swagger-ui.html)

## 기술 스택

### Server-side

- 웹 프레임워크: Spring Boot, Spring Web
- 데이터베이스, ORM: MySQL, Spring Data Jpa, Hibernate
- 인증 및 인가: Spring Security, JWT
- 테스트: JUnit, Mockito

### Deployment

- AWS EC2
- JCloud
- Github Workflow
- Jenkins

### Documentation

- Swagger

## 설치 방법

### 1. 프로젝트 다운 받기

```
git clone https://github.com/colleful/server.git
```

### 2. 설정 정보

`src/main/resources/application-local.properties`에 다음과 같이 적어준다.

```
spring.datasource.url=jdbc:mysql://{mysql 서버 주소}:{mysql 포트}/{데이터베이스 이름}?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC
spring.datasource.username={mysql username}
spring.datasource.password={mysql password}

spring.mail.username={인증번호 발송 이메일}
spring.mail.password={smtp 서버 로그인용 비밀번호}

jwt.secret={jwt 비밀 키}
```

### 3. 빌드 및 실행

```
./gradlew build
java -jar build/libs/server-{버전 명}.jar
```
