package com.midas.shootpointer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("dev")
class ShootpointerApplicationTest {
	@MockitoBean
	private com.google.firebase.messaging.FirebaseMessaging firebaseMessaging;


	@Test
	void contextLoads() {
	}

}
