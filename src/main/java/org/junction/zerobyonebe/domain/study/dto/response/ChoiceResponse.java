package org.junction.zerobyonebe.domain.study.dto.response;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChoiceResponse {
	private String contents;
}
