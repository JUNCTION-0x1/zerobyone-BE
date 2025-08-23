package org.junction.zerobyonebe.domain.study.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SpeakingTestResponse {
	private Boolean correct;
	private String wrongAnswer;
	private String correctAnswer;
	private String correctAnswerCommentary;
}
