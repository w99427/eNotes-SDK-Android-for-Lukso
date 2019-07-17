package io.enotes.sdk.repository.card;

import android.nfc.Tag;


/**
 * A wrapper class for a NFC tag <b>or</b> some BLE devices.
 * Notice: this class do not support wrap NFC tag and BLE devices at the same time.
 */
public class Reader {
    public static String DEFAULT_TARGET_AID = "654e6f7465734170706c6574";
    private Tag tag;

    public Tag getTag() {
        return tag;
    }

    /**
     * Set the wrapped tag and clean the ble devices if any.
     *
     * @param tag
     * @return
     */
    public Reader setTag(Tag tag) {
        this.tag = tag;
        return this;
    }



}
