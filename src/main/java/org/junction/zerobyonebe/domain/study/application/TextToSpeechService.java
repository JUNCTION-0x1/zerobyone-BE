package org.junction.zerobyonebe.domain.study.application;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TextToSpeechService {

	private final TextToSpeechClient ttsClient;

	public byte[] convertTextToSpeech(String text) throws IOException {
		SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
		VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
			.setLanguageCode("en-US")
			.setSsmlGender(SsmlVoiceGender.FEMALE)
			.build();
		AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

		SynthesizeSpeechResponse response = ttsClient.synthesizeSpeech(input, voice, audioConfig);
		return response.getAudioContent().toByteArray();
	}
}
