package service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Single;

public class UserServiceImpl implements UserService{

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final MongoClient mongoClient;

    UserServiceImpl(io.vertx.ext.mongo.MongoClient client, Handler<AsyncResult<UserService>> readyHandle) {
        this.mongoClient = new MongoClient(client);
    }

//    private Single<>


    @Override
    public UserService fetchAllUsers(Handler<AsyncResult<JsonArray>> resultHandler) {
        JsonObject document = new JsonObject().put("isDeleted","0");
        mongoClient.rxFind("hourse",document);
        return null;
    }
}
