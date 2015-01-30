package denominator.verisignmdns;

import static denominator.common.Util.join;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import denominator.model.ResourceRecordSet;
import denominator.model.ResourceRecordSet.Builder;
import denominator.model.rdata.AAAAData;
import denominator.model.rdata.AData;
import denominator.model.rdata.CNAMEData;
import denominator.model.rdata.MXData;
import denominator.model.rdata.NAPTRData;
import denominator.model.rdata.NSData;
import denominator.model.rdata.PTRData;
import denominator.model.rdata.SOAData;
import denominator.model.rdata.SRVData;
import denominator.model.rdata.TXTData;
import denominator.verisignmdns.VerisignMdns.Record;

final class VerisignMdnsContentConversionHelper {

    static ResourceRecordSet<?> convertMDNSRecordToResourceRecordSet(Record mDNSRecord) {
        Builder<Map<String, Object>> builder = ResourceRecordSet.builder()
                .name(mDNSRecord.name)
                .type(mDNSRecord.type)
                .ttl(mDNSRecord.ttl);
        builder.add(forTypeAndRData(mDNSRecord.type, mDNSRecord.rdata));
        return builder.build();
    }


    static Set<ResourceRecordSet<?>> getResourceRecordSet(List<Record> mDNSRecordList) {
        Set<ResourceRecordSet<?>> result = new LinkedHashSet<ResourceRecordSet<?>>();
        for (Record rr : mDNSRecordList) {
                result.add(convertMDNSRecordToResourceRecordSet(rr));
            }
        return result;
    }

    static Map<String, Object> forTypeAndRData(String type, List<String> rdata) {
        if ("A".equals(type)) {
            return AData.create(rdata.get(0));
        } else if ("AAAA".equals(type)) {
            return AAAAData.create(rdata.get(0));
        } else if ("CNAME".equals(type)) {
            return CNAMEData.create(rdata.get(0));
        } else if ("MX".equals(type)) {
            return MXData.create(Integer.valueOf(rdata.get(0)), rdata.get(1));
        } else if ("NS".equals(type)) {
            return NSData.create(rdata.get(0));
        } else if ("PTR".equals(type)) {
            return PTRData.create(rdata.get(0));
        } else if ("SOA".equals(type)) {
            return SOAData.builder().mname(rdata.get(0)).rname(rdata.get(1)).serial(Integer.valueOf(rdata.get(2)))
                    .refresh(Integer.valueOf(rdata.get(3))).retry(Integer.valueOf(rdata.get(4)))
                    .expire(Integer.valueOf(rdata.get(5))).minimum(Integer.valueOf(rdata.get(6))).build();
        } else if ("SRV".equals(type)) {
            return SRVData.builder().priority(Integer.valueOf(rdata.get(0))).weight(Integer.valueOf(rdata.get(1)))
                    .port(Integer.valueOf(rdata.get(2))).target(rdata.get(3)).build();
        } else if ("NAPTR".equals(type)) {
            return NAPTRData.builder().order(Integer.valueOf(rdata.get(0))).preference(Integer.valueOf(rdata.get(1)))
                    .flags(rdata.get(2)).services(rdata.get(3)).regexp(rdata.get(4)).replacement(rdata.get(5)).build();
        } else if ("TXT".equals(type)) {
            return TXTData.create(rdata.get(0));
        } else {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("rdata", join(' ', rdata));
            return map;
        }
    }
}

