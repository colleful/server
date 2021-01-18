# 🌈 Colleful

## 🌻 개요

전북대학교 과팅 매칭 플랫폼 Colleful의 API 서버.  
College와 Colorful의 합성어로, 다채로운 대학 생활을 제공해 주겠다는 의미이다.

## 🔓 설치 방법

### 1. 프로젝트 다운 받기

```
$ git clone https://github.com/colleful/server.git
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
$ ./gradlew build
$ java -jar build/libs/server-{버전 명}.jar
```

## 🗒 API 문서

* [API.md](/API.md)
* [Swagger](http://203.254.143.247:8080/swagger-ui.html)

## 💡 기술 스택

### Server-side

- Spring Boot, Spring Web
- MySQL, Spring Data Jpa, Hibernate
- Spring Security, JWT
- JUnit, Mockito

### Deployment

- AWS EC2 -> JCloud
- Github Workflow
- Jenkins

### Documentation

- Swagger

## 🌴 브랜치 전략

* [Git-flow 사용](https://github.com/voiciphil/gitflow-tutorial)

## 🛠 리팩토링

### 처음 구조의 문제점

- 필요한 클래스가 점점 많아지면서 Layer Architecture가 비효율적으로 느껴짐
- 복잡한 패키지, 클래스 간 의존성
- 비즈니스 로직이 Controller, Service layer에 마구잡이로 섞여 있음
- 테스트 코드 없음

### 리팩토링 규칙

- Domain 중심 Architecture를 사용한다.
- Setter를 되도록 사용하지 않는다.
- Service 클래스를 인터페이스와 구체 클래스로 분리한다.
- 조건식을 추상화하고 의미있는 이름을 부여한다.
- 클래스와 패키지 간 의존성 사이클이 생기지 않도록 한다.
- Controller 클래스는 모든 로직을 Service 클래스에 위임하고 받은 데이터를 가공하여 응답해 주는 역할만 한다.
- Service 클래스는 트랜잭션을 보장하고, 예외 처리를 담당한다.
- Domain 클래스는 비즈니스 로직을 담당한다.
- 도메인 패키지의 api 패키지는 같은 패키지의 service 패키지에만 의존한다.
- 도메인 패키지의 service 패키지는 같은 패키지의 repository 패키지와 다른 패키지의 service 패키지에 의존한다.
