package ve.com.abicelis.pingwidget.util;

import java.util.regex.Pattern;

/**
 * Created by abice on 11/2/2017.
 */

public class AddressValidator {

    private Pattern mDomainPattern;
    private Pattern mIpv4Pattern;
    private Pattern mIpv6Pattern;

    private static final String DOMAIN_NAME_PATTERN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";

    private static final String IPV4_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static final String IPV6_PATTERN = "([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)";


    public AddressValidator() {
        mDomainPattern = Pattern.compile(DOMAIN_NAME_PATTERN);
        mIpv4Pattern = Pattern.compile(IPV4_PATTERN);
        mIpv6Pattern = Pattern.compile(IPV6_PATTERN);
    }

    public boolean validate(String address) {
        if(mDomainPattern.matcher((address)).matches())
            return true;
        if(mIpv4Pattern.matcher((address)).matches())
            return true;
        if(mIpv6Pattern.matcher((address)).matches())
            return true;
        return false;
    }

}
