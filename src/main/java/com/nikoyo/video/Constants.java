package com.nikoyo.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Configuration
public class Constants {
    @Value("${dss.url}")
    public String dssUrl;

    @Value("${dss.user}")
    public String dssUser;

    @Value("${dss.password}")
    public String dssPassword;

    @Value("${dss.dataStore}")
    public String dssDataStore;

    @Value("${dss.onlyKH}")
    public String dssOnlyKH;

    public final String SUCCESS = "success";
    public final String ERROR = "error";

    public String getShotImgPath() {
        return this.dssDataStore + File.separator + "shotImg";
    }

    public String getDbPath() {
        return this.dssDataStore + File.separator + "db";
    }
}
