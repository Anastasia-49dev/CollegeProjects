def f(x):
    return x ** 3 - x - 2

def bisection(a, b, eps):
    while b - a > eps:
        c = (a + b) / 2
        if f(a) * f(c) <= 0:
            b = c
        else:
            a = c
    return (a + b) / 2

a = 1
b = 2
eps = 0.0001

root = bisection(a, b, eps)
print("Корень:", root)
