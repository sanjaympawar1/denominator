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
import denominator.model.Zone;
import denominator.verisignmdns.VerisignMdns.Record;

final class VerisignMDNSResourceRecordSetApi implements ResourceRecordSetApi {
    private final String domainName;
    private final VerisignMdns api;

    VerisignMDNSResourceRecordSetApi(String domainName, VerisignMdns api) {
        this.domainName = domainName;
        this.api = api;
    }

    @Override
    public Iterator<ResourceRecordSet<?>> iterator() {
        List<Record> recordList = api.getResourceRecordsList(domainName);
        return VerisignContentConversionHelper.getResourceRecordSet(recordList).iterator();
    }

    public Iterator<ResourceRecordSet<?>> iterateByNameAndType(String name, String type) {
        checkNotNull(type, "type was null");
        checkNotNull(name, "name was null");
        List<Record> recordList = api.getResourceRecordsListForTypeAndName(domainName, type, name);
        Iterator<ResourceRecordSet<?>> result = VerisignContentConversionHelper.getResourceRecordSet(recordList)
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
        Set<ResourceRecordSet<?>> tempSet = VerisignContentConversionHelper.getResourceRecordSet(recordList);
        if (tempSet.size() > 0) {
            result = tempSet.iterator().next();
        }
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
        // delete all records in recordList
        for (Record record : recordList) {
            api.deleteRecourceRecord(domainName, record.id);
        }
    }

    public static final class Factory implements denominator.ResourceRecordSetApi.Factory {
        private Map<Zone, Set<ResourceRecordSet<?>>> records;
        private VerisignMdns api;

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Inject
        Factory(denominator.Provider provider, VerisignMdns api) {
            this.records = Map.class.cast(records);
            this.api = api;
        }

        @Override
        public ResourceRecordSetApi create(String idOrName) {
            Zone zone = Zone.create(idOrName);
            return new VerisignMDNSResourceRecordSetApi(idOrName, api);
        }
    }

    private String getRDataStringFromRRSet(ResourceRecordSet rrSet) {
        StringBuilder sb = new StringBuilder();
        if (rrSet.type().equals("NAPTR")) {
            sb.append(VerisignMdnsRequestHelper.getNAPTRData(rrSet));
        } else {
            for (Object obj : rrSet.records()) {
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
        return sb.toString();
    }
}

