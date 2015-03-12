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

    static ResourceRecordSet<?> convertMdnsRecordToResourceRecordSet(Record mdnsRecord) {
        Builder<Map<String, Object>> builder = getResourceRecordSetBuilder(mdnsRecord);
        return builder.build();
    }


    static LinkedHashSet<ResourceRecordSet<?>> getResourceRecordSet(List<Record> mdnsRecordList) {
        LinkedHashSet<ResourceRecordSet<?>> result = new LinkedHashSet<ResourceRecordSet<?>>();
        for (Record rr : mdnsRecordList) {
            result.add(convertMdnsRecordToResourceRecordSet(rr));
        }
        return result;
    }


    /**
     * MDNS does not have ResourceRecordSet but only ResourceRecord Here we merge MDNS
     * ResourceRecords by sorting them by Name(owner) and type. Sorting simplifies building of
     * ResourceRecordSet.
     */
    static Set<ResourceRecordSet<?>> getMergedResourceRecordToRRSet(List<Record> mdnsRecordList) {
        Set<ResourceRecordSet<?>> result = new LinkedHashSet<ResourceRecordSet<?>>();
        Collections.sort(mdnsRecordList);
        String currentRecordName = "";
        String currentRecordType = "";
        int currentTtl = -1;
        Builder<Map<String, Object>> builder = null;
        for (Record mdnsRecord : mdnsRecordList) {
            if (!currentRecordName.equals(mdnsRecord.name) || !currentRecordType.equals(mdnsRecord.type)) {
                if (builder != null) {
                    result.add(builder.build());
                }
                builder = getResourceRecordSetBuilder(mdnsRecord);
                currentTtl = mdnsRecord.ttl;
                currentRecordName = mdnsRecord.name;
                currentRecordType = mdnsRecord.type;
            } else {
                builder.add(Util.toMap(mdnsRecord.type, mdnsRecord.rdata));
            }
            if (currentTtl > mdnsRecord.ttl) {
                builder.ttl(mdnsRecord.ttl);
                currentTtl = mdnsRecord.ttl;
            }
        }
        if (builder != null) {
            result.add(builder.build());
        }
        return result;
    }

    private static Builder<Map<String, Object>> getResourceRecordSetBuilder(Record mdnsRecord) {
        Builder<Map<String, Object>> builder =
                ResourceRecordSet.builder().name(mdnsRecord.name).type(mdnsRecord.type).ttl(mdnsRecord.ttl);
        builder.add(Util.toMap(mdnsRecord.type, mdnsRecord.rdata));
        return builder;
    }
}
