package denominator.verisignmdns;

import java.util.List;


import denominator.model.Zone;

import javax.inject.Named;

import feign.Body;
import feign.RequestLine;

interface VerisignMdns {

    @RequestLine("POST")
    List<Zone> getZones(@Named("zoneListPage") int page, @Named("pageSize") int pageSize);

    @RequestLine("POST")
    @Body("<urn2:getResourceRecordList>"
            + "<urn2:domainName>{zoneName}</urn2:domainName>"
            + "<urn2:listPagingInfo>"
                + "<urn2:pageNumber>{page}</urn2:pageNumber>"
                + "<urn2:pageSize>{pageSize}</urn2:pageSize>"
            + "</urn2:listPagingInfo>"
         + "</urn2:getResourceRecordList>")
    List<Record> getResourceRecords(@Named("zoneName") String zoneName, @Named("page") int page,
            @Named("pageSize") int pageSize);

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
    List<Record> getResourceRecords(@Named("zoneName") String zoneName, @Named("name") String name, @Named("type") String type,
             @Named("page") int page, @Named("pageSize") int pageSize);

    @RequestLine("POST")
    @Body("<urn2:getResourceRecord>"
            + "<urn2:resourceRecordId>{id}</urn2:resourceRecordId>"
          + "</urn2:getResourceRecord>")
    List<Record> getResourceRecordByQualifier(@Named("id") String id);

    @RequestLine("POST")
    void createResourceRecords(@Named("zoneName") String zoneName,
            @Named("type") String type, @Named("name") String name,
            @Named("ttl") int ttl, @Named("rdataList") List<String> rdata);

    @RequestLine("POST")
    void deleteRecourceRecords(@Named("zoneName") String zoneName, @Named("recordIdList") List<String> recordIdList);

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
    void updateResourceRecord(@Named("zoneName") String zoneName, @Named("id") String id, @Named("name") String name,
            @Named("type") String type, @Named("ttl") int ttl, @Named("rdata") String rdata);

    static class Record implements Comparable<Record>{
        String id;
        String name;
        String type;
        int ttl;
        String rdata;

       /**
        * MDNS does not support ResourceRecordSet concept.
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
}

