package io.nuls.api;

import io.nuls.api.context.NulsContext;
import io.nuls.api.crypto.ECKey;
import io.nuls.api.crypto.Hex;
import io.nuls.api.model.Address;
import io.nuls.api.utils.JSONUtils;
import io.nuls.api.utils.SerializeUtils;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonTest {

    @Test
    public void testAddress() {
        String priKey = "88061d66be3d054ae6e93094b99e807b57595cbc19b0b3588236cf1cdc131995";
        ECKey ecKey = ECKey.fromPrivate(new BigInteger(Hex.decode(priKey)));
        String pubKey = Hex.encode(ecKey.getPubKey());
        System.out.println(pubKey);

        byte[] hash160 = SerializeUtils.sha256hash160(ecKey.getPubKey());
        Address address = new Address(NulsContext.DEFAULT_CHAIN_ID, NulsContext.DEFAULT_ADDRESS_TYPE, hash160);
        System.out.println(address.getBase58());
    }

    @Test
    public void testMapToString() {
        Map<String, Object> map = new HashMap<>();

        List<Input> inputs = new ArrayList<>();
        Input input = new Input();
        input.setFromHash("002023c66d10cf9047dbcca12aee2235ff9dfe0f13db3c921a2ec22e0dd63331cb85");
        input.setFromIndex(4);
        inputs.add(input);

        List<Output> outputs = new ArrayList<>();
        Output output = new Output();
        output.setAddress("2CjPVMKST7h4Q5Dqa8Q9P9CwYSmN7mG");
        output.setValue(1000000);
        output.setLockTime(0);
        outputs.add(output);

        output = new Output();
        output.setAddress("2CXJEuoXZMajeTEgL6TgiSxTRRMwiMM");
        output.setValue(1000000000000000L - 1000000 - 1000000);
        output.setLockTime(0);
        outputs.add(output);

        map.put("inputs", inputs);
        map.put("outputs", outputs);
        try {
            System.out.println(JSONUtils.obj2json(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
