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
     * @see denominator.verisignmdns.VerisignMdnsErrorDecoder for MDNS response error messages and
     *      error codes
     */
    public int code() {
        return code;
    }

}
