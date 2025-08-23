package org.junction.zerobyonebe.domain.agent.application;

import org.junction.zerobyonebe.domain.study.infrastructure.external.GeminiApiClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IntentExtractionAgent {

	private final GeminiApiClient geminiApiClient;

	/**
	 * 사용자의 발화 스크립트와 음성 파일을 분석하여 발화 의도를 추출
	 *
	 * @param userVoiceText 사용자의 발화 스크립트 텍스트
	 * @param script 주어진 문제 정보
	 * @return 추출된 의도 정보를 담은 String
	 */
	public String extractIntent(String userVoiceText, String script) {
		String prompt = String.format("""
			사용자가 제시된 문장('문장')를 듣고 영어로 번역하여 제시한 사용자의 답변('사용자답변')을 분석하여 다음을 수행합니다.
			사용자 답변 의도 파악: 사용자가 주어진 문장 대해 어떤 목적으로 답변했는지, 즉 답변의 핵심 의도를 파악합니다. 
			답변이 문제에 대해 직접적으로 대답하는 것인지, 아니면 관련 정보를 추가하거나, 질문을 회피하거나, 전혀 다른 의미를 전달하려는 것인지 등을 분석하세요.
			
			입력
			사용자 답변: %s,
			제시된 문장: %s
			     
			결과 형식
			파악된 사용자의 답변 의도를 1-2문장으로 간결하게 요약해서 출력합니다.
			     
	    """, userVoiceText, script);


		String intent = geminiApiClient.askGemini(prompt);
		System.out.println("intent: " + intent);

		return intent;
	}
}
