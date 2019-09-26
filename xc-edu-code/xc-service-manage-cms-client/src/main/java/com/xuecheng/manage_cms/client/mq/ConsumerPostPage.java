package com.xuecheng.manage_cms.client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.manage_cms.client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConsumerPostPage {
    private static Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);

    @Autowired
    private PageService pageService;

    @RabbitListener(queues={"${xuecheng.mq.queue}"})
    public void postPage(String msg){
        Map map = JSON.parseObject(msg, Map.class);
        LOGGER.info("receive cms post page:{}"+msg);
        String pageId = (String) map.get("pageId");
        pageService.savePageToServerPath(pageId);
    }

}
