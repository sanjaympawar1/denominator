package denominator.verisignmdns;

import static denominator.verisignmdns.VerisignMdnsTest.*;
import static java.lang.String.format;
import static org.testng.Assert.*;

import java.io.IOException;
import java.util.Iterator;

import junit.framework.Assert;
import org.testng.annotations.Test;

import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;

import denominator.ZoneApi;
import denominator.model.Zone;

@Test(singleThreaded = true)
public class VerisignMdnsZoneApiTest {
    @Test
    public void validResponseWithZoneList() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(zoneListResponse));
        server.play();
        try {
            ZoneApi api = mockZoneApi(server.getPort());
            Zone zone = api.iterator().next();
            assertEquals(zone.name(), VALID_ZONE_NAME1);
            assertNotNull(zone.id());
            assertEquals(server.getRequestCount(), 1);

            String expectedRequest = format(zoneListRequestTemplate, TEST_USER_NAME, TEST_PASSWORD);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void validCreateZone() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(zoneCreateResponse));
        server.enqueue(new MockResponse().setBody(zoneListResponse));
        server.enqueue(new MockResponse().setBody(zoneDeleteResponse));
        server.play();
        try {
            ZoneApi api = mockZoneApi(server.getPort());
            Zone zone = getTestZone();
            api.put(zone);
            assertEquals(server.getRequestCount(), 1);
            String expectedRequest = format(zoneCreateRequestTemplete, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest);

            Iterator<Zone> zoneIterator = api.iterateByName(zone.name());
            assertTrue(zoneIterator.hasNext());
            Zone receivedZone = zoneIterator.next();
            assertEquals(zone.name(), receivedZone.name());
            assertFalse(zoneIterator.hasNext());
            assertNotNull(server.takeRequest().getBody());

            api.delete(zone.name());
            expectedRequest = format(zoneDeleteRequestTemplete, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest);

        } catch(Exception e) {
            assertFalse(true, "Test failled due to exception " + e.getMessage());
        }
        finally {
            server.shutdown();
        }
    }
    @Test
    public void authenticationFailResponse() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(authFailureResponse));
        server.play();
        try {
            ZoneApi api = mockZoneApi(server.getPort());
            Iterator<Zone> iter = api.iterator();
            assertEquals(server.getRequestCount(), 1);
            assertFalse(iter.hasNext());
        } finally {
            server.shutdown();
        }
    }

    private static Zone getTestZone() {
        return Zone.create(VALID_ZONE_NAME1, VALID_ZONE_NAME1, 86000, "");
    }
}
