package com.apherox.mongo;

import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author apherox
 */
public class MongoConnectionApplicationLiveTest {
	private static final String HOST = "localhost";
	private static final String PORT = "27017";
	private static final String DB = "songs";
	private static final String USER = "root";
	private static final String PASS = "mongo2023";


	// test cases
	private void assertInsertSucceeds(ConfigurableApplicationContext context) {
		String name = "A";

		MongoTemplate mongo = context.getBean(MongoTemplate.class);
		Document doc = Document.parse("{\"name\":\"" + name + "\"}");
		Document inserted = mongo.insert(doc, "items");

		Assertions.assertNotNull(inserted.get("_id"));
		Assertions.assertEquals(inserted.get("name"), name);
	}

	@Test
	public void whenPropertiesConfig_thenInsertSucceeds() {
		SpringApplicationBuilder app = new SpringApplicationBuilder(MongodbConsumerApplication.class);
		app.run();

		assertInsertSucceeds(app.context());
	}
}