package org.junction.zerobyonebe.domain.user.domain;

import org.junction.zerobyonebe.global.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	private String email;

	private String password;

	private String name;

	private User(String email, String password, String name) {
		this.email = email;
		this.password = password;
		this.name = name;
	}

	public static User create(String email, String password, String name) {
		return new User(email, password, name);
	}
}