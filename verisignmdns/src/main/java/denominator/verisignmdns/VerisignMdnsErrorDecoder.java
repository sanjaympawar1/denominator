package denominator.verisignmdns;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;

import javax.inject.Inject;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.sax.SAXDecoder.ContentHandlerWithResult;
import static feign.Util.UTF_8;
import static denominator.common.Util.slurp;

final class VerisignMdnsErrorDecoder implements ErrorDecoder {
    static final Map<String, Integer> MDNS_ERROR_TO_INT_CODE_MAP = new LinkedHashMap<String, Integer>();
    static {
        MDNS_ERROR_TO_INT_CODE_MAP.put("ERROR_MISSING_INVALID_INPUT", new Integer(1));
        MDNS_ERROR_TO_INT_CODE_MAP.put("ERROR_RULE_VALIDATION", new Integer(2));
        MDNS_ERROR_TO_INT_CODE_MAP.put("ERROR_OPERATION_FAILURE", new Integer(3));
        MDNS_ERROR_TO_INT_CODE_MAP.put("ERROR_INTERNAL_ERROR", new Integer(4));
    }

    private final Decoder decoder;

    @Inject
    VerisignMdnsErrorDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            // in case of error parsing, we can access the original contents.
            response = bufferResponse(response);
            VerisignMdnsError error = VerisignMdnsError.class.cast(decoder.decode(response, VerisignMdnsError.class));
            if (error == null)
                return FeignException.errorStatus(methodKey, response);
            String message = format("%s failed", methodKey);
            if (error.code != -1)
                message = format("%s with error %s", message, error.code);
            if (error.description != null)
                message = format("%s: %s", message, error.description);

            return new VerisignMdnsException(message, error.code);
        } catch (IOException ignored) {
            return FeignException.errorStatus(methodKey, response);
        } catch (Exception propagate) {
            return propagate;
        }
    }

    static class VerisignMdnsError extends DefaultHandler implements ContentHandlerWithResult<VerisignMdnsError> {
        @Inject
        VerisignMdnsError() {
        }

        private StringBuilder currentText = new StringBuilder();
        private int code = -1;
        private String description;

        @Override
        public VerisignMdnsError result() {
            return (code == -1 && description == null) ? null : this;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.endsWith(":reason")) {
                try {
                    Integer tempCode = MDNS_ERROR_TO_INT_CODE_MAP.get(attributes.getValue("code"));
                    code = tempCode.intValue();
                } catch (Exception ex) {
                    // ignore
                }
                description = attributes.getValue("description");
            }
            currentText = new StringBuilder();
        }

        @Override
        public void characters(char ch[], int start, int length) {
            currentText.append(ch, start, length);
        }
    }

    static Response bufferResponse(Response response) throws IOException {
        if (response.body() == null)
            return response;
        String body = slurp(response.body().asReader());
        return Response.create(response.status(), response.reason(), response.headers(), body, UTF_8);
    }

}

