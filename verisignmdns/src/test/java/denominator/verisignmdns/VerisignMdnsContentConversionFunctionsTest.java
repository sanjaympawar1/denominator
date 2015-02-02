package denominator.verisignmdns;

import static denominator.verisignmdns.VerisignMdnsTest.VALID_OWNER1;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RR_TYPE_NAPTR;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_RR_TYPE_SRV;
import static denominator.verisignmdns.VerisignMdnsTest.VALID_TTL1;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;



import org.testng.annotations.Test;

import denominator.model.ResourceRecordSet;
import denominator.model.rdata.NAPTRData;
import denominator.model.rdata.SRVData;
import denominator.verisignmdns.VerisignMdnsContentConversionFunctions;
import denominator.verisignmdns.VerisignMdns.Record;

public class VerisignMdnsContentConversionFunctionsTest {

    @Test
    public void convertMdnsNaptrRecordToDenominator() throws IOException {
        Record mDNSRecord = VerisignMdnsTest.mockNaptrRecord();
        ResourceRecordSet<?> rrs = VerisignMdnsContentConversionFunctions.convertMDNSRecordToResourceRecordSet(mDNSRecord);

        assertNotNull(rrs);
        assertEquals(rrs.ttl(), new Integer(Integer.parseInt(VALID_TTL1)));
        assertEquals(rrs.type(), VALID_RR_TYPE_NAPTR);
        assertEquals(rrs.name(), VALID_OWNER1);
        Object entry = rrs.records().get(0);
        assertTrue(entry instanceof NAPTRData);
    }

    @Test
    public void convertMdnsSrvRecordToDenominator() throws IOException {
        Record mDNSRecord = VerisignMdnsTest.mockSrvRecord();
        ResourceRecordSet<?> rrs = VerisignMdnsContentConversionFunctions.convertMDNSRecordToResourceRecordSet(mDNSRecord);

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
        Set<ResourceRecordSet<?>> actualResult = VerisignMdnsContentConversionFunctions
                .getResourceRecordSet(mDNSRecordList);

        assertNotNull(actualResult);
        assertEquals(mDNSRecordList.size(), actualResult.size());
    }
}

