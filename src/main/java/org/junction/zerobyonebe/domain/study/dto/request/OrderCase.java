package org.junction.zerobyonebe.domain.study.dto.request;

import java.util.Map;

public class OrderCase {
	private String caseId;
	private String customerUtterance; // 손님 발화
	private String userResponse;      // 사용자 발화
	private String userResponseKor;   // 사용자 발화 번역
	private Map<String, String> blanks; // 2차 학습용 빈칸 (예: {"menuName": "cheeseburger"})
	private String fullKoreanSentence; // 3차 학습용 전체 한국어 문장
}