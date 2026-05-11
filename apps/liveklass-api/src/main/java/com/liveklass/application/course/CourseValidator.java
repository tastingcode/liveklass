package com.liveklass.application.course;

import com.liveklass.domain.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CourseValidator {
	private final CourseService courseService;


}
