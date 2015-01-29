package denominator.verisignmdns;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import javax.inject.Provider;
import org.testng.annotations.Test;

import denominator.Credentials;
import denominator.verisignmdns.VerisignUtils;

public class VerisignUtilsTest {

    @Test
    public void TestGetMapOfCredentials() {
        Provider<Credentials> aCredentials = (Provider<Credentials>) VerisignMDNSTest.mockProviderCredentials();
        Map<String, String> actualResult = VerisignUtils.getMapOfCredentials(aCredentials);
        assertNotNull(actualResult);
        assertTrue(actualResult.containsKey("username"));
        assertTrue(actualResult.containsKey("password"));
        assertEquals(actualResult.get("username"), VerisignMDNSTest.TEST_USER_NAME);
        assertEquals(actualResult.get("password"), VerisignMDNSTest.TEST_PASSWORD);
    }
}

