package org.junction.zerobyonebe.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
	@Email
	@NotBlank
	private String email;

	@NotBlank
	@Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
	private String password;

	@NotNull
	private String name;
}