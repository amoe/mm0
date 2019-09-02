package uk.ac.susx.shl.micromacro;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.ComponentAttributeProvider;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DefaultAttribute;
import org.jgrapht.io.EmptyComponentAttributeProvider;
import org.jgrapht.io.IntegerComponentNameProvider;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphTest {
    private static final Logger LOG = LoggerFactory.getLogger(GraphTest.class);

    @Test
    public void graphDemo() throws Exception {
        LOG.info("graph demo");

        Graph<TokenDatum, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        TokenDatum d1 = new TokenDatum("Alice", 0, null, null);
        TokenDatum d2 = new TokenDatum("Bob", 0, null, null);
        TokenDatum d3 = new TokenDatum("Carol", 0, null, null);
        TokenDatum d4 = new TokenDatum("Dan", 0, null, null);

        g.addVertex(d1);
        g.addVertex(d2);
        g.addVertex(d3);
        g.addVertex(d4);

        g.addEdge(d1, d2);
        g.addEdge(d2, d3);
        g.addEdge(d2, d4);

        LOG.info("graph is {}", g);

        // If you don't pass this, it's only going to export the structure.
        JSONExporter foo = new JSONExporter(new ComponentNameProvider<TokenDatum>() {
            @Override
            public String getName(TokenDatum d) {
                return Long.toString(d.getId());
            }
        }, new ComponentAttributeProvider<TokenDatum>() {
            @Override
            public Map<String, Attribute> getComponentAttributes(TokenDatum d) {
                Map<String, Attribute> result = new HashMap<>();

                // fill in node attributes here.

                result.put("x", DefaultAttribute.createAttribute(true));
                return result;
            }
        },
            // these fakes just number edges as default and don't output any edge stuff.
            new IntegerComponentNameProvider(),
            new EmptyComponentAttributeProvider());

        StringWriter stringWriter = new StringWriter();

        foo.exportGraph(g, stringWriter);

        String jsonVersion = stringWriter.toString();

        LOG.info("exported to {}", jsonVersion);
    }

}
