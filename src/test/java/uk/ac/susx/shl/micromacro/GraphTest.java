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
import uk.ac.susx.shl.micromacro.jgrapht.TokenDatumVertexAttributeProvider;
import uk.ac.susx.shl.micromacro.jgrapht.TokenDatumVertexNameProvider;
import uk.ac.susx.tag.method51.core.meta.Key;
import uk.ac.susx.tag.method51.core.meta.filters.impl.BooleanFilter;
import uk.ac.susx.tag.method51.core.meta.filters.impl.LabelDecisionFilter;
import uk.ac.susx.tag.method51.core.meta.types.RuntimeType;
import uk.ac.susx.tag.method51.twitter.LabelDecision;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GraphTest {
    private static final Logger LOG = LoggerFactory.getLogger(GraphTest.class);

    @Test
    public void graphDemo() throws Exception {
        LOG.info("{}", new DemoGraphCreator().getExportedGraph());
    }

}
