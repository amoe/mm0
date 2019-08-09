package uk.ac.susx.shl.micromacro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jdbi.v3.core.Jdbi;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.susx.shl.micromacro.jdbi.BaseDAO;
import uk.ac.susx.shl.micromacro.jdbi.DatumStringMapper;
import uk.ac.susx.tag.method51.core.data.store2.query.OrderBy;
import uk.ac.susx.tag.method51.core.data.store2.query.Partition;
import uk.ac.susx.tag.method51.core.data.store2.query.Proximity;
import uk.ac.susx.tag.method51.core.data.store2.query.Select;
import uk.ac.susx.tag.method51.core.data.store2.query.SqlQuery;
import uk.ac.susx.tag.method51.core.meta.Key;
import uk.ac.susx.tag.method51.core.meta.filters.DatumFilter;
import uk.ac.susx.tag.method51.core.meta.filters.KeyFilter;
import uk.ac.susx.tag.method51.core.meta.filters.impl.BooleanFilter;
import uk.ac.susx.tag.method51.core.meta.filters.impl.LabelDecisionFilter;
import uk.ac.susx.tag.method51.core.meta.filters.logic.LogicParser;
import uk.ac.susx.tag.method51.core.meta.types.RuntimeType;
import uk.ac.susx.tag.method51.twitter.LabelDecision;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RingQueryPort {
    private static Logger LOG = LoggerFactory.getLogger(RingQueryPort.class);
    private static String TABLE = "sampled2";


    private BaseDAO<String, SqlQuery> getDao() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setDatabaseName("micromacro");
        ds.setUser("micromacro");
        ds.setPassword("xyzzy");
        Jdbi j = Jdbi.create(ds);
        BaseDAO<String, SqlQuery> d = new BaseDAO<>(j, new DatumStringMapper());
        return d;
    }

    public List<String> runSelectQuery(KeyFilter kf) {
        BaseDAO<String, SqlQuery> dao = getDao();
        Map<String, KeyFilter> literals = new HashMap<>();
        literals.put("g", kf);
        LogicParser p = new LogicParser(literals);
        DatumFilter df = p.parse(null, "g");
        SqlQuery query = new Select(TABLE).where(df);

        return dao.list(query);
    }

    public Graph<ConceptNode, DefaultEdge> conceptGraph() {
        Graph<ConceptNode, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
        ConceptNode n1 = new ConceptNode("Pursuit", new LabelDecisionFilter(Key.of("classify/pursuit-chase", RuntimeType.of(LabelDecision.class)), "chase"));
        ConceptNode n2 = new ConceptNode("Apprehend", new BooleanFilter(Key.of("ob.activity/apprehend", RuntimeType.BOOLEAN), "true"));
        g.addVertex(n1);
        g.addVertex(n2);
        g.addEdge(n1, n2);
        return g;
    }



    public void runHardcodedQueries() {
        Key<LabelDecision> arg1 = Key.of("classify/pursuit-chase", RuntimeType.of(LabelDecision.class));
        KeyFilter pursuitKf = new LabelDecisionFilter(arg1, "chase");
        KeyFilter apprehendKf = new BooleanFilter(Key.of("ob.activity/apprehend", RuntimeType.BOOLEAN), "true");
        KeyFilter maritimeKf = new BooleanFilter(Key.of("ob.sphere/maritime", RuntimeType.BOOLEAN), "true");
        KeyFilter oneKf = new BooleanFilter(Key.of("ob.generalkws/one-kw", RuntimeType.BOOLEAN), "true");

        List<String> set1 = runSelectQuery(pursuitKf);
        LOG.info("count was {}", set1.size());
        List<String> set2 = runSelectQuery(apprehendKf);
        LOG.info("count was {}", set2.size());
        List<String> set3 = runSelectQuery(maritimeKf);
        LOG.info("count was {}", set3.size());
        List<String> set4 = runSelectQuery(oneKf);
        LOG.info("count was {}", set4.size());
    }

    public static void main(String[] args) {
        RingQueryPort port = new RingQueryPort();
        port.runQueryFromGraph();   // escape static context
    }

    private void runQueryFromGraph() {
        Graph<ConceptNode, DefaultEdge> graph = conceptGraph();

        List<ConceptNode> roots = graph.vertexSet().stream().filter(v -> graph.incomingEdgesOf(v).size() == 0).collect(Collectors.toList());
        List<ConceptNode> leaves = graph.vertexSet().stream().filter(v -> graph.outgoingEdgesOf(v).size() == 0).collect(Collectors.toList());

        LOG.info("Roots are {}", roots);
        LOG.info("Leaves are {}", leaves);


        ConceptNode theRoot = roots.get(0);
        ConceptNode theLeaf = leaves.get(0);

        DijkstraShortestPath<ConceptNode, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
        ShortestPathAlgorithm.SingleSourcePaths<ConceptNode, DefaultEdge> iPaths = dijkstraAlg.getPaths(theRoot);
        GraphPath<ConceptNode, DefaultEdge> path = iPaths.getPath(theLeaf);

        List<ConceptNode> steps = path.getVertexList();

        for (ConceptNode step: steps) {
            LOG.info("step is {}", step);

            List<String> result = runSelectQuery(step.getFilter());
            LOG.info("{}: {}", step.getLabel(), result.size());
        }
    }
}
