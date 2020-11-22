package com.colleful.server.dto;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

public class PageDto {

    @Getter
    public static class Response<T> {

        private final List<T> content;
        private final Integer pageNumber;
        private final Integer pageSize;
        private final Integer totalPages;
        private final Long totalElements;

        public Response(Page<T> page) {
            this.content = page.getContent();
            this.pageNumber = page.getPageable().getPageNumber();
            this.pageSize = page.getPageable().getPageSize();
            this.totalPages = page.getTotalPages();
            this.totalElements = page.getTotalElements();
        }
    }
}
