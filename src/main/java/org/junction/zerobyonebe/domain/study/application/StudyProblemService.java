package org.junction.zerobyonebe.domain.study.application;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

import org.junction.zerobyonebe.domain.agent.application.DataContextAgent;
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
	private final DataContextAgent dataContextAgent;

	@PostConstruct
	public void init() {
		Content content = new Content();
		content.setEngTitle("On the Packing Line");
		content.setKorTitle("포장 라인에서");
		content.setEngContent("Hey, could you help me with this box? It's a bit heavy. We need to get these packed before the supervisor checks the pallets. Make sure to put the label on straight. It's hot out here, so stay hydrated and keep an eye on your speed. The goal is 50 boxes an hour today. Let's get it done!");
		content.setKorContent("이 박스 좀 같이 들어줄 수 있을까? 좀 무겁네. 관리자가 팔레트 확인하기 전에 이것들을 다 포장해야 해. 라벨은 꼭 똑바로 붙여줘. 여기 더우니까 수분 보충하고 속도도 잘 봐가면서 해. 오늘 목표는 시간당 50박스야. 해내자!");

		Word word1 = new Word();
		word1.setWord("pallet");
		word1.setMean("상품을 적재하는 운반대, 팔레트");
		word1.setContent(content);

		Word word2 = new Word();
		word2.setWord("supervisor");
		word2.setMean("감독관, 관리자");
		word2.setContent(content);

		Word word3 = new Word();
		word3.setWord("stay hydrated");
		word3.setMean("수분 보충하다");
		word3.setContent(content);

		Word word4 = new Word();
		word4.setWord("get it done");
		word4.setMean("일을 끝내다, 해내다");
		word4.setContent(content);

		content.setWords(Arrays.asList(word1, word2, word3, word4));

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

		//1. 데이터 셋 가져와서
		String dataContext = dataContextAgent.extractDataContext(content.getEngTitle(), content.getEngContent());

		String prompt = String.format("""
	    너는 영어 학습용 문제 출제기이다.
	    영어 회화에 집중해서 문제를 출제해야 해.
	    아래의 영어 문장과 한국어 번역을 참고해서, 학습자가 말하기 연습을 할 수 있는 테스트 문제 1개를 만들어라.
	    주어진 데이터셋을 활용해서 만들어줘.
	    
	    입력
	    데이터셋: %s
	    영문 원문: %s
	    한국어 번역: %s

	    출력 조건:
	  	- 코드블록(```json`)사용하지 않고
		- 마크 다운 사용하지 않고
		- 추가 설명은 쓰지 않고
		- 이 형식으로만 출력해줘.
	    {
	        "questionKor": "한국어 문장",
	        "answerEng": "영어 정답"
	    }
	    """, dataContext, content.getEngContent(), content.getKorContent());

		String problem = geminiApiClient.askGemini(prompt);
		return problem;
	}

	@Transactional
	public String speakingTest(Long caseId) {
		Content content = contentRepository.findById(caseId).orElseThrow(() -> new RuntimeException("Content not found"));

		//1. 데이터 셋 가져와서
		String dataContext = dataContextAgent.extractDataContext(content.getEngTitle(), content.getEngContent());

		//2. 문제 생성
		String prompt = String.format("""
	    너는 영어 학습용 문제 출제기이다.
	    아래의 영어 문장과 한국어 번역을 참고해서, 학습자가 말하기 연습을 할 수 있는 테스트 문제 1개를 만들어라.
	    주어진 데이터셋을 활용해서 만들어줘.
	    
	    입력
	    데이터셋: %s
	    영문 원문: %s
	    한국어 번역: %s

	    출력 조건:
	    - 코드블록(```json`)사용하지 않고
		- 마크 다운 사용하지 않고
		- 추가 설명은 쓰지 않고
		- 이 형식으로만 출력해줘.
	    {
	        "questionKor": "한국어 문장",
	        "answerEng": "영어 정답"
	    }
	    """, dataContext, content.getEngContent(), content.getKorContent());

		String problem = geminiApiClient.askGemini(prompt);
		return problem;
	}

	@Transactional
	public ChoiceResponse choiceTest(Long caseId) throws IOException {
		Content content = contentRepository.findById(caseId)
			.orElseThrow(() -> new RuntimeException("Content not found"));
		
		//1. 데이터 셋 가져와서
		String dataContext = dataContextAgent.extractDataContext(content.getEngTitle(), content.getEngContent());

		//2. 문제 생성
		String prompt = String.format("""
		너는 영어 학습용 문제 출제기이다.
		아래의 주어진 영어 문장과 한국어 번역을 참고해서, 영어 문제 리스닝에 대한 학습자가 객관식 문제로 연습할 수 있도록 문제 1개를 만들어줘.
		객관식 보기 갯수는 3개이고, 문제와 객관식 보기는 모두 한국어여야해.
		주어진 데이터셋을 활용해서 만들어줘.
		
		\s
		입력
		데이터 셋: %s
		영문 원문: %s
		한국어 번역: %s
		
		\s
		출력 조건
		- 코드블록(```json`)사용하지 않고
		- 마크 다운 사용하지 않고
		- 추가 설명은 쓰지 않고
		- 이 형식으로만 출력해줘.
		{
			"questionKor": "한국어 문장",
			"answerChoices": [
				"보기 1",
				"보기 2",
				"보기 3"
			],
			"answerCorr": "정답 보기"
		}
		""", dataContext, content.getEngContent(), content.getKorContent());

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
