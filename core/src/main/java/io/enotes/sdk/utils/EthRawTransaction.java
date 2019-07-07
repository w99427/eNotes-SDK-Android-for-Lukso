package io.enotes.sdk.utils;

import android.util.Log;

import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;

import io.enotes.sdk.repository.card.CommandException;
import io.enotes.sdk.repository.db.entity.Card;
import io.enotes.sdk.repository.provider.CardProvider;

import static io.enotes.sdk.utils.SignatureUtils.str2EthSignature;


public class EthRawTransaction {
    private static final String TAG = "EthRawTransaction";

    public String getRawTransaction(Card card, CardProvider cardProvider, byte[] nonce, byte[] gasPrice, byte[] gasLimit, byte[] receiveAddress, byte[] value, byte[] data) throws CommandException {
        Log.i(TAG, "params=\n" + "nonce=" + ByteUtil.byteArrayToInt(nonce) + "\ngasPrice=" + ByteUtil.bytesToBigInteger(gasPrice).toString()
                + "\ngasLimit=" + ByteUtil.byteArrayToInt(gasLimit) + "\nreceiveAddress=" + ByteUtil.toHexString(receiveAddress) +
                "\nvalue=" + ByteUtil.bytesToBigInteger(value).toString());
        Transaction tx = new Transaction(nonce, gasPrice, gasLimit, receiveAddress, value, data);
        if (cardProvider != null) {
            try {
                LogUtils.i(TAG,"getEncodedRaw:"+ ByteUtil.toHexString(tx.getEncodedRaw()));
                LogUtils.i(TAG,"tx_hash_before_sign:"+ ByteUtil.toHexString(tx.getRawHash()));
                String signature = cardProvider.verifyCoinAndSignTx(card, tx.getRawHash());
                LogUtils.i(TAG,"signature->"+signature);
                ECKey ecKey = ECKey.fromPublicOnly(ByteUtil.hexStringToBytes(ByteUtil.toHexString(card.getEthECKey().getPubKey())));
                Log.i(TAG, "address=" + ByteUtil.toHexString(ecKey.getAddress()));
                ECKey.ECDSASignature ecdsaSignature = str2EthSignature(card.getCert().getNetWork(), ecKey, tx.getRawHash(), signature);
                LogUtils.i(TAG,"R->"+ecdsaSignature.r.toString(16)+"\n S->"+ecdsaSignature.s.toString(16));
                Transaction rawTx = new Transaction(nonce, gasPrice, gasLimit, receiveAddress, value, data, ecdsaSignature.r.toByteArray(), ecdsaSignature.s.toByteArray(), ecdsaSignature.v);
                String hexString = ByteUtil.toHexString(rawTx.getEncoded());
                LogUtils.i(TAG, "eth_tx_hex=\n" + hexString);
                return hexString;
            } catch (CommandException e) {
                throw e;
            }
        }
        return null;
    }


    public Pair getRawTransactionPair(Card card, CardProvider cardProvider, byte[] nonce, byte[] gasPrice, byte[] gasLimit, byte[] receiveAddress, byte[] value, byte[] data) throws CommandException {
        Log.i(TAG, "params=\n" + "nonce=" + ByteUtil.byteArrayToInt(nonce) + "\ngasPrice=" + ByteUtil.bytesToBigInteger(gasPrice).toString()
                + "\ngasLimit=" + ByteUtil.byteArrayToInt(gasLimit) + "\nreceiveAddress=" + ByteUtil.toHexString(receiveAddress) +
                "\nvalue=" + ByteUtil.bytesToBigInteger(value).toString());
        Transaction tx = new Transaction(nonce, gasPrice, gasLimit, receiveAddress, value, data);
        if (cardProvider != null) {
            try {
                Pair pair = new Pair();
                LogUtils.i(TAG,"getEncodedRaw:"+ ByteUtil.toHexString(tx.getEncodedRaw()));
                LogUtils.i(TAG,"tx_hash_before_sign:"+ ByteUtil.toHexString(tx.getRawHash()));
                String signature = cardProvider.verifyCoinAndSignTx(card, tx.getRawHash());
                LogUtils.i(TAG,"signature->"+signature);
                ECKey ecKey = ECKey.fromPublicOnly(ByteUtil.hexStringToBytes(ByteUtil.toHexString(card.getEthECKey().getPubKey())));
                Log.i(TAG, "address=" + ByteUtil.toHexString(ecKey.getAddress()));
                ECKey.ECDSASignature ecdsaSignature = str2EthSignature(card.getCert().getNetWork(), ecKey, tx.getRawHash(), signature);
                LogUtils.i(TAG,"R->"+ecdsaSignature.r.toString(16)+"\n S->"+ecdsaSignature.s.toString(16));
                pair.setR(ecdsaSignature.r.toString(16));
                pair.setS(ecdsaSignature.s.toString(16));
                pair.setV((int)ecdsaSignature.v);
                Transaction rawTx = new Transaction(nonce, gasPrice, gasLimit, receiveAddress, value, data, ecdsaSignature.r.toByteArray(), ecdsaSignature.s.toByteArray(), ecdsaSignature.v);
                String hexString = ByteUtil.toHexString(rawTx.getEncoded());
                pair.setRawTx(hexString);
                LogUtils.i(TAG, "eth_tx_hex=\n" + hexString);
                return pair;
            } catch (CommandException e) {
                throw e;
            }
        }
        return null;
    }

    public static class Pair{
        private String r;
        private String s;
        private int v;
        private String rawTx;

        public String getR() {
            return r;
        }

        public void setR(String r) {
            this.r = r;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public int getV() {
            return v;
        }

        public void setV(int v) {
            this.v = v;
        }

        public String getRawTx() {
            return rawTx;
        }

        public void setRawTx(String rawTx) {
            this.rawTx = rawTx;
        }
    }
}
