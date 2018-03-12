/**
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.api.client.utils;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by Niels on 2017/10/31.
 *
 */
public class RestFulUtilsTest {
    private RestFulUtils util;
    private String serverUri;
    private Client client;

    @Before
    public void init() {
        client = ClientBuilder.newClient();
        serverUri = "http://127.0.0.1:8765/nuls";
        this.util = RestFulUtils.getInstance();
        this.util.init(serverUri);
    }

    /**
     * (necessary) need a running server, otherwise pls comment it.
     */
    @Test
    public void test() {
        //RpcClientResult result = this.util.get("/", null);
        //System.out.println(result.toString());
    }

    private String get(String path, Map<String, String> params) {
        WebTarget target = client.target(serverUri).path(path);
        if (null != params && !params.isEmpty()) {
            for (String key : params.keySet()) {
                target.queryParam(key, params.get(key));
            }
        }
        String result = target.request().get(String.class);
        return result;
    }

    private RpcClientResult getJson(String path, Map<String, String> params) {
        WebTarget target = client.target(serverUri).path(path);
        if (null != params && !params.isEmpty()) {
            for (String key : params.keySet()) {
                target.queryParam(key, params.get(key));
            }
        }
        return target.request(APPLICATION_JSON).get(RpcClientResult.class);
    }
}
