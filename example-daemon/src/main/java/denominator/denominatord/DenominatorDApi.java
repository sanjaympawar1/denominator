package denominator.denominatord;

import denominator.model.ResourceRecordSet;
import denominator.model.Zone;
import feign.Headers;
import feign.RequestLine;
import feign.Response;

import javax.inject.Named;
import java.util.List;

/**
 * Defines the interface of {@link DenominatorD}, where all responses are in json.
 * <p/>
 * All responses throw a 400 if there is a problem with the request data.
 * <p/>
 * 404 would be unexpected as that implies a malformed request.
 */
public interface DenominatorDApi {

  @RequestLine("GET /healthcheck") Response healthcheck();

  @RequestLine("GET /zones") List<Zone> zones();

  @RequestLine("GET /zones/{zoneIdOrName}/recordsets")
  List<ResourceRecordSet<?>> recordSets(@Named("zoneIdOrName") String zoneIdOrName);

  @RequestLine("GET /zones/{zoneIdOrName}/recordsets?name={name}")
  List<ResourceRecordSet<?>> recordSetsByName(@Named("zoneIdOrName") String zoneIdOrName, @Named("name") String name);

  @RequestLine("GET /zones/{zoneIdOrName}/recordsets?name={name}&type={type}")
  List<ResourceRecordSet<?>> recordSetsByNameAndType(@Named("zoneIdOrName") String zoneIdOrName, @Named("name") String name,
                                                     @Named("type") String type);

  @RequestLine("GET /zones/{zoneIdOrName}/recordsets?name={name}&type={type}&qualifier={qualifier}")
  List<ResourceRecordSet<?>> recordsetsByNameAndTypeAndQualifier(@Named("zoneIdOrName") String zoneIdOrName, @Named("name") String name,
                                                                 @Named("type") String type, @Named("qualifier") String qualifier);

  @RequestLine("PUT /zones/{zoneIdOrName}/recordsets?name={name}")
  @Headers("Content-Type: application/json")
  void putRecordSet(@Named("zoneIdOrName") String zoneIdOrName, ResourceRecordSet<?> update);

  @RequestLine("DELETE /zones/{zoneIdOrName}/recordsets?name={name}&type={type}")
  void deleteRecordSetByNameAndType(@Named("zoneIdOrName") String zoneIdOrName, @Named("name") String name,
                                    @Named("type") String type);

  @RequestLine("DELETE /zones/{zoneIdOrName}/recordsets?name={name}&type={type}&qualifier={qualifier}")
  void deleteRecordSetByNameTypeAndQualifier(@Named("zoneIdOrName") String zoneIdOrName, @Named("name") String name,
                                             @Named("type") String type, @Named("qualifier") String qualifier);

}
