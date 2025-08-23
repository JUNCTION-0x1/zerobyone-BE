package org.junction.zerobyonebe.domain.agent.application;

import java.util.List;

import org.junction.zerobyonebe.domain.study.dto.response.ChoiceResponse;
import org.junction.zerobyonebe.domain.study.dto.response.SpeakingResponse;
import org.junction.zerobyonebe.domain.study.infrastructure.external.GeminiApiClient;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationAgent {

	private final int MAX_RETRIES = 10;
	private final GeminiApiClient geminiApiClient;
	private final ObjectMapper objectMapper;

	/**
	 * 사용자의 발화 스크립트와 음성 파일을 분석하여 발화 의도를 추출
	 *
	 * @param questionKor 주어진 문제(한국어 문장)
	 * @param answerEng 주어진 문제에 대한 정답(영어 ㅁ누장)
	 * @param userVoiceText 사용자의 발화 스크립트 텍스트
	 * @param intent 사용자의 발화 의도
	 * @return 주어진 정보를 기반으로 정답, 레벨 도출
	 */
	public SpeakingResponse speakingValidation(String questionKor, String answerEng, String userVoiceText, String intent) {
		String prompt = String.format("""
			너는 영어 문장 채점기이다.
			아래에 주어진 한국어 문장에 대해 학습자가 영어로 말한 답과 의도 그리고 정답이 주어진다. 
			주어진 값에 따라 답을 채점하고, 답에 대한 피드백을 적어줘.
			
		   \s
			채점 기준:
			- 사용자의 의도를 고려해서 두 문장이 80%% 이상 의미와 문법적으로 일치하면 "정답"
			- 아니라면 "오답"
		   \s
		   출력 조건:
			- 코드블록(```json`)사용하지 않고
			- 마크 다운 사용하지 않고
			- 추가 설명은 쓰지 않고
			- 이 형식으로만 출력해줘.
			{
				"isCorrect": true/false,
				"questionKor": questionKor,
				"answerEng": answerEng,
				"userAnswer: userAnswer,
				"answerCommentary": "오답인 이유를 간단한 1줄 정도 피드백하고, 정답에 대해 왜 이게 정답인지 사용하는 중요한 단어의 이유는 뭔지 2~4줄로 설명해줘."
			}
			 
			주어진 문장: %s
			학습자 답: %s
			학습자 의도: %s
			정답: %s
	    }
	    """, questionKor, userVoiceText, intent, answerEng);

		// 재시도 루프
		int retryCount = 0;
		while (retryCount < MAX_RETRIES) {
			try {
				String json = geminiApiClient.askGemini(prompt);

				SpeakingResponse response = objectMapper.readValue(json, SpeakingResponse.class);
				return response;

			} catch (MismatchedInputException e) {
				retryCount++;
			} catch (Exception e) {
				log.error("예상치 못한 오류 발생: " + e.getMessage());
				break;
			}
		}

		return SpeakingResponse.builder().build();
	}

}
