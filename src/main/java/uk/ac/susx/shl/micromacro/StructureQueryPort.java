package uk.ac.susx.shl.micromacro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jdbi.v3.core.Jdbi;
import org.junit.Test;
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
import uk.ac.susx.tag.method51.core.meta.filters.impl.LabelDecisionFilter;
import uk.ac.susx.tag.method51.core.meta.filters.logic.LogicParser;
import uk.ac.susx.tag.method51.core.meta.types.RuntimeType;
import uk.ac.susx.tag.method51.twitter.LabelDecision;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureQueryPort {
    private static Logger LOG = LoggerFactory.getLogger(StructureQueryPort.class);

    private SqlQuery makeProximityQuery() {
        String table = "sampled";
        String tableWhere = null;
        String proximity = "e";
        String target = "c";
        int p = 3;
        int innerLimit = 0;
        int innerOffset = 0;
        int outerLimit = 0;

        Key partitionKey = Key.of("ob/trialAccount-id", RuntimeType.STRING);
        OrderBy orderBy = new OrderBy();
        Partition.Function function = Partition.Function.ROW_NUMBER;
        Key argKey = null;


        Partition partition = new Partition(function, argKey, partitionKey, orderBy);

        boolean desc = true;
        boolean numeric = false;
        Key key = Key.of("ob/sentence-id", RuntimeType.STRING);

        OrderBy orderBy2 = new OrderBy().withClause(key, desc, numeric);

        Partition scope = new Partition(function, argKey, partitionKey, orderBy2);

        Map<String, KeyFilter> literals = new HashMap<>();

        Key<LabelDecision> arg1 = Key.of("ob.concept/light-illuminated", RuntimeType.of(LabelDecision.class));
        KeyFilter kf = new LabelDecisionFilter(arg1, "illuminated");
        literals.put("e", kf);

        Key<LabelDecision> arg2 = Key.of("ob.behaviour/observed-c", RuntimeType.of(LabelDecision.class));
        KeyFilter kf2 = new LabelDecisionFilter(arg2, "observed");
        literals.put("c", kf2);

        LogicParser parser = new LogicParser(literals);

        DatumFilter tableFilter = null;
        DatumFilter targetFilter = parser.parse(null, target);
        DatumFilter proximityFilter = parser.parse(null, proximity);

        Proximity proximityQuery = new Proximity(
            table, tableFilter, targetFilter, proximityFilter, scope, partition, p, innerLimit, innerOffset, outerLimit
        );

        // MODIFIES STATE!
        proximityQuery.literals(literals);

        return proximityQuery;
    }

    private BaseDAO<String, SqlQuery> getDao() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setDatabaseName("micromacro");
        ds.setUser("micromacro");
        ds.setPassword("xyzzy");
        Jdbi j = Jdbi.create(ds);
        SqlQuery query = makeProximityQuery();
        BaseDAO<String, SqlQuery> d = new BaseDAO<>(j, new DatumStringMapper());

        return d;
    }

    public void runProximityQuery() {
        BaseDAO<String, SqlQuery> dao = getDao();
        SqlQuery sqlQuery = makeProximityQuery();

        List<String> results = dao.list(sqlQuery);
        LOG.info("results are {}", results.size());
        JsonParser parser = new JsonParser();

        for (String s : results) {
            JsonElement element = parser.parse(s);
            JsonObject object = element.getAsJsonObject();
            boolean isProximityMatch = object.get("__proximity").getAsBoolean();
            boolean isTargetMatch = object.get("__target").getAsBoolean();

            LOG.info("proximity: {}, target: {}", isProximityMatch, isTargetMatch);
        }
    }

    public void runSelectQuery() {
        BaseDAO<String, SqlQuery> dao = getDao();

        Key<LabelDecision> arg1 = Key.of("classify/confidenceexpfiltered-confidenceexpressed", RuntimeType.of(LabelDecision.class));
        KeyFilter kf = new LabelDecisionFilter(arg1, "confidenceexpressed");

        Map<String, KeyFilter> literals = new HashMap<>();
        literals.put("g", kf);
        LogicParser p = new LogicParser(literals);
        DatumFilter df = p.parse(null, "g");
        SqlQuery query = new Select("sampled").where(df);

        LOG.info("results are {}", dao.stream(query).count());
    }

    public static void main(String[] args) {
        StructureQueryPort port = new StructureQueryPort();
        port.runProximityQuery();
        port.runSelectQuery();
    }
}
