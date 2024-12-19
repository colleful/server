package com.colleful.server.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    NOT_FOUND_USER("가입되지 않은 유저입니다."),
    NOT_FOUND_TEAM("팀 정보가 없습니다."),
    NOT_FOUND_INVITATION("초대 정보가 없습니다."),
    NOT_FOUND_MATCHING_REQUEST("매칭 요청 정보가 없습니다."),
    NOT_FOUND_DEPARTMENT("학과 정보가 없습니다."),
    ALREADY_EXIST_EMAIL("중복된 이메일입니다."),
    ALREADY_EXIST_USER("이미 가입된 유저입니다."),
    ALREADY_EXIST_NICKNAME("중복된 닉네임입니다."),
    ALREADY_EXIST_TEAM_NAME("중복된 팀 이름입니다."),
    IS_LEADER("팀의 리더는 할 수 없습니다."),
    IS_NOT_LEADER("리더가 아닌 사용자는 할 수 없습니다."),
    IS_NOT_MEMBER("멤버가 아닙니다."),
    ALREADY_HAS_TEAM("다른 팀에 속해있습니다."),
    CANNOT_ACCESS("조회할 수 없습니다."),
    CANNOT_CHANGE_STATUS("상태를 변경할 수 없습니다."),
    CANNOT_INVITE("초대할 수 없습니다."),
    CANNOT_MATCH("매칭될 수 없는 팀입니다."),
    IS_NOT_MATCHED("매칭된 팀이 없습니다."),
    IS_NOT_MY_INVITATION("다른 사용자에게 온 초대입니다."),
    ALREADY_INVITED("이미 초대했습니다."),
    ALREADY_REQUESTED("이미 요청한 팀입니다."),
    ALREADY_MATCHED("이미 다른 팀과 매칭되어 있습니다.")
    ;

    private final String message;
}
