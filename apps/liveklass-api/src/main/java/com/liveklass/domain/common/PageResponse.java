package com.liveklass.domain.common;


import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
		List<T> content,
		int pageNumber,
		int pageSize,
		long totalElements,
		int totalPage
) {
	public static <T> PageResponse<T> of(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPage) {
		return new PageResponse<>(content, pageNumber, pageSize, totalElements, totalPage);
	}

	public <R> PageResponse<R> map(Function<? super T, R> mapper) {
		List<R> mappedContent = content.stream()
				.map(mapper)
				.toList();

		return new PageResponse<>(
				mappedContent,
				pageNumber,
				pageSize,
				totalElements,
				totalPage
		);
	}
}
