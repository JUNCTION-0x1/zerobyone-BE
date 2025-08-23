package org.junction.zerobyonebe.domain.study.application;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

import org.junction.zerobyonebe.domain.study.domain.Content;
import org.junction.zerobyonebe.domain.study.domain.Word;
import org.junction.zerobyonebe.domain.study.dto.response.ChoiceResponse;
import org.junction.zerobyonebe.domain.study.dto.response.ContentResponse;
import org.junction.zerobyonebe.domain.study.dto.response.LevelTestResponse;
import org.junction.zerobyonebe.domain.study.dto.response.WordResponse;
import org.junction.zerobyonebe.domain.study.infrastructure.external.GeminiApiClient;
import org.junction.zerobyonebe.domain.study.infrastructure.repository.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyProblemService {

	private final ContentRepository contentRepository;
	private final GeminiApiClient geminiApiClient;
	private final SpeechToTextService sttService;
	private final TextToSpeechService tstService;

	@PostConstruct
	public void init() {
		Content content = new Content();
		content.setEngTitle("Hello World");
		content.setKorTitle("안녕하세요 세상");
		content.setEngContent("This is a sample English content.");
		content.setKorContent("이것은 샘플 한국어 컨텐츠입니다.");

		Word word1 = new Word();
		word1.setWord("Hello");
		word1.setMean("안녕");
		word1.setContent(content);

		Word word2 = new Word();
		word2.setWord("World");
		word2.setMean("세상");
		word2.setContent(content);

		content.setWords(Arrays.asList(word1, word2));

		contentRepository.save(content);
	}

	@Transactional
	public ContentResponse getCaseContent(Integer caseId) {
		Content content = contentRepository.findById(Long.valueOf(caseId))
			.orElseThrow(() -> new RuntimeException("Content not found"));

		ContentResponse response = new ContentResponse();
		response.setIdx(content.getId().intValue());
		response.setEng_title(content.getEngTitle());
		response.setKor_title(content.getKorTitle());
		response.setEng_content(content.getEngContent());
		response.setKor_content(content.getKorContent());
		response.setWords(
			content.getWords().stream().map(this::mapWordToDto).collect(Collectors.toList())
		);

		return response;
	}

	private WordResponse mapWordToDto(Word word) {
		WordResponse wr = new WordResponse();
		wr.setWord(word.getWord());
		wr.setMean(word.getMean());
		return wr;
	}

	//test
	// @Transactional
	// public String levelTest(Long codeId) {
	// 	Content content = contentRepository.findById(codeId).orElseThrow(() -> new RuntimeException("Content not found"));
	//
	// 	String prompt = String.format("""
    //     너는 영어 학습용 문제 출제기이다.
    //     아래의 영어 문장과 한국어 번역을 참고해서, 학습자가 말하기 연습을 할 수 있는 테스트 문제 3개를 만들어라.
	//
    //     출력 조건:
    //     - 코드 블록(```), 마크다운, 추가 설명, 불필요한 텍스트 절대 출력하지 말 것
    //     - [{}, {}, {}] 이 형식인 JSON 배열로만 출력할 것
    //     - 배열의 각 원소는 다음 구조를 가짐:
    //     {
    //         "questionKor": "한국어 문장",
    //         "answerEng": "영어 정답"
    //     }
	//
    //     영문 원문: %s
    //     한국어 번역: %s
    //     """, content.getEngContent(), content.getKorContent());
	//
	// 	String problem = geminiApiClient.askGemini(prompt);
	// 	return problem;
	// }

	//TODO: demo용
	@Transactional
	public String levelTest(Long codeId) {
		Content content = contentRepository.findById(codeId).orElseThrow(() -> new RuntimeException("Content not found"));

		String prompt = String.format("""
	    너는 영어 학습용 문제 출제기이다.
	    아래의 영어 문장과 한국어 번역을 참고해서, 학습자가 말하기 연습을 할 수 있는 테스트 문제 1개를 만들어라.
	    
	    영문 원문: %s
	    한국어 번역: %s

	    출력 조건:
	    - 이 형식으로 출력해줘.
	    - ```json``` 절대 쓰지마.
	    {
	        "questionKor": "한국어 문장",
	        "answerEng": "영어 정답"
	    }
	    """, content.getEngContent(), content.getKorContent());

		String problem = geminiApiClient.askGemini(prompt);
		return problem;
	}

	@Transactional
	public String speakingTest(Long caseId) {
		Content content = contentRepository.findById(caseId).orElseThrow(() -> new RuntimeException("Content not found"));

		String prompt = String.format("""
	    너는 영어 학습용 문제 출제기이다.
	    아래의 영어 문장과 한국어 번역을 참고해서, 학습자가 말하기 연습을 할 수 있는 테스트 문제 1개를 만들어라.
	    
	    영문 원문: %s
	    한국어 번역: %s

	    출력 조건:
	    - 이 형식으로 출력해줘.
	    - ```json``` 절대 쓰지마.
	    {
	        "questionKor": "한국어 문장",
	        "answerEng": "영어 정답"
	    }
	    """, content.getEngContent(), content.getKorContent());

		String problem = geminiApiClient.askGemini(prompt);
		return problem;
	}

	@Transactional
	public ChoiceResponse choiceTest(Long caseId) throws IOException {
		Content content = contentRepository.findById(caseId)
			.orElseThrow(() -> new RuntimeException("Content not found"));

		String prompt = String.format("""
		너는 영어 학습용 문제 출제기이다.
		아래의 영어 문장과 한국어 번역을 참고해서, 영어 문제 리스닝에 대한 학습자가 객관식 문제로 연습할 수 있도록 문제 1개를 만들어라.
	
		영문 원문: %s
		한국어 번역: %s
	
		출력 조건:
		- 오직 아래 JSON 형식으로만 출력하고, 다른 설명이나 문장은 절대 포함하지 마.
		- 코드 블록(```json`), 마크다운, **줄바꿈(\\n), 탭(\\t), 백슬래시(\\) 등 어떤 특수 문자도 사용하지 말고 한 줄로 출력해.**
			   \s
		{
			"questionKor": "한국어 문장",
			"answerChoices": [
				"보기 1",
				"보기 2",
				"보기 3"
			],
			"answerEng": "정답 영어 문장"
		}
		""", content.getEngContent(), content.getKorContent());

		String problem = geminiApiClient.askGemini(prompt);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(problem);
		String cleanJson = mapper.writeValueAsString(jsonNode);

		return ChoiceResponse.builder().contents(cleanJson).build();
	}

	public byte[] choiceVoice(Long caseId) throws IOException {
		Content content = contentRepository.findById(caseId)
			.orElseThrow(() -> new RuntimeException("Content not found"));

		return tstService.convertTextToSpeech(content.getEngContent());
	}

}
