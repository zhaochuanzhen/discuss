package com.mright.discuss.framework.utils.meeting;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaochuanzhen
 * @date 2021/2/25 10:17
 * @desc 抢占会议室
 * <p>
 * <p>
 * http://jmrs.jd.com/#/
 */
public class OccupyMeetingRoom {

    private static volatile boolean occupy;
    /**
     * 重试次数
     */
    private static final ThreadLocal<Integer> RETRY_THREAD_LOCAL = new ThreadLocal<>();
    private static final List<MeetingRoom> MEETING_LIST;
    private static final String COOKIE;
    private static final String MEETING_DATE;
    private static final String MEETING_SUBJECT;

    static {
        // 预约日期
        MEETING_DATE = "2021-03-01";
        // 主页显示标题
        MEETING_SUBJECT = "xxx预约了会议室";
        COOKIE = "3AB9D23F7A4B3C9B=6MOWOMT3GECOKE246IFPAPHJ354CZFLGC55HUE2J5Q3ZNW7JJNPKHIJ5CQBADUWHECL5TDAQIBL4YPOYC4ACSENDNU; sso.jd.com=HK.0660d3b6a37c4eccb26ca4c8ad7a60c9; jd.erp.lang=en_US; __jda=234025733.16145261580982029213332.1614526158.1614526158.1614526158.1; __jdb=234025733.1.16145261580982029213332|1.1614526158; __jdc=234025733; __jdv=234025733|direct|-|none|-|1614526158100; __jdu=16145261580982029213332; RT=\"z=1&dm=jd.com&si=wo6zw9tqzbj&ss=klpb6r7h&sl=1&tt=1lb&ld=2ha";
        // 生成会议室对象
        MEETING_LIST = new ArrayList<>();
        // 下午5点-6点的宝瓶座或天鹅座
        MEETING_LIST.addAll(getMeetingRoom(1600, 1700, MeetingRoomEnum.TIAN_E_ZUO, MeetingRoomEnum.BAO_PING_ZUO));
        // 下午6点-7点的宝瓶座或天鹅座
        MEETING_LIST.addAll(getMeetingRoom(1800, 1900, MeetingRoomEnum.TIAN_E_ZUO, MeetingRoomEnum.BAO_PING_ZUO));
        occupy = false;
    }

