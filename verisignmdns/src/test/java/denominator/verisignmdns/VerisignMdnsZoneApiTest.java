package denominator.verisignmdns;

import static denominator.verisignmdns.VerisignMdnsTest.*;
import static denominator.verisignmdns.VerisignMdns.ZoneInfo;
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
    public void validResponseWithZoneListByName() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(zoneInfoResponse));
        server.play();
        try {
            ZoneApi api = mockZoneApi(server.getPort());
            Zone zone = api.iterateByName(VALID_ZONE_NAME1).next();
            assertEquals(zone.name(), VALID_ZONE_NAME1);
            assertNotNull(zone.id());
            assertEquals(server.getRequestCount(), 1);

            String expectedRequest = format(zoneInfoTemplate, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest);
        } finally {
            server.shutdown();
        }
    }
    @Test void validZoneInfo() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(zoneInfoResponse));
        server.play();
        try {
            VerisignMdnsZoneApi api = (VerisignMdnsZoneApi) mockZoneApi(server.getPort());
            Zone zone = getTestZone();
            VerisignMdns.ZoneInfo zoneInfo =  api.zoneInfo(zone.name());
            assertNotNull(zoneInfo);
            assertEquals(zoneInfo.name, zone.name());
            assertEquals(zoneInfo.email, VALID_SOA_EMAIL);
            assertEquals(zoneInfo.expire, Integer.parseInt(VALID_SOA_EXPIRE));
            assertEquals(zoneInfo.refresh, Integer.parseInt(VALID_SOA_REFRESH));
            assertEquals(zoneInfo.retry, Integer.parseInt(VALID_SOA_RETRY));
            assertEquals(zoneInfo.serial, Long.parseLong(VALID_SOA_SERIAL));
            String expectedRequest = format(zoneInfoTemplate, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest);
        } catch (Exception ex) {
            assertTrue(false, "Exception :" + ex.getMessage());
        }
    }

    @Test
    public void validZoneSOAUpdate() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(soaUpdateResponse));
        server.play();
        try {
            VerisignMdnsZoneApi api = (VerisignMdnsZoneApi) mockZoneApi(server.getPort());
            ZoneInfo zoneInfo = getTestZoneInfo();
            api.updateSOA(zoneInfo);
            String expectedRequest = format(soaUpdateTemplate, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1,
                    VALID_SOA_EMAIL, VALID_SOA_RETRY, VALID_SOA_TTL, VALID_SOA_REFRESH, VALID_SOA_EXPIRE);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest);
        } catch (Exception ex) {
            assertTrue(false, "Exception :" + ex.getMessage());
        }
    }

    @Test
    public void validCreateZone() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(zoneCreateResponse));
        server.enqueue(new MockResponse().setBody(zoneInfoResponse));
        server.enqueue(new MockResponse().setBody(soaUpdateResponse));
        server.enqueue(new MockResponse().setBody(zoneInfoResponse));
        server.enqueue(new MockResponse().setBody(zoneDeleteResponse));
        server.play();
        try {
            ZoneApi api = mockZoneApi(server.getPort());
            Zone zone = getTestZone();
            api.put(zone);
            assertEquals(server.getRequestCount(), 3);
            String expectedRequest = format(zoneCreateRequestTemplete, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest);
            server.takeRequest().getBody(); // ignore request for zone info
            server.takeRequest().getBody(); // ignore request for zone soa update.

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

    private static ZoneInfo getTestZoneInfo() {
        ZoneInfo zoneInfo = new ZoneInfo();
        zoneInfo.name = VALID_ZONE_NAME1;
        zoneInfo.email = VALID_SOA_EMAIL;
        zoneInfo.expire = Integer.parseInt(VALID_SOA_EXPIRE);
        zoneInfo.refresh = Integer.parseInt(VALID_SOA_REFRESH);
        zoneInfo.retry = Integer.parseInt(VALID_SOA_RETRY);
        zoneInfo.ttl = Integer.parseInt(VALID_SOA_TTL);
        zoneInfo.serial = Integer.parseInt(VALID_SOA_SERIAL);
        return zoneInfo;
    }
}
