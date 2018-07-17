package io.nuls.api;

import io.nuls.api.context.NulsContext;
import io.nuls.api.crypto.ECKey;
import io.nuls.api.crypto.Hex;
import io.nuls.api.model.Address;
import io.nuls.api.utils.AddressTool;
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
        /*String priKey = "0c40c210ea5633b136b1cc7cd4bdf3071f7cab6d2fa23b9a88dcc9e9ac1729ff";
        String pubKey = "02b62b2e4c8bb8c9af4b2795d8bfa7f053b55cbe62e0c6c6c9d3e06330842c2cf8";*/
        /*String priKey = "eef52b738e4865b353a4215827cdc3f8698467ad16a5ca127e4cdd1283d6f4ce";
        String pubKey = "02c5ce7230620f87811a52e8f95bb4c99a51318664c15bc22e287ca14a67befc29";*/
        /*String priKey = "1e750f72f29d6efe2bb195cd5c470e662328c6d17f53bf83416f80a0eaee585c";
        String pubKey = "026f6e66a7c2c7c868a51544e37fba284110096147d3be2f0ee8f3549aab427dd5";*/
        String priKey = "a90dff6fccb4bc0ef0d54065b3fc6d24cfda241a69dd1c49b0dd578a3b5f9dbd";
        String pubKey = "025066b0e62e030a0b5cef2966bd9b829e690140eff93ec16039a3a9bc7840e3ed";
        /*String priKey = "51b0e7d394dde394314d250c804017a65a383dc2d21a2746ba748333f2b793b2";
        String pubKey = "0244340f75605ee710a313cb6d4bec99b1ec051c78b41df0dd0dcfad4c8bd89262";*/
        ECKey ecKey = ECKey.fromPrivate(new BigInteger(Hex.decode(priKey)));

        byte[] hash160 = SerializeUtils.sha256hash160(ecKey.getPubKey());
        byte[] hash1602 = SerializeUtils.sha256hash160(Hex.decode(pubKey));

        Address address = new Address(NulsContext.DEFAULT_CHAIN_ID, NulsContext.DEFAULT_ADDRESS_TYPE, hash160);
        Address address2 = new Address(NulsContext.DEFAULT_CHAIN_ID, NulsContext.DEFAULT_ADDRESS_TYPE, hash1602);
        System.out.println(address.getBase58());
        System.out.println(address2.getBase58());
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
