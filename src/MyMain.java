//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MyMain {

    public static void test1() {
        Comparator<Integer> intComp = new Comparator<>() {
            @Override
            public long compare(Integer o1, Integer o2) {
                return (long)o1 - o2;
            }

            @Override
            public Integer MAX() {
                return Integer.MAX_VALUE;
            }

            @Override
            public Integer MIN() {
                return Integer.MIN_VALUE;
            }
        };
        TwoThreeTree<Integer> tree = new TwoThreeTree<>(intComp, null);

        tree.printInOrder();

        for(int i = 100; i > 1; i--) {
            tree.insert(i);
        }

        tree.printInOrder();

        for(int i = 101; i < 200; i++) {
            tree.insert(i);
        }

        tree.printInOrder();

        for(int i = 100; i > 1; i--) {
            tree.delete(i);
        }

        tree.printInOrder();

        for(int i = 101; i < 200; i++) {
            tree.delete(i);
        }

        tree.printInOrder();
    }
    public static void main(String[] args) {
        TwoThreeTree<Doctor> tree = new TwoThreeTree<>(Doctor.comparator, null);
        tree.insert(new Doctor("A1"));
        tree.insert(new Doctor("B1"));
        tree.insert(new Doctor("C1"));
        tree.insert(new Doctor("D1"));
        tree.insert(new Doctor("A4"));
        tree.insert(new Doctor("B1123"));
        tree.insert(new Doctor("Cfsdf"));
        tree.printPreOrder();
    }
}