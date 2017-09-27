package service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.ext.mongo.MongoClient;
import rx.Single;

public class HouseServiceImpl implements HouseService {

    private MongoClient mongoClient;

    HouseServiceImpl(io.vertx.ext.mongo.MongoClient mongoClient, Handler<AsyncResult<HouseService>> readyHandle){
        this.mongoClient = new MongoClient(mongoClient);
        Single.just(this).subscribe(RxHelper.toSubscriber(readyHandle));
    }

    @Override
    public HouseService findAllHouseByType(int pageSize, int pageNumber, String type, Handler<AsyncResult<JsonArray>> resultHandler) {
        return null;
    }
}
