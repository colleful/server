# Colleful RESTful API

## 0. 목차
1. [로그인 관련](#1-로그인-관련)
2. [User 관련 (사용자용)](#2-user-관련-사용자용)
3. [User 관련 (관리자용)](#3-user-관련-관리자용)
4. [Team 관련 (사용자용)](#4-team-관련-사용자용)
5. [Team 관련 (관리자용)](#5-team-관련-관리자용)
6. [초대 관련 (사용자용)](#6-초대-관련-사용자용)
7. [학과 정보](#7-학과-정보)

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
|gender|String|Yes|성별("MALE", "FEMALE")|
|departmentId|Long|Yes|학과 아이디|
|selfIntroduction|String|Yes|자기소개|

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("MALE", "FEMALE")|
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

### POST /auth/join/email
> 회원가입용 인증번호 이메일 전송

**Request**

|name|type|requires|description|
|--|--|--|--|
|email|String|Yes|이메일|

### POST /auth/password/email
> 비밀번호 변경용 인증번호 이메일 전송

**Request**

|name|type|requires|description|
|--|--|--|--|
|email|String|Yes|이메일|

### PATCH /auth/password
> 비밀번호 변경

**Request**

|name|type|requires|description|
|--|--|--|--|
|email|String|Yes|이메일|
|password|String|Yes|비밀번호|

### PATCH /auth/check
> 인증번호 확인

**Request**

|name|type|requires|description|
|--|--|--|--|
|email|String|Yes|이메일|
|code|Integer|Yes|인증번호|

## 2. User 관련 (사용자용)

### GET /api/users
> 자신의 정보 보기

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("MALE", "FEMALE")|
|department|String|학과|
|selfIntroduction|String|자기소개|
|teams|List|소속한 팀들의 정보|

### GET /api/users/{id}
> id에 해당하는 user 정보 보기

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("MALE", "FEMALE")|
|department|String|학과|
|selfIntroduction|String|자기소개|
|teams|List|소속한 팀들의 정보|

### GET /api/users/nickname/{nickname}
> 닉네임에 해당하는 user 정보 보기

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("MALE", "FEMALE")|
|department|String|학과|
|selfIntroduction|String|자기소개|
|teams|List|소속한 팀들의 정보|

### PATCH /api/users
> 자신의 회원 정보 수정

**Request**

|name|type|required|description|
|--|--|--|--|
|nickname|String|No|닉네임|
|departmentId|Long|No|학과 id|
|selfIntroduction|String|No|자기소개|

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("MALE", "FEMALE")|
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
|gender|String|성별("MALE", "FEMALE")|
|department|String|학과|
|selfIntroduction|String|자기소개|
|teams|List|소속한 팀들의 정보|

### DELETE /api/users
> 회원 탈퇴

## 3. User 관련 (관리자용)

### GET /admin/users
> 전체 유저 정보 보기

**Query Parameter**

|name|default|description|
|--|--|--|
|page|0|페이지 번호 (0부터 시작)|
|size|10|데이터 개수|

**Response**

|name|type|description|
|--|--|--|
|content|List|유저 정보 리스트|
|pageNumber|Integer|이메일|
|pageSize|Integer|닉네임|
|totalPages|Integer|나이|

**Response.content(List)**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("MALE", "FEMALE")|
|department|String|학과|
|selfIntroduction|String|자기소개|
|teams|List|소속한 팀들의 정보|

### GET /admin/users/{id}
> 특정 유저 정보 보기

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("MALE", "FEMALE")|
|department|String|학과|
|selfIntroduction|String|자기소개|
|teams|List|소속한 팀들의 정보|

### PATCH /admin/users/{id}
> 특정 회원 정보 수정

**Request**

|name|type|required|description|
|--|--|--|--|
|nickname|String|No|닉네임|
|college|String|No|단과대학|
|selfIntroduction|String|No|자기소개|

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|email|String|이메일|
|nickname|String|닉네임|
|age|Integer|나이|
|gender|String|성별("MALE", "FEMALE")|
|department|String|학과|
|selfIntroduction|String|자기소개|
|teams|List|소속한 팀들의 정보|

### DELETE /admin/users/{id}
> 회원 탈퇴

## 4. Team 관련 (사용자용)

### GET /api/teams
> 준비된 팀 전체 보기

**Query Parameter**

|name|default|description|
|--|--|--|
|page|0|페이지 번호 (0부터 시작)|
|size|10|데이터 개수|

**Response**

|name|type|description|
|--|--|--|
|content|List|팀 정보 리스트|
|pageNumber|Integer|이메일|
|pageSize|Integer|닉네임|
|totalPages|Integer|나이|

**Response.content(List)**

|name|type|description|
|--|--|--|
|id|Long|id|
|updatedAt|String|마지막 업데이트 시간|
|teamName|String|닉네임|
|headcound|Integer|인원 수|
|gender|String|성별("MALE", "FEMALE")|
|status|String|상태|
|leaderId|Long|리더 id|
|members|List|소속한 멤버들의 정보|

### GET /api/teams/{id}
> 특정 준비된 팀만 보기

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|updatedAt|String|마지막 업데이트 시간|
|teamName|String|닉네임|
|headcound|Integer|인원 수|
|gender|String|성별("MALE", "FEMALE")|
|status|String|상태|
|leaderId|Long|리더 id|
|averageAge|Double|평균 나이|
|members|List|소속한 멤버들의 정보|

### POST /api/teams
> 팀 생성

**Request**

|name|type|required|description|
|--|--|--|--|
|teamName|String|Yes|팀 이름|

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|updatedAt|String|마지막 업데이트 시간|
|teamName|String|닉네임|
|headcound|Integer|인원 수|
|gender|String|성별("MALE", "FEMALE")|
|status|String|상태|
|leaderId|Long|리더 id|
|averageAge|Double|평균 나이|
|members|List|소속한 멤버들의 정보|

### PATCH /api/teams/{id}
> id에 해당하는 팀 상태 변경 (리더만 변경 가능)

**Request**

|name|type|required|description|
|--|--|--|--|
|status|String|Yes|팀 상태|

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|updatedAt|String|마지막 업데이트 시간|
|teamName|String|닉네임|
|headcound|Integer|인원 수|
|gender|String|성별("MALE", "FEMALE")|
|status|String|상태|
|leaderId|Long|리더 id|
|averageAge|Double|평균 나이|
|members|List|소속한 멤버들의 정보|

### DELETE /api/teams/{id}
> id에 해당하는 팀 상태 삭제

### DELETE /api/teams/{id}/members
> id에 해당하는 팀에서 나가기

## 5. Team 관련 (관리자용)

### GET /admin/teams
> 모든 팀 전체 보기

**Query Parameter**

|name|default|description|
|--|--|--|
|page|0|페이지 번호 (0부터 시작)|
|size|10|데이터 개수|

**Response**

|name|type|description|
|--|--|--|
|content|List|팀 정보 리스트|
|pageNumber|Integer|이메일|
|pageSize|Integer|닉네임|
|totalPages|Integer|나이|

**Response(List)**

|name|type|description|
|--|--|--|
|id|Long|id|
|updatedAt|String|마지막 업데이트 시간|
|teamName|String|닉네임|
|headcound|Integer|인원 수|
|gender|String|성별("MALE", "FEMALE")|
|status|String|상태|
|leaderId|Long|리더 id|
|members|List|소속한 멤버들의 정보|

### PATCH /admin/teams/{id}
> id에 해당하는 팀 이름 변경

**Request**

|name|type|required|description|
|--|--|--|--|
|teamName|String|Yes|팀 이름|

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|updatedAt|String|마지막 업데이트 시간|
|teamName|String|닉네임|
|headcound|Integer|인원 수|
|gender|String|성별("MALE", "FEMALE")|
|status|String|상태|
|leaderId|Long|리더 id|
|averageAge|Double|평균 나이|
|members|List|소속한 멤버들의 정보|

### DELETE /admin/teams/{id}
> id에 해당하는 팀 상태 삭제

## 6. 초대 관련 (사용자용)

### GET /api/invitations
> 자신에게 온 팀 초대 정보 모두 보기

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|team|Object|초대받은 팀 정보|

### POST /api/invitations/{team-id}/{user-id}
> user-id에 해당하는 유저를 team-id에 해당하는 팀에 초대

### DELETE /api/invitations/{id}/accept
> 초대 수락

### DELETE /api/invitations/{id}/refuse
> 초대 거절

## 7. 학과 정보

### GET /api/departments
> 전체 학과 정보 보기

**Response(List)**

|name|type|description|
|--|--|--|
|id|Long|id|
|collegeName|String|단과대학 이름|
|departmentName|String|학과 이름|

### GET /api/departments/{id}
> id에 해당하는 학과 정보 보기

**Response**

|name|type|description|
|--|--|--|
|id|Long|id|
|collegeName|String|단과대학 이름|
|departmentName|String|학과 이름|
