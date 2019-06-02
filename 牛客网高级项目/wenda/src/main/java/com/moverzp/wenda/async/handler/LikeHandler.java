package com.moverzp.wenda.async.handler;

import com.moverzp.wenda.async.EventHandler;
import com.moverzp.wenda.async.EventModel;
import com.moverzp.wenda.async.EventType;
import com.moverzp.wenda.model.Message;
import com.moverzp.wenda.model.User;
import com.moverzp.wenda.service.MessageService;
import com.moverzp.wenda.service.UserService;
import com.moverzp.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        //用户A给用户B点赞以后，系统给B发站内信通知A点赞了某个回答
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(model.getActorId());
        message.setContent("用户" + user.getName()
                            + "赞了你的评论，http://127.0.0.1:8080/question/" //这里演示的是questionID，不是评论id，按理说每个评论是有自己问题和详情页面的
                            + model.getExt("questionId"));

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
