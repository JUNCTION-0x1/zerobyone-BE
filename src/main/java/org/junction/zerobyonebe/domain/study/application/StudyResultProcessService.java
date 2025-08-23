package org.junction.zerobyonebe.domain.study.application;

import java.io.IOException;

import org.junction.zerobyonebe.domain.agent.application.IntentExtractionAgent;
import org.junction.zerobyonebe.domain.agent.application.ValidationAgent;
import org.junction.zerobyonebe.domain.study.dto.request.LevelTestRequest;
import org.junction.zerobyonebe.domain.study.dto.response.LevelTestResponse;
import org.junction.zerobyonebe.domain.study.dto.response.SpeakingTestResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyResultProcessService {

	private final SpeechToTextService sttService;
	private final IntentExtractionAgent intentExtractionAgent;
	private final ValidationAgent validationAgent;


	// TODO: 여러 개의 test시 처리 (지금은 1개의 테스트)
	// 사용자의 레벨 테스트 워크플로우
	public LevelTestResponse levelTestValidation(LevelTestRequest levelTestRequest, MultipartFile audio) throws IOException {
		String questionKor = levelTestRequest.getQuestionKor();
		String answerEng = levelTestRequest.getAnswerEng();
		String userVoiceText = sttService.transcribe(audio);

		//1. 유저 의도 파악
		String intent = intentExtractionAgent.extractIntent(userVoiceText, questionKor);

		//2. 레벨 검증
		SpeakingTestResponse speakingTestResponse = validationAgent.speakingValidation(
			levelTestRequest.getQuestionKor(), levelTestRequest.getAnswerEng(), userVoiceText, intent);
		
		//3. 레벨에 맞는 값 반환
		if(!speakingTestResponse.getIsCorrect()) return LevelTestResponse.builder().level(1).name("오렌지 농장").build();
		else return LevelTestResponse.builder().level(3).name("카페").build();
	}

}
