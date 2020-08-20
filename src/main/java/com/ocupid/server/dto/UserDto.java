package com.ocupid.server.dto;

import com.ocupid.server.domain.User;
import lombok.Getter;

public class UserDto {

    @Getter
    public static class Request {

        private String gender;
        private String collage;

        public User toEntity() {
            User user = new User();
            user.setGender(gender);
            user.setCollage(collage);
            return user;
        }
    }

    @Getter
    public static class Response {

        private final Long id;
        private final String gender;
        private final String collage;

        public Response(User user) {
            this.id = user.getId();
            this.gender = user.getGender();
            this.collage = user.getCollage();
        }
    }
}
