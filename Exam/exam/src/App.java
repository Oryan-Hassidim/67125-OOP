public class App {
    private int var = 10;

    public static class InnerApp<T> {
        public T innerVar;

        public InnerApp(T innerVar, App app) {
            this.innerVar = innerVar;
            System.out.println(app.var);
        }
    }

    @FunctionalInterface
    public static interface Someable {
        int calc(int a, int b);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Someable some = (a, b) -> a + b;
    }
}
