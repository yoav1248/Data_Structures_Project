//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Comparator<Integer> intComp = new Comparator<Integer>() {
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
        TwoThreeTree<Integer> tree = new TwoThreeTree<Integer>(intComp);

        for(int i = 100; i > 1; i--) {
            tree.insert(i);
        }

        for(int i = 100; i < 200; i++) {
            tree.insert(i);
        }
        tree.printInOrder();
    }
}