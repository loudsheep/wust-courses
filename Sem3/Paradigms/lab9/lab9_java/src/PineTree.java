public class PineTree extends ChristmasTree {
    public PineTree(int height) {
        super(height);
        this.generate();
    }

    @Override
    protected void generate() {
        this.treeRepresentation.clear();

        for (int i = 0; i < this.height; i++) {
            StringBuilder line = new StringBuilder();

            line.append(" ".repeat(this.height - i - 1));
            line.append("*".repeat(Math.max(0, 2 * i + 1)));

            this.treeRepresentation.add(line);
        }

        // pieniek
        StringBuilder trunk = new StringBuilder();
        trunk.append(" ".repeat(this.height - 1));
        trunk.append("#");
        this.treeRepresentation.add(trunk);
    }
}
