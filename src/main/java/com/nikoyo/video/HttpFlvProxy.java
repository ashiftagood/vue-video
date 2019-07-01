package com.nikoyo.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

@RestController
public class HttpFlvProxy {

    @Autowired
    private Constants constants;

    @GetMapping("/flv/hls/{flvName:.+}")
    public void proxy(@PathVariable String flvName, HttpServletRequest request, HttpServletResponse response) {
        try {
            URL url = new URL(constants.dssUrl + "/flv/hls/" + flvName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            Enumeration<String> headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()) {
                String s = headerNames.nextElement();
                String header = request.getHeader(s);
                connection.addRequestProperty(s, header);
            }
            connection.connect();
            FileCopyUtils.copy(connection.getInputStream(), response.getOutputStream());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
