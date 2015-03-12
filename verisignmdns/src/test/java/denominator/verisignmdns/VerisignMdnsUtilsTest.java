package denominator.verisignmdns;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import javax.inject.Provider;
import org.testng.annotations.Test;

import denominator.Credentials;
import denominator.verisignmdns.VerisignMdnsUtils;

public class VerisignMdnsUtilsTest {

    @Test
    public void TestGetMapOfCredentials() {
        Provider<Credentials> aCredentials = (Provider<Credentials>) VerisignMdnsTest.mockProviderCredentials();
        Map<String, String> actualResult = VerisignMdnsUtils.getMapOfCredentials(aCredentials);
        assertNotNull(actualResult);
        assertTrue(actualResult.containsKey("username"));
        assertTrue(actualResult.containsKey("password"));
        assertEquals(actualResult.get("username"), VerisignMdnsTest.TEST_USER_NAME);
        assertEquals(actualResult.get("password"), VerisignMdnsTest.TEST_PASSWORD);
    }
}
