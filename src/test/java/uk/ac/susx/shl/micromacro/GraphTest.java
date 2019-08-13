package uk.ac.susx.shl.micromacro;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.JSONExporter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.susx.tag.method51.core.meta.Key;
import uk.ac.susx.tag.method51.core.meta.filters.impl.BooleanFilter;
import uk.ac.susx.tag.method51.core.meta.filters.impl.LabelDecisionFilter;
import uk.ac.susx.tag.method51.core.meta.types.RuntimeType;
import uk.ac.susx.tag.method51.twitter.LabelDecision;

import java.io.StringWriter;
import java.util.List;

public class GraphTest {
    private static final Logger LOG = LoggerFactory.getLogger(GraphTest.class);

    @Test
    public void graphDemo() throws Exception {
        LOG.info("graph demo");

        Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        g.addVertex("Alice");
        g.addVertex("Bob");
        g.addVertex("Carol");
        g.addVertex("Dan");

        g.addEdge("Alice", "Bob");
        g.addEdge("Bob", "Carol");
        g.addEdge("Bob", "Dan");

        LOG.info("graph is {}", g);



        System.out.println("Shortest path from i to c:");
        DijkstraShortestPath<String, DefaultEdge> dijkstraAlg =
            new DijkstraShortestPath<>(g);
        ShortestPathAlgorithm.SingleSourcePaths<String, DefaultEdge> iPaths = dijkstraAlg.getPaths("Alice");
        GraphPath<String, DefaultEdge> path = iPaths.getPath("Bob");

        LOG.info("path is {}", path);

        List<String> vertexList = path.getVertexList();

        LOG.info("iterating over path");

        for (String s: vertexList) {
            LOG.info("I will visit s");
        }

        // If you don't pass this, it's only going to export the structure.
        JSONExporter foo = new JSONExporter(new ComponentNameProvider<String>() {
            @Override
            public String getName(String s) {
                return s;
            }
        });

        StringWriter stringWriter = new StringWriter();

        foo.exportGraph(g, stringWriter);

        String jsonVersion = stringWriter.toString();

        LOG.info("exported to {}", jsonVersion);
    }

}
