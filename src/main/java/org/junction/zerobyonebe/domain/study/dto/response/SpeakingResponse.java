package org.junction.zerobyonebe.domain.study.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingResponse {
	private Boolean isCorrect;
	private String questionKor;
	private String answerEng;
	private String userAnswer;
	private String answerCommentary;
}
