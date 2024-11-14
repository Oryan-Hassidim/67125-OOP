public interface InnerApp {
    public void innerMethod();
    public int innerVar = 10;
    public static int getInnerVar() {
        return innerVar;
    }
    default void defaultMethod() {
        System.out.println("Default method");
        privateMethod();
    }
    private void privateMethod() {
        System.out.println("Private method");
    }
}