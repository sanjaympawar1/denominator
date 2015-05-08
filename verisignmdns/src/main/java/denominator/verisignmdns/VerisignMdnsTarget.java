package denominator.verisignmdns;

import static feign.Util.UTF_8;
import static java.lang.String.format;

import java.net.URI;

import javax.inject.Inject;

import denominator.Credentials;
import denominator.Provider;
import feign.Request;
import feign.RequestTemplate;
import feign.Target;

final class VerisignMdnsTarget implements Target<VerisignMdns> {
    private final Provider provider;
    private final javax.inject.Provider<Credentials> credentials;

    @Inject
    VerisignMdnsTarget(Provider provider, javax.inject.Provider<Credentials> credentials) {
        this.provider = provider;
        this.credentials = credentials;
    }

    @Override
    public Class<VerisignMdns> type() {
        return VerisignMdns.class;
    }

    @Override
    public String name() {
        return provider.name();
    }


    @Override
    public String url() {
        return provider.url();
    }

    @Override
    public Request apply(RequestTemplate input) {
        input.insert(0, url());
        input.body(format(SOAP_TEMPLATE, VerisignMdnsUtils.getUsername(credentials),
                VerisignMdnsUtils.getPassword(credentials), new String(input.body(), UTF_8)));
        input.header("Host", URI.create(input.url()).getHost());
        input.header("Content-Type", "application/soap+xml");
        return input.request();
    }

    static final String SOAP_TEMPLATE = "" //
            + "<?xml version='1.0' encoding='UTF-8'?>"
            + "<S:Envelope xmlns:S='http://www.w3.org/2003/05/soap-envelope'"
                                    + " xmlns:urn='urn:com:verisign:dnsa:messaging:schema:1' "
                                    + "xmlns:urn1='urn:com:verisign:dnsa:auth:schema:1' "
                                    + "xmlns:urn2='urn:com:verisign:dnsa:api:schema:1'>"
                + "<S:Header>" 
                    + "<urn1:authInfo>" 
                        + "<urn1:userToken>"
                            + "<urn1:userName>%s</urn1:userName>"
                            + "<urn1:password>%s</urn1:password>"
                        + "</urn1:userToken>"
                    + "</urn1:authInfo>"
                + "</S:Header>"
                + "<S:Body>" + "%s" + "</S:Body>" 
            + "</S:Envelope>";
}