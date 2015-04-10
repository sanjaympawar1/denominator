package denominator.verisignmdns;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import denominator.model.Zone;

final class VerisignMdnsZoneApi implements denominator.ZoneApi {
    private static final int DEFAULT_PAGE_SIZE = 500;

    @Override
    public Iterator<Zone> iterateByName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String put(Zone zone) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(String id) {
        throw new UnsupportedOperationException();
    }

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
            tempResult = api.getZones(pageCounter, DEFAULT_PAGE_SIZE);
            result.addAll(tempResult);
            pageCounter++;
        } while (tempResult.size() >= DEFAULT_PAGE_SIZE);
        return result.iterator();
    }
}
