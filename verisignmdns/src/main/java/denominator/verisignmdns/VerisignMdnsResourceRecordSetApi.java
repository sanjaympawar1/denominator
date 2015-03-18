package denominator.verisignmdns;

import static denominator.common.Preconditions.checkNotNull;
import static denominator.common.Util.filter;
import static denominator.model.ResourceRecordSets.nameEqualTo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import denominator.ResourceRecordSetApi;
import denominator.common.Util;
import denominator.model.ResourceRecordSet;
import denominator.verisignmdns.VerisignMdns.Record;

final class VerisignMdnsResourceRecordSetApi implements ResourceRecordSetApi {
    private static final int DEFAULT_PAGE_SIZE = 100;
    private final String domainName;
    private final VerisignMdns api;

    VerisignMdnsResourceRecordSetApi(String domainName, VerisignMdns api) {
        this.domainName = domainName;
        this.api = api;
    }

    @Override
    public Iterator<ResourceRecordSet<?>> iterator() {
        List<Record> recordList = new ArrayList<Record>();
        int pageCounter = 1;
        List<Record> tempList;
        do {
            tempList = api.getResourceRecords(domainName, pageCounter, DEFAULT_PAGE_SIZE);
            recordList.addAll(tempList);
            pageCounter++;
        } while (tempList.size() >= DEFAULT_PAGE_SIZE);
        Iterator<ResourceRecordSet<?>> result =
                VerisignMdnsContentConversionFunctions.getMergedResourceRecordToRRSet(recordList).iterator();
        return result;
    }

    public Iterator<ResourceRecordSet<?>> iterateByNameAndType(String name, String type) {
        checkNotNull(type, "type was null");
        checkNotNull(name, "name was null");
        Iterator<ResourceRecordSet<?>> result =
                VerisignMdnsContentConversionFunctions.getMergedResourceRecordToRRSet(
                        getByNameAndTypefromMDNS(name, type)).iterator();
        return result;
    }

    @Override
    public Iterator<ResourceRecordSet<?>> iterateByName(String name) {
      checkNotNull(name, "name was null");
      return filter(iterator(), nameEqualTo(name));
    }

    @Override
    public ResourceRecordSet<?> getByNameAndType(String name, String type) {
        checkNotNull(type, "type was null");
        checkNotNull(name, "name was null");
        ResourceRecordSet<?> result = null;
        Iterator<ResourceRecordSet<?>> iter = iterateByNameAndType(name, type);
        if (iter.hasNext()) {
            result = iter.next();
        }
        return result;
    }

    /**
     * Put should replace entire existing RRSet with new RRSet (by deleting existing RRSet) However
     * MDNS has latency in delete operation, i.e. the deleted record can cause error while inserting
     * new record with similar attributes. Thus we are trying to minimize delete operations by using
     * update operation for existing ResourceRecords in MDNS.
     */
    @Override
    public void put(ResourceRecordSet<?> rrset) {
        checkNotNull(rrset, "Resource Record was null");
        checkNotNull(rrset.name(), "Resource Record Name was null");
        checkNotNull(rrset.type(), "Resource Record Type was null");
        // deleting existing RRs
        List<Record> mdnsRecords = getByNameAndTypefromMDNS(rrset.name(), rrset.type());
        int ttlInt = 86000;
        Integer ttlRRSet = rrset.ttl();
        if (ttlRRSet != null) {
            ttlInt = ttlRRSet.intValue();
        }
        Set<String> rdataMdnsExisting = new LinkedHashSet<String>();
        Map<String, Record> mdnsRDataMap = new LinkedHashMap<String, Record>();
        for (Record record : mdnsRecords) {
            mdnsRDataMap.put(record.rdata, record);
            rdataMdnsExisting.add(record.rdata);
        }
        Set<String> rDataForUpdate = new LinkedHashSet<String>(rdataMdnsExisting);
        Set<String> rDataForDelete = new LinkedHashSet<String>(rdataMdnsExisting);
        Set<String> rDataFromInputRRSet = getRDataFromRRSet(rrset);
        rDataForUpdate.retainAll(rDataFromInputRRSet);
        rDataForDelete.removeAll(rDataForUpdate);
        rDataFromInputRRSet.removeAll(rDataForUpdate);
        if (!rDataForDelete.isEmpty()) {
            deleteMdnsRecords(mdnsRDataMap, rDataForDelete);
        }
        if (!rDataForUpdate.isEmpty()) {
            updateMdnsRecords(mdnsRDataMap, rDataForUpdate, rrset.ttl());
        }
        if (!rDataFromInputRRSet.isEmpty()) {
            api.createResourceRecords(domainName, rrset.type(), rrset.name(), ttlInt, new ArrayList<String>(
                    rDataFromInputRRSet));
        }
    }

    @Override
    public void deleteByNameAndType(String name, String type) {
        List<Record> recordList = getByNameAndTypefromMDNS(name, type);
        if (recordList.isEmpty()) {
            return;
        }
        List<String> recordIdList = new LinkedList<String>();
        for (Record record : recordList) {
            recordIdList.add(record.id);
        }
        api.deleteRecourceRecords(domainName, recordIdList);
    }

    public static final class Factory implements denominator.ResourceRecordSetApi.Factory {
        private VerisignMdns api;

        @Inject
        Factory(VerisignMdns api) {
            this.api = api;
        }

        @Override
        public ResourceRecordSetApi create(String idOrName) {
            return new VerisignMdnsResourceRecordSetApi(idOrName, api);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<String> getRDataFromRRSet(ResourceRecordSet<?> rrset) {
        Set<String> result = new LinkedHashSet<String>();
        for (Object obj : rrset.records()) {
            if (obj instanceof Map) {
                result.add(Util.flatten((Map<String, Object>) obj));
            } else {
                result.add(obj.toString());
            }
        }
        return result;
    }

    private List<Record> getByNameAndTypefromMDNS(String name, String type) {
        List<Record> recordList = new ArrayList<Record>();
        int pageCounter = 1;
        List<Record> tempList;
        do {
            tempList = api.getResourceRecords(domainName, name, type, pageCounter, DEFAULT_PAGE_SIZE);
            recordList.addAll(tempList);
            pageCounter++;
        } while (tempList.size() >= DEFAULT_PAGE_SIZE);
        return recordList;
    }

    private void updateMdnsRecords(Map<String, Record> existingRRMap, Set<String> rDataSet, int ttl) {
        for (String rData : rDataSet) {
            Record rr = existingRRMap.get(rData);
            api.updateResourceRecord(domainName, rr.id, rr.name, rr.type, ttl, rr.rdata);
        }
    }

    private void deleteMdnsRecords(Map<String, Record> existingRRMap, Set<String> rDataSet) {
        List<String> recordIdList = new ArrayList<String>();
        for (String rData : rDataSet) {
            Record rr = existingRRMap.get(rData);
            recordIdList.add(rr.id);
        }
        if (!recordIdList.isEmpty()) {
            api.deleteRecourceRecords(domainName, recordIdList);
        }
    }
}
