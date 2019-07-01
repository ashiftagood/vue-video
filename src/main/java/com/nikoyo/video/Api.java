package com.nikoyo.video;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class Api {

    @Autowired
    private HttpUtils httpUtils;

    @Autowired
    private Constants constants;

    @Autowired
    HttpServletRequest request;

    private Map<String, String> supportStaffStatusMap = new HashMap<>();

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String userPwd) {
        if(!StringUtils.isEmpty(httpUtils.dssLogin(username, userPwd))) {
            request.getSession().setAttribute("currentUser", username);
            return constants.SUCCESS;
        } else {
            return constants.ERROR;
        }
    }

    @PostMapping("/live/sessions")
    public String liveSessions() {
        String sessions = httpUtils.dssLiveSessions();
        if("on".equals(constants.dssOnlyKH)) {
            JSONArray jsonArray = JSONArray.parseArray(sessions);
            JSONArray result = new JSONArray();
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("Id");
                if(id.startsWith("KH")) {
                    result.add(jsonObject);
                }
            }
            return result.toJSONString();
        } else {
            return sessions;
        }
    }

    @PostMapping("/liveRecord")
    public String saveLiveRecord(@RequestBody Video video) {
        video.setCreateTime(new Date());
        String currentUser = String.valueOf(request.getSession().getAttribute("currentUser"));
        video.setService(currentUser);
        File file = new File(constants.getDbPath());
        if(!file.exists()) {
            file.mkdirs();
        }
        try(FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile() + File.separator + video.getVideoId());
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8")){
            osw.write(JSONObject.toJSONString(video));
            osw.flush();
            supportStaffStatusMap.put(currentUser, "busy");
            return constants.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return constants.ERROR;
        }
    }

    @GetMapping("/supportStaff")
    public Map<String, String> getKFInfo() {
        return supportStaffStatusMap;
    }

    @GetMapping("/liveRecords")
    public List<Video> liveRecords() {
        List<Video> list = new ArrayList<>();
        File file = new File(constants.getDbPath());
        if(file.exists()) {
            File[] files = file.listFiles();
            try {
                for(File ff : files) {
                    InputStream is = new FileInputStream(ff);
                    Video v = JSONObject.parseObject(is, Video.class);
                    list.add(v);
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @PostMapping("/shotImg/{videoId}")
    public String shotImgUpload(@PathVariable String videoId, MultipartHttpServletRequest httpServletRequest) {
        File file = new File(constants.getShotImgPath() + File.separator + videoId);
        MultipartFile shotImg = httpServletRequest.getFile("shotImg");
        if(!file.exists()) {
            file.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        File img = new File(file.getAbsolutePath() + File.separator + sdf.format(new Date()) + "_" + shotImg.getOriginalFilename());
        try {
            shotImg.transferTo(img);
            return constants.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return constants.ERROR;
        }
    }

    @GetMapping("/shotImg/{videoId}")
    public List<String> getShotImgUrl(@PathVariable String videoId) {
        List<String> list = new ArrayList<>();
        File file = new File(constants.getShotImgPath() + File.separator + videoId);
        if(file.exists()) {
            for(String s : file.list()) {
                list.add(s);
            }
        }
        return list;
    }

    @GetMapping("/downShotImg/{videoId}")
    public void getShotImg(@PathVariable String videoId, @RequestParam String imgName, HttpServletResponse response) {
        File file = new File(constants.getShotImgPath() + File.separator + videoId + File.separator + imgName);
        response.setContentType("image/png");
        try {
            FileCopyUtils.copy(new FileInputStream(file),response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
