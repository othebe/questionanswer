import clients.GroupClient;
import clients.UserClient;
import common.CouchDBClient;
import models.Group;
import models.Message;
import models.MessageThread;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static common.Constants.QUESTIONS_DB_NAME;
import static common.Constants.THREADS_DB_NAME;

public class QuestionsSelector {
    public static void main(String[] args) throws Exception {
        String authBearer = "Bearer 107-AZbcQGm4WmQG6LlRvaDVKw";
        long userId = 1552476865;

        CouchDBClient threadsDBClient = new CouchDBClient(THREADS_DB_NAME);
        CouchDBClient questionsDBClient = new CouchDBClient(QUESTIONS_DB_NAME);

        for (String threadId : threadsDBClient.db.getAllDocIds()) {
            MessageThread thread = MessageThread.fromMap(threadsDBClient.readData(threadId, Map.class));
            if (isQuestion(thread)) {
                questionsDBClient.saveData(String.valueOf(thread.threadStarter.id), thread);
            }
        }
    }

    private static boolean isQuestion(MessageThread messageThread) {
        String body = messageThread.threadStarter.body;
        boolean isQuestion = false;
        int startNdx = 0;

        do {
            int newNdx = body.indexOf("?", startNdx + 1);
            if (newNdx >= 0) {
                String substring = body.substring(startNdx, newNdx + 1);
                isQuestion = isQuestion || !substring.contains("http");
            }
            startNdx = newNdx;
        } while (startNdx != -1);

        return isQuestion;
    }
}
