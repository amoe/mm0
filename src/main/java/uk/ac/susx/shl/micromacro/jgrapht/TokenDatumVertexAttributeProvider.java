package uk.ac.susx.shl.micromacro.jgrapht;

import org.jgrapht.io.Attribute;
import org.jgrapht.io.ComponentAttributeProvider;
import org.jgrapht.io.DefaultAttribute;
import uk.ac.susx.shl.micromacro.TokenDatum;

import java.util.HashMap;
import java.util.Map;

public class TokenDatumVertexAttributeProvider implements ComponentAttributeProvider<TokenDatum> {
    @Override
    public Map<String, Attribute> getComponentAttributes(TokenDatum tokenDatum) {
        Map<String, Attribute> result = new HashMap<>();
        result.put("x", DefaultAttribute.createAttribute(true));
        return result;
    }
}
