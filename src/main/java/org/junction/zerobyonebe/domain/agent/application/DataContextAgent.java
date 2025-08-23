package org.junction.zerobyonebe.domain.agent.application;

import org.junction.zerobyonebe.domain.study.infrastructure.external.GeminiSearchClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataContextAgent {

	private final GeminiSearchClient geminiSearchClient;

	/**
	 * 영어 컨텐츠에 대한 데이터 셋 추출
	 *
	 * @param engTitle 영어 타이틀 제목
	 * @param engContent 영어 컨텐츠
	 * @return 해당 영어 컨텐츠에 대한 데이터 셋
	 */
	public String extractDataContext(String engTitle, String engContent) {
		String prompt = String.format("""
		너는 제공된 영어 제목과 컨텐츠를 분석하여, 해당 주제와 관련된 가장 최신의 트렌드, 슬랭, 그리고 문화적 맥락을 포함하는 데이터셋을 생성하는 전문가다. 
		생성된 데이터셋은 영어 회화 학습 및 채점 시스템에 활용될 것이다.

		입력:
		- 영어 제목: `%s`
		- 영어 컨텐츠: `%s`
	
		작업 지시:
		아래의 3가지 항목을 기반으로 데이터셋을 생성해.

		1.  핵심 주제 및 어휘:
		-   제공된 제목과 컨텐츠의 핵심 주제를 1-2문장으로 요약해.
		-   해당 주제와 관련하여 현대 영어 회화에서 자주 사용되는 필수 단어와 구문 5-10개를 추출하고, 각 단어의 한국어 의미를 병기해. (예: "binge-watch": 몰아보기)

		2.  최신 슬랭 및 유행어:
		-   입력된 컨텐츠의 맥락과 관련하여 현재 소셜 미디어, 영화, 일상 대화에서 유행하는 슬랭이나 신조어 3-5개를 찾아.
		-   각 슬랭의 의미와 실제 대화에서 사용될 수 있는 예시 문장 1개를 함께 제시해.

		3.  문화적 맥락 및 배경 지식:
		-   해당 컨텐츠가 다루는 주제와 관련된 미국, 영국 등 특정 문화권의 고유한 표현, 관습, 혹은 최근 이슈를 1-2가지로 요약해. 이는 학습자가 해당 표현을 더 깊이 이해하는 데 도움을 줄 수 있어야 해.

		출력 형식:
		- 코드블록(```json`)사용하지 않고
		- 마크 다운 사용하지 않고
		- 추가 설명은 쓰지 않고
		- 이 형식으로만 출력해줘.
		{
			"topicSummary": "여기에 핵심 주제 요약",
			"keyVocabulary": [
				{
					"word": "단어1",
					"meaning": "의미1"
				},
				{
					"word": "단어2",
					"meaning": "의미2"
				}
		  	],
			"latestSlangs": [
				{
					"slang": "슬랭1",
					"meaning": "의미1",
					"example": "예시 문장1"
				},
				{
					"slang": "슬랭2",
					"meaning": "의미2",
					"example": "예시 문장2"
				}
		  	],
			"culturalContext": [
				"여기에 문화적 맥락 1",
				"여기에 문화적 맥락 2"
		  	]
		}
		""", engTitle, engContent);

		String dataContext = geminiSearchClient.generateAnswer(prompt);
		System.out.println("context: " + dataContext);

		return dataContext;
	}
}
