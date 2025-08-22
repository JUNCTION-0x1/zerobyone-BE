package org.junction.zerobyonebe.domain.study.controller;// package org.junction.zerobyonebe.domain.study.controller;

import org.junction.zerobyonebe.domain.study.application.StudyService;
import org.junction.zerobyonebe.domain.study.dto.response.ContentResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/validate")
@RequiredArgsConstructor
public class StudyController {

	private final StudyService studyService;

	@GetMapping("/content/{caseId}")
	public ContentResponse getStudyContent(@PathVariable Integer caseId) {
		return studyService.getCaseContent(caseId);
	}

	//
	// @PostMapping("/{stage}")
	// public ValidationResult validateStage(
	// 	@PathVariable Integer stage,
	// 	@RequestBody UserValidationRequest request) {
	//
	// 	return studyService.validate(stage, request);
	// }
}