package com.nikoyo.video;

import com.alibaba.fastjson.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpUtils {

    private Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private final String TOKEN = "token";

    @Autowired
    private Constants constants;

    public String dssLogin(String username, String userPwd) {
        if(StringUtils.isEmpty(username)) {
            username = constants.dssUser;
        }
        if(StringUtils.isEmpty(userPwd)) {
            userPwd = constants.dssPassword;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        StringBuilder body = new StringBuilder();
        body.append("username=").append(username).append("&password=").append(DigestUtils.md5DigestAsHex(userPwd.getBytes())).append("&onlytoken=true");
        String resp = requestDss("/login", headers, body.toString(), HttpMethod.POST);
        if(resp != null) {
            return JSONObject.parseObject(resp).getString(TOKEN);
        }
        return null;
    }

    public String dssLogin() {
        return dssLogin(null, null);
    }

    public String dssLiveSessions() {
        String token = dssLogin();
        if(StringUtils.isEmpty(token)) {
            logger.error("token is null, login failed.");
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        StringBuilder body = new StringBuilder();
        body.append("token=").append(token);
        String resp = requestDss("/live/sessions", headers, body.toString(), HttpMethod.POST);
        return resp;
    }

    public String dssLiveSession(String id) {
        String token = dssLogin();
        if(StringUtils.isEmpty(token)) {
            logger.error("token is null, login failed.");
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        StringBuilder body = new StringBuilder();
        body.append("id=").append(id);
        body.append("&token=").append(token);
        String resp = requestDss("/live/get", headers, body.toString(), HttpMethod.POST);
        return resp;
    }

    private String requestDss(String apiUrl, HttpHeaders headers, String body, HttpMethod httpMethod) {
        RestTemplate rest = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = rest.exchange(constants.dssUrl.concat(apiUrl), httpMethod, entity, String.class);
        if(response.getStatusCode().is2xxSuccessful()) {
            logger.info("api:{}, response body:{}", apiUrl, response.getBody());
            return response.getBody();
        } else {
            logger.error("requestDss failed. api:{}; status code:{}; response body:{}", apiUrl, response.getStatusCode().value(), response.getBody());
        }
        return null;
    }
}
