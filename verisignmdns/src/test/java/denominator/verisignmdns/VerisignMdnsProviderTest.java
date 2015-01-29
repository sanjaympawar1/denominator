package denominator.verisignmdns;

import static denominator.verisignmdns.VerisignMdnsTest.VALID_URL;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Collection;
import java.util.Map;

import org.testng.annotations.Test;

import denominator.verisignmdns.VerisignMdnsProvider;

public class VerisignMdnsProviderTest {

    @Test
    public void credentialTypeToParameterNames() {
        VerisignMdnsProvider vrsnDNSProvider = new VerisignMdnsProvider();
        Map<String, Collection<String>> actualResult = vrsnDNSProvider.credentialTypeToParameterNames();
        assertNotNull(actualResult);
        Map<String, Collection<String>> expectedResult = VerisignMdnsTest.MockcredentialTypeToParameterNamesResponse();
        assertNotNull(expectedResult);
        assertEquals(actualResult.containsKey("password"), expectedResult.containsKey("password"));
    }

    @Test
    public void profileToRecordTypes() {
        VerisignMdnsProvider vrsnDNSProvider = new VerisignMdnsProvider();
        Map<String, Collection<String>> actualResult = vrsnDNSProvider.profileToRecordTypes();
        assertNotNull(actualResult);
        Map<String, Collection<String>> expectedResult = VerisignMdnsTest.mockProfileToRecordTypesResponse();
        assertNotNull(actualResult);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void url() {
        VerisignMdnsProvider vrsnDNSProvider = new VerisignMdnsProvider();
        String actualResult = vrsnDNSProvider.url();
        assertNotNull(actualResult);
        assertEquals(actualResult, VALID_URL);
    }
}

