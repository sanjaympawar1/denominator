package denominator.verisignmdns;

import static denominator.common.Preconditions.checkNotNull;

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
            tempList = api.getResourceRecordsList(domainName, pageCounter, DEFAULT_PAGE_SIZE);
            recordList.addAll(tempList);
            pageCounter++;
        } while (tempList.size() >= DEFAULT_PAGE_SIZE);
        return VerisignMdnsContentConversionFunctions.getResourceRecordSet(recordList).iterator();
    }

    @Override
    public Iterator<ResourceRecordSet<?>> iterateByName(String name) {
        // @TODO IMPLEMENT -- in future development phase /////////
        throw new UnsupportedOperationException();
    }

    protected void put(Filter<ResourceRecordSet<?>> valid, ResourceRecordSet<?> rrset) {
        // @TODO IMPLEMENT -- in future development phase /////////
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(ResourceRecordSet<?> rrset) {
        // @TODO IMPLEMENT -- in future development phase /////////
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<ResourceRecordSet<?>> iterateByNameAndType(String name, String type) {
        checkNotNull(type, "type was null");
        checkNotNull(name, "name was null");
        List<Record> recordList = new ArrayList<Record>();
        int pageCounter = 1;
        List<Record> tempList;
        do {
            tempList = api.getResourceRecordsListForNameAndType(domainName, name, type, pageCounter
                    , DEFAULT_PAGE_SIZE);
            recordList.addAll(tempList);
            pageCounter++;
        } while (tempList.size() >= DEFAULT_PAGE_SIZE);
        Iterator<ResourceRecordSet<?>> result = VerisignMdnsContentConversionFunctions.getResourceRecordSet(recordList)
                .iterator();
        return result;
    }

    @Override
    public ResourceRecordSet<?> getByNameTypeAndQualifier(String name, String type, String qualifier) {
        ResourceRecordSet<?> result = null;
        List<Record> recordList = api.getResourceRecordByQualifier(qualifier);
        Set<ResourceRecordSet<?>> rrset = VerisignMdnsContentConversionFunctions.getResourceRecordSet(recordList);
        if (!rrset.isEmpty()) {
            result = rrset.iterator().next();
        }
        return result;
    }

    @Override
    public void deleteByNameTypeAndQualifier(String name, String type, String qualifier) {
        // @TODO IMPLEMENT -- in future development phase /////////
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteByNameAndType(String name, String type) {
        throw new UnsupportedOperationException();
    }

    static class Factory implements denominator.AllProfileResourceRecordSetApi.Factory {
        private VerisignMdns api;

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Inject
        Factory(VerisignMdns api) {
            this.api = api;
        }

        @Override
        public AllProfileResourceRecordSetApi create(String idOrName) {
            return new VerisignMdnsAllProfileResourceRecordSetApi(idOrName, api);
        }
    }
}

