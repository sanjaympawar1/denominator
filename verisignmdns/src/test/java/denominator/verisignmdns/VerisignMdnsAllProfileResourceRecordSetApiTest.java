package denominator.verisignmdns;

import static denominator.verisignmdns.VerisignMdnsTest.RESOURCE_RECORD_ID1;
import static denominator.verisignmdns.VerisignMdnsTest.TEST_PASSWORD;
import static denominator.verisignmdns.VerisignMdnsTest.TEST_USER_NAME;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_OWNER1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RDATA1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RR_TYPE_CNAME;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_TTL1;
import static denominator.verisignmdns.VerisignMdnsTest.mockAllProfileResourceRecordSetApi;
import static denominator.verisignmdns.VerisignMdnsTest.rrListCNAMETypesResponse;
import static denominator.verisignmdns.VerisignMdnsTest.rrListCNAMETypesTemplete;
import static denominator.verisignmdns.VerisignMdnsTest.rrListValildResponse;
import static java.lang.String.format;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import org.testng.annotations.Test;

import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;

import denominator.model.ResourceRecordSet;
import denominator.model.rdata.CNAMEData;
import denominator.verisignmdns.VerisignMdnsAllProfileResourceRecordSetApi;

public class VerisignMdnsAllProfileResourceRecordSetApiTest {

    @Test
    public void getByNameTypeAndQualifier() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(rrListCNAMETypesResponse));
        server.play();
        try {
            VerisignMdnsAllProfileResourceRecordSetApi vrsnAllProfileResourceRecordSetApi = mockAllProfileResourceRecordSetApi(server
                    .getPort());
            ResourceRecordSet<?> actualResult = vrsnAllProfileResourceRecordSetApi.getByNameTypeAndQualifier(
                    TEST_USER_NAME, VALID_RR_TYPE_CNAME, RESOURCE_RECORD_ID1);
            assertNotNull(actualResult);

            String expectedRequest = format(rrListCNAMETypesTemplete, TEST_USER_NAME, TEST_PASSWORD, RESOURCE_RECORD_ID1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void iterateByNameAndType() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(rrListValildResponse));
        server.play();
        try {
            VerisignMdnsAllProfileResourceRecordSetApi vrsnAllProfileResourceRecordSetApi = mockAllProfileResourceRecordSetApi(server
                    .getPort());
            Iterator<ResourceRecordSet<?>> actulResult = vrsnAllProfileResourceRecordSetApi.iterateByNameAndType(
                    VALID_OWNER1, VALID_RR_TYPE_CNAME);
            assertNotNull(actulResult);

            ResourceRecordSet<?> rrs = actulResult.next();

            assertNotNull(rrs);
            assertEquals(rrs.ttl(), new Integer(Integer.parseInt(VALID_TTL1)));
            assertEquals(rrs.type(), VALID_RR_TYPE_CNAME);
            assertEquals(rrs.name(), VALID_OWNER1);

            Object entry = rrs.records().get(0);

            assertTrue(entry instanceof CNAMEData);
            CNAMEData cnameData = (CNAMEData) entry;
            assertEquals(cnameData.values().iterator().next(), VALID_RDATA1);
            // verify we have 2 records as expected.
            assertTrue(actulResult.hasNext());
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void iterator() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(rrListValildResponse));
        server.play();
        try {
            VerisignMdnsAllProfileResourceRecordSetApi vrsnAllProfileResourceRecordSetApi = mockAllProfileResourceRecordSetApi(server
                    .getPort());
            Iterator<ResourceRecordSet<?>> actulResult = vrsnAllProfileResourceRecordSetApi.iterator();
            assertNotNull(actulResult);

            ResourceRecordSet<?> rrs = actulResult.next();

            assertNotNull(rrs);
            assertEquals(rrs.ttl(), new Integer(Integer.parseInt(VALID_TTL1)));
            assertEquals(rrs.type(), VALID_RR_TYPE_CNAME);
            assertEquals(rrs.name(), VALID_OWNER1);

            Object entry = rrs.records().get(0);

            assertTrue(entry instanceof CNAMEData);
            CNAMEData cnameData = (CNAMEData) entry;
            assertEquals(cnameData.values().iterator().next(), VALID_RDATA1);
            // verify we have 2 records as expected.
            assertTrue(actulResult.hasNext());
        } finally {
            server.shutdown();
        }
    }
}

