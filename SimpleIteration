public class SimpleIteration {

    static double iteration(double x0, double eps, int n) {
        double x = x0;
        for (int i = 0; i < n; i++) {
            double newX = Math.pow(x + 2, 1.0 / 3);
            if (Math.abs(newX - x) < eps) {
                return newX;
            }
            x = newX;
        }
        return x;
    }

    public static void main(String[] args) {
        double a = 1;
        double b = 2;
        double eps = 0.0001;
        double x0 = 1.5;
        int n = 100;

        double root = iteration(x0, eps, n);
        System.out.println("Корень: " + root);
    }
}
