package denominator.verisignmdns;

import denominator.DNSApiManagerFactory;
import denominator.TestGraph;

import static feign.Util.emptyToNull;
import static java.lang.System.getProperty;

public class VerisignMdnsTestGraph extends TestGraph {

    private static final String url = emptyToNull(getProperty("verisignmdns.url"));
    private static final String zone = emptyToNull(getProperty("verisignmdns.zone"));

    public VerisignMdnsTestGraph() {
        super(DNSApiManagerFactory.create(new VerisignMdnsProvider(url)), zone);
    }
}
