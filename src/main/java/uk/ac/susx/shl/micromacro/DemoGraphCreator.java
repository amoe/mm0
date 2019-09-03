package uk.ac.susx.shl.micromacro;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.EmptyComponentAttributeProvider;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.IntegerComponentNameProvider;
import org.jgrapht.io.JSONExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.susx.shl.micromacro.jgrapht.TokenDatumVertexAttributeProvider;
import uk.ac.susx.shl.micromacro.jgrapht.TokenDatumVertexNameProvider;

import java.io.StringWriter;
import java.util.Optional;

public class DemoGraphCreator {
    private static final Logger LOG = LoggerFactory.getLogger(DemoGraphCreator.class);

    public String getExportedGraph() {
        LOG.info("graph demo");

        Graph<TokenDatum, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        TokenDatum d1 = new TokenDatum("Alice", 0, "Token", Optional.empty());
        TokenDatum d2 = new TokenDatum("Bob", 0, "Token", Optional.of(1L));
        TokenDatum d3 = new TokenDatum("Carol", 0, "Token", Optional.of(2L));
        TokenDatum d4 = new TokenDatum("Dan", 0, "Token", Optional.of(3L));

        g.addVertex(d1);
        g.addVertex(d2);
        g.addVertex(d3);
        g.addVertex(d4);

        g.addEdge(d1, d2);
        g.addEdge(d2, d3);
        g.addEdge(d2, d4);

        LOG.info("graph is {}", g);

        // If you don't pass this, it's only going to export the structure.
        JSONExporter<TokenDatum, DefaultEdge> foo = new JSONExporter<>(
            new TokenDatumVertexNameProvider(),
            new TokenDatumVertexAttributeProvider(),
            // these fakes just number edges as default and don't output any edge stuff.
            new IntegerComponentNameProvider<DefaultEdge>(),
            new EmptyComponentAttributeProvider<DefaultEdge>()
        );

        return exportAsJson(foo, g);
    }

    private String exportAsJson(JSONExporter<TokenDatum, DefaultEdge> exporter, Graph<TokenDatum, DefaultEdge> g) {
        try {
            StringWriter stringWriter = new StringWriter();
            exporter.exportGraph(g, stringWriter);
            String jsonVersion = stringWriter.toString();
            return jsonVersion;
        } catch (ExportException e) {
            throw new RuntimeException(e);
        }
    }
}
