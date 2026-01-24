public class StringComparator extends Comparator<String> {
    @Override
    public long compare(String o1, String o2) {
        return o1.compareTo(o2);
    }

    @Override
    public String MAX() {
        return "";
    }

    @Override
    public String MIN() {
        return "";
    }
}
