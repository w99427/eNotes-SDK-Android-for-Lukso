package io.enotes.sdk.utils;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.support.annotation.NonNull;

public class ReaderUtils {

    /**
     * Check whether the device supports nfc or not.
     *
     * @param context
     * @return
     */
    public static boolean supportNfc(@NonNull Context context) {
        return NfcAdapter.getDefaultAdapter(context) != null;
    }
}
