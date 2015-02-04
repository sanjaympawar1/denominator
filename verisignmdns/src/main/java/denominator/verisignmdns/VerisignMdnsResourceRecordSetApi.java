package denominator.verisignmdns;

import static denominator.common.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import denominator.ResourceRecordSetApi;
import denominator.common.Util;
import denominator.model.ResourceRecordSet;
import denominator.verisignmdns.VerisignMdns.Record;

final class VerisignMdnsResourceRecordSetApi implements ResourceRecordSetApi {
    private final String domainName;
    private final VerisignMdns api;

    VerisignMdnsResourceRecordSetApi(String domainName, VerisignMdns api) {
        this.domainName = domainName;
        this.api = api;
    }

    @Override
    public Iterator<ResourceRecordSet<?>> iterator() {
        List<Record> recordList = api.getResourceRecordsList(domainName);
        return VerisignMdnsContentConversionFunctions.getResourceRecordSet(recordList).iterator();
    }

    public Iterator<ResourceRecordSet<?>> iterateByNameAndType(String name, String type) {
        checkNotNull(type, "type was null");
        checkNotNull(name, "name was null");
        List<Record> recordList = api.getResourceRecordsListForTypeAndName(domainName, type, name);
        Iterator<ResourceRecordSet<?>> result = VerisignMdnsContentConversionFunctions.getResourceRecordSet(recordList)
                .iterator();
        return result;
    }

    @Override
    public Iterator<ResourceRecordSet<?>> iterateByName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceRecordSet<?> getByNameAndType(String name, String type) {
        checkNotNull(type, "type was null");
        checkNotNull(name, "name was null");
        ResourceRecordSet<?> result = null;
        List<Record> recordList = api.getResourceRecordsListForTypeAndName(domainName, type, name);
        Set<ResourceRecordSet<?>> tempSet = VerisignMdnsContentConversionFunctions.getResourceRecordSet(recordList);
        result = tempSet.iterator().next();
        return result;
    }

    @Override
    public void put(ResourceRecordSet<?> rrset) {
        checkNotNull(rrset, "Resource Record was null");
        checkNotNull(rrset.name(), "Resource Record Name was null");
        checkNotNull(rrset.type(), "Resource Record Type was null");
        // At this point disable delete
        // as we might delete multiple records for name and type.
        // Need to decide if that is correct
        // ResourceRecordSet<?> rrsMatch = getByNameAndType(rrset.name(),
        // rrset.type());
        // if (rrsMatch != null) {
        // deleteByNameAndType(rrset.name(), rrset.type());
        // }
        int ttlInt = 86000;
        Integer ttlRRSet = rrset.ttl();
        if (ttlRRSet != null) {
            ttlInt = ttlRRSet.intValue();
        }
        String rData = getRDataStringFromRRSet(rrset);
        api.createResourceRecord(domainName, rrset.type(), rrset.name(), "" + ttlInt, rData);
    }

    @Override
    public void deleteByNameAndType(String name, String type) {
        List<Record> recordList = api.getResourceRecordsListForTypeAndName(domainName, type, name);
        for (Record record : recordList) {
            api.deleteRecourceRecord(domainName, record.id);
        }
    }

    public static final class Factory implements denominator.ResourceRecordSetApi.Factory {
        private VerisignMdns api;

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Inject
        Factory(VerisignMdns api) {
            this.api = api;
        }

        @Override
        public ResourceRecordSetApi create(String idOrName) {
            return new VerisignMdnsResourceRecordSetApi(idOrName, api);
        }
    }

    private String getRDataStringFromRRSet(ResourceRecordSet rrset) {
        StringBuilder sb = new StringBuilder();
        if (rrset != null) {
            if (rrset.type().equals("NAPTR")) {
                sb.append(VerisignMdnsRequestFunctions.getNAPTRData(rrset));
            } else {
                for (Object obj : rrset.records()) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    if (obj instanceof Map) {
                        sb.append(Util.flatten((Map<String, Object>) obj));
                    } else {
                        sb.append(obj.toString());
                    }
                }
            }
        }
        return sb.toString();
    }
}

