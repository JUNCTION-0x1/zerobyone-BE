package org.junction.zerobyonebe.domain.study.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LevelTestResponse {
	private int level;
	private String name;
}
