package io.nuls.api.utils;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.constant.NulsConstant;
import io.nuls.api.crypto.Hex;
import io.nuls.api.entity.Utxo;
import io.nuls.api.exception.NulsException;
import io.nuls.api.exception.NulsRuntimeException;

import io.nuls.api.model.*;
import io.nuls.api.model.tx.AliasTransaction;
import io.nuls.api.model.tx.CancelDepositTransaction;
import io.nuls.api.model.tx.DepositTransaction;
import io.nuls.api.model.tx.TransferTransaction;
import io.nuls.sdk.core.contast.SDKConstant;
import org.spongycastle.util.Arrays;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TransactionTool {

    /**
     * 获取转账交易手续费
     * 交易手续费的计算：手续费单价 * 交易大小
     * 手续费单价(min)：100000 NA/1KB
     * 交易大小的计算：（124 + 50 * inputs.length + 38 * outputs.length + remark.bytes.length ）/1024
     *
     * @param utxoList  可用未花费集合
     * @param amount    转账金额
     * @param remark    交易备注
     * @param unitPrice 手续费单价
     * @return
     */
    public static Na getTransferTxFee(List<Utxo> utxoList, long amount, String remark, long unitPrice) {
        int size = 162;                  // 124 + 38;
        if (StringUtils.isNotBlank(remark)) {
            try {
                size += remark.getBytes(SDKConstant.DEFAULT_ENCODING).length;
            } catch (UnsupportedEncodingException e) {
                throw new NulsRuntimeException(ErrorCode.PARAMETER_ERROR);
            }
        } else {
            size += 1;
        }
        return calcFee(utxoList, size, amount, Na.valueOf(unitPrice));
    }

    /**
     * 组装转账交易
     *
     * @param utxoList  可用未花费集合
     * @param toAddress 接收人地址
     * @param amount    转账金额
     * @param remark    交易备注
     * @param fee       交易手续费
     * @return
     */
    public static TransferTransaction createTransferTx(List<Utxo> utxoList, String toAddress, long amount, String remark, long fee) {
        List<Coin> outputs = new ArrayList<>();
        Coin to = new Coin();
        to.setLockTime(0);
        to.setNa(Na.valueOf(amount));
        to.setOwner(AddressTool.getAddress(toAddress));
        outputs.add(to);

        CoinData coinData = createCoinData(utxoList, outputs, amount + fee);
        if (coinData != null) {
            TransferTransaction tx = new TransferTransaction();
            if (StringUtils.isNotBlank(remark)) {
                try {
                    tx.setRemark(remark.getBytes(SDKConstant.DEFAULT_ENCODING));
                } catch (UnsupportedEncodingException e) {
                    throw new NulsRuntimeException(ErrorCode.PARAMETER_ERROR);
                }
            }
            tx.setCoinData(coinData);
            try {
                tx.setHash(NulsDigestData.calcDigestData(tx.serializeForHash()));
            } catch (IOException e) {
                throw new NulsRuntimeException(ErrorCode.DATA_PARSE_ERROR);
            }
            return tx;
        }

        return null;
    }

    public static Na getAliasTxFee(List<Utxo> utxoList, String address, String alias) {
        int size = 0;
        byte[] addressBytes = AddressTool.getAddress(address);
        size += VarInt.sizeOf((addressBytes).length) + (addressBytes).length;
        try {
            size += alias.getBytes(SDKConstant.DEFAULT_ENCODING).length;
        } catch (UnsupportedEncodingException e) {
            throw new NulsRuntimeException(ErrorCode.PARAMETER_ERROR);
        }

        size += 162;                  // 124 + 38;
        return calcFee(utxoList, size, NulsConstant.ALIAS_NA.getValue(), TransactionFeeCalculator.OTHER_PRECE_PRE_1024_BYTES);
    }

    public static AliasTransaction createAliasTx(List<Utxo> utxoList, String address, String alias, long fee) {
        List<Coin> outputs = new ArrayList<>();
        Coin to = new Coin();
        to.setLockTime(0);
        to.setNa(NulsConstant.ALIAS_NA);
        to.setOwner(NulsConstant.BLACK_HOLE_ADDRESS);
        outputs.add(to);

        CoinData coinData = createCoinData(utxoList, outputs, NulsConstant.ALIAS_NA.getValue() + fee);
        if (coinData != null) {
            AliasTransaction aliasTx = new AliasTransaction();
            aliasTx.setTxData(new Alias(AddressTool.getAddress(address), alias));
            aliasTx.setCoinData(coinData);
            try {
                aliasTx.setHash(NulsDigestData.calcDigestData(aliasTx.serializeForHash()));
            } catch (IOException e) {
                throw new NulsRuntimeException(ErrorCode.DATA_PARSE_ERROR);
            }
            return aliasTx;
        }
        return null;
    }

    public static Na getJoinAgentTxFee(List<Utxo> utxoList, long amount) {
        int size = 288;
        long values = 0;
        Na fee = null;
        boolean enough = false;
        for (int i = 0; i < utxoList.size(); i++) {
            size += 50;
            values += utxoList.get(i).getAmount();
            if (i == 127) {
                size += 1;
            }
            fee = TransactionFeeCalculator.getFee(size, TransactionFeeCalculator.OTHER_PRECE_PRE_1024_BYTES);
            if (values >= amount + fee.getValue()) {
                enough = true;
            }
        }
        if (enough) {
            return fee;
        }
        return null;
    }

    public static DepositTransaction createDepositTx(List<Utxo> utxoList, String address, String agentHash, long amount, long fee) {
        NulsDigestData hash = null;
        try {
            hash = NulsDigestData.fromDigestHex(agentHash);
        } catch (NulsException e) {
            throw new NulsRuntimeException(ErrorCode.HASH_ERROR);
        }

        List<Coin> outputs = new ArrayList<>();
        Coin to = new Coin();
        to.setLockTime(-1);
        to.setNa(NulsConstant.ALIAS_NA);
        to.setOwner(AddressTool.getAddress(address));
        outputs.add(to);

        Deposit deposit = new Deposit();
        deposit.setAddress(AddressTool.getAddress(address));
        deposit.setAgentHash(hash);
        deposit.setDeposit(Na.valueOf(amount));
        CoinData coinData = createCoinData(utxoList, outputs, amount + fee);
        if (coinData != null) {
            DepositTransaction depositTx = new DepositTransaction();
            depositTx.setTxData(deposit);
            depositTx.setCoinData(coinData);
            try {
                depositTx.setHash(NulsDigestData.calcDigestData(depositTx.serializeForHash()));
            } catch (IOException e) {
                throw new NulsRuntimeException(ErrorCode.DATA_PARSE_ERROR);
            }
            return depositTx;
        }
        return null;
    }

    public static CancelDepositTransaction createCancelDepositTx(Utxo utxo) {
        CancelDeposit cancelDeposit = new CancelDeposit();
        cancelDeposit.setAddress(AddressTool.getAddress(utxo.getAddress()));
        try {
            cancelDeposit.setJoinTxHash(NulsDigestData.fromDigestHex(utxo.getTxHash()));
        } catch (NulsException e) {
            throw new NulsRuntimeException(ErrorCode.HASH_ERROR);
        }
        //组装input
        Coin from = new Coin();
        byte[] key = Arrays.concatenate(Hex.decode(utxo.getTxHash()), new VarInt(utxo.getTxIndex()).encode());
        List<Coin> inputsList = new ArrayList<>();
        from.setOwner(key);
        from.setNa(Na.valueOf(utxo.getAmount()));
        from.setLockTime(-1);
        inputsList.add(from);

        //手续费
        Na fee = TransactionFeeCalculator.OTHER_PRECE_PRE_1024_BYTES;
        //组装output
        List<Coin> toList = new ArrayList<>();
        toList.add(new Coin(cancelDeposit.getAddress(), Na.valueOf(utxo.getAmount()).subtract(fee), 0));
        CancelDepositTransaction tx = new CancelDepositTransaction();
        CoinData coinData = new CoinData();
        coinData.setFrom(inputsList);
        coinData.setTo(toList);
        tx.setCoinData(coinData);
        tx.setTime(TimeService.currentTimeMillis());
        tx.setTxData(cancelDeposit);
        try {
            tx.setHash(NulsDigestData.calcDigestData(tx.serializeForHash()));
        } catch (IOException e) {
            throw new NulsRuntimeException(ErrorCode.DATA_PARSE_ERROR);
        }
        return tx;
    }

    private static Na calcFee(List<Utxo> utxoList, int size, long amount, Na unitPrice) {
        long values = 0;
        Utxo utxo;
        Na fee = null;
        boolean enough = false;
        for (int i = 0; i < utxoList.size(); i++) {
            utxo = utxoList.get(i);
            values += utxo.getAmount();
            //每条from的长度为50
            size += 50;
            if (i == 127) {
                size += 1;
            }

            //每次累加一条未花费余额时，需要重新计算手续费
            fee = TransactionFeeCalculator.getFee(size, unitPrice);
            //需要判断是否找零，如果有找零，则需要重新计算手续费
            if (values > amount + fee.getValue()) {
                fee = TransactionFeeCalculator.getFee(size + 38, unitPrice);
                if (values < amount + fee.getValue()) {
                    continue;
                }
                enough = true;
                break;
            }
        }

        if (enough) {
            return fee;
        }
        return null;
    }

    public static CoinData createCoinData(List<Utxo> utxoList, List<Coin> outputs, long amount) {
        List<Coin> inputs = new ArrayList<>();
        Utxo utxo;
        boolean enough = false;
        long values = 0;

        for (int i = 0; i < utxoList.size(); i++) {
            utxo = utxoList.get(i);
            values += utxo.getAmount();
            inputs.add(transferToFrom(utxo));

            if (values >= amount) {
                enough = true;
                //是否找零
                if (values > amount) {
                    long change = values - amount;
                    Coin coin = new Coin();
                    coin.setNa(Na.valueOf(change));
                    coin.setLockTime(0);
                    coin.setOwner(AddressTool.getAddress(utxo.getAddress()));
                    outputs.add(coin);
                }
                break;
            }
        }
        if (enough) {
            CoinData coinData = new CoinData();
            coinData.setFrom(inputs);
            coinData.setTo(outputs);
            return coinData;
        }
        return null;
    }


    public static Coin transferToFrom(Utxo utxo) {
        Coin coin = new Coin();
        byte[] txHashBytes = Hex.decode(utxo.getTxHash());
        coin.setOwner(Arrays.concatenate(txHashBytes, new VarInt(utxo.getTxIndex()).encode()));
        coin.setLockTime(0);
        coin.setNa(Na.valueOf(utxo.getAmount()));
        return coin;
    }
}
