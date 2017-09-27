package service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.List;

public class UserServiceImpl implements UserService{

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final MongoClient mongoClient;

    private final String dataBase = "user";

    UserServiceImpl(io.vertx.ext.mongo.MongoClient client, Handler<AsyncResult<UserService>> readyHandle) {
        this.mongoClient = new MongoClient(client);
        Single.just(this).subscribe(RxHelper.toSubscriber(readyHandle));
    }



    @Override
    public UserService fetchAllUsers(int pageSize,int pageNumber, Handler<AsyncResult<JsonArray>> resultHandler) {
        JsonObject document = new JsonObject().put("isDeleted","0");
        FindOptions findOptions = new FindOptions().setSkip(pageNumber*pageSize)
                .setLimit(pageSize).setSort(new JsonObject().put("createDate", 1));
        mongoClient.rxFindWithOptions(dataBase,document,findOptions)
        .flatMapObservable(res -> Observable.from(res))
        .collect(JsonArray::new,JsonArray::add)
        .subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }

    @Override
    public UserService countAllUsers(Handler<AsyncResult<Long>> resultHandler) {
        JsonObject document = new JsonObject().put("isDeleted","0");
        mongoClient.rxCount(dataBase,document).subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }

    @Override
    public UserService insertUser(JsonObject user, Handler<AsyncResult<String>> resultHandler) {
        mongoClient.rxSave(dataBase,user).subscribeOn(Schedulers.io())
                .subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }

    @Override
    public UserService findUser(String username, Handler<AsyncResult<JsonObject>> resultHandler) {
        mongoClient.rxFindOne(dataBase,new JsonObject().put("username",username),null)
                .subscribeOn(Schedulers.io())
                .subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }
}
