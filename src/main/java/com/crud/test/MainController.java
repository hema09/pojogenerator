package com.crud.test;

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: hbhatia
 * Date: 6/3/15
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Named
@Path("/")
public class MainController {

    @POST
    //@Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/getclass")
    public Response getClass(String jsonData, @QueryParam("classname") String className) throws IOException, URISyntaxException {
        String s = GeneratePojo.getClass(jsonData, className);
        /*return Response.ok(s).header("Content-Disposition", "attachment; filename=" + className + ".java").build();*/
        return Response.ok(s).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/info")
    public Response getInfo() {
        String result = null;
        System.out.println(result);
        return Response.ok(result).build();
    }
}
