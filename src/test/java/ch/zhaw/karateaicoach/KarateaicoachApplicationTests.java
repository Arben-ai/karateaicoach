package ch.zhaw.karateaicoach;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import ch.zhaw.karateaicoach.repository.SportlerRepository;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;
import ch.zhaw.karateaicoach.repository.TrainingsfokusRepository;

@SpringBootTest
class KarateaicoachApplicationTests {

	@MockitoBean
	TrainingsplanRepository trainingsplanRepository;

	@MockitoBean
	SportlerRepository sportlerRepository;

	@MockitoBean
	TrainingsfokusRepository trainingsfokusRepository;

	@MockitoBean
	JwtDecoder jwtDecoder;

	@MockitoBean
	MongoTemplate mongoTemplate;

	@Test
	void contextLoads() {
	}

}
