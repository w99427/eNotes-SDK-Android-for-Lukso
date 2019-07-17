package io.enotes.sdk.core.interfaces;


import android.app.Activity;
import android.support.annotation.NonNull;

import io.enotes.sdk.core.Callback;
import io.enotes.sdk.core.CardManager;
import io.enotes.sdk.repository.card.Command;
import io.enotes.sdk.repository.card.CommandException;
import io.enotes.sdk.repository.db.entity.Card;

public interface CardInterface {


    void enableNfcReader(Activity activity);


    void disableNfcReader(Activity activity);


    void setReadCardCallback(Callback<Card> cardCallback);


    String transmitApdu(@NonNull Command command) throws CommandException;


    byte[] readBlockchainPublicKey() throws CommandException;

    int readTransactionSignCounter() throws CommandException;

    boolean verifyBloackchainPublicKey(byte[] publicKey) throws CommandException;

    CardManager.Pair signTransactionHash(byte[] hash, byte[] publicKey) throws CommandException;

}
