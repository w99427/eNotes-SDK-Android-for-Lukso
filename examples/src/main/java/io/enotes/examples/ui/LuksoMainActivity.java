package io.enotes.examples.ui;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;

import java.math.BigInteger;
import java.util.Arrays;

import io.enotes.examples.R;
import io.enotes.examples.common.runtimepermission.PermissionsManager;
import io.enotes.examples.common.runtimepermission.PermissionsResultAction;
import io.enotes.sdk.constant.Status;
import io.enotes.sdk.core.CardManager;
import io.enotes.sdk.repository.card.CommandException;

public class LuksoMainActivity extends AppCompatActivity {
    private CardManager mCardManager;
    private byte[] mPublicKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lukso);
        mCardManager = new CardManager(this);
        mCardManager.setReadCardCallback(resource -> {
            if (resource.status == Status.SUCCESS) {
                findViewById(R.id.tv).setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "OK, begin test", Toast.LENGTH_SHORT).show();
                ((TextView) findViewById(R.id.tv_pub)).setText("");
                ((TextView) findViewById(R.id.tv_signature_count)).setText("");
                ((TextView) findViewById(R.id.tv_challenge)).setText("");
                ((TextView) findViewById(R.id.tv_tx)).setText("");
                ((TextView) findViewById(R.id.tv_raw)).setText("");
            }

        });
        initViews();
    }

    private void initViews() {
        findViewById(R.id.btn_pub).setOnClickListener((v) -> {
            try {
                mPublicKey = mCardManager.readBlockchainPublicKey();
                ((TextView) findViewById(R.id.tv_pub)).setText(ByteUtil.toHexString(mPublicKey));
            } catch (CommandException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.btn_count).setOnClickListener((v) -> {
            try {
                ((TextView) findViewById(R.id.tv_signature_count)).setText(mCardManager.readTransactionSignCounter() + "");
            } catch (CommandException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_chanllenge).setOnClickListener((v) -> {
            if (mPublicKey == null) return;
            try {
                ((TextView) findViewById(R.id.tv_challenge)).setText(mCardManager.verifyBloackchainPublicKey(mPublicKey) ? "Success" : "Fail");
            } catch (CommandException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        findViewById(R.id.btn_tx).setOnClickListener((v) -> {
            if (mPublicKey != null) {
                int chainId = 42;
                Transaction tx = getEthTransaction();
                try {
                    CardManager.Pair pair = mCardManager.signTransactionHash(tx.getRawHash(), mPublicKey);
                    int vData = getSignV(pair, tx, chainId);
                    ((TextView) findViewById(R.id.tv_tx)).setText("V->" + vData + "\nR->" + ByteUtil.toHexString(pair.getR()) + "\nS->" + ByteUtil.toHexString(pair.getS()));

                    Transaction rawTx = getRawEthTransaction(pair, vData);
                    ((TextView) findViewById(R.id.tv_raw)).setText(ByteUtil.toHexString(rawTx.getEncoded()));
                } catch (CommandException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private Transaction getEthTransaction() {
        String nonce = "0";
        String estimateGas = "18000000000";
        String gasPrice = "21000";
        String toAddress = "0x291b6374780b72b3a32cbb96b227df4bd9baf642";
        String value = "1000000000000000000";
        byte[] data = null;

        BigInteger toValue;
        if (value.equals("0")) {
            toValue = new BigInteger(value);
        } else {
            toValue = new BigInteger(value).subtract((new BigInteger(gasPrice).multiply(new BigInteger(estimateGas))));
        }
        Transaction tx = new Transaction(ByteUtil.bigIntegerToBytes(new BigInteger(nonce)), ByteUtil.bigIntegerToBytes(new BigInteger(gasPrice)), ByteUtil.bigIntegerToBytes(new BigInteger(estimateGas)), ByteUtil.hexStringToBytes(toAddress), ByteUtil.bigIntegerToBytes(toValue), data);
        return tx;
    }

    private Transaction getRawEthTransaction(CardManager.Pair pair, int vData) {
        String nonce = "0";
        String estimateGas = "18000000000";
        String gasPrice = "21000";
        String toAddress = "0x291b6374780b72b3a32cbb96b227df4bd9baf642";
        String value = "1000000000000000000";
        byte[] data = null;

        BigInteger toValue;
        if (value.equals("0")) {
            toValue = new BigInteger(value);
        } else {
            toValue = new BigInteger(value).subtract((new BigInteger(gasPrice).multiply(new BigInteger(estimateGas))));
        }
        Transaction rawTx = new Transaction(ByteUtil.bigIntegerToBytes(new BigInteger(nonce)), ByteUtil.bigIntegerToBytes(new BigInteger(gasPrice)), ByteUtil.bigIntegerToBytes(new BigInteger(estimateGas)), ByteUtil.hexStringToBytes(toAddress), ByteUtil.bigIntegerToBytes(toValue), data, pair.getR(), pair.getS(), (byte) vData);
        return rawTx;
    }

    private int getSignV(CardManager.Pair pair, Transaction tx, int chainId) {
        int vData = 0;
        ECKey.ECDSASignature sig = new ECKey.ECDSASignature(new BigInteger(1, pair.getR()), new BigInteger(1, pair.getS()));
        int recId = -1;
        byte[] thisKey = mPublicKey;
        for (int i = 0; i < 4; ++i) {
            byte[] k = ECKey.recoverPubBytesFromSignature(i, sig, tx.getRawHash());
            if (k != null && Arrays.equals(k, thisKey)) {
                recId = i;
                break;
            }
        }

        if (recId == -1) {
            throw new RuntimeException("Could not construct a recoverable key. This should never happen.");
        } else {
            if (chainId > 0) {
                vData = (byte) (recId + chainId * 2 + 35);
            } else {
                vData = (byte) (recId + 27);
            }
        }
        return vData;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardManager.enableNfcReader(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCardManager.disableNfcReader(this);
    }

    /**
     * request all needed permission
     */
    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied(String permission) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
