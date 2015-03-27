package denominator.verisignmdns;

import static denominator.verisignmdns.VerisignMdnsTest.TEST_PASSWORD;
import static denominator.verisignmdns.VerisignMdnsTest.TEST_USER_NAME;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_OWNER1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RDATA1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RDATA_MX1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RDATA_MX2;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RDATA_MX3;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RR_TYPE_CNAME;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RR_TYPE_MX;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_TTL1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_ZONE_NAME1;
import static denominator.verisignmdns.VerisignMdnsTest.createRequestARecordResponse;
import static denominator.verisignmdns.VerisignMdnsTest.createRequestRecordTemplete;
import static denominator.verisignmdns.VerisignMdnsTest.mockAllProfileResourceRecordSetApi;
import static denominator.verisignmdns.VerisignMdnsTest.mockResourceRecordSetApi;
import static denominator.verisignmdns.VerisignMdnsTest.rrByNameAndTypeTemplate;
import static denominator.verisignmdns.VerisignMdnsTest.rrDeleteResponse;
import static denominator.verisignmdns.VerisignMdnsTest.rrDeleteTemplete;
import static denominator.verisignmdns.VerisignMdnsTest.rrListCNAMETypesResponse;
import static denominator.verisignmdns.VerisignMdnsTest.rrListRequestTemplate;
import static denominator.verisignmdns.VerisignMdnsTest.rrListValidResponseNoRecords;
import static denominator.verisignmdns.VerisignMdnsTest.rrListValildResponse;
import static denominator.verisignmdns.VerisignMdnsTest.mXDataSameOwnerResponse;
import static denominator.verisignmdns.VerisignMdnsTest.rrDelete2RecordsTemplete;
import static denominator.verisignmdns.VerisignMdnsTest.rrRecordUpdateResponse;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_ZONE_NAME;
import static denominator.verisignmdns.VerisignMdnsTest.RESOURCE_RECORD_ID1;
import static denominator.verisignmdns.VerisignMdnsTest.RESOURCE_RECORD_ID2;
import static denominator.verisignmdns.VerisignMdnsTest.updateRecordTemplate;
import static java.lang.String.format;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;

import denominator.ResourceRecordSetApi;
import denominator.common.Util;
import denominator.model.ResourceRecordSet;
import denominator.model.rdata.CNAMEData;
import denominator.model.rdata.MXData;
import denominator.verisignmdns.VerisignMdnsContentConversionFunctions;
import denominator.verisignmdns.VerisignMdns.Record;

public class VerisignMdnsResourceRecordSetApiTest {

