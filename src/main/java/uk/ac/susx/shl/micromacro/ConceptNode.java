package uk.ac.susx.shl.micromacro;

import uk.ac.susx.tag.method51.core.meta.filters.KeyFilter;

import java.util.Objects;

public class ConceptNode {
    private String label;
    private KeyFilter filter;

    public ConceptNode(String label, KeyFilter filter) {
        this.label = label;
        this.filter = filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConceptNode that = (ConceptNode) o;
        return Objects.equals(label, that.label) &&
            Objects.equals(filter, that.filter);
    }

    public String getLabel() {
        return label;
    }

    public KeyFilter getFilter() {
        return filter;
    }

    @Override
    public String toString() {
        return "ConceptNode{" +
            "label='" + label + '\'' +
            '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, filter);
    }

}
