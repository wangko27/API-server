package io.nuls.api.entity;

import io.nuls.api.constant.NulsConstant;
import io.nuls.api.utils.TimeService;

import java.util.Objects;

public class Utxo {

    private Long id;

    private String txHash;

    private Integer txIndex;

    private String hashIndex;

    private String address;

    private Long amount;

    private Long lockTime;

    private String spendTxHash;

    private String key;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getLockTime() {
        return lockTime;
    }

    public void setLockTime(Long lockTime) {
        this.lockTime = lockTime;
    }

    public String getSpendTxHash() {
        return spendTxHash;
    }

    public void setSpendTxHash(String spendTxHash) {
        this.spendTxHash = spendTxHash == null ? null : spendTxHash.trim();
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public Integer getTxIndex() {
        return txIndex;
    }

    public void setTxIndex(Integer txIndex) {
        this.txIndex = txIndex;
    }

    public String getHashIndex() {
        return hashIndex;
    }

    public void setHashIndex(String hashIndex) {
        this.hashIndex = hashIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utxo)) return false;
        Utxo utxo = (Utxo) o;
        return Objects.equals(getKey(), utxo.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getKey(), getSpendTxHash());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id").append(id).append("--");
        sb.append("hashIndex").append(hashIndex).append("--");
        sb.append("address").append(address).append("--");
        sb.append("amount").append(amount).append("--");
        sb.append("lockTime").append(lockTime).append("--");
        sb.append("spendTxHash").append(spendTxHash).append("--");
        return sb.toString();
    }

    public String getKey() {
        if (key == null) {
            key = txHash + "_" + txIndex;
        }
        return key;
    }
    /**
     * 根据当前时间和当前最新高度，判断coin是否可用
     *
     * @return boolean
     */
    public boolean usable(Long bestHeight) {
        if (lockTime < 0) {
            return false;
        }
        if (lockTime == 0) {
            return true;
        }

        long currentTime = TimeService.currentTimeMillis();
        //long bestHeight = NulsContext.getInstance().getBestHeight();

        if (lockTime > NulsConstant.BlOCKHEIGHT_TIME_DIVIDE) {
            if (lockTime <= currentTime) {
                return true;
            } else {
                return false;
            }
        } else {
            if (lockTime <= bestHeight) {
                return true;
            } else {
                return false;
            }
        }
    }
}