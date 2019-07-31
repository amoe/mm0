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

import javax.sql.DataSource;
import java.util.List;

public class PlaygroundTest {
    private static Logger LOG = LoggerFactory.getLogger(PlaygroundTest.class);

    @Test
    public void foo() throws Exception {
        LOG.info("sanity check");

        SqlQuery query = new Select("sampled").limit(10);
        PGSimpleDataSource ds = new PGSimpleDataSource();

        ds.setDatabaseName("micromacro");
        ds.setUser("micromacro");
        ds.setPassword("xyzzy");

        Jdbi j = Jdbi.create(ds);

        BaseDAO<String, SqlQuery> d = new BaseDAO<>(j, new DatumStringMapper());
        List<String> results = d.list(query);
        LOG.info("results are {}", results.size());
    }
}
