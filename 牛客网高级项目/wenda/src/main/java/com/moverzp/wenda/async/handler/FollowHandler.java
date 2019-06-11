package com.moverzp.wenda.async.handler;

import com.moverzp.wenda.async.EventHandler;
import com.moverzp.wenda.async.EventModel;
import com.moverzp.wenda.async.EventType;
import com.moverzp.wenda.model.EntityType;
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
public class FollowHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;


    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(model.getActorId());

        //根据关注的不同实体类型，发送不同的站内信
        if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("用户" + user.getName()
                + "关注了你的问题，http://127.0.0.1:8080/question/"
                + model.getEntityId());
        } else if (model.getEntityType() == EntityType.ENTITY_USER) {
            message.setContent("用户" + user.getName()
                + "关注了你，http://127.0.0.1:8080/user/"
                + model.getActorId());
        }
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
