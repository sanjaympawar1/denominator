package denominator.verisignmdns;

import static java.lang.String.format;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;

final class VerisignMdnsFormEncoder implements Encoder {
    private static final String RR_ELEMENT =
           "<urn2:resourceRecord allowanyIP='false'>"
                    + "<urn2:owner>%s</urn2:owner>"
                    + "<urn2:type>%s</urn2:type>"
                    + "<urn2:ttl>%s</urn2:ttl>"
                    + "<urn2:rData>%s</urn2:rData>" 
           + "</urn2:resourceRecord>";

    private static final String GET_ZONE_LIST_ELEMENT =
            "<ns3:getZoneList xmlns='urn:com:verisign:dnsa:messaging:schema:1' "
                                     + "xmlns:ns2='urn:com:verisign:dnsa:auth:schema:1' "
                                     + "xmlns:ns3='urn:com:verisign:dnsa:api:schema:1'>"
                    + "<ns3:listPagingInfo>"
                        + "<ns3:pageNumber>%s</ns3:pageNumber>"
                        + "<ns3:pageSize>%s</ns3:pageSize>"
                    + "</ns3:listPagingInfo>"
             + "</ns3:getZoneList>";

    @SuppressWarnings("unchecked")
    public void encode(Object object, RequestTemplate template) throws EncodeException {
        Map<String, ?> formParams = Map.class.cast(object);
        if (formParams.containsKey("recordIdList")) {
            template.body(encodeDeleteRecords((String) formParams.get("zoneName"),
                    (List<String>) formParams.get("recordIdList")));
        } else if (formParams.containsKey("rdataList")) {
            template.body(encodeCreateRecords(formParams));
        } else if (formParams.containsKey("zoneListPage")) {
            template.body(encodeZoneListRequest(formParams));
        }
    }

    static String encodeDeleteRecords(String zoneName, Collection<String> recordIdList) {
        StringBuilder sb = new StringBuilder("<urn2:deleteResourceRecords>");
        sb.append(format("<urn2:domainName>%s</urn2:domainName>", zoneName));
        for (String recordId : recordIdList) {
            sb.append(format("<urn2:resourceRecordId>%s</urn2:resourceRecordId>", recordId));
        }
        sb.append("</urn2:deleteResourceRecords>");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    static String encodeCreateRecords(Map<String, ?> formParams) {
        StringBuilder sb = new StringBuilder("<urn2:createResourceRecords>");
        sb.append(format("<urn2:domainName>%s</urn2:domainName>", (String) formParams.get("zoneName")));
        String type = (String) formParams.get("type");
        String name = (String) formParams.get("name");
        Integer ttl = (Integer) formParams.get("ttl");
        List<String> rdataList = (List<String>) formParams.get("rdataList");
        for (String rdata : rdataList) {
            sb.append(format(RR_ELEMENT, name, type, ttl, rdata));
        }
        sb.append("</urn2:createResourceRecords>");
        return sb.toString();
    }

    static String encodeZoneListRequest(Map<String, ?> formParams) {
        return format(GET_ZONE_LIST_ELEMENT, formParams.get("zoneListPage"), formParams.get("pageSize"));
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        encode(object, template);
    }
}
