public class TwoThreeTree<T> {
    private final Comparator<T> comp;
    private Node root;

    public TwoThreeTree(Comparator<T> comp) {
        this.comp = comp;

        Node l = new Node(comp.MIN());
        Node m = new Node(comp.MAX());
        Node x = new Node(comp.MAX());

        l.p = m.p = x;
        x.left = l;
        x.middle = m;

        this.root = x;
    }

    public T search(T k) {
        Node result = root.search(k);
        if (result == null)
            return null;
        return result.key;
    }

    private Node minNode() {
        Node x = root;
        while (!x.isLeaf())
            x = x.left;
        x = x.p.middle;
        return x;
    }

    public T min() {
        return minNode().key;
    }

    public void printInOrder() {
        Node x = minNode();
        while (x != null) {
            System.out.print(x.key + " ");
            x = x.succ();
        }
        System.out.println();
    }

    public void insert(T x) {
        insert(new Node(x));
    }

    private void insert(Node z) {
        Node y = root;
        while (!y.isLeaf()) {
            if (comp.lessThan(z.key, y.left.key)) {
                y = y.left;
            } else if (comp.lessThan(z.key, y.middle.key)) {
                y = y.middle;
            } else {
                y = y.right;
            }
        }

        Node x = y.p;
        z = x.insertAndSplit(z);
        while (x != root) {
            x = x.p;
            if (z != null) {
                z = x.insertAndSplit(z);
            } else {
                x.updateKey();
            }
        }

        if (z != null) {
            Node w = new Node();
            w.setChildren(x, z);
            root = w;
        }
    }

    private class Node {
        T key;
        Node left, middle, right, p;

        Node() {
        }

        Node(T key) {
            this.key = key;
        }

        boolean isLeaf() {
            return left == null;
        }

        void updateKey() {
            key = left.key;
            if (middle != null)
                key = middle.key;
            if (right != null)
                key = right.key;
        }

        void setChildren(Node l, Node m, Node r) {
            left = l;
            middle = m;
            right = r;
            l.p = this;
            if (m != null)
                m.p = this;
            if (r != null)
                r.p = this;
            updateKey();
        }

        void setChildren(Node l, Node m) {
            setChildren(l, m, null);
        }

        Node search(T k) {
            if (isLeaf()) {
                if (comp.equals(key, k))
                    return this;
                else
                    return null;
            }

            if (comp.leq(k, left.key)) {
                return left.search(k);
            } else if (comp.leq(k, middle.key)) {
                return middle.search(k);
            } else {
                return right.search(k);
            }
        }

        Node insertAndSplit(Node other) {
            Node l = left;
            Node m = middle;
            Node r = right;

            if (r == null) {
                if (comp.lessThan(other.key, l.key)) {
                    this.setChildren(other, l, m);
                } else if (comp.lessThan(other.key, m.key)) {
                    this.setChildren(l, other, m);
                } else {
                    this.setChildren(l, m, other);
                }
                return null;
            }

            Node newNode = new Node();

            if (comp.lessThan(other.key, l.key)) {
                this.setChildren(other, l);
                newNode.setChildren(m, r);
            } else if (comp.lessThan(other.key, m.key)) {
                this.setChildren(l, other);
                newNode.setChildren(m, r);
            } else if (comp.lessThan(other.key, r.key)) {
                this.setChildren(l, m);
                newNode.setChildren(other, r);
            } else {
                this.setChildren(l, m);
                newNode.setChildren(r, other);
            }
            return newNode;
        }

        Node succ() {
            Node x = this;
            Node z = x.p;

            while (x == z.right || (z.right == null && x == z.middle)) {
                x = z;
                z = z.p;
            }

            Node y;
            if (x == z.left)
                y = z.middle;
            else
                y = z.right;

            while (!y.isLeaf()) {
                y = y.left;
            }

            if (comp.lessThan(y.key, comp.MAX()))
                return y;
            else
                return null;
        }
    }
}