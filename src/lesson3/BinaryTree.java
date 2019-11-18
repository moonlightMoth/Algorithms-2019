package lesson3;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

// Attention: comparable supported but comparator is not
public class BinaryTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T> {
        final T value;

        Node<T> left = null;

        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;
    private boolean changed;
    private int size = 0;

    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        }
        else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
        }
        else {
            assert closest.right == null;
            closest.right = newNode;
        }

        size++;
        changed = true;
        return true;
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    public int height() {
        return height(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    private int height(Node<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     *
     * O(log(n)) - in case of well balanced tree
     * O(n) - in case of tree each node of has one child or no children at all
     */
    @Override
    public boolean remove(Object o) {

        T valueToRemove = (T)o;
        Node<T> nodeToRemove = find(valueToRemove);
        if (nodeToRemove == null || nodeToRemove.value.compareTo(valueToRemove) != 0)
            return false;
        Node<T> parent = findParent(valueToRemove);
        size--;
        return remove(nodeToRemove, parent);
    }

    private boolean remove(Node<T> nodeToRemove, Node<T> parent)
    {
        if (parent == nodeToRemove)
            parent = null;

        if(nodeToRemove.left == null && nodeToRemove.right == null)
        {
            if (parent != null)
            {
                if (parent.left == nodeToRemove)
                {
                    parent.left = null;
                    changed = true;
                    return true;
                }
                if (parent.right == nodeToRemove)
                {
                    parent.right = null;
                    changed = true;
                    return true;
                }
            }
            else
            {
                root = null;
                changed = true;
                return true;
            }
        }

        if ((nodeToRemove.left == null) ^ (nodeToRemove.right == null))
        {
            if (parent != null)
            {
                if (nodeToRemove.left == null)
                {
                    if(parent.right == nodeToRemove)
                        parent.right = nodeToRemove.right;
                    else
                        parent.left = nodeToRemove.right;
                    changed = true;
                    return true;
                }
                if(parent.right == nodeToRemove)
                    parent.right = nodeToRemove.left;
                else
                    parent.left = nodeToRemove.left;
                changed = true;
                return true;
            }
            else
            {
                if (nodeToRemove.left == null)
                {
                    root = nodeToRemove.right;
                    changed = true;
                    return true;
                }
                root = nodeToRemove.left;
                changed = true;
                return true;

            }
        }

        if(nodeToRemove.right.left == null)
        {
            nodeToRemove.right.left = nodeToRemove.left;
            if (parent != null)
            {
                if (parent.left == nodeToRemove)
                {
                    parent.left = nodeToRemove.right;
                    changed = true;
                    return true;
                }
                if (parent.right == nodeToRemove)
                {
                    parent.right = nodeToRemove.right;
                    changed = true;
                    return true;
                }
            }
            else
            {
                root = nodeToRemove.right;
                changed = true;
                return true;
            }
        }
        else
        {
            if(parent != null)
            {
                Node<T> mostLeft = findMostLeft(nodeToRemove.right);
                Node<T> newNode = new Node<>(mostLeft.value);
                newNode.left = nodeToRemove.left;
                newNode.right = nodeToRemove.right;
                if (parent.left == nodeToRemove)
                {
                    parent.left = newNode;
                }
                if (parent.right == nodeToRemove)
                {
                    parent.right = newNode;
                }
                return remove(mostLeft, findParent(nodeToRemove.right, mostLeft.value));
            }
            else
            {
                Node<T> mostLeft = findMostLeft(nodeToRemove.right);
                Node<T> newNode = new Node<>(mostLeft.value);
                newNode.left = nodeToRemove.left;
                newNode.right = nodeToRemove.right;

                root = newNode;

                return remove(mostLeft, findParent(nodeToRemove.right, mostLeft.value));
            }
        }
        return false;
    }

    private Node<T> findMostLeft(Node<T> start)
    {
        if (start.left != null)
            return findMostLeft(start.left);
        return start;
    }



    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    private Node<T> findParent(T value)
    {
        if (root == null || root.value == value ||!contains(value))
            return null;
        return findParent(root, value);
    }

    private Node<T> findParent(Node<T> start, T value)
    {

        if (start.left != null && value.compareTo(start.left.value) == 0)
            return start;
        if (start.right != null && value.compareTo(start.right.value) == 0)
            return start;

        int comparison = value.compareTo(start.value);

        if (comparison < 0) {
            if (start.left == null) return start;
            return findParent(start.left, value);
        }
        else {
            if (start.right == null) return start;
            return findParent(start.right, value);
        }
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        }
        else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        }
        else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    public class BinaryTreeIterator implements Iterator<T> {

        private int current;
        List<T> list;

        /*
        * Fail-fast iterator
        * O(n) - put all elements into List
        */
        private BinaryTreeIterator()
        {
            list = toList();

            if (root != null)
            {
                current = 0;
                changed = false;
            }
        }

        private List<T> toList()
        {
            List<T> list = new ArrayList<>();
            if (root != null)
                addToList(list, root);
            return list;
        }

        private void addToList(List<T> list, Node<T> node)
        {
            if (node.left != null)
                addToList(list, node.left);
            list.add(node.value);
            if (node.right != null)
                addToList(list, node.right);
        }

        /**
         * Проверка наличия следующего элемента
         * Средняя
         *
         * O(1) - linear time, no size dependency
         */
        @Override
        public boolean hasNext() {
            if (changed)
                throw new ConcurrentModificationException();
            return list.size() > current;
        }

        /**
         * Поиск следующего элемента
         * Средняя
         *
         * O(1) - linear time, no size dependency
         */
        @Override
        public T next() {
            if (changed)
                throw new ConcurrentModificationException();

            return list.get(current++);
        }

        /**
         * Удаление следующего элемента
         * Сложная
         *
         * O(n) || O(log(n)) - see remove() method in tree itself
         */
        @Override
        public void remove() {
            if (changed)
                throw new ConcurrentModificationException();
            BinaryTree.this.remove(list.get(current-1));
            changed = false;
        }
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinaryTreeIterator();
    }

    @Override
    public int size() {
        return size;
    }


    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {

        throw new NotImplementedError();
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        // TODO
        throw new NotImplementedError();
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }
}
