package org.junction.zerobyonebe.domain.study.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "contents")
@Getter
@Setter
public class Content {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String engTitle;
	private String korTitle;

	@Column(columnDefinition = "TEXT")
	private String engContent;

	@Column(columnDefinition = "TEXT")
	private String korContent;

	@OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Word> words = new ArrayList<>();
}
