package uk.ac.susx.shl.micromacro.jgrapht;

import org.jgrapht.io.ComponentNameProvider;
import uk.ac.susx.shl.micromacro.TokenDatum;

public class TokenDatumVertexNameProvider implements ComponentNameProvider<TokenDatum> {
    @Override
    public String getName(TokenDatum tokenDatum) {
        return Long.toString(tokenDatum.getId());
    }
}
