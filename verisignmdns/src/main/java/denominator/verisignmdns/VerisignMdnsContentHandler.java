package denominator.verisignmdns;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import denominator.model.Zone;
import denominator.verisignmdns.VerisignMdns.Record;
import feign.sax.SAXDecoder.ContentHandlerWithResult;
import denominator.verisignmdns.VerisignMdns.ZoneInfo;

final class VerisignMdnsContentHandler {

    static class ZoneListHandler extends DefaultHandler implements ContentHandlerWithResult<List<Zone>> {
        @Inject
        ZoneListHandler() {}

        private final List<Zone> zones = new ArrayList<Zone>();
        private boolean domainElementFound = false;

        @Override
        public List<Zone> result() {
            return zones;
        }

        @Override
        public void startElement(String uri, String name, String qName, Attributes attrs) {
            if (qName != null && qName.endsWith("domainName")) {
                domainElementFound = true;
            }
        }

        @Override
        public void characters(char ch[], int start, int length) throws SAXException {
            if (domainElementFound) {
                zones.add(Zone.create(new String(ch, start, length)));
            }
        }

        @Override
        public void endElement(String uri, String name, String qName) throws SAXException {
            if (qName != null && qName.endsWith("domainName")) {
                domainElementFound = false;
            }
        }
    }

    static class RecordListHandler extends DefaultHandler implements ContentHandlerWithResult<List<Record>> {
        private final List<Record> rrs = new ArrayList<Record>();

        @Inject
        RecordListHandler() {}

        private Record rr;
        private boolean inResourceRecordSet = false;
        private StringBuilder currentText;

        @Override
        public List<Record> result() {
            return rrs;
        }

        @Override
        public void startElement(String uri, String name, String qName, Attributes attrs) {
            if (qName.endsWith("resourceRecord")) {
                rr = new Record();
                inResourceRecordSet = true;
                currentText = new StringBuilder();
            }
        }

        @Override
        public void endElement(String uri, String name, String qName) {
            if (qName.endsWith("resourceRecord")) {
                rrs.add(rr);
                inResourceRecordSet = false;
                currentText = null;
            }
            if (inResourceRecordSet && qName.endsWith("resourceRecordId")) {
                rr.id = currentText.toString().trim();
            }
            if (inResourceRecordSet && qName.endsWith("owner")) {
                rr.name = currentText.toString().trim();
            }
            if (inResourceRecordSet && qName.endsWith("type")) {
                rr.type = currentText.toString().trim();
            }
            if (inResourceRecordSet && qName.endsWith("ttl")) {
                rr.ttl = Integer.parseInt(currentText.toString());
            }
            if (inResourceRecordSet && qName.endsWith("rData")) {
                rr.rdata = currentText.toString();
            }
            currentText = new StringBuilder();
        }

        /**
         * This method is to ensure all space characters are accounted for while processing rData
         */
        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            String tempStr = new String(ch, start, length);
            currentText.append(tempStr);
        }

        @Override
        public void characters(char ch[], int start, int length) throws SAXException {
            if (inResourceRecordSet && length > 0) {
                currentText.append(new String(ch, start, length));
            }
        }
    }

    static class ZoneInfoHandler extends DefaultHandler implements ContentHandlerWithResult<ZoneInfo> {
        private ZoneInfo zoneInfo = new ZoneInfo();

        @Inject
        ZoneInfoHandler() {}

        private boolean inZoneSOAInfo = false;
        private boolean inDomainName = false;
        private StringBuilder currentElementText;

        @Override
        public ZoneInfo result() {
            return zoneInfo;
        }

        @Override
        public void startElement(String uri, String name, String qName, Attributes attrs) {

            if (qName.endsWith("zoneSOAInfo")) {
                inZoneSOAInfo = true;
                inDomainName = false;
                currentElementText = new StringBuilder();
            }
            if (qName.endsWith("domainName")) {
                inDomainName = true;
                inZoneSOAInfo = false;
                currentElementText = new StringBuilder();
            }
        }

        @Override
        public void endElement(String uri, String name, String qName) {

            if (inDomainName && qName.endsWith("domainName")) {
                zoneInfo.name = currentElementText.toString().trim();
                inDomainName = false;
                currentElementText = null;
            }

            if (inZoneSOAInfo && qName.endsWith("zoneSOAInfo")) {
                inZoneSOAInfo = false;
                currentElementText = null;
            }

            if (inZoneSOAInfo && qName.endsWith("email")) {
                zoneInfo.email = currentElementText.toString().trim();
            }

            if (inZoneSOAInfo && qName.endsWith("retry")) {
                zoneInfo.retry = Integer.parseInt(currentElementText.toString().trim());
            }

            if (inZoneSOAInfo && qName.endsWith("refresh")) {
                zoneInfo.refresh = Integer.parseInt(currentElementText.toString().trim());
            }

            if (inZoneSOAInfo && qName.endsWith("ttl")) {
                zoneInfo.ttl = Integer.parseInt(currentElementText.toString());
            }

            if (inZoneSOAInfo && qName.endsWith("expire")) {
                zoneInfo.expire = Integer.parseInt(currentElementText.toString().trim());
            }

            if (inZoneSOAInfo && qName.endsWith("serial")) {
                zoneInfo.serial = Long.parseLong(currentElementText.toString().trim());
            }

            currentElementText = new StringBuilder();
        }

        /**
         * This method is to ensure all space characters are accounted for while processing rData
         */
        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            String tempStr = new String(ch, start, length);
            currentElementText.append(tempStr);
        }

        @Override
        public void characters(char ch[], int start, int length) throws SAXException {
            if ((inZoneSOAInfo || inDomainName) && length > 0) {
                currentElementText.append(new String(ch, start, length));
            }
        }
    }
}
