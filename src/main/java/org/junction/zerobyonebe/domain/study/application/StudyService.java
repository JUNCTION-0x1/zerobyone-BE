package org.junction.zerobyonebe.domain.study.application;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junction.zerobyonebe.domain.study.domain.Content;
import org.junction.zerobyonebe.domain.study.domain.Word;
import org.junction.zerobyonebe.domain.study.dto.response.ContentResponse;
import org.junction.zerobyonebe.domain.study.dto.response.WordResponse;
import org.junction.zerobyonebe.domain.study.infrastructure.repository.ContentRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyService {

	private final ContentRepository contentRepository;

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

}
