package com.intellij.jira.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DividedListImpl<E> implements DividedList<E> {

    private static final int UNSELECTED = -1;
    private static final int MIN_SELECTED = 0;

    private final List<E> myGlobalItems;
    private final List<E> myProjectItems;
    private int mySelectedIndex = UNSELECTED;

    public DividedListImpl() {
        this(new ArrayList<>(), new ArrayList<>(), UNSELECTED);
    }

    public DividedListImpl(List<E> globalItems, List<E> projectItems) {
        this(globalItems, projectItems, MIN_SELECTED);
    }

    public DividedListImpl(List<E> globalItems, List<E> projectItems, int selected) {
        myGlobalItems = globalItems;
        myProjectItems = projectItems;
        setSelected(selected);
    }

    @Override
    public List<E> getFirstList() {
        return myGlobalItems;
    }

    @Override
    public List<E> getSecondList() {
        return myProjectItems;
    }

    @Override
    public List<E> getAll() {
        List<E> all = new ArrayList<>();
        all.addAll(getFirstList());
        all.addAll(getSecondList());

        return all;
    }

    @Override
    public int getSelectedIndex() {
        return mySelectedIndex;
    }

    @Override
    public E getSelected() {
        List<E> all = getAll();
        if (UNSELECTED < mySelectedIndex && mySelectedIndex < all.size()) {
            return all.get(mySelectedIndex);
        }

        return null;
    }

    @Override
    public void setSelected(E e) {
        List<E> all = getAll();
        int index = all.indexOf(e);
        if (index > UNSELECTED && index < all.size()) {
            mySelectedIndex = index;
        }
    }

    @Override
    public void setSelected(int index) {
        if (index > UNSELECTED && index < getAll().size()) {
            mySelectedIndex = index;
        }
    }

    @Override
    public boolean isSelected(E e) {
        int index = getAll().indexOf(e);

        return index == mySelectedIndex;
    }

    @Override
    public void add(E e, Predicate<E> condition) {
        if (condition.test(e)) {
            if (mySelectedIndex >= myGlobalItems.size()) {
                mySelectedIndex++;
            }
            myGlobalItems.add(e);
        } else {
            myProjectItems.add(e);
        }
    }

    @Override
    public void moveToFirst(E e) {
        E selected = getSelected();
        boolean removed = myProjectItems.remove(e);
        if (removed) {
            myGlobalItems.add(e);
            setSelected(selected);
        }
    }

    @Override
    public void moveToSecond(E e) {
        E selected = getSelected();
        boolean removed = myGlobalItems.remove(e);
        if (removed) {
            myProjectItems.add(e);
            setSelected(selected);
        }
    }

    @Override
    public boolean remove(E e) {
        boolean removed = removeFromGlobal(e);
        if (!removed) {
            removed = removeFromProject(e);
        }

        return removed;
    }

    @Override
    public void update(E e) {
        boolean updated = update(myGlobalItems, e);
        if (!updated) {
            update(myProjectItems, e);
        }
    }

    private boolean removeFromGlobal(E e) {
        boolean removed = false;
        int index = myGlobalItems.indexOf(e);
        int size = myGlobalItems.size();
        if (index > UNSELECTED) {
            E selected = getSelected();
            myGlobalItems.remove(index);
            removed = true;
            if (!selected.equals(e)) {
                setSelected(selected);
            } else {
                if (index < mySelectedIndex || index == size - 1) {
                    mySelectedIndex--;
                }
            }
        }

        return removed;
    }

    private boolean removeFromProject(E e) {
        boolean removed = false;
        int index = myProjectItems.indexOf(e);
        int size = myProjectItems.size();
        if (index > UNSELECTED) {
            E selected = getSelected();
            myProjectItems.remove(index);
            removed = true;
            if (!selected.equals(e)) {
                setSelected(selected);
            } else {
                if (index == size - 1) {
                    mySelectedIndex--;
                }
            }
        }

        return removed;
    }

    private boolean update(List<E> items, E e) {
        boolean updated = false;
        int index = items.indexOf(e);
        if (index > UNSELECTED) {
            items.remove(index);
            items.add(index, e);
            updated = true;
        }

        return updated;
    }
}
