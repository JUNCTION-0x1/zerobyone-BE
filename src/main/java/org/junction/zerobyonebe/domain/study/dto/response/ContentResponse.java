package org.junction.zerobyonebe.domain.study.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ContentResponse {
	private int idx;
	private String eng_title;
	private String kor_title;
	private String eng_content;
	private String kor_content;
	private List<WordResponse> words;
}

