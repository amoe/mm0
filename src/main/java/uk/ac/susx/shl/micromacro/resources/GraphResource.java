package uk.ac.susx.shl.micromacro.resources;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.JSONExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.StringWriter;

@Path("/graph")
@Produces("application/json")
public class GraphResource {
    private static final Logger LOG = LoggerFactory.getLogger(GraphResource.class);

    public String getGraphJson() {
        LOG.info("graph demo");

        Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        g.addVertex("Alice");
        g.addVertex("Bob");
        g.addVertex("Carol");
        g.addVertex("Dan");

        g.addEdge("Alice", "Bob");
        g.addEdge("Bob", "Carol");
        g.addEdge("Bob", "Dan");

        // If you don't pass this, it's only going to export the structure.
        JSONExporter exporter = new JSONExporter(new ComponentNameProvider<String>() {
            @Override
            public String getName(String s) {
                return s;
            }
        });

        StringWriter stringWriter = new StringWriter();
        try {
            exporter.exportGraph(g, stringWriter);
        } catch (ExportException e) {
            throw new RuntimeException(e);
        }

        String jsonVersion = stringWriter.toString();
        return jsonVersion;
    }


    @GET
    public Response getGraph() {
        String content = getGraphJson();
        return Response.status(Response.Status.OK).entity(content).build();
    }
}
