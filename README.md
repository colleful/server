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

- 필요한 클래스가 점점 많아지면서 Layer Architecture가 비효율적으로 느껴졌다.
- 패키지, 클래스 간 의존성이 복잡하다.
- 비즈니스 로직이 Controller, Service layer에 마구잡이로 섞여 있다.
- 테스트 코드가 없다.

### 리팩토링 규칙

- Domain 중심 디렉토리 구조를 사용한다.
- Setter를 되도록 사용하지 않는다.
- Service 클래스를 인터페이스와 구체 클래스로 분리한다.
- 조건식을 캡슐화하고 의미있는 이름을 부여한다.
- 클래스와 패키지 간 의존성 사이클이 생기지 않도록 한다.
- Controller 객체는 모든 로직을 Service 객체에 위임하고 받은 데이터를 가공하여 응답해 주는 역할만 한다.
- Service 객체는 트랜잭션을 보장하고, 예외 처리를 담당한다.
- Domain 객체는 비즈니스 로직을 담당한다.
- 도메인 패키지의 api 패키지는 같은 패키지의 service 패키지에만 의존한다.
- 도메인 패키지의 service 패키지는 같은 패키지의 repository 패키지와 다른 패키지의 service 패키지에 의존한다.
- 테스트 코드를 추가한다.

### 리팩토링으로 얻은 이점
- Domain 중심 디렉토리 구조를 사용하여 관련된 코드들을 쉽게 찾을 수 있었다.
- 조건식을 Domain 클래스 내로 캡슐화하여 의미있는 이름을 부여하니 가독성이 상승했다.
- 객체의 역할을 명확히 정하니 코딩할 때 혼란이 덜 하였다.
- 객체 간 의존 규칙을 정하니 코드 변경 시에 어떤 부분이 영향을 받는지 쉽게 파악하고 고칠 수 있었다.
- 테스트 코드를 추가하니 코드 변경 시에 발생할 수 있는 문제를 잡을 수 있었다.

## 🧪 테스트

### 초기의 테스트
- Service 객체의 테스트를 진행할 때 스프링 통합 테스트로 진행했다.
- 데이터베이스에 연결하고 데이터를 직접 데이터베이스에 CRUD하는 과정에서 많은 시간이 소요됐다.
- 테스트용 데이터를 생성하고 테스트가 끝나면 지우는 코드를 작성하느라 오버헤드가 발생하고 코드도 길어졌다.

### Mockito를 이용한 단위 테스트
- Mockito를 이용해 가짜 Repository 객체를 생성하여 데이터베이스 연결 없이 쉽게 테스트할 수 있었다.
- 테스트 코드를 작성하는 시간도 감소되고 테스트 코드를 실행하는 시간도 감소되었다.
