package denominator.verisignmdns;

import static denominator.model.ResourceRecordSets.a;
import static denominator.model.ResourceRecordSets.aaaa;
import static denominator.model.ResourceRecordSets.cname;
import static denominator.model.ResourceRecordSets.txt;
import static denominator.model.ResourceRecordSets.mx;
import static denominator.model.ResourceRecordSets.naptr;
import static denominator.model.ResourceRecordSets.ptr;
import static denominator.model.ResourceRecordSets.srv;
import static denominator.model.ResourceRecordSets.ns;
import static denominator.model.ResourceRecordSets.ds;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import denominator.model.ResourceRecordSet;
import denominator.verisignmdns.VerisignMdns.Record;

final class VerisignContentConversionHelper {

    static ResourceRecordSet<?> convertMDNSRecordToResourceRecordSet(Record mDNSRecord) {

            if ("A".equals(mDNSRecord.type))
                return a(mDNSRecord.name, mDNSRecord.ttl, mDNSRecord.rdata);

            if ("AAAA".equals(mDNSRecord.type))
                return aaaa(mDNSRecord.name, mDNSRecord.ttl, mDNSRecord.rdata);

            if ("NS".equals(mDNSRecord.type))
                return ns(mDNSRecord.name, mDNSRecord.ttl, mDNSRecord.rdata);

            if ("CNAME".equals(mDNSRecord.type))
                return cname(mDNSRecord.name, mDNSRecord.ttl, mDNSRecord.rdata);

            if ("TXT".equals(mDNSRecord.type))
                return txt(mDNSRecord.name, mDNSRecord.ttl, mDNSRecord.rdata);

            if ("MX".equals(mDNSRecord.type))
                return mx(mDNSRecord.name, mDNSRecord.ttl, mDNSRecord.rdata);

            if ("PTR".equals(mDNSRecord.type))
                return ptr(mDNSRecord.name, mDNSRecord.ttl, mDNSRecord.rdata);

            if ("NAPTR".equals(mDNSRecord.type)) {
                List<String> tempRdata = mDNSRecord.rdata;
                return naptr(mDNSRecord.name, mDNSRecord.ttl, tempRdata);
            }

            if ("SRV".equals(mDNSRecord.type))
                return srv(mDNSRecord.name, mDNSRecord.ttl, mDNSRecord.rdata);

            if ("DS".equals(mDNSRecord.type))
                return ds(mDNSRecord.name, mDNSRecord.ttl, mDNSRecord.rdata);

        return null;
    }


    static Set<ResourceRecordSet<?>> getResourceRecordSet(List<Record> mDNSRecordList) {
        Set<ResourceRecordSet<?>> result = new LinkedHashSet<ResourceRecordSet<?>>();
        for (Record rr : mDNSRecordList) {
                result.add(convertMDNSRecordToResourceRecordSet(rr));
            }
        return result;
    }

}

