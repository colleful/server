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
|gender|String|Yes|"male", "female"|
|college|String|Yes|단과대학|

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|college|String|단과대학|
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

### GET /api/user/{id}
> id에 해당하는 user의 정보를 응답

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|college|String|단과대학|
|teams|List|소속한 팀들의 정보|

### PUT /api/user
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
|college|String|단과대학|
|teams|List|소속한 팀들의 정보|

### PATCH /api/user/password
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
|college|String|단과대학|
|teams|List|소속한 팀들의 정보|

### DELETE /api/user
> 회원 탈퇴
