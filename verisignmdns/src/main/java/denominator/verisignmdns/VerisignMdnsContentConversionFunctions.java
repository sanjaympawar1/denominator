package denominator.verisignmdns;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import denominator.common.Util;
import denominator.model.ResourceRecordSet;
import denominator.model.ResourceRecordSet.Builder;
import denominator.verisignmdns.VerisignMdns.Record;

final class VerisignMdnsContentConversionFunctions {

    static ResourceRecordSet<?> convertMDNSRecordToResourceRecordSet(Record mDNSRecord) {
        Builder<Map<String, Object>> builder = ResourceRecordSet.builder()
                .name(mDNSRecord.name)
                .type(mDNSRecord.type)
                .ttl(mDNSRecord.ttl);
        builder.add(Util.toMap(mDNSRecord.type, mDNSRecord.rdata));
        return builder.build();
    }


    static Set<ResourceRecordSet<?>> getResourceRecordSet(List<Record> mDNSRecordList) {
        Set<ResourceRecordSet<?>> result = new LinkedHashSet<ResourceRecordSet<?>>();
        for (Record rr : mDNSRecordList) {
                result.add(convertMDNSRecordToResourceRecordSet(rr));
            }
        return result;
    }
}

