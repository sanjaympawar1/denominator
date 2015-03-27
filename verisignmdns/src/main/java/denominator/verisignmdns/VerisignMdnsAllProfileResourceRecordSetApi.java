package denominator.verisignmdns;

import static denominator.common.Preconditions.checkNotNull;
import static denominator.common.Util.filter;
import static denominator.model.ResourceRecordSets.nameEqualTo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import denominator.AllProfileResourceRecordSetApi;
import denominator.common.Filter;
import denominator.model.ResourceRecordSet;
import denominator.verisignmdns.VerisignMdns.Record;

final class VerisignMdnsAllProfileResourceRecordSetApi implements denominator.AllProfileResourceRecordSetApi {
    private static final int DEFAULT_PAGE_SIZE = 100;
    private final String domainName;
    private final VerisignMdns api;

    VerisignMdnsAllProfileResourceRecordSetApi(String domainName, VerisignMdns api) {
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
                VerisignMdnsContentConversionFunctions.getMergedResourceRecordToRRSet(recordList, domainName).iterator();
        return result;
    }

    @Override
    public Iterator<ResourceRecordSet<?>> iterateByName(String name) {
      checkNotNull(name, "name was null");
      return filter(iterator(), nameEqualTo(name));
    }


    protected void put(Filter<ResourceRecordSet<?>> valid, ResourceRecordSet<?> rrset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(ResourceRecordSet<?> rrset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<ResourceRecordSet<?>> iterateByNameAndType(String name, String type) {
        checkNotNull(type, "type was null");
        checkNotNull(name, "name was null");
        Iterator<ResourceRecordSet<?>> result =
                VerisignMdnsContentConversionFunctions.getMergedResourceRecordToRRSet(
                        getByNameAndTypeFromMDNS(name, type), domainName).iterator();
        return result;
    }

    @Override
    public ResourceRecordSet<?> getByNameTypeAndQualifier(String name, String type, String qualifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteByNameTypeAndQualifier(String name, String type, String qualifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteByNameAndType(String name, String type) {
        throw new UnsupportedOperationException();
    }

    static class Factory implements denominator.AllProfileResourceRecordSetApi.Factory {
        private VerisignMdns api;

        @Inject
        Factory(VerisignMdns api) {
            this.api = api;
        }

        @Override
        public AllProfileResourceRecordSetApi create(String idOrName) {
            return new VerisignMdnsAllProfileResourceRecordSetApi(idOrName, api);
        }
    }

    private List<Record> getByNameAndTypeFromMDNS(String name, String type) {
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
}
