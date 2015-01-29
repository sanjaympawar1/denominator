package denominator.verisignmdns;

import feign.FeignException;

final class VerisignMdnsException extends FeignException {
    private static final long serialVersionUID = 1L;

    private final int code;

    VerisignMdnsException(String message, int code) {
        super(message);
        this.code = code;
    }

    /**
     * Error code values -1 == > Method Not Implemented, this is internal error
     * of Verisign MDNS Provider
     * 
     * @see denominator.verisignmdns.VrsnMDNSErrorDecoder for MDNS response
     * error messages and error codes
     */
    public int code() {
        return code;
    }

}

