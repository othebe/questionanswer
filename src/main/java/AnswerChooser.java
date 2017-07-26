import clients.GroupClient;
import clients.UserClient;
import common.CouchDBClient;
import models.Group;
import models.Message;
import models.MessageThread;

import java.util.*;

import static common.Constants.ANSWERS_DB_NAME;
import static common.Constants.QUESTIONS_DB_NAME;

public class AnswerChooser {
    public static void main(String[] args) throws Exception {
        String authBearer = "Bearer 107-AZbcQGm4WmQG6LlRvaDVKw";
        long userId = 1552476865;

        Scanner scanner = new Scanner(System.in);

        CouchDBClient questionsCouchDBClient = new CouchDBClient(QUESTIONS_DB_NAME);
        CouchDBClient answersCouchDBClient = new CouchDBClient(ANSWERS_DB_NAME);

        List<String> threadIds = questionsCouchDBClient.db.getAllDocIds();

        long seed = System.nanoTime();
        Collections.shuffle(threadIds, new Random(seed));

        for (String threadId : threadIds) {
            if (!answersCouchDBClient.db.contains(threadId)) {
                MessageThread thread = MessageThread.fromMap(questionsCouchDBClient.readData(threadId, Map.class));

                System.out.println(thread.threadStarter.body);
                System.out.println("-------------------------------");

                int replyNdx = 0;
                for (Message message : thread.replies) {
                    System.out.printf("[%d] %s\n\n", replyNdx, message.body);
                    replyNdx++;
                }

                String answers = scanner.next();
                recordAnswers(thread, answers, answersCouchDBClient);
            }
        }
    }

    private static void recordAnswers(MessageThread thread, String answers, CouchDBClient couchDBClient) {
        if (answers.trim().compareTo("x") == 0) {
            return;
        }

        String[] answerArray = answers.trim().split(",");
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("thread", thread);
        data.put("answers", answerArray);

        couchDBClient.saveData(String.valueOf(thread.threadStarter.id), data);
    }
}
