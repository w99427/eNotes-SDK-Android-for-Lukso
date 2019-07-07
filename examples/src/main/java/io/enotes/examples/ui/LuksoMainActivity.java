package io.enotes.examples.ui;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.enotes.examples.R;
import io.enotes.examples.common.runtimepermission.PermissionsManager;
import io.enotes.examples.common.runtimepermission.PermissionsResultAction;
import io.enotes.sdk.constant.Status;
import io.enotes.sdk.core.CardManager;
import io.enotes.sdk.repository.card.CommandException;
import io.enotes.sdk.repository.db.entity.Card;
import io.enotes.sdk.utils.EthRawTransaction;

public class LuksoMainActivity extends AppCompatActivity {
    private CardManager mCardManager;
    private Card mCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lukso);
        mCardManager = new CardManager(this);
        mCardManager.setReadCardCallback(resource -> {
            if (resource.status == Status.SUCCESS) {
                mCard = resource.data;
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
                ((TextView) findViewById(R.id.tv_pub)).setText(mCardManager.getPublicKey());
            } catch (CommandException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.btn_count).setOnClickListener((v) -> {
            try {
                ((TextView) findViewById(R.id.tv_signature_count)).setText(mCardManager.getSignatureCount() + "");
            } catch (CommandException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.btn_chanllenge).setOnClickListener((v) -> {
            try {
                ((TextView) findViewById(R.id.tv_challenge)).setText(mCardManager.challengeBloackChainPrv(mCardManager.getPublicKey()) ? "Success" : "Fail");
            } catch (CommandException e) {
                e.printStackTrace();
            }

        });

        findViewById(R.id.btn_tx).setOnClickListener((v) -> {
            if (mCard != null) {
                String nonce = "0";
                String estimateGas = "18000000000";
                String gasPrice = "21000";
                String toAddress = "0x291b6374780b72b3a32cbb96b227df4bd9baf642";
                String value = "1000000000000000000";
                byte[] data = null;
                mCardManager.getEthRawTransactionPair(mCard, nonce, estimateGas, gasPrice, toAddress, value, data, resource -> {
                    if (resource.status == Status.SUCCESS) {
                        EthRawTransaction.Pair pair = resource.data;

                        ((TextView) findViewById(R.id.tv_tx)).setText("V->" + pair.getV() + "\nR->" + pair.getR() + "\nS->" + pair.getS());
                        ((TextView) findViewById(R.id.tv_raw)).setText(pair.getRawTx());
                    }
                });
            }
        });


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
