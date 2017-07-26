package models;

import clients.dto.FeedDto;
import clients.dto.MessageDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageThread {
    public Message threadStarter;
    public List<Message> replies;
    public long groupId;

    public static List<MessageThread> fromGroupFeedDto(FeedDto feedDto) {
        List<MessageThread> messageThreads = new ArrayList<MessageThread>();
        for (MessageDto messageDto : feedDto.messages) {
            MessageThread messageThread = new MessageThread();
            messageThread.threadStarter = Message.fromMessageDto(messageDto);
            messageThreads.add(messageThread);
        }

        return messageThreads;
    }

    public static MessageThread fromMap(Map<String, Object> map) {
        MessageThread messageThread = new MessageThread();
        messageThread.threadStarter = Message.fromMap((Map<String, Object>) map.get("threadStarter"));

        List<Message> replies = new ArrayList<Message>();
        List<Map<String, Object>> replyMaps = (List<Map<String, Object>>) map.get("replies");
        for (Map<String, Object> replyMap : replyMaps) {
            replies.add(Message.fromMap(replyMap));
        }
        messageThread.replies = replies;

        return messageThread;
    }
}
