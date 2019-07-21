package io.enotes.sdk.repository.provider;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ethereum.util.ByteUtil;

import io.enotes.sdk.repository.base.Resource;
import io.enotes.sdk.repository.card.CardScannerReader;
import io.enotes.sdk.repository.card.Command;
import io.enotes.sdk.repository.card.CommandException;
import io.enotes.sdk.repository.card.Commands;
import io.enotes.sdk.repository.card.ICardReader;
import io.enotes.sdk.repository.card.ICardScanner;
import io.enotes.sdk.repository.card.TLVBox;
import io.enotes.sdk.repository.db.entity.Card;


public class CardProvider {
    private CardScannerReader mCardScannerReader;

    public CardProvider(Context context) {
        mCardScannerReader = new CardScannerReader.Builder(context).build();
    }


    /**
     * Notice：It will remove and destroy old scanners and readers if they are not in the new scanMode.
     * New scanners and readers may be added if there was no related scanner and reader before.
     *
     * @param scanMode
     * @param readMode
     */
    public void setScanReadMode(@ICardScanner.ScanMode int scanMode, @ICardReader.ReadMode int readMode) {
        mCardScannerReader.setScanReadMode(scanMode, readMode);
    }


    /**
     * For NFC Mode: Limit the NFC controller to reader mode while this Activity is in the foreground.
     *
     * @param activity
     * Use {@link NfcAdapter#enableReaderMode(Activity, NfcAdapter.ReaderCallback, int, Bundle)}
     */
    @MainThread
    public void enterForeground(Activity activity) {
        mCardScannerReader.enterForeground(activity);
    }

    /**
     * For NFC Mode: Restore the NFC adapter to normal mode of operation
     *
     * @param activity
     * Use {@link NfcAdapter#disableReaderMode(Activity)}
     */
    @MainThread
    public void enterBackground(Activity activity) {
        mCardScannerReader.enterBackground(activity);
    }


    /**
     * Send Command with raw ISO-DEP data to the card and receive the response.
     * The response should be trimmed if it a valid response {@link Commands#isSuccessResponse(String)}
     *
     * @param command
     * @return
     * @throws CommandException if the response is not valid or connect error
     */
    @Nullable
    public String transceive(@NonNull Command command) throws CommandException {
        return mCardScannerReader.transceive(command);
    }

    public void challengeBlockChainPrv(String publicKey) throws CommandException {
        mCardScannerReader.checkBlockChainPrv(ByteUtil.hexStringToBytes(publicKey));
    }

    /**
     * Send Command with raw ISO-8316 data to the card and receive the response.
     * The response should be trimmed if it a valid response {@link Commands#isSuccessResponse(String)}
     *
     * @param command
     * @return
     * @throws CommandException if the response is not valid or connect error
     */
    @Nullable
    public TLVBox transceive2TLV(@NonNull Command command) throws CommandException {
        return mCardScannerReader.transceive2TLV(command);
    }



    /**
     * Get connected coin card currently.
     * <p>
     * The {@link Resource} data {@link Card} will be nonnull, but it may be a error {@link Resource} if fail to connect.
     *
     * @return
     */
    @NonNull
    public LiveData<Resource<Card>> getCard() {
        return mCardScannerReader.getCard();
    }

}
