package models;

import clients.dto.BodyDto;
import clients.dto.MessageDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Message {
    public long id;
    public String body;
    public int likesCount;
    public long sender_id;
    public List<User> likedBy;
    public User user;
    public long groupId;

    private Message() {
        this.likedBy = new ArrayList<User>();
    }

    public static Message fromMessageDto(MessageDto messageDto) {
        Message message = new Message();
        message.id = messageDto.id;
        message.body = messageDto.body.parsed;
        message.likesCount = messageDto.likesCount;
        message.sender_id = messageDto.sender_id;
        message.groupId = messageDto.groupId;

        return message;
    }

    public static List<Message> fromMessageDtos(List<MessageDto> messageDtos) {
        List<Message> messages = new ArrayList<Message>();
        for (MessageDto messageDto : messageDtos) {
            messages.add(fromMessageDto(messageDto));
        }

        return messages;
    }

    public static Message fromMap(Map<String, Object> map) {
        Message message = new Message();
        message.id = Long.valueOf(map.get("id").toString());
        message.body = map.get("body").toString();
        message.likesCount = Integer.valueOf(map.get("likesCount").toString());
        message.sender_id = Long.valueOf(map.get("sender_id").toString());

        List<User> likedBy = new ArrayList<User>();
        List<Map<String, Object>> likedByMaps = (List<Map<String, Object>>) map.get("likedBy");
        for (Map<String, Object> likedByMap : likedByMaps) {
            likedBy.add(User.fromMap(likedByMap));
        }
        message.likedBy = likedBy;

        message.user = User.fromMap((Map<String, Object>) map.get("user"));
        message.groupId = Long.valueOf(map.get("groupId").toString());

        return message;
    }
}
