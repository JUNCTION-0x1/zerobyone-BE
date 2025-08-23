package org.junction.zerobyonebe.domain.study.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class SpeakingTestResponse {
	private Boolean isCorrect;
	private String questionKor;
	private String answerEng;
	private String userAnswer;
	private String answerCommentary;
}
