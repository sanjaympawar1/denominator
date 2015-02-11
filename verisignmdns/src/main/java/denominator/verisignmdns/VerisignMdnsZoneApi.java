package denominator.verisignmdns;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import denominator.model.Zone;

final class VerisignMdnsZoneApi implements denominator.ZoneApi {
    private static final int DEFAULT_PAGE_SIZE = 500;
    private final VerisignMdns api;
    
    @Inject
    VerisignMdnsZoneApi(VerisignMdns api) {
        this.api = api;
    }

    @Override
    public Iterator<Zone> iterator() {
        List<Zone> result = new ArrayList<Zone>();
        int pageCounter = 1;
        List<Zone> tempResult;
        do {
            tempResult = api.getZonesForUser(pageCounter, DEFAULT_PAGE_SIZE);
            result.addAll(tempResult);
            pageCounter++;
        } while(tempResult.size() >= DEFAULT_PAGE_SIZE);
        return result.iterator();
    }
}

