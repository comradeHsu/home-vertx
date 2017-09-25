package service;

import com.mongodb.client.model.Sorts;
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
import rx.Single;
import util.Page;

import java.util.List;

public class UserServiceImpl implements UserService{

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final MongoClient mongoClient;

    private final String dataBase = "hourse";

    UserServiceImpl(io.vertx.ext.mongo.MongoClient client, Handler<AsyncResult<UserService>> readyHandle) {
        this.mongoClient = new MongoClient(client);
    }

//    private Single<>


    @Override
    public UserService fetchAllUsers(Page page, Handler<AsyncResult<JsonArray>> resultHandler) {
        JsonObject document = new JsonObject().put("isDeleted","0");
        FindOptions findOptions = new FindOptions().setSkip(page.getPageNumber()*page.getPageSize())
                .setLimit(page.getPageSize()).setSort(new JsonObject().put("createDate", 1));
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
}
