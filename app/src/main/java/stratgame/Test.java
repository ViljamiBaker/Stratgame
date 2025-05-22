package stratgame;
// java frq ass class
public class Test {
    public static class A{
        public A(){
            System.out.println("A");
        }
        public void method(){
            System.out.println("AM");
        }
    }
    public static class B extends A{
        public B(){
            System.out.println("B");
        }
        public void method(){
            System.out.println("BM");
        }
    }
    public static class C extends A{
        public C(){
            System.out.println("C");
        }
        @Override
        public void method(){
            System.out.println("CM");
        }
    }
    public static void main(String[] args) {
        new A().method();
        new B().method();
        new C().method();
    }
}
