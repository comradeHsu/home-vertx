package service;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.mongo.MongoClient;

@ProxyGen
@VertxGen
public interface HouseService {

    @Fluent
    HouseService findAllHouseByType(int pageSize,int pageNumber,String type,Handler<AsyncResult<JsonArray>> resultHandler);

    @GenIgnore
    static HouseService create(MongoClient mongoClient, Handler<AsyncResult<HouseService>> readyHandle){
        return new HouseServiceImpl(mongoClient, readyHandle);
    }

    @GenIgnore
    static service.rxjava.HouseService createProxy(Vertx vertx, String address) {
        return new service.rxjava.HouseService(new HouseServiceVertxEBProxy(vertx,address));
    }
}