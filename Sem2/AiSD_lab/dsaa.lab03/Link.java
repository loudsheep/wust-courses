package dsaa.lab03;

public class Link {
    public String ref;

    public Link(String ref) {
        this.ref = ref;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Link))
            return false;

        return this.ref.equals(((Link) obj).ref);
    }

    @Override
    public String toString() {
        return ref;
    }
}
