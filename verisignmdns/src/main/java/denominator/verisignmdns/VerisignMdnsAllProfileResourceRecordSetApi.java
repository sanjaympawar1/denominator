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
        // @TODO IMPLEMENT -- in future development phase /////////
        throw new UnsupportedOperationException();
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
        // @TODO IMPLEMENT -- in future development phase /////////
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceRecordSet<?> getByNameTypeAndQualifier(String name, String type, String qualifier) {
        // @TODO IMPLEMENT -- in future development phase /////////
        throw new UnsupportedOperationException();
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

