import java.util.List;

public class AngelDecoration implements IDecoration {
    @Override
    public void decorate(List<StringBuilder> representation) {
        if (representation.isEmpty()) return;

        StringBuilder tip = representation.get(0);
        int starIndex = tip.indexOf("*");

        if (starIndex != -1) {
            tip.setCharAt(starIndex, 'A');
        }
    }
}
