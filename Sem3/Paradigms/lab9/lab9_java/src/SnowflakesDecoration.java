import java.util.List;

public class SnowflakesDecoration implements IDecoration {
    @Override
    public void decorate(List<StringBuilder> representation) {
        for (StringBuilder line: representation) {
            int starCount = 0;
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == '*') {
                    starCount++;
                    if (starCount == 1 || i == line.length() - 1) {
                        line.setCharAt(i, '.');
                    }
                }
            }
        }
    }
}
