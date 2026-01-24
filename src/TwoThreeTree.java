public class TwoThreeTree<T> {
    private final Comparator<T> comp;
    private final Measure<T> measure;
    private Node root;

    public TwoThreeTree(Comparator<T> comp, Measure<T> measure) {
        this.comp = comp;
        this.measure = measure;

        Node l = new Node(comp.MIN());
        Node m = new Node(comp.MAX());
        Node x = new Node(comp.MAX());

        l.p = m.p = x;
        x.left = l;
        x.middle = m;

        this.root = x;
    }

    public int getSize() {
        return root.size;
    }

    public boolean isEmpty() {
        // Only the two sentinels
        return root.size == 0;
    }

    public T search(T k) {
        Node result = root.search(k);
        return result == null ? null : result.key;
    }

    public void printPreOrder() {
        printPreOrder(root);
    }

    private void printPreOrder(Node x) {
        if (x == null)
            return;
        System.out.println(x.key + " " + x.size);
        printPreOrder(x.left);
        printPreOrder(x.middle);
        printPreOrder(x.right);
    }

    private Node minNode() {
        Node x = root;
        while (!x.isLeaf())
            x = x.left;
        x = x.p.middle;
        return x;
    }

    public T popMin() {
        Node minNode = minNode();
        delete(minNode);
        return minNode.key;
    }

    public T getMin() {
        return minNode().key;
    }

    public void printInOrder() {
        if (isEmpty()) {
            System.out.println();
            return;
        }

        Node x = minNode();
        while (x != null) {
            System.out.println(x.key + " " + x.size);
            x = x.succ();
        }
        System.out.println();
    }

    public int aggregateLower(T k, boolean includeEqual, boolean isWeight) {
        return innerAggregateLower(root, k, includeEqual, isWeight);
    }

    private int innerAggregateLower(Node x, T k, boolean includeEqual, boolean isWeight) {
        if (x.isLeaf()) {
            if (includeEqual ? comp.leq(x.key, k) : comp.lessThan(x.key, k)) {
                return x.sizeOrWeight(isWeight);
            } else {
                return 0;
            }
        }
        if (comp.leq(k, x.left.key)) {
            return innerAggregateLower(x.left, k, includeEqual, isWeight);
        } else if (comp.leq(k, x.middle.key)) {
            return x.left.sizeOrWeight(isWeight) + innerAggregateLower(x.middle, k, includeEqual, isWeight);
        } else {
            return x.left.sizeOrWeight(isWeight) + x.middle.sizeOrWeight(isWeight)
                    + innerAggregateLower(x.right, k, includeEqual, isWeight);
        }
    }

    public boolean delete(T x) {
        Node n = root.search(x);
        if (n == null) {
            return false;
        }
        delete(n);
        return true;
    }

    private void delete(Node x) {
        Node y = x.p;
        if (x == y.left) {
            y.setChildren(y.middle, y.right);
        } else if (x == y.middle) {
            y.setChildren(y.left, y.right);
        } else {
            y.setChildren(y.left, y.middle);
        }

        while (y != null) {
            if (y.middle != null) {
                y.updateKey();
                y = y.p;
            } else {
                if (y != root) {
                    y = y.BorrowOrMerge();
                } else {
                    root = y.left;
                    y.left.p = null;
                    return;
                }
            }
        }
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
        int size;
        int weight;

        Node() {
        }

        Node(T key) {
            this.key = key;
            if (comp.equals(key, comp.MIN()) || comp.equals(key, comp.MAX())) {
                this.size = 0;
                this.weight = 0;
            } else {
                this.size = 1;
                this.weight = measure == null ? 0 : measure.get(key);
            }
        }

        boolean isLeaf() {
            return left == null;
        }

        void updateKey() {
            key = left.key;
            size = left.size;
            weight = left.weight;
            if (middle != null) {
                key = middle.key;
                size += middle.size;
                weight += middle.weight;
            }
            if (right != null) {
                key = right.key;
                size += right.size;
                weight += right.weight;
            }
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

        Node BorrowOrMerge() {
            Node z = this.p;
            if (this == z.left) {
                Node x = z.middle;
                if (x.right != null) {
                    this.setChildren(this.left, x.left);
                    x.setChildren(x.middle, x.right);
                } else {
                    x.setChildren(this.left, x.left, x.middle);
                    z.setChildren(x, z.right);
                }
                return z;
            }
            if (this == z.middle) {
                Node x = z.left;
                if (x.right != null) {
                    this.setChildren(x.right, this.left);
                    x.setChildren(x.left, x.middle);
                } else {
                    x.setChildren(x.left, x.middle, this.left);
                    z.setChildren(x, z.right);
                }
                return z;
            }
            if (this == z.right) {
                Node x = z.middle;
                if (x.right != null) {
                    this.setChildren(x.right, this.left);
                    x.setChildren(x.left, x.middle);
                } else {
                    x.setChildren(x.left, x.middle, this.left);
                    z.setChildren(z.left, x);
                }
                return z;
            }
            return z;
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

        int sizeOrWeight(boolean isWeight) {
            return isWeight ? weight : size;
        }
    }
}