# Colleful RESTful API

## 목차
1. [로그인 관련](#1-로그인-관련)
2. [User 관련](#2-user-관련)
3. [Team 관련](#3-team-관련)
4. [초대 관련](#4-초대-관련)
5. [매칭 관련](#5-매칭-관련)
6. [학과 정보](#6-학과-정보)

## 응답

**페이징 응답 형식**
|name|type|description|
|--|--|--|
|content|List|요청한 정보 리스트|
|pageNumber|Integer|현재 응답의 페이지 번호|
|pageSize|Integer|현재 응답의 데이터 개수|
|totalPages|Integer|총 페이지 개수|

**User 정보**
|name            |type   |description          |
|----------------|-------|---------------------|
|id|Long|id|
|email|String |이메일|
|nickname|String |닉네임|
|age|Integer|나이|
|gender|String |성별("MALE", "FEMALE")|
|department|String |학과|
|selfIntroduction|String |자기소개|

**Team 정보**
|name|type|description|
|----|----|-----------|
|id|Long|id|
|updatedAt|String|마지막 업데이트 시간|
|teamName|String|닉네임|
|headcount|Integer|인원 수|
|gender|String|성별("MALE", "FEMALE")|
|status|String|상태|
|leaderId|Long|리더 id|
|matchedTeamId|Long|매치된 팀의 아이디|

**초대 정보**
|name|type|description|
|----|----|-----------|
|id|Long|id|
|team|Team|초대받은 팀 정보|

**학과 정보**
|name|type|description|
|----|----|-----------|
|id|Long|id|
|collegeName|String|단과대학 이름|
|departmentName|String|학과 이름|

## 1. 로그인 관련

### POST /auth/join
> 회원가입

**Request Body**
|name|type|required|description|
|--|--|--|--|
|email|String|Yes|이메일|
|password|String|Yes|비밀번호|
|nickname|String|Yes|닉네임|
|birthYear|Integer|Yes|태어난 해|
|gender|String|Yes|성별("MALE", "FEMALE")|
|departmentId|Long|Yes|학과 아이디|
|selfIntroduction|String|Yes|자기소개|

### POST /auth/login
> 로그인, Authentication 헤더에 JWT 전송

**Request Body**
|name|type|requires|description|
|--|--|--|--|
|email|String|Yes|이메일|
|password|String|Yes|비밀번호|

### POST /auth/join/email
> 회원가입용 인증번호 이메일 전송

**Request Body**
|name|type|requires|description|
|--|--|--|--|
|email|String|Yes|이메일|

### POST /auth/password/email
> 비밀번호 변경용 인증번호 이메일 전송

**Request Body**
|name|type|requires|description|
|--|--|--|--|
|email|String|Yes|이메일|

### PATCH /auth/password
> 비밀번호 변경

**Request Body**
|name|type|requires|description|
|--|--|--|--|
|email|String|Yes|이메일|
|password|String|Yes|비밀번호|

### PATCH /auth/check
> 인증번호 확인

**Request Body**
|name|type|requires|description|
|--|--|--|--|
|email|String|Yes|이메일|
|code|Integer|Yes|인증번호|

## 2. User 관련

### GET /api/users
> 자신의 정보 조회

### GET /api/users/{id}
> id에 해당하는 user 정보 조회

### GET /api/users/nickname/{nickname}
> 닉네임에 특정 단어를 포함하는 user 정보 모두 조회

### PATCH /api/users
> 자신의 회원 정보 수정

**Request Body**
|name|type|required|description|
|--|--|--|--|
|nickname|String|No|닉네임|
|selfIntroduction|String|No|자기소개|

### PATCH /api/users/password
> 비밀번호 변경

**Request Body**
|name|type|requires|description|
|--|--|--|--|
|password|String|Yes|비밀번호|


### DELETE /api/users
> 회원 탈퇴


## 3. Team 관련

### GET /api/teams
> 준비된 팀 조회(페이징 응답)

**Query Parameter**
|name|default|description|
|--|--|--|
|page|0|페이지 번호 (0부터 시작)|
|size|10|데이터 개수|

### GET /api/teams/{id}
> 특정 준비된 팀 조회 또는 자기 팀 조회

### GET /api/teams/team-name/{team-name}
> 팀 이름에 특정 단어를 포함하는 팀 모두 조회

### GET /api/teams/{id}/members
> 특정 팀에 속하는 멤버 모두 조회

### POST /api/teams
> 팀 생성, Location 헤더에 자원 URL 제공

### POST /api/teams/leave
> id에 해당하는 팀에서 나가기

**Request Body**
|name|type|required|description|
|--|--|--|--|
|teamName|String|Yes|팀 이름|

### PATCH /api/teams/{id}
> id에 해당하는 팀 상태 변경 (리더만 변경 가능)

**Request Body**
|name|type|required|description|
|--|--|--|--|
|status|String|Yes|팀 상태("PENDING", "READY", "WATCHING", "MATCHED")|

### DELETE /api/teams/{id}
> id에 해당하는 팀 상태 삭제

## 4. 초대 관련

### GET /api/invitations
> 자신에게 온 초대 정보 모두 보기

### POST /api/invitations/{team-id}/{user-id}
> user-id에 해당하는 유저를 team-id에 해당하는 팀에 초대

### POST /api/invitations/{id}/accept
> 초대 수락

### POST /api/invitations/{id}/refuse
> 초대 거절

## 5. 매칭 관련

### GET /api/matching
> 자신의 팀에게 온 매칭 요청 조회(리더만 조회 가능)

### POST /api/matching/{team-id}
> team-id에 해당하는 팀에게 매칭 요청(리더만 요청 가능)

### POST /api/matching/{id}/accept
> id에 해당하는 매칭 요청 수락

### POST /api/matching/{id}/refuse
> id에 해당하는 매칭 요청 거절

## 6. 학과 정보

### GET /api/departments
> 전체 학과 정보 조회

### GET /api/departments/{id}
> id에 해당하는 학과 정보 조회
