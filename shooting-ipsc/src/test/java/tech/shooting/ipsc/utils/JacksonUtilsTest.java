package tech.shooting.ipsc.utils;

import com.mpsdevelopment.plasticine.commons.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.utils.JacksonUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
public class JacksonUtilsTest {

    @BeforeAll
    public static void before () {
        new File("data").mkdirs();
    }

    @Test
    public void checkGetJson () throws IOException {
        User user = new User().setName("Thor").setSurname("Viking").setCount(5000).setSalary(2000L);

        String userJson = JacksonUtils.getJson(user);
        String prettyUserJson = JacksonUtils.getPrettyJson(user);
        String prettyUserForPublicJson = JacksonUtils.getJson(TestViews.Public.class, user);
        String prettyUserForPrivateJson = JacksonUtils.getJson(TestViews.Private.class, user);

        log.info("ToString User is %s", user);
        log.info("Jackson User is %s", userJson);
        log.info("Jackson pretty User is %s", prettyUserJson);
        log.info("Jackson pretty User for public is %s", prettyUserForPublicJson);
        assertFalse(prettyUserForPublicJson.contains("5000"));
        log.info("Jackson pretty User for private is %s", prettyUserForPrivateJson);
        assertTrue(prettyUserForPrivateJson.contains("5000"));

        log.info("ToString UserJson is %s", user = JacksonUtils.fromJson(User.class, userJson));
        assertEquals("Thor", user.getName());
        assertEquals("Viking", user.getSurname());
        assertEquals(2000, user.getSalary(), 0.001);
        assertEquals(5000, user.getCount(), 0.001);
        assertNull(user.getPassword());
        log.info("ToString PrettyUserJson is %s", user = JacksonUtils.fromJson(User.class, prettyUserJson));
        assertEquals("Thor", user.getName());
        assertEquals("Viking", user.getSurname());
        assertEquals(2000, user.getSalary(), 0.001);
        assertEquals(5000, user.getCount(), 0.001);
        assertNull(user.getPassword());

        log.info("ToString prettyUserForPublicJson is %s", user = JacksonUtils.fromJson(User.class, prettyUserForPublicJson));
        assertEquals("Thor", user.getName());
        assertEquals("Viking", user.getSurname());
        assertNull(user.getCount());
        assertNull(user.getSalary());
        assertNull(user.getPassword());

        log.info("ToString prettyUserForPrivateJson is %s", user = JacksonUtils.fromJson(User.class, prettyUserForPrivateJson));
        assertEquals("Thor", user.getName());
        assertEquals("Viking", user.getSurname());
        assertEquals(5000, user.getCount(), 0.001);
        assertNull(user.getSalary());
        assertNull(user.getPassword());
    }

    @Test
    public void checkFile () throws IOException {
        User user = new User().setName("Thor").setSurname("Viking").setCount(5000).setSalary(2000L);

        JacksonUtils.getJson(user, new File("data/user.json"));

        User userfromFile = JacksonUtils.fromJson(User.class, new File("data/user.json"));

        log.info("ToString UserfromFile is %s", userfromFile);
    }

    @Test
    public void checkLong () throws IOException {
        User user = new User().setName("Thor").setSurname("Viking").setCount(5000).setSalary(Long.MAX_VALUE);
        String json = JacksonUtils.getJson(user);
        log.info("Json is %s", json);
        assertTrue(json.contains(String.valueOf(Long.MAX_VALUE)));

    }

    @Test
    public void checkDate () throws IOException {
        User user = new User().setName("Thor").setSurname("Viking").setCount(5000).setDate(DateUtils.createDate(2018, 0, 01));
        String json = JacksonUtils.getJson(user);
        log.info("Json date is %s", json);
        assertTrue(json.contains("2018-01-01T"));

    }

    @Test
    public void checkList () throws IOException {
        List<User> list = new ArrayList<>();
        list.add(new User().setName("Thor").setSurname("Viking").setCount(5000).setSalary(Long.MAX_VALUE));
        list.add(new User().setName("Frey").setSurname("Viking").setCount(5000).setSalary(Long.MAX_VALUE));
        list.add(new User().setName("Loki").setSurname("Viking").setCount(5000).setSalary(Long.MAX_VALUE));

        String json = JacksonUtils.getJson(list);
        log.info("Json list is %s", json);
        assertTrue(json.contains(String.valueOf(Long.MAX_VALUE)));

        List<User> listConverted = JacksonUtils.getListFromJson(User[].class, json);

        assertEquals(list, listConverted);

    }

    @Test
    public void checkMap () throws IOException {
        String json = "{\"1\":{\"name\" : \"Thor\"}}";
        Map<String, User> map = JacksonUtils.getMapFromJson(User.class, json);
        log.info("Map is %s", map);
    }

    @Test
    public void checkComplexList () {
        List<List<List<Double>>> list = Collections.singletonList(Arrays.asList(Arrays.asList(-103.0, 47.5), Arrays.asList(-103.0, 48.0), Arrays.asList(-102.5, 48.0), Arrays.asList(-102.5, 47.5), Arrays.asList(-103.0, 47.5)));
        log.info("Json list is %s", JacksonUtils.getJson(list));
    }
}
