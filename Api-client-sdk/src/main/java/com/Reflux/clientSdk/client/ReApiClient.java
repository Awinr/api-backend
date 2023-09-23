package com.Reflux.clientSdk.client;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.Reflux.clientSdk.model.APIHeaderConstant;
import com.Reflux.clientSdk.model.User;
import com.Reflux.clientSdk.utils.SignUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.Reflux.clientSdk.utils.SignUtils.genSign;

/**
 * 调用第三方接口的客户端，即开发者
 * 为了开发者们更方便地调用第三方接口，不用每次调用都自己实现签名算法，只需要关注调用什么接口，传递什么参数，就跟调用自己写的接口一样
 * 因此，需要给开发者们提供一个sdk，即客户端，导入starter，就可以直接通过@Resource注解引入xxxClient
 */

@Slf4j
@Data
public class ReApiClient {
    private static String GATEWAY_HOST = "http://localhost:8090";

    private String accessKey;

    private String secretKey;

    public ReApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public void setGateway_Host(String gatewayHost) {
        GATEWAY_HOST = gatewayHost;
    }


    /**
     * 生成新的请求头，里面包含accessKey，并且为了安全，使用签名密钥
     *
     * @param body   调用接口所需要的的该接口的参数
     * @param method 调用接口所需要的method
     * @return 新的请求头
     */
    private Map<String, String> getHeaderMap(long id, String body, String method, String path, String url) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put(APIHeaderConstant.ACCESSKEY, accessKey);
        // 接口ID
        hashMap.put(APIHeaderConstant.API_ID, String.valueOf(id));
        // 一定不能直接发送  hashMap.put("secretKey", secretKey);

        // 加上随机数nonce, 一次请求只能调用一次接口，用后随机数作废
        hashMap.put(APIHeaderConstant.NONCE, RandomUtil.randomNumbers(4));
        // 请求一定时间内有效
        hashMap.put(APIHeaderConstant.TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
        // 签名
        hashMap.put(APIHeaderConstant.SIGN, SignUtils.genSign(body, secretKey));
        // 处理参数中文问题
        body = URLUtil.encode(body, CharsetUtil.CHARSET_UTF_8);
        hashMap.put(APIHeaderConstant.BODY, body);
        // 下面这三个是寻找调用接口的关键
        hashMap.put(APIHeaderConstant.METHOD, method);
        hashMap.put(APIHeaderConstant.PATH, path);
        hashMap.put(APIHeaderConstant.URL, url);
        return hashMap;
    }

    /**
     * 支持调用任意接口，把请求导向网关
     *
     * @param params 接口参数
     * @param url    接口地址
     * @param method 接口使用方法
     * @return 接口调用结果
     */
    public String invokeInterface(long id, String params, String url, String method, String path) {
        String result;
        log.info("SDK正在转发至GATEWAY_HOST:{}", GATEWAY_HOST);
        try (
                HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + path)
                        // 处理中文编码
                        .header("Accept-Charset", CharsetUtil.UTF_8)
                        .addHeaders(getHeaderMap(id, params, method, path, url))
                        .body(params)
                        .execute()) {
            String body = httpResponse.body();
            /*
            // 可以在SDK处理接口404的情况
            if(httpResponse.getStatus()==404){
                body = String.format("{\"code\": %d,\"msg\":\"%s\",\"data\":\"%s\"}",
                        httpResponse.getStatus(), "接口请求路径不存在", "null");
                log.info("响应结果：" + body);
            }
            */
            // 将返回的JSON结果格式化，其实就是加换行符
            result = JSONUtil.formatJsonStr(body);
        }
        log.info("SDK调用接口完成，响应数据：{}", result);
        return result;
    }


    /**
     * 下面三个为client测试直接调用interface里的模拟接口
     */
    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/get", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post(GATEWAY_HOST + "/api/name/post", paramMap);
        System.out.println(result);
        return result;
    }

    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaderMap(1L, json, "POST", "/api/name/user", "http://localhost:8081/api/name/user"))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }
}
