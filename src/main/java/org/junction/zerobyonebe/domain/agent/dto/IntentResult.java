package org.junction.zerobyonebe.domain.agent.dto;

import lombok.Builder;

@Builder
public class IntentResult {
	private String userAnswer;
	private String problemEng;
	private String intent;
}
