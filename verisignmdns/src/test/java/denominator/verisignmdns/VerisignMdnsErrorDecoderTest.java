package denominator.verisignmdns;

import static denominator.verisignmdns.VerisignMDNSTest.METHODKEY;
import static denominator.verisignmdns.VerisignMDNSTest.mockResponse;
import static denominator.verisignmdns.VerisignMDNSTest.rrListInvalidZoneResponse;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import feign.Response;
import feign.codec.ErrorDecoder;

public class VerisignMdnsErrorDecoderTest {

    @Test
    public void decode() {
        Response response = mockResponse(rrListInvalidZoneResponse);
        ErrorDecoder errorDecoder = VerisignMDNSTest.mockErrorDecoder();
        Exception actualResult = errorDecoder.decode(METHODKEY, response);
        assertNotNull(actualResult);
    }
}

