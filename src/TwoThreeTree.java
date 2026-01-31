public class TwoThreeTree<T> {
    private final Comparator<T> comparator;
    private final Measure<T> measure;
    private Node root;

    public TwoThreeTree(Comparator<T> comparator, Measure<T> measure) {
        this.comparator = comparator;
        this.measure = measure;

        Node leftSentinel = new Node(comparator.MIN());
        Node rightSentinel = new Node(comparator.MAX());
        Node initialRoot = new Node(comparator.MAX());

        leftSentinel.parent = rightSentinel.parent = initialRoot;
        initialRoot.left = leftSentinel;
        initialRoot.middle = rightSentinel;

        this.root = initialRoot;
    }

    public int getSize() {
        return root.subtreeSize;
    }

    public boolean isEmpty() {
        return root.subtreeSize == 0;
    }

    public T search(T key) {
        Node result = root.search(key);
        return result == null ? null : result.key;
    }

    private Node findMinNode() {
        Node currentNode = root;
        while (!currentNode.isLeaf())
            currentNode = currentNode.left;

        currentNode = currentNode.parent.middle;
        return currentNode;
    }

    public T popMin() {
        Node minNode = findMinNode();
        deleteNode(minNode);
        return minNode.key;
    }

    public T getMin() {
        return findMinNode().key;
    }

    public int aggregateLower(T key, boolean includeEqual, boolean isWeight) {
        return recursiveAggregateLower(root, key, includeEqual, isWeight);
    }

    private int recursiveAggregateLower(Node currentNode, T targetKey, boolean includeEqual, boolean isWeight) {
        if (currentNode.isLeaf()) {
            boolean condition = includeEqual ?
                    comparator.leq(currentNode.key, targetKey) :
                    comparator.lessThan(currentNode.key, targetKey);

            if (condition) {
                return currentNode.getSizeOrWeight(isWeight);
            } else {
                return 0;
            }
        }

        if (comparator.leq(targetKey, currentNode.left.key)) {
            return recursiveAggregateLower(currentNode.left, targetKey, includeEqual, isWeight);
        } else if (comparator.leq(targetKey, currentNode.middle.key)) {
            return currentNode.left.getSizeOrWeight(isWeight) +
                    recursiveAggregateLower(currentNode.middle, targetKey, includeEqual, isWeight);
        } else {
            return currentNode.left.getSizeOrWeight(isWeight) +
                    currentNode.middle.getSizeOrWeight(isWeight) +
                    recursiveAggregateLower(currentNode.right, targetKey, includeEqual, isWeight);
        }
    }

    public boolean delete(T value) {
        Node nodeToDelete = root.search(value);
        if (nodeToDelete == null) {
            return false;
        }
        deleteNode(nodeToDelete);
        return true;
    }

    private void deleteNode(Node nodeToDelete) {
        Node parentNode = nodeToDelete.parent;

        if (nodeToDelete == parentNode.left) {
            parentNode.setChildren(parentNode.middle, parentNode.right);
        } else if (nodeToDelete == parentNode.middle) {
            parentNode.setChildren(parentNode.left, parentNode.right);
        } else {
            parentNode.setChildren(parentNode.left, parentNode.middle);
        }

        while (parentNode != null) {
            if (parentNode.middle != null) {
                parentNode.updateStats();
                parentNode = parentNode.parent;
            } else {
                if (parentNode != root) {
                    parentNode = parentNode.borrowOrMerge();
                } else {
                    root = parentNode.left;
                    parentNode.left.parent = null;
                    return;
                }
            }
        }
    }

    public void insert(T value) {
        insertNode(new Node(value));
    }

    private void insertNode(Node nodeToInsert) {
        Node currentNode = root;

        while (!currentNode.isLeaf()) {
            if (comparator.lessThan(nodeToInsert.key, currentNode.left.key)) {
                currentNode = currentNode.left;
            } else if (comparator.lessThan(nodeToInsert.key, currentNode.middle.key)) {
                currentNode = currentNode.middle;
            } else {
                currentNode = currentNode.right;
            }
        }

        Node parentNode = currentNode.parent;
        Node splitNode = parentNode.insertAndSplit(nodeToInsert);

        while (parentNode != root) {
            parentNode = parentNode.parent;
            if (splitNode != null) {
                splitNode = parentNode.insertAndSplit(splitNode);
            } else {
                parentNode.updateStats();
            }
        }

        if (splitNode != null) {
            Node newRoot = new Node();
            newRoot.setChildren(parentNode, splitNode);
            root = newRoot;
        }
    }

    private class Node {
        T key;
        Node left, middle, right, parent;
        int subtreeSize;
        int subtreeWeight;

        Node() {
        }

        Node(T key) {
            this.key = key;
            if (comparator.equals(key, comparator.MIN()) || comparator.equals(key, comparator.MAX())) {
                this.subtreeSize = 0;
                this.subtreeWeight = 0;
            } else {
                this.subtreeSize = 1;
                this.subtreeWeight = measure == null ? 0 : measure.get(key);
            }
        }

        boolean isLeaf() {
            return left == null;
        }

        void updateStats() {
            key = left.key;
            subtreeSize = left.subtreeSize;
            subtreeWeight = left.subtreeWeight;

            if (middle != null) {
                key = middle.key;
                subtreeSize += middle.subtreeSize;
                subtreeWeight += middle.subtreeWeight;
            }

            if (right != null) {
                key = right.key;
                subtreeSize += right.subtreeSize;
                subtreeWeight += right.subtreeWeight;
            }
        }

        void setChildren(Node newLeft, Node newMiddle, Node newRight) {
            left = newLeft;
            middle = newMiddle;
            right = newRight;

            newLeft.parent = this;
            if (newMiddle != null)
                newMiddle.parent = this;
            if (newRight != null)
                newRight.parent = this;

            updateStats();
        }

        void setChildren(Node newLeft, Node newMiddle) {
            setChildren(newLeft, newMiddle, null);
        }

        Node search(T targetKey) {
            if (isLeaf()) {
                if (comparator.equals(key, targetKey))
                    return this;
                else
                    return null;
            }

            if (comparator.leq(targetKey, left.key)) {
                return left.search(targetKey);
            } else if (comparator.leq(targetKey, middle.key)) {
                return middle.search(targetKey);
            } else {
                return right.search(targetKey);
            }
        }

        Node insertAndSplit(Node childToInsert) {
            Node l = left;
            Node m = middle;
            Node r = right;

            if (r == null) {
                if (comparator.lessThan(childToInsert.key, l.key)) {
                    this.setChildren(childToInsert, l, m);
                } else if (comparator.lessThan(childToInsert.key, m.key)) {
                    this.setChildren(l, childToInsert, m);
                } else {
                    this.setChildren(l, m, childToInsert);
                }
                return null;
            }

            Node newSiblingNode = new Node();

            if (comparator.lessThan(childToInsert.key, l.key)) {
                this.setChildren(childToInsert, l);
                newSiblingNode.setChildren(m, r);
            } else if (comparator.lessThan(childToInsert.key, m.key)) {
                this.setChildren(l, childToInsert);
                newSiblingNode.setChildren(m, r);
            } else if (comparator.lessThan(childToInsert.key, r.key)) {
                this.setChildren(l, m);
                newSiblingNode.setChildren(childToInsert, r);
            } else {
                this.setChildren(l, m);
                newSiblingNode.setChildren(r, childToInsert);
            }
            return newSiblingNode;
        }

        Node borrowOrMerge() {
            Node parentNode = this.parent;

            if (this == parentNode.left) {
                Node sibling = parentNode.middle;
                if (sibling.right != null) {
                    this.setChildren(this.left, sibling.left);
                    sibling.setChildren(sibling.middle, sibling.right);
                } else {
                    sibling.setChildren(this.left, sibling.left, sibling.middle);
                    parentNode.setChildren(sibling, parentNode.right);
                }
                return parentNode;
            }

            if (this == parentNode.middle) {
                Node sibling = parentNode.left;
                if (sibling.right != null) {
                    this.setChildren(sibling.right, this.left);
                    sibling.setChildren(sibling.left, sibling.middle);
                } else {
                    sibling.setChildren(sibling.left, sibling.middle, this.left);
                    parentNode.setChildren(sibling, parentNode.right);
                }
                return parentNode;
            }

            if (this == parentNode.right) {
                Node sibling = parentNode.middle;
                if (sibling.right != null) {
                    this.setChildren(sibling.right, this.left);
                    sibling.setChildren(sibling.left, sibling.middle);
                } else {
                    sibling.setChildren(sibling.left, sibling.middle, this.left);
                    parentNode.setChildren(parentNode.left, sibling);
                }
                return parentNode;
            }
            return parentNode;
        }

        int getSizeOrWeight(boolean isWeight) {
            return isWeight ? subtreeWeight : subtreeSize;
        }
    }
}