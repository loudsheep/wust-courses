import java.lang.reflect.Field;

public class Debug {
    public static void fields(Object obj) {
        Class<?> classObj = obj.getClass();
        System.out.println("Class: " + classObj.getSimpleName());

        Field[] fields = classObj.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String name = field.getName();
                String type = field.getType().getSimpleName();
                Object value = field.get(obj);

                System.out.println("Field: " + name + " => " + type + " = " + value);
            } catch (IllegalAccessException e) {
                System.out.println("Error accessing field: " + field.getName());
            }
        }

        System.out.println("============");
    }

    public static void main(String[] args) {
        class Point {
            int x = 324;
            int y = 1234;
            String name = "Point name";
        }

        class Student {
            private String name = "Jan Kowalski";
            private boolean isActive = true;
        }

        class Empty {};

        Debug.fields(new Point());
        Debug.fields(new Student());
        Debug.fields(new Empty());
    }
}