package uk.ac.susx.shl.micromacro;

import com.google.common.base.Preconditions;

import java.util.Objects;
import java.util.Optional;

public class TokenDatum {
    // Really unclear that int is the correct numeric type here.
    private String content;
    private long id;
    private String label;
    private Optional<Long> strength;

    public String getContent() {
        return content;
    }

    public long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Optional<Long> getStrength() {
        return strength;
    }

    public TokenDatum(String content, long id, String label, Optional<Long> strength) {
        Preconditions.checkNotNull(content);
        Preconditions.checkNotNull(label);

        this.content = content;
        this.id = id;
        this.label = label;
        this.strength = strength;
    }

    @Override
    public String toString() {
        return "TokenDatum{" +
            "content='" + content + '\'' +
            ", id=" + id +
            ", label='" + label + '\'' +
            ", strength=" + strength +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenDatum that = (TokenDatum) o;
        return id == that.id &&
            Objects.equals(content, that.content) &&
            Objects.equals(label, that.label) &&
            Objects.equals(strength, that.strength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, id, label, strength);
    }
}
