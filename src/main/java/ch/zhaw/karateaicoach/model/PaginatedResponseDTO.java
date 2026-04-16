package ch.zhaw.karateaicoach.model;

import java.util.List;

import org.springframework.data.domain.Page;

public record PaginatedResponseDTO<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public static <T> PaginatedResponseDTO<T> fromPage(Page<T> page) {
        return new PaginatedResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
