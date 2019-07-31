package uk.ac.susx.shl.micromacro;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.susx.shl.micromacro.jdbi.BaseDAO;
import uk.ac.susx.shl.micromacro.jdbi.DatumStringMapper;
import uk.ac.susx.tag.method51.core.data.store2.query.Select;
import uk.ac.susx.tag.method51.core.data.store2.query.SqlQuery;
import uk.ac.susx.tag.method51.core.data.store2.query.Update;
import uk.ac.susx.tag.method51.core.meta.Key;
import uk.ac.susx.tag.method51.core.meta.filters.DatumFilter;
import uk.ac.susx.tag.method51.core.meta.filters.KeyFilter;
import uk.ac.susx.tag.method51.core.meta.filters.impl.LabelDecisionFilter;
import uk.ac.susx.tag.method51.core.meta.filters.logic.LogicParser;
import uk.ac.susx.tag.method51.core.meta.types.RuntimeType;
import uk.ac.susx.tag.method51.twitter.LabelDecision;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaygroundTest {
    private static Logger LOG = LoggerFactory.getLogger(PlaygroundTest.class);

    @Test
    public void runSelectQuery() throws Exception {
        LOG.info("sanity check");
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setDatabaseName("micromacro");
        ds.setUser("micromacro");
        ds.setPassword("xyzzy");
        Jdbi j = Jdbi.create(ds);
        Key<LabelDecision> arg1 = Key.of("ob.place/classifier-1750-1825-pubs", RuntimeType.of(LabelDecision.class));
        KeyFilter kf = new LabelDecisionFilter(arg1, "other");
        Map<String, KeyFilter> literals = new HashMap<>();
        literals.put("a", kf);
        LogicParser p = new LogicParser(literals);
        DatumFilter df = p.parse(null, "a");
        SqlQuery query = new Select("sampled").where(df);
        BaseDAO<String, SqlQuery> d = new BaseDAO<>(j, new DatumStringMapper());
        List<String> results = d.list(query);
        LOG.info("results are {}", results.size());
    }
}
