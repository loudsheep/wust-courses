import java.util.List;

public class LightsDecoration implements IDecoration {
    @Override
    public void decorate(List<StringBuilder> representation) {
        for (StringBuilder line: representation) {
            int starCount = 0;
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == '*') {
                    starCount++;
                    if (starCount % 2 == 0) {
                        line.setCharAt(i, 'x');
                    }
                }
            }
        }
    }
}
