package denominator.verisignmdns;

import java.util.Collections;
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
        Builder<Map<String, Object>> builder = getResourceRecordSetBuilder(mDNSRecord);
        return builder.build();
    }


    static LinkedHashSet<ResourceRecordSet<?>> getResourceRecordSet(List<Record> mDNSRecordList) {
        LinkedHashSet<ResourceRecordSet<?>> result = new LinkedHashSet<ResourceRecordSet<?>>();
        for (Record rr : mDNSRecordList) {
                result.add(convertMDNSRecordToResourceRecordSet(rr));
            }
        return result;
    }
    

    /**
     * MDNS does not have ResourceRecordSet but only ResourceRecord
     * Here we merge MDNS ResourceRecords by sorting them by Name(owner) and type.
     * Sorting simplifies building of ResourceRecordSet.
     */
    static Set<ResourceRecordSet<?>> getMergedResourceRecordToRRSet(List<Record> mDNSRecordList) {
        Set<ResourceRecordSet<?>> result = new LinkedHashSet<ResourceRecordSet<?>>();
        Collections.sort(mDNSRecordList);
        String currentRecordName = "";
        String currentRecordType = "";
        int currentTtl = -1;
        Builder<Map<String, Object>> builder = null;
        for (Record mDNSRecord : mDNSRecordList) {
            if (!currentRecordName.equals(mDNSRecord.name) || !currentRecordType.equals(mDNSRecord.type)) {
                if (builder != null) {
                    result.add(builder.build());
                }
                builder = getResourceRecordSetBuilder(mDNSRecord);
                currentTtl = mDNSRecord.ttl;
                currentRecordName = mDNSRecord.name;
                currentRecordType = mDNSRecord.type;
            } else {
                builder.add(Util.toMap(mDNSRecord.type, mDNSRecord.rdata));
            }
            if(currentTtl > mDNSRecord.ttl) {
                builder.ttl(mDNSRecord.ttl);
                currentTtl = mDNSRecord.ttl;
            }
        }
        if (builder != null) {
            result.add(builder.build());
        }
        return result;
    }
    
    private static Builder<Map<String, Object>> getResourceRecordSetBuilder(Record mDNSRecord) {
        Builder<Map<String, Object>> builder = ResourceRecordSet.builder().name(mDNSRecord.name).type(mDNSRecord.type)
                .ttl(mDNSRecord.ttl);
        builder.add(Util.toMap(mDNSRecord.type, mDNSRecord.rdata));
        return builder;
    }
}

