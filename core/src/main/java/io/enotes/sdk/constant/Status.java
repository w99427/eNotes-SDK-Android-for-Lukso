package io.enotes.sdk.constant;

public class Status {
    /**
     * callback is success
     */
    public static final int SUCCESS = 0;
    /**
     * callback is error, you can get error code by status.getCode()
     */
    public static final int ERROR = 1;
    /**
     * nfc is connected , will begin parse card
     */
    public static final int NFC_CONNECTED = 2;

    /**
     * card parse finish
     */
    public static final int CARD_PARSE_FINISH = 7;

}
