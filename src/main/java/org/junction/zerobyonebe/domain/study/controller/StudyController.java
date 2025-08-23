package org.junction.zerobyonebe.domain.study.controller;// package org.junction.zerobyonebe.domain.study.controller;

import java.io.IOException;

import org.junction.zerobyonebe.domain.study.application.SpeechToTextService;
import org.junction.zerobyonebe.domain.study.application.StudyProblemService;
import org.junction.zerobyonebe.domain.study.application.StudyValidationService;
import org.junction.zerobyonebe.domain.study.application.TextToSpeechService;
import org.junction.zerobyonebe.domain.study.dto.request.ChoiceRequest;
import org.junction.zerobyonebe.domain.study.dto.response.ChoiceResponse;
import org.junction.zerobyonebe.domain.study.dto.response.LevelTestResponse;
import org.junction.zerobyonebe.domain.study.dto.response.ContentResponse;
import org.junction.zerobyonebe.domain.study.dto.response.SpeakingTestResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StudyController {

	private final StudyProblemService studyProblemService;
	private final SpeechToTextService sttService;
	private final TextToSpeechService ttsService;
	private final StudyValidationService studyValidationService;

	@GetMapping("/content/{caseId}")
	public ContentResponse getStudyContent(@PathVariable Integer caseId) {
		return studyProblemService.getCaseContent(caseId);
	}

	//sst
	@PostMapping(value = "/speech-to-text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String handleAudioMessage(
		@RequestParam("audioFile") MultipartFile audioFile
	) throws IOException {
		String transcribe = sttService.transcribe(audioFile);
		String s = "변환에 성공하였습니다.\n" + transcribe;
		return s;
	}

	//tts
	@GetMapping("/convert-to-speech")
	public ResponseEntity<byte[]> convertTextToSpeech(@RequestParam String text) throws Exception {
		byte[] audio = ttsService.convertTextToSpeech(text);
		return ResponseEntity.ok()
			.header("Content-Type", "audio/mpeg")
			.body(audio);
	}

	//leveltest
	@GetMapping("/levelTest/{caseId}")
	public String levelTest(@PathVariable Long caseId) {
		return studyProblemService.levelTest(caseId);
	}

	@PostMapping(value = "/levelTest/validation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public LevelTestResponse levelTestValidation(
		@RequestPart("questions") String questions,
		@RequestPart("audio") MultipartFile audio
	) throws IOException {
		return studyValidationService.levelTestValidation(questions, audio);
	}

	//TODO: demo
	@GetMapping("/problem/choice/{caseId}")
	public ChoiceResponse getChoiceProblem(@PathVariable Long caseId) throws IOException {
		return studyProblemService.choiceTest(caseId);
	}

	@GetMapping("/problem/choice/{caseId}/voice")
	public ResponseEntity<byte[]> getChoiceVoice(@RequestParam Long caseId) throws Exception {
		byte[] audio = studyProblemService.choiceVoice(caseId);
		return ResponseEntity.ok()
			.header("Content-Type", "audio/mpeg")
			.body(audio);
	}

	@PostMapping(value = "/problem/choice/validation")
	public SpeakingTestResponse choiceValidation(
		@RequestBody ChoiceRequest choiceRequest) {
		return studyValidationService.choiceValidation(choiceRequest);
	}

	@GetMapping("/problem/speaking/{caseId}")
	public String getSpeakingProblem(@PathVariable Long caseId) throws IOException {
		return studyProblemService.speakingTest(caseId);
	}

	@PostMapping(value = "/problem/speaking/validation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String speakingValidation(
		@RequestPart("questions") String questions,
		@RequestPart("audio") MultipartFile audio) throws IOException {
		return studyValidationService.speakingValidation(questions, audio);
	}









}