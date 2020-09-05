# Colleful RESTful API

## 0. 목차
1. [로그인 관련](#1-로그인-관련)
2. [User 관련 (사용자용)](#2-user-관련-사용자용)

## 1. 로그인 관련

### POST /auth/join
> 회원가입

**Request**

|name|type|required|description|
|--|--|--|--|
|email|String|Yes|이메일|
|password|String|Yes|비밀번호|
|nickname|String|Yes|닉네임|
|birthYear|Integer|Yes|태어난 해|
|gender|String|Yes|성별("남자", "여자")|
|departmentId|Long|Yes|학과 아이디|
|selfIntroduction|String|Yes|자기소개|

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("남자", "여자")|
|department|String|학과|
|selfIntroduction|String|자기소개|
|teams|List|소속한 팀들의 정보|

### POST /auth/login
> 로그인

**Request**

|name|type|requires|description|
|--|--|--|--|
|email|String|Yes|이메일|
|password|String|Yes|비밀번호|

**Response**

|name|type|description|
|--|--|--|
|token|String|jwt|

## 2. User 관련 (사용자용)

### GET /api/users
> 자신의 정보를 응답

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("남자", "여자")|
|department|String|학과|
|selfIntroduction|String|자기소개|
|teams|List|소속한 팀들의 정보|

### GET /api/users/{id}
> id에 해당하는 user의 정보를 응답

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("남자", "여자")|
|department|String|학과|
|selfIntroduction|String|자기소개|
|teams|List|소속한 팀들의 정보|

### PUT /api/users
> 자신의 회원 정보 수정

**Request**

|name|type|required|description|
|--|--|--|--|
|nickname|String|No|닉네임|
|college|String|No|단과대학|

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("남자", "여자")|
|department|String|학과|
|selfIntroduction|String|자기소개|
|teams|List|소속한 팀들의 정보|

### PATCH /api/users/password
> 비밀번호 변경

**Request**

|name|type|requires|description|
|--|--|--|--|
|password|String|Yes|비밀번호|

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("남자", "여자")|
|department|String|학과|
|selfIntroduction|String|자기소개|
|teams|List|소속한 팀들의 정보|

### DELETE /api/users
> 회원 탈퇴
