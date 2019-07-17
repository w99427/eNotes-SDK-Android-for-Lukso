package io.enotes.sdk.core;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;

import java.math.BigInteger;

import io.enotes.sdk.constant.ErrorCode;
import io.enotes.sdk.constant.Status;
import io.enotes.sdk.core.interfaces.CardInterface;
import io.enotes.sdk.repository.card.Command;
import io.enotes.sdk.repository.card.CommandException;
import io.enotes.sdk.repository.card.Commands;
import io.enotes.sdk.repository.card.TLVBox;
import io.enotes.sdk.repository.db.entity.Card;
import io.enotes.sdk.repository.provider.CardProvider;
import io.enotes.sdk.viewmodel.CardViewModel;

public class CardManager implements CardInterface {
    private CardProvider cardProvider;
    private @NonNull
    FragmentActivity fragmentActivity;
    private Callback readCardCallback;
    private Handler handler;

    public CardManager(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        handler = new Handler(fragmentActivity.getMainLooper());
        CardViewModel cardViewModel = ViewModelProviders.of(fragmentActivity).get(CardViewModel.class);
        cardProvider = cardViewModel.getCardProvider();
        initCallback();
    }

    public CardManager(Fragment fragment) {
        this.fragmentActivity = fragment.getActivity();
        handler = new Handler(fragmentActivity.getMainLooper());
        CardViewModel cardViewModel = ViewModelProviders.of(fragment).get(CardViewModel.class);
        cardProvider = cardViewModel.getCardProvider();
        initCallback();
    }

    private void initCallback() {
        if (cardProvider != null) {
            cardProvider.getCard().observe(fragmentActivity, (resource -> {
                if (resource.status == Status.SUCCESS) {
                }
                if (readCardCallback != null) {
                    readCardCallback.onCallBack(resource);
                }
            }));


        }
    }

    @Override
    public void enableNfcReader(Activity activity) {
        cardProvider.enterForeground(activity);
    }

    @Override
    public void disableNfcReader(Activity activity) {
        cardProvider.enterBackground(activity);
    }

    @Override
    public void setReadCardCallback(Callback<Card> cardCallback) {
        readCardCallback = cardCallback;
    }




    @Override
    public String transmitApdu(@NonNull Command command) throws CommandException {
        return cardProvider.transceive(command);
    }

    @Override
    public Pair signTransactionHash(byte[] hash, byte[] publicKey) throws CommandException {
        TLVBox tlvBox = new TLVBox();
        tlvBox.putBytesValue(Commands.TLVTag.Transaction_Hash, hash);
        try {
            byte[] bytes = ByteUtil.hexStringToBytes(transmitApdu(Commands.signTX(tlvBox.serialize())));
            TLVBox signatureTLV = TLVBox.parse(bytes, 0, bytes.length);
            String signature = signatureTLV.getStringValue(Commands.TLVTag.Transaction_signature);
            if (signature.length() != 128) {
                throw new CommandException(ErrorCode.INVALID_CARD, "please_right_card");
            }
            String r = signature.substring(0, 64);
            String s = signature.substring(64);
            ECKey.ECDSASignature sig = new ECKey.ECDSASignature(new BigInteger(r, 16), new BigInteger(s, 16)).toCanonicalised();
            return new Pair(sig.r.toByteArray(),sig.s.toByteArray());
        } catch (CommandException e) {
            throw e;
        }
    }

    @Override
    public byte[] readBlockchainPublicKey() throws CommandException {
        try {
            byte[] bytes = ByteUtil.hexStringToBytes(transmitApdu(Commands.getCurrencyPubKey()));
            TLVBox signatureTLV = TLVBox.parse(bytes, 0, bytes.length);
            String pubKey = signatureTLV.getStringValue(Commands.TLVTag.BlockChain_PublicKey);
            if (TextUtils.isEmpty(pubKey))
                throw new CommandException(ErrorCode.INVALID_CARD, "wrong public key format");
            if (pubKey.length() == 130 && pubKey.startsWith("04"))
                return ByteUtil.hexStringToBytes(pubKey);
            else if (pubKey.length() == 128)
                return ByteUtil.hexStringToBytes("04" + pubKey);
            else if (pubKey.length() == 66 && (pubKey.startsWith("02") || pubKey.startsWith("03")))
                return ByteUtil.hexStringToBytes(pubKey);
            throw new CommandException(ErrorCode.INVALID_CARD, "wrong public key format");
        } catch (CommandException e) {
            throw e;
        }
    }

    @Override
    public int readTransactionSignCounter() throws CommandException {
        try {
            byte[] bytes = ByteUtil.hexStringToBytes(transmitApdu(Commands.getTxSignCounter()));
            TLVBox signatureTLV = TLVBox.parse(bytes, 0, bytes.length);
            String signatureCount = signatureTLV.getStringValue(Commands.TLVTag.Transaction_Signature_Counter);
            if (signatureCount != null) {
                return new BigInteger(signatureCount, 16).intValue();
            }
            throw new CommandException(ErrorCode.INVALID_CARD, "Fail to read status");
        } catch (CommandException e) {
            throw e;
        }

    }

    @Override
    public boolean verifyBloackchainPublicKey(byte[] publicKey) throws CommandException {
        try {
            cardProvider.challengeBlockChainPrv(ByteUtil.toHexString(publicKey));
            return true;
        } catch (CommandException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class Pair{
        private byte[] r;
        private byte[] s;

        public Pair(byte[] r, byte[] s){
            this.r=r;
            this.s=s;
        }

        public byte[] getR() {
            return r;
        }

        public void setR(byte[] r) {
            this.r = r;
        }

        public byte[] getS() {
            return s;
        }

        public void setS(byte[] s) {
            this.s = s;
        }

    }
}