    @Test
    public void rrListValidResponseNoRecords() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        // setting two mock response to check result list is not appended
        server.enqueue(new MockResponse().setBody(rrListValidResponseNoRecords));
        server.play();
        try {
            ResourceRecordSetApi api = mockResourceRecordSetApi(server.getPort());
            Iterator<ResourceRecordSet<?>> iter = api.iterator();
            assertNotNull(iter);
            assertFalse(iter.hasNext());
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void rrListvalidResponse() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        // setting two mock response to check result list is not appended
        server.enqueue(new MockResponse().setBody(rrListValildResponse));
        server.enqueue(new MockResponse().setBody(rrListValildResponse));
        server.play();
        try {
            ResourceRecordSetApi api = mockResourceRecordSetApi(server.getPort());
            Iterator<ResourceRecordSet<?>> iter = api.iterator();
            ResourceRecordSet<?> rrs = iter.next();
            assertNotNull(rrs);
            assertEquals(rrs.ttl(), new Integer(Integer.parseInt(VALID_TTL1)));
            assertEquals(rrs.type(), VALID_RR_TYPE_CNAME);
            assertEquals(rrs.name(), VALID_OWNER1);
            Object entry = rrs.records().get(0);
            assertTrue(entry instanceof CNAMEData);
            CNAMEData cnameData = (CNAMEData) entry;
            assertEquals(cnameData.values().iterator().next(), VALID_RDATA1);
            // verify we have 2 records as expected.
            assertTrue(iter.hasNext());
            String expectedRequest = format(rrListRequestTemplate, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest);

            // again requesting the same to check records are not appended.
            iter = api.iterator();
            int count = 0;
            while (iter.hasNext()) {
                iter.next();
                count++;
            }
            assertEquals(count, 2);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void iterateByName() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(rrListValildResponse));
        server.play();
        try {
            VerisignMdnsAllProfileResourceRecordSetApi verisignMdnsAllProfileResourceRecordSetApi =
                    mockAllProfileResourceRecordSetApi(server.getPort());
            Iterator<ResourceRecordSet<?>> actulResult = verisignMdnsAllProfileResourceRecordSetApi.iterateByName(VALID_OWNER1);
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
            // verify we have 1 records as expected.
            assertTrue(!actulResult.hasNext());
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void rrListvalidResponseSameOwnerAndType() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        // setting two mock response to check result list is not appended
        server.enqueue(new MockResponse().setBody(mXDataSameOwnerResponse));
        server.enqueue(new MockResponse().setBody(mXDataSameOwnerResponse));
        server.play();
        try {
            ResourceRecordSetApi api = mockResourceRecordSetApi(server.getPort());
            Iterator<ResourceRecordSet<?>> iter = api.iterator();
            ResourceRecordSet<?> rrs = iter.next();
            assertNotNull(rrs);
            assertEquals(rrs.ttl(), new Integer(Integer.parseInt(VALID_TTL1)));
            assertEquals(rrs.type(), VALID_RR_TYPE_MX);
            assertEquals(rrs.name(), VALID_OWNER1);
            Object entry = rrs.records().get(0);
            assertTrue(entry instanceof MXData);
            String rData = Util.flatten((Map<String, Object>) entry);
            assertTrue(VALID_RDATA_MX1.equals(rData) || VALID_RDATA_MX2.equals(rData));
            // verify we have 1 records as expected.
            assertTrue(!iter.hasNext());
            String expectedRequest = format(rrListRequestTemplate, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest);

            // again requesting the same to check records are not appended.
            iter = api.iterator();
            int count = 0;
            while (iter.hasNext()) {
                iter.next();
                count++;
            }
            assertEquals(count, 1);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void rrListByNameAndTypeValidResponse() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(rrListValildResponse));
        server.play();
        try {
            ResourceRecordSetApi api = mockResourceRecordSetApi(server.getPort());
            ResourceRecordSet<?> rrs = api.getByNameAndType(VALID_OWNER1, VALID_RR_TYPE_CNAME);
            assertNotNull(rrs);
            assertEquals(rrs.ttl(), new Integer(Integer.parseInt(VALID_TTL1)));
            assertEquals(rrs.type(), VALID_RR_TYPE_CNAME);
            assertEquals(rrs.name(), VALID_OWNER1);
            Object entry = rrs.records().get(0);
            assertTrue(entry instanceof CNAMEData);
            CNAMEData cnameData = (CNAMEData) entry;
            assertEquals(cnameData.values().iterator().next(), VALID_RDATA1);
            String expectedRequest =
                    format(rrByNameAndTypeTemplate, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1,
                            VALID_RR_TYPE_CNAME, VALID_OWNER1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void testGetByNameAndType() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(rrListValildResponse));
        server.play();
        try {
            ResourceRecordSetApi api = mockResourceRecordSetApi(server.getPort());
            ResourceRecordSet<?> actualResult = api.getByNameAndType(VALID_OWNER1, VALID_RR_TYPE_CNAME);
            assertNotNull(actualResult);
            assertEquals(actualResult.ttl(), new Integer(Integer.parseInt(VALID_TTL1)));
            assertEquals(actualResult.type(), VALID_RR_TYPE_CNAME);
            assertEquals(actualResult.name(), VALID_OWNER1);
            Object entry = actualResult.records().get(0);
            assertTrue(entry instanceof CNAMEData);
            CNAMEData cnameData = (CNAMEData) entry;
            assertEquals(cnameData.values().iterator().next(), VALID_RDATA1);
            String expectedRequest =
                    format(rrByNameAndTypeTemplate, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1,
                            VALID_RR_TYPE_CNAME, VALID_OWNER1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void testDeleteByNameAndType() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(rrListCNAMETypesResponse));
        server.enqueue(new MockResponse().setBody(rrDeleteResponse));
        server.play();
        try {
            ResourceRecordSetApi apiforDelete = mockResourceRecordSetApi(server.getPort());
            apiforDelete.deleteByNameAndType(VALID_OWNER1, VALID_RR_TYPE_CNAME);
            String expectedRequest1 =
                    format(rrByNameAndTypeTemplate, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1,
                            VALID_RR_TYPE_CNAME, VALID_OWNER1);
            String expectedRequest2 =
                    format(rrDeleteTemplete, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1, RESOURCE_RECORD_ID1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest2);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void testDeleteByNameAndTypeSameOwnerType2Records() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(mXDataSameOwnerResponse));
        server.enqueue(new MockResponse().setBody(rrDeleteResponse));
        server.play();
        try {
            ResourceRecordSetApi apiforDelete = mockResourceRecordSetApi(server.getPort());
            apiforDelete.deleteByNameAndType(VALID_OWNER1, VALID_RR_TYPE_MX);
            String expectedRequest1 =
                    format(rrByNameAndTypeTemplate, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1, VALID_RR_TYPE_MX,
                            VALID_OWNER1);
            String expectedRequest2 = format(rrDelete2RecordsTemplete, TEST_USER_NAME, TEST_PASSWORD);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest2);
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void testPut() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(mXDataSameOwnerResponse));
        server.enqueue(new MockResponse().setBody(rrDeleteResponse));
        String updateResponse =
                format(rrRecordUpdateResponse, VALID_ZONE_NAME, RESOURCE_RECORD_ID1, VALID_OWNER1, VALID_RR_TYPE_MX,
                        VALID_TTL1, VALID_RDATA_MX1);
        server.enqueue(new MockResponse().setBody(updateResponse));
        server.enqueue(new MockResponse().setBody(createRequestARecordResponse));
        server.play();
        try {
            ResourceRecordSetApi api = mockResourceRecordSetApi(server.getPort());
            List<Record> mdnsRecordList = new ArrayList<Record>();
            mdnsRecordList.add(VerisignMdnsTest.mockRecord(VALID_OWNER1, VALID_RR_TYPE_MX, VALID_RDATA_MX1));
            mdnsRecordList.add(VerisignMdnsTest.mockRecord(VALID_OWNER1, VALID_RR_TYPE_MX, VALID_RDATA_MX3));

            Set<ResourceRecordSet<?>> inputRecordSet =
                    VerisignMdnsContentConversionFunctions.getMergedResourceRecordToRRSet(mdnsRecordList, VALID_ZONE_NAME);
            api.put(inputRecordSet.iterator().next());

            String expectedRequest1 =
                    format(rrByNameAndTypeTemplate, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1, VALID_RR_TYPE_MX,
                            VALID_OWNER1);
            String expectedRequest2 =
                    format(rrDeleteTemplete, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1, RESOURCE_RECORD_ID2);
            String expectRequestUpdate =
                    format(updateRecordTemplate, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1, RESOURCE_RECORD_ID1,
                            VALID_OWNER1, VALID_RR_TYPE_MX, VALID_TTL1, VALID_RDATA_MX1);
            String expectedRequestCreate =
                    format(createRequestRecordTemplete, TEST_USER_NAME, TEST_PASSWORD, VALID_ZONE_NAME1, VALID_OWNER1,
                            VALID_RR_TYPE_MX, VALID_TTL1, VALID_RDATA_MX3);

            assertEquals(new String(server.takeRequest().getBody()), expectedRequest1);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequest2);
            assertEquals(new String(server.takeRequest().getBody()), expectRequestUpdate);
            assertEquals(new String(server.takeRequest().getBody()), expectedRequestCreate);
        } finally {
            server.shutdown();
        }
    }
}