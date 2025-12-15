import java.util.ArrayList;
import java.util.List;

public abstract class ChristmasTree {
    protected int height;
    protected List<StringBuilder> treeRepresentation;

    public ChristmasTree(int height) {
        assert height >= 2: "height must be >= 2";
        this.height = height;
        this.treeRepresentation = new ArrayList<>();
    }

    public void applyDecoration(IDecoration decoration) {
        decoration.decorate(this.treeRepresentation);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(StringBuilder line: this.treeRepresentation) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    protected abstract void generate();
}
