package denominator.verisignmdns;

import java.util.Iterator;

import javax.inject.Inject;

import denominator.model.Zone;

final class VerisignMdnsZoneApi implements denominator.ZoneApi {
    private final VerisignMdns api;

    @Inject
    VerisignMdnsZoneApi(VerisignMdns api) {
        this.api = api;
    }

    @Override
    public Iterator<Zone> iterator() {
        return api.getZonesForUser().iterator();
    }
}

