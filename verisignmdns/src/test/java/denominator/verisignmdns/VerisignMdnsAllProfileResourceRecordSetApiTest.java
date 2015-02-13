package denominator.verisignmdns;

import static denominator.verisignmdns.VerisignMdnsTest.VALID_OWNER1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RDATA1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RR_TYPE_CNAME;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_TTL1;
import static denominator.verisignmdns.VerisignMdnsTest.mockAllProfileResourceRecordSetApi;
import static denominator.verisignmdns.VerisignMdnsTest.rrListValildResponse;

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
