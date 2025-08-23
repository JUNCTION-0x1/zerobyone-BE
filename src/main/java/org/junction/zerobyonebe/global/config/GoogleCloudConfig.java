package org.junction.zerobyonebe.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v2.stub.SpeechStubSettings;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;

@Configuration
public class GoogleCloudConfig {

	@Value("classpath:stt.json")
	Resource gcsCredentials;

	@Bean
	public SpeechSettings speechSettings() {
		try {
			return SpeechSettings.newBuilder()
				.setCredentialsProvider(() -> GoogleCredentials.fromStream(gcsCredentials.getInputStream()))
				.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public SpeechStubSettings speechStubSettings() {
		try {
			return SpeechStubSettings.newBuilder()
				.setCredentialsProvider(() -> GoogleCredentials.fromStream(gcsCredentials.getInputStream()))
				.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public TextToSpeechClient textToSpeechClient() {
		try {
			return TextToSpeechClient.create(
				TextToSpeechSettings.newBuilder()
					.setCredentialsProvider(() -> GoogleCredentials.fromStream(gcsCredentials.getInputStream()))
					.build()
			);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
