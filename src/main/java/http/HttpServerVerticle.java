package http;

import handler.HouseHandler;
import handler.UserHandler;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.SessionHandler;
import io.vertx.rxjava.ext.web.sstore.LocalSessionStore;
import io.vertx.rxjava.ext.web.sstore.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.rxjava.HouseService;
import service.rxjava.UserService;
import utils.DataUtil;

public class HttpServerVerticle extends AbstractVerticle {

    public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
    public static final String CONFIG_USERDB_QUEUE = "userdb.queue";
    public static final String CONFIG_HOUSEDB_QUEUE = "housedb.queue";

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    private UserService userService;

    private HouseService houseService;

    private UserHandler userHandler;

    private HouseHandler houseHandler;


    public void start(Future<Void> startFuture) throws Exception {
        String userDbQueue = config().getString(CONFIG_USERDB_QUEUE, "userdb.queue");
        userService = service.UserService.createProxy(vertx.getDelegate(),userDbQueue);
        userHandler = new UserHandler(userService);

        String houseDbQueue = config().getString(CONFIG_HOUSEDB_QUEUE,"housedb.queue");
        houseService = service.HouseService.createProxy(vertx.getDelegate(),houseDbQueue);
        houseHandler = new HouseHandler(houseService);

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        SessionStore store = LocalSessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(store);
        router.route().handler(BodyHandler.create());
        router.route().handler(sessionHandler);
        router.post("/api/accounts").handler(userHandler::getAllUsers);
        router.post("/api/login").handler(userHandler::login);
        router.put("/api/update").handler(houseHandler::update);

        //restful url 这样声名
        router.route(HttpMethod.POST,"/api/:userId/hourses/:type").handler(houseHandler::findAllHouseByUserAndType);
        router.route(HttpMethod.GET,"/api/front/hourses/:type").handler(houseHandler::findAllHouseByType);

        int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
        server
                .requestHandler(router::accept)
                .rxListen(portNumber)
                .subscribe(s -> {
                    LOGGER.info("HTTP server running on port " + portNumber);
                    startFuture.complete();
                }, t -> {
                    LOGGER.error("Could not start a HTTP server", t);
                    startFuture.fail(t);
                });
    }



}
