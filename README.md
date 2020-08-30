# Colleful RESTful API

## 0. 목차
1. [로그인 관련](#1-로그인-관련)

## 1. 로그인 관련

### POST /auth/join
> 회원가입

* request

|name|type|description|
|--|--|--|
|email|String|이메일|
|password|String|비밀번호|
|nickname|String|닉네임|
|birthYear|Integer|태어난 해|
|gender|String|"male", "female"|
|college|String|단과대학|

### POST /auth/login
> 로그인

* request

|name|type|description|
|--|--|--|
|email|String|이메일|
|password|String|비밀번호|

* response

|name|type|description|
|--|--|--|
|token|String|jwt|
