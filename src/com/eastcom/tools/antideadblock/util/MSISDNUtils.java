public final class MsisdnUtils {
  public static final String MSISDN = "^(\\+?63?|0)[89]([0-9]{9})$";
  public static final Integer MIN_LENGTH_SHORT = 10;
  private MsisdnUtils() {
      // hiding constructor
  }
  public static String convertMsisdnWithPlus(String msisdn) {
      if(isValidMsisdn(msisdn)) {
          return convertMsisdn(msisdn, "+63");
      }
      return null;
  }
  public static String convertMsisdn(String msisdn, String prefix) {
      if(prefix == null) {
          throw new IllegalArgumentException("Msisdn Prefix is invalid.");
      }
      if(isValidMsisdn(msisdn)) {
          return prefix + msisdn.substring(msisdn.length() - MIN_LENGTH_SHORT);
      }
      return null;
  }
  public static boolean isValidMsisdn(String msisdn) {
      if(StringUtils.isBlank(msisdn)){
          return false;
      }
      return Pattern.compile(MSISDN).matcher(msisdn).matches();
  }
}

MsisdnUtils Notes
* MSISDN = Mobile Number
* Valid Mobile Number formats samples: +639191234567, 639191234567, 09191234567
* Convert methods returns the converted mobile number or null if mobile number is not valid
* Validation methods return true if mobile number is valid, false otherwise.
