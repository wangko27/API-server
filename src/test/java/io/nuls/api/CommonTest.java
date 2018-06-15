package io.nuls.api;

import io.nuls.api.utils.JSONUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonTest {

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
