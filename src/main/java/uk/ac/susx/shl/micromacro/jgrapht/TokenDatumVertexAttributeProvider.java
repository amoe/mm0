package uk.ac.susx.shl.micromacro.jgrapht;

import org.jgrapht.io.Attribute;
import org.jgrapht.io.ComponentAttributeProvider;
import org.jgrapht.io.DefaultAttribute;
import uk.ac.susx.shl.micromacro.TokenDatum;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TokenDatumVertexAttributeProvider implements ComponentAttributeProvider<TokenDatum> {
    @Override
    public Map<String, Attribute> getComponentAttributes(TokenDatum tokenDatum) {
        Map<String, Attribute> result = new HashMap<>();

        Attribute strengthAttribute = tokenDatum
            .getStrength()
            .map(DefaultAttribute::createAttribute)
            .orElse(DefaultAttribute.NULL);

        result.put("strength", strengthAttribute);
        result.put("content", DefaultAttribute.createAttribute(tokenDatum.getContent()));
        result.put("label", DefaultAttribute.createAttribute(tokenDatum.getLabel()));

        return result;
    }
}
