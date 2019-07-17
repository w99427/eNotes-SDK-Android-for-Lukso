package io.enotes.sdk.repository.db.entity;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigInteger;


public class Card {
    /**
     * Tx signed times = 0
     */
    public static final int STATUS_SAFE = 0;
    /**
     * Tx signed times big than 0
     */
    public static final int STATUS_UNSAFE = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_SAFE, STATUS_UNSAFE})
    @interface Status {
    }

    private long id;

    private Cert cert;
    private long createTime;
    private long updateTime;
    private String currencyPubKey;//uncompressed
    private String address;
    private BigInteger balance;
    private int status;
    private String txId;

    //1.2.0
    private String account;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Cert getCert() {
        return cert;
    }

    public void setCert(Cert cert) {
        this.cert = cert;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getCurrencyPubKey() {
        return currencyPubKey;
    }

    public void setCurrencyPubKey(String currencyPubKey) {
        this.currencyPubKey = currencyPubKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigInteger getBalance() {
        return balance;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }



}
