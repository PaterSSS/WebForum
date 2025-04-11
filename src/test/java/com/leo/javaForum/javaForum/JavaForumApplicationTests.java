package com.leo.javaForum.javaForum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JavaForumApplicationTests {
	@Test
	void goodTest() {
		Assertions.assertTrue(true);
	}

	@Test
	void testForActions() {
		Assertions.assertEquals(1 , 1);
	}

}