    /**
     * 会议室web地址：  http://jmrs.jd.com/#/
     * <p>
     * 1. 维护MeetingRoomEnum枚举、把需要抢占的会议室 meetingCode 和 meetingName 加上
     * 2. 维护MEETING_LIST，指定要抢占的会议室枚举 和 抢占时间段
     * 3. 修改 MEETING_DATE ，预占哪天；修改 MEETING_SUBJECT ，京me上会展示这段话，一般设置自己名字，比如：xxx预约了会议室；维护 Cookie，访问会议室web地址登录上、随便找个请求携带的cookie
     * 4. 如果当前还未到9点，那么程序睡眠等待到9点；如果到了9点，那么每个会议室每个时间段会尝试抢占10次，其中一次抢占成功本线程结束。
     */
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 8, 200, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
        // 1. 校验是否到9点
        validateNine();
        MEETING_LIST.forEach(meetingRoom -> threadPoolExecutor.execute(() -> {
            // 2. 每间会议室抢占重试次数
            if (null == RETRY_THREAD_LOCAL.get()) {
                RETRY_THREAD_LOCAL.set(10);
            }
            for (int i = 1; i <= RETRY_THREAD_LOCAL.get(); i++) {
                // 3. 抢占逻辑
                occupy(meetingRoom, i);
            }
            RETRY_THREAD_LOCAL.remove();
        }));
        threadPoolExecutor.shutdown();
    }

    /**
     * 抢占会议室
     */
    private static void occupy(MeetingRoom meetingRoom, int i) {
        String meetingUrl = "http://jmrs.jd.com/meetingOrder/create";
        Map<String, String> headers = generateHeaders();
        String body = JSON.toJSONString(meetingRoom);
        try {
            String response = HttpUtil.post(meetingUrl, body, headers);
            System.out.println(meetingRoom.getMeetingName() + "\t" + meetingRoom.getMeetingEstimateStime() + "-" + meetingRoom.getMeetingEstimateEtime() + "\t" + "尝试第" + i + "次" + response);
            JSONObject jo = JSON.parseObject(response);
            if (jo.getInteger("resultCode") == 1) {
                System.out.print("抢占成功：" + meetingRoom.toString());
                RETRY_THREAD_LOCAL.set(0);
            } else {
                if ("预订会议室失败，该会议室已被占用。".equals(jo.getString("message"))) {
                    System.out.println(meetingRoom.getMeetingName() + " 已被别人占、结束重试");
                    RETRY_THREAD_LOCAL.set(0);
                }
                // 如果抢不到、休息10毫秒
                TimeUnit.MILLISECONDS.sleep(10);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验是否到9点、没到就睡
     */
    private static void validateNine() throws InterruptedException {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long nineTime = cal.getTime().getTime();
        if (System.currentTimeMillis() < nineTime) {
            long sleep = nineTime - System.currentTimeMillis();
            System.out.println("还未到9点，sleep:" + sleep / 1000 / 60 + "分钟");
            TimeUnit.MILLISECONDS.sleep(sleep);
        }
    }

    /**
     * 获取会议室对象
     */
    private static List<MeetingRoom> getMeetingRoom(Integer start, Integer end, MeetingRoomEnum... meetingRoomEnums) {
        List<MeetingRoom> list = new ArrayList<>();
        Arrays.stream(meetingRoomEnums).forEach(meetingRoomEnum -> {
            list.add(MeetingRoom.builder()
                    .meetingName(meetingRoomEnum.getMeetingName())
                    .meetingCode(meetingRoomEnum.getMeetingCode())
                    .workplaceCode(meetingRoomEnum.getWorkplaceCode())
                    .districtCode(meetingRoomEnum.getDistrictCode())
                    .meetingEstimateDate(MEETING_DATE)
                    .meetingEstimateStime(start)
                    .meetingEstimateEtime(end)
                    .bookJoyMeeting(meetingRoomEnum.getBookJoyMeeting())
                    .joyMeetingParam(meetingRoomEnum.getJoyMeetingParam())
                    .meetingSubject(MEETING_SUBJECT)
                    .lang(meetingRoomEnum.getLang())
                    .build());
        });
        return list;
    }

    /**
     * 生成headers
     */
    private static Map<String, String> generateHeaders() {
        Map<String, String> headers = new HashMap<>(16);
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Connection", "keep-alive");
        headers.put("Accept", "application/json, text/plain, */*");
        headers.put("Cookie", COOKIE);
        headers.put("Referer", "http://jmrs.jd.com/saas/sso?jdme_router=jdme%3A%2F%2Fweb%2F201908080584%3Fbrowser%3D1%26url%3Dhttps%253A%252F%252Fjmrs.jd.com%252Fsaas%252Fme%253Ftab%253D1");
        headers.put("Origin", "http://jmrs.jd.com");
        headers.put("jms-tenant-code", "CN.JD.GROUP");
        headers.put("Host", "jmrs.jd.com");
        headers.put("jms-lang", "zh");
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("Content-Length", "<calculated when request is sent>");
        return headers;
    }
}

@Getter
@AllArgsConstructor
@NoArgsConstructor
enum MeetingRoomEnum {

    /**
     * 天鹅座
     */
    TIAN_E_ZUO(
            "天鹅座",
            "2001006206",
            "1001000530",
            "13",
            "",
            1700,
            1800,
            0,
            "",
            "",
            "zh"
    ),
    /**
     * 宝瓶座
     */
    BAO_PING_ZUO(
            "宝瓶座",
            "2001005991",
            "1001000530",
            "13",
            "",
            1700,
            1800,
            0,
            "",
            "",
            "zh"
    ),
    /**
     * 猎犬座
     */
    LIE_QUAN_ZUO(
            "猎犬座",
            "2001006216",
            "1001000530",
            "13",
            "2021-02-25",
            1930,
            1900,
            0,
            "",
            "",
            "zh"),
    /**
     * 御夫座
     */
    YU_FU_ZUO(
            "御夫座",
            "2001006220",
            "1001000530",
            "13",
            "2021-02-25",
            1930,
            1900,
            0,
            "",
            "",
            "zh"
    ),
    /**
     * 三角座
     */
    SAN_JIAO_ZUO(
            "三角座",
            "2001006222",
            "1001000530",
            "13",
            "2021-02-25",
            1930,
            1900,
            0,
            "",
            "",
            "zh"
    );

    private String meetingName;
    private String meetingCode;
    private String workplaceCode;
    private String districtCode;
    private String meetingEstimateDate;
    private Integer meetingEstimateStime;
    private Integer meetingEstimateEtime;
    private Integer bookJoyMeeting;
    private String joyMeetingParam;
    private String meetingSubject;
    private String lang;
}

/**
 * 会议室对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MeetingRoom implements Serializable {
    private static final long serialVersionUID = -445822934180081426L;
    private String meetingName;
    private String meetingCode;
    private String workplaceCode;
    private String districtCode;
    private String meetingEstimateDate;
    private Integer meetingEstimateStime;
    private Integer meetingEstimateEtime;
    private Integer bookJoyMeeting;
    private String joyMeetingParam;
    private String meetingSubject;
    private String lang;

    MeetingRoom changeTime(Integer meetingEstimateStime, Integer meetingEstimateEtime) {
        this.meetingEstimateStime = meetingEstimateStime;
        this.meetingEstimateEtime = meetingEstimateEtime;
        return this;
    }

    @Override
    public String toString() {
        return "MeetingRoom{" +
                "meetingName='" + meetingName + '\'' +
                ", meetingEstimateDate='" + meetingEstimateDate + '\'' +
                ", meetingEstimateStime=" + meetingEstimateStime +
                ", meetingEstimateEtime=" + meetingEstimateEtime +
                '}';
    }
}

/**
 * Http Post
 */
class HttpUtil {

    private HttpUtil() {
    }

    public static String post(String url, String body, Map<String, String> headers) throws IOException {
        return fetch("POST", url, body, headers);
    }

    public static String fetch(String method, String url, String body, Map<String, String> headers) throws IOException {
        // connection
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setConnectTimeout(100000);
        conn.setReadTimeout(300000);

        // method
        if (method != null) {
            conn.setRequestMethod(method);
        }

        // headers
        if (headers != null) {
            for (String key : headers.keySet()) {
                conn.addRequestProperty(key, headers.get(key));
            }
        }

        // body
        if (body != null) {
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes());
            os.flush();
            os.close();
        }

        // response
        InputStream is = conn.getInputStream();
        String response = streamToString(is);
        is.close();

        // handle redirects
        if (conn.getResponseCode() == 301) {
            String location = conn.getHeaderField("Location");
            return fetch(method, location, body, headers);
        }

        return response;
    }

    /**
     * streamToString
     */
    public static String streamToString(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line;
        while ((line = br.readLine()) != null) {
            out.append(line);
        }

        return out.toString();
    }
}
