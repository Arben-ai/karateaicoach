package ch.zhaw.karateaicoach;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import ch.zhaw.karateaicoach.repository.SportlerRepository;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;

@SpringBootTest(properties = {
		"spring.security.oauth2.resourceserver.jwt.issuer-uri=http://dummy",
		"spring.autoconfigure.exclude=org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration,"
				+ "org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration,"
				+ "org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration" })
class KarateaicoachApplicationTests {

	@MockitoBean
	TrainingsplanRepository trainingsplanRepository;

	@MockitoBean
	SportlerRepository sportlerRepository;

	@MockitoBean
	JwtDecoder jwtDecoder;

	@MockitoBean
	MongoTemplate mongoTemplate;

	@Test
	void contextLoads() {
	}

}
