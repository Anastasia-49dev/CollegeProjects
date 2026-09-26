public class Main {
    public static void main(String[] args) {
        int a = 65;
        int b = 87;
        System.out.println("До:");
        System.out.println("a=" + a);
        System.out.println("b=" + b);
        a = a + b;
        b = a - b;
        a = a - b;
        System.out.println("После:");
        System.out.println("a=" + a);
        System.out.println("b=" + b);
    }
}
