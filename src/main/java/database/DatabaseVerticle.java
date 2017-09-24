package database;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.mongo.MongoClient;

public class DatabaseVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        MongoClient mongoClient = MongoClient.createShared(vertx,new JsonObject());
    }
}
