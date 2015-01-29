package denominator.verisignmdns;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Provider;

import denominator.Credentials;
import denominator.model.Zone;

final class VerisignMDNSZoneApi implements denominator.ZoneApi {
    private final VerisignMdns api;

    @Inject
    VerisignMDNSZoneApi(VerisignMdns api) {
        this.api = api;
    }

    @Override
    public Iterator<Zone> iterator() {
        return api.getZonesForUser().iterator();
    }
}

