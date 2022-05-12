package io.github.xanderstuff.ultimatehud.util;

import io.github.xanderstuff.ultimatehud.UltimateHud;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class TreeNode<T> {
    private final T data;
    @Nullable
    private TreeNode<T> parent;
    private final List<TreeNode<T>> children = new LinkedList<>();

    public TreeNode(T data, @Nullable TreeNode<T> parent) {
        this.data = data;
        this.parent = parent;
    }

    public T get() {
        return data;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void addChild(T child) {
        if (this.contains(child)) {
            UltimateHud.LOGGER.warn("Attempted to add '" + child + "' to a Tree that already contains it! Skipping...");
            return;
        }
        children.add(new TreeNode<>(child, this));
    }

    private void addChildren(List<TreeNode<T>> children) {
        for (TreeNode<T> child : children) {
            if (this.contains(child)) {
                UltimateHud.LOGGER.warn("Attempted to add '" + child + "' to a Tree that already contains it! Skipping...");
                continue;
            }
            child.parent = this;
            this.children.add(child);
        }
    }

    public List<TreeNode<T>> delete() {
        if (parent == null) {
            // Special case: we are the root node
            for (TreeNode<T> child : children) {
                child.parent = null;
            }
//            children = null; // is setting this reference to null necessary? I'm pretty sure it's "not"
            return children;
        } else {
            for (TreeNode<T> child : children) {
                child.parent = parent;
                parent.children.add(child);
            }
//            parent = null; // is setting this reference to null necessary? I'm pretty sure it's "not"
//            children = null;
            return null;
        }
    }

    public boolean contains(TreeNode<T> child) {
        if (this == child || data == child.get()) {
            return true;
        }
        for (TreeNode<T> next : children) {
            if (next.contains(child)) { // recursion!
                return true;
            }
        }

        return false;
    }

    public boolean contains(T child) {
        if (this.data == child) {
            return true;
        }
        for (TreeNode<T> next : children) {
            if (next.contains(child)) { // recursion!
                return true;
            }
        }

        return false;
    }
}
