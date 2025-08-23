package org.junction.zerobyonebe.domain.study.application;

import java.io.IOException;

import org.junction.zerobyonebe.domain.study.dto.request.ChoiceRequest;
import org.junction.zerobyonebe.domain.study.dto.response.LevelTestResponse;
import org.junction.zerobyonebe.domain.study.dto.response.SpeakingTestResponse;
import org.junction.zerobyonebe.domain.study.infrastructure.external.GeminiApiClient;
import org.junction.zerobyonebe.domain.study.infrastructure.repository.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyValidationService {

	private final ContentRepository contentRepository;
	private final GeminiApiClient geminiApiClient;
	private final SpeechToTextService sttService;
	private final TextToSpeechService tstService;

	//TODO: demo용
	public LevelTestResponse levelTestValidation(String questions, MultipartFile audio) throws IOException {
		String problem = questions;
		String speechAnswer = sttService.transcribe(audio);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(problem);
		String answerEng = rootNode.get("answerEng").asText();

		return LevelTestResponse.builder().level(1).name("오렌지 농장").build();
	}

	//TODO: demo용
	public String speakingValidation(String questions, MultipartFile audio) throws IOException {
		String problem = questions;
		String speechAnswer = sttService.transcribe(audio);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(problem);
		String answerEng = rootNode.get("answerEng").asText();

		String prompt = String.format("""
			너는 영어 말하기 채점기이다.
			아래에 학습자가 말한 답과 정답이 주어진다.
		   \s
			채점 기준:
			- 두 문장이 80%% 이상 의미와 문법적으로 일치하면 "정답"
			- 아니라면 "오답"
		   \s
			- 이 형식으로 출력해줘.
			- JSON 형식으로만 출력하고, 코드블록(```json`)이나 마크다운, 추가 설명은 절대 쓰지 말 것.
			{
				"correct": true/false,
				"wrongAnswer": "학습자가 말한 답 (오답일 경우만 기입)",
				"correctAnswer": "정답 문장",
				"correctAnswerCommentary": "정답 또는 오답에 대한 간단한 피드백"
			}
			 
			학습자 답: %s
			정답: %s
	    }
	    """, speechAnswer, answerEng);

		String answer = geminiApiClient.askGemini(prompt);

		return answer;
	}

	public SpeakingTestResponse choiceValidation(ChoiceRequest choiceRequest, String answer) throws
		JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(choiceRequest.getContents());

		String correctAnswer = rootNode.get("answerEng").asText();

		String userAnswer = answer;

		boolean isCorrect = correctAnswer.trim().equalsIgnoreCase(userAnswer.trim());
		String prompt = String.format("""
		너는 영어 학습용 채점 및 해설 생성기야.
		아래 학습자의 답과 정답을 보고, 간단한 영어 학습용 해설을 만들어줘.
		학습자 답: %s
		정답: %s
		출력 형식: 한 줄의 해설 텍스트
		""", userAnswer, correctAnswer);
		String commentary = geminiApiClient.askGemini(prompt);

		return SpeakingTestResponse.builder()
			.correct(isCorrect)
			.wrongAnswer(isCorrect ? null : userAnswer)
			.correctAnswer(correctAnswer)
			.correctAnswerCommentary(commentary)
			.build();

	}
}
