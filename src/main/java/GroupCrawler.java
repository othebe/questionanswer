import clients.GroupClient;
import clients.UserClient;
import common.CouchDBClient;
import models.Group;
import models.MessageThread;

import java.util.List;

import static common.Constants.THREADS_DB_NAME;

public class GroupCrawler {
    public static final String TOKEN = "";

    public static void main(String[] args) throws Exception {
        String authBearer = "Bearer 107-AZbcQGm4WmQG6LlRvaDVKw";
        long userId = 1552476865;

        CouchDBClient threadsDbClient = new CouchDBClient(THREADS_DB_NAME);

        int ndx = 0;
        List<Group> userGroups = UserClient.getUserGroups(userId, authBearer);
        for (Group group : userGroups) {
            ndx++;
            System.out.printf("Crawling group %d... [%d/%d]\n", group.id, ndx, userGroups.size());
            try {
                for (MessageThread thread : GroupClient.getThreads(group.id, authBearer)) {
                    threadsDbClient.saveData(String.valueOf(thread.threadStarter.id), thread);
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }
}
