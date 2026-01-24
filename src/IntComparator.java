public class IntComparator extends Comparator<Integer> {

    @Override
    public long compare(Integer o1, Integer o2) {
        return o1.compareTo(o2);
    }

    @Override
    public Integer MAX() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Integer MIN() {
        return Integer.MIN_VALUE;
    }
}
