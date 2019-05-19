package com.moverzp.wenda.dao;

import com.moverzp.wenda.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, conversation_id, created_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{fromId},#{toId},#{content},#{hasRead},#{conversationId},#{createdDate})"})
    int addMessage(Message message);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);



    @Select({"select t1.cnt as id, t3.* ", "from ",
             "(select conversation_id, count(*) as cnt from", TABLE_NAME, "group by conversation_id) t1",
             "JOIN (select conversation_id, MAX(created_date) as max_date from", TABLE_NAME, "GROUP BY conversation_id) t2 ",
                "ON t1.conversation_id = t2.conversation_id",
             "JOIN (select", INSERT_FIELDS, "from", TABLE_NAME, "where from_id=#{userId} OR to_id=#{userId}) t3 ",
                "ON t1.conversation_id = t3.conversation_id AND t2.max_date = t3.created_date",
             "ORDER BY created_date DESC LIMIT #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    @Select({"select count(id) from ", TABLE_NAME, "where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId") int userId,
                                  @Param("conversationId") String conversationId);

    @Update({"update", TABLE_NAME, "set has_read=1 where conversation_id=#{conversationId} and to_id=#{toId}"})
    void setConversationReadState(@Param("conversationId") String conversationId,
                                  @Param("toId") int toId);
}
