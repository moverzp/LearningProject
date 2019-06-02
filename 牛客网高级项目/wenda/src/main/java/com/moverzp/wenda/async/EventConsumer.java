package com.moverzp.wenda.async;

import com.alibaba.fastjson.JSON;
import com.moverzp.wenda.util.JedisAdapter;
import com.moverzp.wenda.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();//value是处理该key(事件)的Handller
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;


    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);//获取所有EventHandler的类名及其本身
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();//获取当前Handler支持的事件类型

                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {//如果config没有创建某个EventType列表，则创建该列表，表示首次有处理该类型是Handler被创建
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());//把Handler放入处理还类型事件的列表中
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String  key = RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0, key);//从阻塞队列中获取事件

                    for (String message: events) {
                        if (message.equals(key)) {
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message, EventModel.class);//反序列化事件
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的时间");
                            continue;
                        }

                        //根据事件的类型，得到处理该类型的所有Handler，然后用Handler处理该事件
                        for (EventHandler handler : config.get(eventModel.getType())){
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
