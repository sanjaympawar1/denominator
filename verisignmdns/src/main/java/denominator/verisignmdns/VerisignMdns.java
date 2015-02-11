package denominator.verisignmdns;

import java.util.List;
import java.util.Map;

import denominator.model.Zone;

import javax.inject.Named;

import sun.misc.Compare;
import feign.Body;
import feign.RequestLine;

interface VerisignMdns {

    @RequestLine("POST")
    List<Zone> getZonesForUser(@Named("zoneListPage") int page, @Named("pageSize") int pageSize);

    @RequestLine("POST")
    @Body("<urn2:getResourceRecordList>"
            + "<urn2:domainName>{zonename}</urn2:domainName>"
            + "<urn2:listPagingInfo>"
                + "<urn2:pageNumber>{page}</urn2:pageNumber>"
                + "<urn2:pageSize>{pageSize}</urn2:pageSize>"
            + "</urn2:listPagingInfo>"
         + "</urn2:getResourceRecordList>")
    List<Record> getResourceRecordsList(@Named("zonename") String zonename, @Named("page") int page,
            @Named("pageSize") int pageSize);

    @RequestLine("POST")
    @Body("<ns3:getResourceRecordList xmlns='urn:com:verisign:dnsa:auth:schema:1' xmlns:ns2='urn:com:verisign:dnsa:messaging:schema:1' xmlns:ns3='urn:com:verisign:dnsa:api:schema:1'>"
            + "<ns3:domainName>{zonename}</ns3:domainName>"
            + "<ns3:resourceRecordType>{type}</ns3:resourceRecordType>"
            + "<ns3:owner>{name}</ns3:owner>"
            + "<ns3:listPagingInfo>"
                + "<ns3:pageNumber>{page}</ns3:pageNumber>"
                + "<ns3:pageSize>{pageSize}</ns3:pageSize>"
            + "</ns3:listPagingInfo>"
           + "</ns3:getResourceRecordList>")
    List<Record> getResourceRecordsListForNameAndType(@Named("zonename") String zonename, @Named("name") String name, @Named("type") String type,
             @Named("page") int page, @Named("pageSize") int pageSize);

    @RequestLine("POST")
    @Body("<urn2:getResourceRecord>"
            + "<urn2:resourceRecordId>{id}</urn2:resourceRecordId>"
          + "</urn2:getResourceRecord>")
    List<Record> getResourceRecordByQualifier(@Named("id") String id);

    @RequestLine("POST")
    void createResourceRecords(@Named("zonename") String zonename,
            @Named("type") String type, @Named("name") String name,
            @Named("ttl") String ttl, @Named("rdataList") List<String> rdata);

    @RequestLine("POST")
    void deleteRecourceRecords(@Named("zonename") String zonename, @Named("recordIdList") List<String> recordIdList);

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

