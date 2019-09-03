package uk.ac.susx.shl.micromacro.resources;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.JSONExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.susx.shl.micromacro.DemoGraphCreator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.StringWriter;

@Path("/graph")
@Produces("application/json")
public class GraphResource {
    private static final Logger LOG = LoggerFactory.getLogger(GraphResource.class);

    @GET
    public Response getGraph() {
        DemoGraphCreator demoGraphCreator = new DemoGraphCreator();
        String content = demoGraphCreator.getExportedGraph();
        return Response.status(Response.Status.OK).entity(content).build();
    }
}
