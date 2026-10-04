public class Pets {

    static String dogName;
    static int dogAge;
    static String dogSound;

    static String catName;
    static int catAge;
    static String catSound;

    static void createDog() {
        System.out.println("Создаю собаку");
        dogName = "гаф";
        dogAge = 3;
        dogSound = "Гав";
        System.out.println("Собака создана");
    }

    static void createCat() {
        System.out.println("Создаю кошку");
        catName = "алиса";
        catAge = 2;
        catSound = "Мяу";
        System.out.println("Кошка создана");
    }

    static void dogSpeak() {
        System.out.println("Собака говорит");
        System.out.println(dogName + ": " + dogSound);
    }

    static void catSpeak() {
        System.out.println("Кошка говорит");
        System.out.println(catName + ": " + catSound);
    }

    static void printPetInfo() {
        System.out.println("Вывожу информацию о животных");
        System.out.println("Собака: имя - " + dogName + ", возраст - " + dogAge + ", звук - " + dogSound);
        System.out.println("Кошка: имя - " + catName + ", возраст - " + catAge + ", звук - " + catSound);
    }

    public static void main(String[] args) {
        createDog();
        createCat();

        dogSpeak();
        catSpeak();

        printPetInfo();
    }
}
