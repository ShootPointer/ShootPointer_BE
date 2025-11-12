package com.midas.shootpointer.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "fcm.enabled", havingValue = "true", matchIfMissing = false)
public class FcmConfig {
	
	@Value("${firebase.service_key.path}")
	private String SERVICE_ACCOUNT_PATH;
	
	@Bean
	public FirebaseApp firebaseApp() throws IOException {
		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(new ClassPathResource(SERVICE_ACCOUNT_PATH).getInputStream()))
			.build();
		
		return FirebaseApp.initializeApp(options);
	}
	
	@Bean
	public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
		return FirebaseMessaging.getInstance(firebaseApp);
	}
}
