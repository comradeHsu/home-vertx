package handler;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;
import service.rxjava.UserService;
import utils.DataUtil;

import java.time.Instant;

public class UserHandler extends BaseHandler{

    private UserService userService;

    public UserHandler(UserService userService){
        this.userService = userService;
    }

    public void getAllUsers(RoutingContext context){
        JsonObject page = context.getBodyAsJson();
        int pageSize = page.getInteger("pageSize",10);
        int pageNumber = page.getInteger("pageNumber",0);
        userService.rxFetchAllUsers(pageSize,pageNumber)
                .zipWith(userService.rxCountAllUsers(),(array,count) -> new JsonObject()
                        .put("data",array).put("totalCount",count))
                .subscribe(rs -> {
                    rs.put("pageSize",pageSize)
                            .put("pageNumber",pageNumber)
                            .put("msg","success");
                    apiResponse(context,200,"data",rs);
                },throwable -> apiFailure(context,throwable));
    }

    public void login(RoutingContext context){
        JsonObject user = context.getBodyAsJson();
        userService.rxFindUser(user.getString("username",null))
                .subscribe(res -> {
                    if(res != null && res.getString("password").equals(user.getString("password"))){
                        user.put("type",res.getValue("type"));
                        Session session = context.session();
                        session.put("user",user);
                        DataUtil.handler(res);
                        apiResponse(context,200,"data",res);
                    } else {
                        apiResponse(context,201,"data","用户名或密码不正确");
                    }
                },throwable -> apiFailure(context,throwable));
    }

    public void deleteUser(RoutingContext context){
        String userId = context.pathParam("userId");
        userService.rxDeleteUserById(userId).map(r -> new JsonObject().put("data",r)).subscribe(rs -> {
            rs.put("msg","success");
            apiResponse(context,200,"data",rs);
        },throwable -> apiFailure(context,throwable));
    }

    public void insertUser(RoutingContext context){
        JsonObject user = context.getBodyAsJson();
        user.put("createDate", Instant.now()).put("isDeleted","0");
        userService.rxInsertUser(user).map(r -> new JsonObject().put("data",r)).subscribe(rs -> {
            rs.put("msg","success");
            apiResponse(context,200,"data",rs);
        },throwable -> apiFailure(context,throwable));
    }
}
