package io.nuls.api;

import io.nuls.api.crypto.ECKey;
import io.nuls.api.crypto.Hex;
import io.nuls.api.utils.JSONUtils;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonTest {

    @Test
    public void testNumber(){
        String str="242____56";
        String regEx = "^[a-z0-9]{1}[a-z0-9_]{0,98}[a-z0-9]{1}$";
        System.out.println(str.matches(regEx));
    }

    @Test
    public void testAddress() {
        String priKey = "bf55ed1e8665187f5b9ed7f74fc09e92f28e1619a07493b70be781192d796054";
        String pubKey = "03e8d5cec36087d1432133ccd773f48a1e57aa41854eebe77deb312559f67d3555";
        ECKey ecKey = ECKey.fromPrivate(new BigInteger(Hex.decode(priKey)));
        System.out.println(ecKey.getPublicKeyAsHex(false));
        System.out.println(ecKey.getPublicKeyAsHex(true));
        System.out.println("xxxxxxxxxxxx");
        /*byte[] hash160 = SerializeUtils.sha256hash160(ecKey.getPubKey());
        byte[] hash1602 = SerializeUtils.sha256hash160(Hex.decode(pubKey));

        Address address = new Address(NulsContext.DEFAULT_CHAIN_ID, NulsContext.DEFAULT_ADDRESS_TYPE, hash160);
        Address address2 = new Address(NulsContext.DEFAULT_CHAIN_ID, NulsContext.DEFAULT_ADDRESS_TYPE, hash1602);
        System.out.println(address.getBase58());
        System.out.println(address2.getBase58());*/
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
