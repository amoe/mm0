package uk.ac.susx.shl.micromacro.resources;


import uk.ac.susx.shl.micromacro.api.ProxyRep;
import uk.ac.susx.shl.micromacro.api.WorkspaceRep;
import uk.ac.susx.shl.micromacro.core.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Path("workspace")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WorkspaceResource {

    private final Workspaces workspaces;
    private final QueryFactory queryFactory;
    private final WorkspaceFactory workspaceFactory;

    public WorkspaceResource(Workspaces workspaces, WorkspaceFactory workspaceFactory, QueryFactory queryFactory) {
        this.workspaces = workspaces;
        this.workspaceFactory = workspaceFactory;
        this.queryFactory = queryFactory;
    }

    @POST
    @Path("addQuery")
    public Response addQuery(@QueryParam("workspace") String workspaceName,
                             @QueryParam("queryName") String queryName,
                             ProxyRep query) throws SQLException {

        Workspace workspace = workspaces.get(workspaceName);

        workspace.add(queryName, queryFactory.proxy(query));

        return Response.status(Response.Status.OK).entity(
                workspace
        ).build();
    }



    @GET
    @Path("load")
    public Response load(@QueryParam("name") String name) {

        return Response.status(Response.Status.OK).entity(
                workspaceFactory.rep(name)
        ).build();
    }



}