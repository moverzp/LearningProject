package com.moverzp.wenda.controller;

import com.moverzp.wenda.async.EventModel;
import com.moverzp.wenda.async.EventProducer;
import com.moverzp.wenda.async.EventType;
import com.moverzp.wenda.model.Comment;
import com.moverzp.wenda.model.EntityType;
import com.moverzp.wenda.model.HostHolder;
import com.moverzp.wenda.service.CommentService;
import com.moverzp.wenda.service.LikeService;
import com.moverzp.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        Comment comment = commentService.getCommentById(commentId);
        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);

        //发送事件
        eventProducer.fireEvent(new EventModel(EventType.LIKE) //新建事件
                .setActorId(hostHolder.getUser().getId()).setEntityId(commentId) //设置事件发起者id，事件处理的实体id
                .setEntityType(EntityType.ENTITY_COMMENT).setEntityOwnerId(comment.getUserId()) //设置事件处理的实体类型，设置时间的面向用户id
                .setExt("questionId", String.valueOf(comment.getEntityId())));//设置question的id

        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
    @ResponseBody
    public  String dislike(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
