public abstract class Comparator<T> {
    public abstract long compare(T o1, T o2);
    public abstract T MAX();
    public abstract T MIN();
    public boolean equals(T o1, T o2) {
        return compare(o1, o2) == 0;
    }

    public boolean leq(T o1, T o2) {
        return compare(o1, o2) <= 0;
    }

    public boolean lessThan(T o1, T o2) {
        return compare(o1, o2) < 0;
    }
}
