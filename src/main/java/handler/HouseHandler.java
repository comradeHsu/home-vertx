package handler;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import service.rxjava.HouseService;

import java.util.Date;
import java.util.OptionalInt;

public class HouseHandler extends BaseHandler{

    private HouseService houseService;

    public HouseHandler(HouseService houseService) {
        this.houseService = houseService;
    }

    public void findAllHouseByType(RoutingContext context){
//        int pageSize = page.getInteger("pageSize",10);
//        int pageNumber = page.getInteger("pageNumber",0);
        int pageSize = OptionalInt.of(Integer.valueOf(context.request().getParam("pageSize"))).orElse(10);
        int pageNumber = OptionalInt.of(Integer.valueOf(context.request().getParam("pageNumber"))).orElse(0);
        String type = context.pathParam("type");
        houseService.rxFindAllHouseByType(pageSize,pageNumber,type)
                .zipWith(houseService.rxCountByType(type),(array,count) -> new JsonObject()
                        .put("data",array).put("totalCount",count))
                .subscribe(rs -> {
                    rs.put("pageSize",pageSize)
                            .put("pageNumber",pageNumber)
                            .put("msg","success");
                    apiResponse(context,200,"data",rs);
                },throwable -> apiFailure(context,throwable));
    }

    public void findAllHouseByUserAndType(RoutingContext context){
        JsonObject page = context.getBodyAsJson();
        int pageSize = page.getInteger("pageSize",10);
        int pageNumber = page.getInteger("pageNumber",0);
        String userId = context.pathParam("userId");
        String type = context.pathParam("type");
        houseService.rxFindAllHouseByUserAndType(pageSize,pageNumber,userId,type)
                .zipWith(houseService.rxCountByUserAndType(userId,type),(array,count) -> new JsonObject()
                        .put("data",array).put("totalCount",count))
                .subscribe(rs -> {
                    rs.put("pageSize",pageSize)
                            .put("pageNumber",pageNumber)
                            .put("msg","success");
                    apiResponse(context,200,"data",rs);
                },throwable -> apiFailure(context,throwable));
    }

    public void insertHouse(RoutingContext context){
        JsonObject house = context.getBodyAsJson();
        String type = context.request().getParam("type");
        house.put("createDate",new Date()).put("type",type);
        houseService.rxInsertHouse(house).map(r -> new JsonObject().put("data",r)).subscribe(rs -> {
            rs.put("msg","success");
            apiResponse(context,200,"data",rs);
        },throwable -> apiFailure(context,throwable));
    }
}
