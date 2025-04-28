package zadanie3;

import java.util.Objects;

public class Page {
    private String value;

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
        return "Page{" +value + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Page page)) return false;
        return Objects.equals(value, page.value);
    }
}
