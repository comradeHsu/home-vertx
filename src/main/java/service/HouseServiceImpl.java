package service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.ext.mongo.MongoClient;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class HouseServiceImpl implements HouseService {

    private MongoClient mongoClient;

    private final String dataBase = "hourse";

    HouseServiceImpl(io.vertx.ext.mongo.MongoClient mongoClient, Handler<AsyncResult<HouseService>> readyHandle){
        this.mongoClient = new MongoClient(mongoClient);
        Single.just(this).subscribe(RxHelper.toSubscriber(readyHandle));
    }

    @Override
    public HouseService findAllHouseByType(int pageSize, int pageNumber, String type, Handler<AsyncResult<JsonArray>> resultHandler) {
        JsonObject document = new JsonObject().put("isDeleted","0").put("type",type);
        FindOptions findOptions = new FindOptions().setSkip(pageNumber*pageSize)
                .setLimit(pageSize).setSort(new JsonObject().put("createDate", 1));
        mongoClient.rxFindWithOptions(dataBase,document,findOptions).flatMapObservable(res -> Observable.from(res))
                .collect(JsonArray::new,JsonArray::add)
                .subscribeOn(Schedulers.io())
                .subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }
}
