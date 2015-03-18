package denominator.verisignmdns;

import denominator.BasicProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Provides;
import denominator.AllProfileResourceRecordSetApi;
import denominator.CheckConnection;
import denominator.DNSApiManager;
import denominator.ResourceRecordSetApi;
import denominator.ZoneApi;
import denominator.config.GeoUnsupported;
import denominator.config.NothingToClose;
import denominator.config.WeightedUnsupported;
import denominator.verisignmdns.VerisignMdnsContentHandler.RecordListHandler;
import denominator.verisignmdns.VerisignMdnsContentHandler.ZoneListHandler;
import denominator.verisignmdns.VerisignMdnsErrorDecoder.VerisignMdnsError;
import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.sax.SAXDecoder;

public class VerisignMdnsProvider extends BasicProvider {
    private final String url;

    public VerisignMdnsProvider() {
        this(null);
    }

    public VerisignMdnsProvider(String url) {
        this.url = url == null || url.isEmpty() ? "https://api.dns-tool.com/dnsa-ws/V2.0/dnsaapi" : url;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public Set<String> basicRecordTypes() {
        Set<String> types = new LinkedHashSet<String>();
        types.addAll(Arrays.asList("A", "AAAA", "CNAME", "MX", "NS", "NAPTR", "PTR", "SRV", "TXT"));
        return types;
    }

    @Override
    public Map<String, Collection<String>> credentialTypeToParameterNames() {
        Map<String, Collection<String>> options = new LinkedHashMap<String, Collection<String>>();
        options.put("password", Arrays.asList("username", "password"));
        return options;
    }

    @Override
    public Map<String, Collection<String>> profileToRecordTypes() {
        Map<String, Collection<String>> profileToRecordTypes = new LinkedHashMap<String, Collection<String>>();
        return profileToRecordTypes;
    }

    @dagger.Module(injects = DNSApiManager.class, complete = false, includes = {NothingToClose.class, GeoUnsupported.class,
            WeightedUnsupported.class, FeignModule.class})
    public static final class Module {

        @Provides
        CheckConnection alwaysOK() {
            return new CheckConnection() {
                public boolean ok() {
                    return true;
                }
            };
        }

        @Provides
        @Singleton
        ZoneApi provideZoneApi(VerisignMdnsZoneApi in) {
            return in;
        }

        @Provides
        ResourceRecordSetApi.Factory provideResourceRecordSetApiFactory(VerisignMdnsResourceRecordSetApi.Factory in) {
            return in;
        }

        @Provides
        AllProfileResourceRecordSetApi.Factory provideAllProfileResourceRecordSetApiFactory(
                VerisignMdnsAllProfileResourceRecordSetApi.Factory in) {
            return in;
        }
    }

    @dagger.Module(
            injects = {VerisignMdnsResourceRecordSetApi.Factory.class}, 
            complete = false, overrides = true,
            includes = {XMLCodec.class})
    final static class FeignModule {

        @Singleton
        @Provides
        VerisignMdns verisignMdns(Feign feign, VerisignMdnsTarget target) {
            return feign.newInstance(target);
        }
        @Provides
        Logger logger() {
          return new Logger.NoOpLogger();
        }

        @Provides
        Logger.Level logLevel() {
          return Logger.Level.NONE;
        }

        @Provides
        @Singleton
        Feign feign(Logger logger, Logger.Level logLevel) {
          Decoder decoder = SAXDecoder.builder()//
                  .registerContentHandler(ZoneListHandler.class)//
                  .registerContentHandler(RecordListHandler.class)//
                  .registerContentHandler(VerisignMdnsError.class).build();

          return Feign.builder()
              .logger(logger)
              .logLevel(logLevel)
              .encoder( new VerisignMdnsFormEncoder())
              .decoder(decoder)
              .errorDecoder(new VerisignMdnsErrorDecoder(decoder))
              .build();
        }
    }

    @dagger.Module(//
            injects = {Encoder.class, Decoder.class, ErrorDecoder.class},//
            overrides = true, // ErrorDecoder
            complete = false
    )
    static final class XMLCodec {
        @Provides
        Encoder formEncoder() {
            return new VerisignMdnsFormEncoder();
        }

        @Provides
        Decoder saxDecoder() {
            return SAXDecoder.builder()//
                    .registerContentHandler(ZoneListHandler.class)//
                    .registerContentHandler(RecordListHandler.class)//
                    .registerContentHandler(VerisignMdnsError.class).build();
        }

        @Provides
        ErrorDecoder errorDecoders(VerisignMdnsErrorDecoder errorDecoder) {
            return errorDecoder;
        }
    }
}
