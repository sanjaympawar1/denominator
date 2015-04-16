package denominator.verisignmdns;

import java.util.List;



import denominator.model.Zone;

import javax.inject.Named;

import feign.Body;
import feign.Param;
import feign.RequestLine;

interface VerisignMdns {

    @RequestLine("POST")
    List<Zone> getZones(@Param("zoneListPage") int page, @Param("pageSize") int pageSize);

    @RequestLine("POST")
    @Body("<urn2:createZone>"
                + "<urn2:domainName>{zoneName}</urn2:domainName>"
                + "<urn2:type>DNS Hosting</urn2:type>"
                + "<urn2:serviceLevel>COMPLETE</urn2:serviceLevel>"
            + "</urn2:createZone>")
    void createZone(@Param("zoneName") String zoneName);

    @RequestLine("POST")
    @Body("<ns3:deleteZone xmlns=\"urn:com:verisign:dnsa:messaging:schema:1\" "
                        + "xmlns:ns2=\"urn:com:verisign:dnsa:auth:schema:1\" "
                        + "xmlns:ns3=\"urn:com:verisign:dnsa:api:schema:1\">"
                + "<ns3:domainName>{zoneName}</ns3:domainName>"
            + "</ns3:deleteZone>")
    void deleteZone(@Param("zoneName") String zoneName);

    @RequestLine("POST")
    @Body("<urn2:getZoneInfo>"
                + "<urn2:domainName>{zoneName}</urn2:domainName>"
           + "</urn2:getZoneInfo>")
    ZoneInfo getZoneInfo(@Param("zoneName") String zoneName);

    @RequestLine("POST")
    @Body("<urn2:updateSOA>"
            + "<urn2:domainName>{zoneName}</urn2:domainName>"
            + "<urn2:zoneSOAInfo>"
            + "<urn2:email>{email}</urn2:email>"
            + "<urn2:retry>{retry}</urn2:retry>"
            + "<urn2:ttl>{ttl}</urn2:ttl>"
            + "<urn2:refresh>{refresh}</urn2:refresh>"
            + "<urn2:expire>{expire}</urn2:expire>"
            + "</urn2:zoneSOAInfo>"
          + "</urn2:updateSOA>")
    void updateZoneSOA(@Param("zoneName") String zoneName, @Param("email") String email, @Param("retry") int retry, @Param("ttl") int ttl, @Param("refresh") int refresh, @Param("expire") int expire);

    @RequestLine("POST")
    @Body("<urn2:getResourceRecordList>"
            + "<urn2:domainName>{zoneName}</urn2:domainName>"
            + "<urn2:listPagingInfo>"
                + "<urn2:pageNumber>{page}</urn2:pageNumber>"
                + "<urn2:pageSize>{pageSize}</urn2:pageSize>"
            + "</urn2:listPagingInfo>"
         + "</urn2:getResourceRecordList>")
    List<Record> getResourceRecords(@Param("zoneName") String zoneName, @Param("page") int page,
            @Param("pageSize") int pageSize);


    @RequestLine("POST")
    @Body("<ns3:getResourceRecordList xmlns='urn:com:verisign:dnsa:auth:schema:1' "
                            + "xmlns:ns2='urn:com:verisign:dnsa:messaging:schema:1' "
                            + "xmlns:ns3='urn:com:verisign:dnsa:api:schema:1'>"
            + "<ns3:domainName>{zoneName}</ns3:domainName>"
            + "<ns3:resourceRecordType>{type}</ns3:resourceRecordType>"
            + "<ns3:owner>{name}</ns3:owner>"
            + "<ns3:listPagingInfo>"
                + "<ns3:pageNumber>{page}</ns3:pageNumber>"
                + "<ns3:pageSize>{pageSize}</ns3:pageSize>"
            + "</ns3:listPagingInfo>"
           + "</ns3:getResourceRecordList>")
    List<Record> getResourceRecords(@Param("zoneName") String zoneName, @Param("name") String name, @Param("type") String type,
             @Param("page") int page, @Param("pageSize") int pageSize);

    @RequestLine("POST")
    @Body("<urn2:getResourceRecord>"
            + "<urn2:resourceRecordId>{id}</urn2:resourceRecordId>"
          + "</urn2:getResourceRecord>")
    List<Record> getResourceRecordByQualifier(@Param("id") String id);

    @RequestLine("POST")
    void createResourceRecords(@Param("zoneName") String zoneName,
            @Param("type") String type, @Param("name") String name,
            @Param("ttl") int ttl, @Param("rdataList") List<String> rdata);

    @RequestLine("POST")
    void deleteRecourceRecords(@Param("zoneName") String zoneName, @Param("recordIdList") List<String> recordIdList);

    @RequestLine("POST")
    @Body("<urn2:updateResourceRecord>"
            + "<urn2:domainName>{zoneName}</urn2:domainName>" 
            + "<urn2:resourceRecord allowanyIP=\"false\">"
                + "<urn2:resourceRecordId>{id}</urn2:resourceRecordId>"
                + "<urn2:owner>{name}</urn2:owner>"
                + "<urn2:type>{type}</urn2:type>"
                + "<urn2:ttl>{ttl}</urn2:ttl>"
                + "<urn2:rData>{rdata}</urn2:rData>"
            + "</urn2:resourceRecord>"
         + "</urn2:updateResourceRecord>")
    void updateResourceRecord(@Param("zoneName") String zoneName, @Param("id") String id, @Param("name") String name,
            @Param("type") String type, @Param("ttl") int ttl, @Param("rdata") String rdata);

    static class Record implements Comparable<Record>{
        String id;
        String name;
        String type;
        int ttl;
        String rdata;

       /**
        * MDNS SOAP response does not support ResourceRecordSet concept.
        * GetResourceRecord web-service command orders ResourceRecords only by 'owner'(name)
        * To create ResourceRecordSet we need to sort by owner+type
        */
        @Override
        public int compareTo(Record that) {
            int result = this.name.compareTo(that.name);
            if (result == 0) {
                result = this.type.compareTo(that.type);
            }
            return result;
        }
    }

    static class ZoneInfo {
        String name;
        String email;
        int ttl;
        int retry;
        int refresh;
        int expire;
        long serial;
    }
}

