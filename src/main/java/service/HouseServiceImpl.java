package service;

import com.mongodb.async.client.FindIterable;
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
import utils.DataUtil;


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
                .setLimit(pageSize).setSort(new JsonObject().put("createDate", -1));
        mongoClient.rxFindWithOptions(dataBase,document,findOptions).flatMapObservable(res -> Observable.from(res))
                .map(data -> DataUtil.noVoidhandler(data))
                .collect(JsonArray::new,JsonArray::add)
                .subscribeOn(Schedulers.io())
                .subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }

    @Override
    public HouseService countByType(String type, Handler<AsyncResult<Long>> resultHandler) {
        JsonObject document = new JsonObject().put("isDeleted","0").put("type",type);
        mongoClient.rxCount(dataBase,document).subscribeOn(Schedulers.io())
                .subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }

    /**
     * 使用了mongo的or查询
     * @param pageSize
     * @param pageNumber
     * @param userId
     * @param type
     * @param resultHandler
     * @return
     */
    @Override
    public HouseService findAllHouseByUserAndType(int pageSize, int pageNumber, String userId, String type,
                                                  Handler<AsyncResult<JsonArray>> resultHandler){
        //构建符合mongo规范的or查询 jsonObjcet对象
        JsonObject document = new JsonObject().put("isDeleted","0").put("type",type)
                .put("$or",new JsonArray().add(new JsonObject().put("userId",userId)).add(new JsonObject().put("isPublic","1")));

        FindOptions findOptions = new FindOptions().setSkip(pageNumber*pageSize)
                .setLimit(pageSize).setSort(new JsonObject().put("createDate", 1));
        mongoClient.rxFindWithOptions(dataBase,document,findOptions).flatMapObservable(res -> Observable.from(res))
                .map(data -> DataUtil.noVoidhandler(data))
                .collect(JsonArray::new,JsonArray::add)
                .subscribeOn(Schedulers.io())
                .subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }

    @Override
    public HouseService countByUserAndType(String userId, String type, Handler<AsyncResult<Long>> resultHandler) {
        JsonObject document = new JsonObject().put("isDeleted","0").put("type",type)
                .put("$or",new JsonArray().add(new JsonObject().put("userId",userId))
                        .add(new JsonObject().put("isPublic","1")));
        mongoClient.rxCount(dataBase,document).subscribeOn(Schedulers.io())
                .subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }

    @Override
    public HouseService insertHouse(JsonObject house, Handler<AsyncResult<String>> resultHandler) {
        JsonObject document = new JsonObject().put("isDeleted","0");
        mongoClient.rxSave(dataBase,document).subscribeOn(Schedulers.io())
                .subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }

    @Override
    public HouseService update(JsonObject house, Handler<AsyncResult<JsonObject>> resultHandler) {
        JsonObject query = new JsonObject().put("_id",house.getValue("id"));
        mongoClient.rxFindOneAndUpdate(dataBase,query,house).subscribeOn(Schedulers.io())
                .subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }

}
