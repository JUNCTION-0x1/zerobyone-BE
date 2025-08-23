package org.junction.zerobyonebe.domain.study.controller;// package org.junction.zerobyonebe.domain.study.controller;

import java.io.IOException;

import org.junction.zerobyonebe.domain.study.application.SpeechToTextService;
import org.junction.zerobyonebe.domain.study.application.StudyService;
import org.junction.zerobyonebe.domain.study.application.TextToSpeechService;
import org.junction.zerobyonebe.domain.study.dto.response.ContentResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StudyController {

	private final StudyService studyService;
	private final SpeechToTextService sttService;
	private final TextToSpeechService ttsService;

	@GetMapping("/content/{caseId}")
	public ContentResponse getStudyContent(@PathVariable Integer caseId) {
		return studyService.getCaseContent(caseId);
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

}