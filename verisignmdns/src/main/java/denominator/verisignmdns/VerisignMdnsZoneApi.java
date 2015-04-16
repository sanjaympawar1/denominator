package denominator.verisignmdns;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import denominator.model.Zone;

import static denominator.common.Preconditions.checkNotNull;
import static denominator.common.Util.filter;
import static denominator.model.Zones.nameEqualTo;
import static denominator.verisignmdns.VerisignMdns.ZoneInfo;

final class VerisignMdnsZoneApi implements denominator.ZoneApi {
    private static final int DEFAULT_PAGE_SIZE = 500;

    @Override
    public Iterator<Zone> iterateByName(String name) {
        checkNotNull(name, "name was null");
        ZoneInfo zoneInfo = zoneInfo(name);
        ArrayList<Zone> temp = new ArrayList<Zone>();
        temp.add(Zone.create(zoneInfo.name, zoneInfo.name, zoneInfo.ttl, zoneInfo.email));
        return temp.iterator();
    }

    @Override
    public String put(Zone zone) {
        api.createZone(zone.name());
        ZoneInfo zoneInfo = api.getZoneInfo(zone.name());
        zoneInfo.email = zone.email();
        zoneInfo.ttl = zone.ttl();
        updateSOA(zoneInfo);
        return zone.name();
    }

    @Override
    public void delete(String id) {
        api.deleteZone(id);
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

    ZoneInfo zoneInfo(String zoneName) {
        return api.getZoneInfo(zoneName);
    }

    void updateSOA(ZoneInfo zoneInfo){
        api.updateZoneSOA(zoneInfo.name, zoneInfo.email, zoneInfo.retry, zoneInfo.ttl, zoneInfo.refresh, zoneInfo.expire);
    }
}
