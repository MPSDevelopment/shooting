package tech.shooting.ipsc.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.pojo.StandardScore;

@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class StandardPassEnumTest {

	private String json;

	@Test
	public void checkJson() {

		var standard = new StandardScore();
		standard.setPassScore(StandardPassEnum.EXCELLENT);

		json = JacksonUtils.getJson(standard);

		log.info("Json is %s", json);

		assertTrue(json.contains("EXCELLENT"));
	}

	@Test
	public void checkEnum() {

		var json = "{\"passScore\": \"EXCELLENT\"}";
		var standard = JacksonUtils.fromJson(StandardScore.class, json);
		assertEquals(StandardPassEnum.EXCELLENT, standard.getPassScore());

		json = "{\"passScore\": \"\"}";
		standard = JacksonUtils.fromJson(StandardScore.class, json);
		assertEquals(StandardPassEnum.UNSATISFACTORY, standard.getPassScore());
	}
}
