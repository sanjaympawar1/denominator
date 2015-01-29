package denominator.verisignmdns;

import static denominator.common.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import denominator.AllProfileResourceRecordSetApi;
import denominator.common.Filter;
import denominator.model.ResourceRecordSet;
import denominator.model.Zone;
import denominator.verisignmdns.VerisignMdns.Record;

final class VerisignMDNSAllProfileResourceRecordSetApi implements denominator.AllProfileResourceRecordSetApi {
    private final String domainName;
    private final VerisignMdns api;

    VerisignMDNSAllProfileResourceRecordSetApi(String domainName,
            VerisignMdns api) {
        this.domainName = domainName;
        this.api = api;
    }

    /**
     * Returns Sorted Set of Resource Record Set to to keep behavior similar to
     * MDNS web UI.
     */
    @Override
    public Iterator<ResourceRecordSet<?>> iterator() {
        List<Record> recordList = api.getResourceRecordsList(domainName);
        return VerisignContentConversionHelper.getResourceRecordSet(recordList).iterator();
    }

    @Override
    public Iterator<ResourceRecordSet<?>> iterateByName(String name) {
        throw new UnsupportedOperationException();
    }

    void put(Filter<ResourceRecordSet<?>> valid, ResourceRecordSet<?> rrset) {
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

        List<Record> recordList = api.getResourceRecordsListForTypeAndName(domainName, type, name);
        Iterator<ResourceRecordSet<?>> result = VerisignContentConversionHelper.getResourceRecordSet(recordList)
                .iterator();
        return result;
    }

    /**
     * NOTE- for MDNS get only required ResourceRecordId ie. qualifier.
     * Parameters name and type are ignored.
     */
    @Override
    public ResourceRecordSet<?> getByNameTypeAndQualifier(String name, String type, String qualifier) {
        ResourceRecordSet<?> result = null;
        List<Record> recordList = api.getResourceRecordByQualifier(qualifier);
        Set<ResourceRecordSet<?>> rrSet = VerisignContentConversionHelper.getResourceRecordSet(recordList);
        if (!rrSet.isEmpty()) {
            result = rrSet.iterator().next();
        }
        return result;
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
        private Map<Zone, Set<ResourceRecordSet<?>>> records;
        private VerisignMdns api;

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Inject
        Factory(denominator.Provider provider, VerisignMdns api) {
            this.records = Map.class.cast(records);
            this.api = api;
        }

        @Override
        public AllProfileResourceRecordSetApi create(String idOrName) {
            return new VerisignMDNSAllProfileResourceRecordSetApi(idOrName, api);
        }
    }
}

