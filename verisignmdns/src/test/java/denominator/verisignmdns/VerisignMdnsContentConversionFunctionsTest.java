package denominator.verisignmdns;

import static denominator.verisignmdns.VerisignMdnsTest.VALID_OWNER1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RR_TYPE_NAPTR;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RR_TYPE_SRV;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RR_TYPE_CNAME;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RR_TYPE_MX;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RR_TYPE_TXT;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RDATA_MX1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RDATA_MX2;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RDATA1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_TTL1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_ZONE_NAME;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import denominator.common.Util;
import denominator.model.ResourceRecordSet;
import denominator.model.rdata.NAPTRData;
import denominator.model.rdata.SRVData;
import denominator.verisignmdns.VerisignMdnsContentConversionFunctions;
import denominator.verisignmdns.VerisignMdns.Record;

public class VerisignMdnsContentConversionFunctionsTest {

    @Test
    public void convertMdnsNaptrRecordToResourceRecordSet() throws IOException {
        Record mDNSRecord = VerisignMdnsTest.mockNaptrRecord();
        ResourceRecordSet<?> rrs =
                VerisignMdnsContentConversionFunctions.convertMdnsRecordToResourceRecordSet(mDNSRecord, VALID_ZONE_NAME);

        assertNotNull(rrs);
        assertEquals(rrs.ttl(), new Integer(Integer.parseInt(VALID_TTL1)));
        assertEquals(rrs.type(), VALID_RR_TYPE_NAPTR);
        assertEquals(rrs.name(), VALID_OWNER1);
        Object entry = rrs.records().get(0);
        assertTrue(entry instanceof NAPTRData);
    }

    @Test
    public void convertMdnsSrvRecordToResourceRecordSet() throws IOException {
        Record mDNSRecord = VerisignMdnsTest.mockSrvRecord();
        ResourceRecordSet<?> rrs =
                VerisignMdnsContentConversionFunctions.convertMdnsRecordToResourceRecordSet(mDNSRecord, VALID_ZONE_NAME);

        assertNotNull(rrs);
        assertEquals(rrs.ttl(), new Integer(Integer.parseInt(VALID_TTL1)));
        assertEquals(rrs.type(), VALID_RR_TYPE_SRV);
        assertEquals(rrs.name(), VALID_OWNER1);
        Object entry = rrs.records().get(0);
        assertTrue(entry instanceof SRVData);
    }

    @Test
    public void getResourceRecordSet() {
        List<Record> mDNSRecordList = new ArrayList<Record>();
        mDNSRecordList.add(VerisignMdnsTest.mockNaptrRecord());
        Set<ResourceRecordSet<?>> actualResult =
                VerisignMdnsContentConversionFunctions.getResourceRecordSet(mDNSRecordList, VALID_ZONE_NAME);

        assertNotNull(actualResult);
        assertEquals(mDNSRecordList.size(), actualResult.size());
    }

    @Test
    public void getMergedResourceRecordSet() {
        List<Record> mDNSRecordList = new ArrayList<Record>();
        mDNSRecordList.add(VerisignMdnsTest.mockRecord(VALID_OWNER1, VALID_RR_TYPE_CNAME, VALID_RDATA1));
        mDNSRecordList.add(VerisignMdnsTest.mockRecord(VALID_OWNER1, VALID_RR_TYPE_MX, VALID_RDATA_MX1));
        mDNSRecordList.add(VerisignMdnsTest.mockRecord(VALID_OWNER1, VALID_RR_TYPE_TXT, VALID_RDATA1));
        mDNSRecordList.add(VerisignMdnsTest.mockRecord(VALID_OWNER1, VALID_RR_TYPE_MX, VALID_RDATA_MX2));

        Set<ResourceRecordSet<?>> actualResult =
                VerisignMdnsContentConversionFunctions.getMergedResourceRecordToRRSet(mDNSRecordList, VALID_ZONE_NAME);

        assertNotNull(actualResult);
        assertEquals(actualResult.size(), 3);
        for (ResourceRecordSet<?> rrs : actualResult) {
            if (rrs.type().equals(VALID_RR_TYPE_MX)) {
                assertEquals(rrs.records().size(), 2);
                String rDataString1 = Util.flatten(rrs.records().get(0));
                String rDataString2 = Util.flatten(rrs.records().get(1));
                assertTrue(rDataString1.equals(VALID_RDATA_MX1) || rDataString1.equals(VALID_RDATA_MX2));
                assertTrue(rDataString2.equals(VALID_RDATA_MX1) || rDataString2.equals(VALID_RDATA_MX2));
            } else if (rrs.type().equals(VALID_RR_TYPE_CNAME)) {
                assertEquals(rrs.records().size(), 1);
                String rDataString1 = Util.flatten(rrs.records().get(0));
                assertTrue(rDataString1.equals(VALID_RDATA1));
            } else if (rrs.type().equals(VALID_RR_TYPE_TXT)) {
                assertEquals(rrs.records().size(), 1);
                String rDataString1 = Util.flatten(rrs.records().get(0));
                assertTrue(rDataString1.equals(VALID_RDATA1));
            } else {
                assertTrue(false);
            }
        }
    }

    @Test
    public void getMergedResourceRecordSetEmptyInput() {
        List<Record> mDNSRecordList = new ArrayList<Record>();
        Set<ResourceRecordSet<?>> actualResult =
                VerisignMdnsContentConversionFunctions.getMergedResourceRecordToRRSet(mDNSRecordList, VALID_ZONE_NAME);
        assertNotNull(actualResult);
        assertEquals(actualResult.size(), 0);
    }
}
