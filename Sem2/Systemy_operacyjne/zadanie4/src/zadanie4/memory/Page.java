package zadanie4.memory;

import java.util.Objects;

public class Page {
    private String value;
    private boolean referenceBit = false;

    public Page(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Page{" +value + ',' + referenceBit + '}';
    }

    public boolean hasReferenceBitSet() {
        return referenceBit;
    }

    public void setReferenceBit(boolean referenceBit) {
        this.referenceBit = referenceBit;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Page page)) return false;
        return Objects.equals(value, page.value);
    }
}
