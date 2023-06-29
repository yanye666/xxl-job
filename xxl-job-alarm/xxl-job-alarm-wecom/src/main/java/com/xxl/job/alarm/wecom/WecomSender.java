package com.xxl.job.alarm.wecom;

import com.xxl.job.alarm.AlarmConstants;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created on 2022/2/23.
 *
 * @author lan
 */
public class WecomSender {

    private static final Logger logger = LoggerFactory.getLogger(WecomSender.class);

    private final String robotKey;

    static final String httpUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=%s";

    public WecomSender(Properties config) {
        robotKey = config.getProperty(AlarmConstants.ALARM_TARGET);
    }

    public boolean sendMsg(String message) {
        try {
            HttpPost httpPost = new HttpPost(String.format(httpUrl, robotKey));
            httpPost.setEntity(new StringEntity(message, ContentType.APPLICATION_JSON));
            CloseableHttpResponse execute = HttpClientSingleton.INSTANCE.getHttpClient().execute(httpPost);
            execute.close();
        } catch (Exception e) {
            logger.error("Http send msg :{} failed", message, e);
        }
        return true;
    }
}
