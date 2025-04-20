package com.intellij.jira.util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SimpleSelectableList<E>{
    private static final int UNSELECTED = -1;
    private static final Integer MIN_SELECTED = 0;
    private static final Integer MAX_SELECTED = Integer.MAX_VALUE;

    private List<E> myItems;
    private int mySelectedItem;

    public SimpleSelectableList() {
        this(new ArrayList<>(), UNSELECTED);
    }

    private SimpleSelectableList(List<E> items){
        this(items, MIN_SELECTED);
    }

    private SimpleSelectableList(List<E> items, int selectedItem){
        this.myItems = items;
        selectItem(selectedItem);
    }

    public static <E> SimpleSelectableList<E> empty(){
        return new SimpleSelectableList<>();
    }

    public static <E> SimpleSelectableList<E> of(List<E> items){
        return new SimpleSelectableList<>(items);
    }

    public static <E> SimpleSelectableList<E> of(List<E> items, int selectedItem){
        return new SimpleSelectableList<>(items, selectedItem);
    }

    public boolean add(E item){
        if(myItems.isEmpty()){
            myItems.add(item);
            selectItem(MIN_SELECTED);
        }else{
            myItems.add(item);
        }

        return true;
    }


    public void add(E item, boolean selected){
        add(item);
        if(selected){
            selectItem(getLastSelectableItemIndex());
        }
    }


    public boolean addAll(Collection<? extends E> items) {
        this.myItems.addAll(items);
        if(!hasSelectedItem() && !items.isEmpty()){
            selectItem(MIN_SELECTED);
        }

        return true;
    }

    public void update(int index, E item){
       update(index, item, false);
    }

    public void update(int index, E item, boolean selected){
        if(index >= MIN_SELECTED && index <= getLastSelectableItemIndex()){
            this.myItems.remove(index);
            this.myItems.add(index, item);
            updateSelectedItem(index, selected);
        }
    }

    public E remove(E item){
        return remove(myItems.indexOf(item));
    }

    public E remove(int index){
        if(index < 0 || index > getLastSelectableItemIndex()){
            return null;
        }

        E element = this.myItems.remove(index);
        if(this.mySelectedItem > getLastSelectableItemIndex()){
            selectItem(getSelectedItemIndex() - 1);
        }

        if(index < this.mySelectedItem){
            selectItem(index);
        }

        return element;
    }


    public void clear() {
        myItems.clear();
        this.mySelectedItem = UNSELECTED;
    }

    public List<E> getItems() {
        return Collections.unmodifiableList(myItems);
    }

    public int getSelectedItemIndex() {
        return mySelectedItem;
    }

    @Nullable
    public E getSelectedItem(){
        if(hasSelectedItem()){
            return this.myItems.get(getSelectedItemIndex());
        }

        return null;
    }


    public void updateSelectedItem(E item, boolean selected){
        updateSelectedItem(myItems.indexOf(item), selected);
    }

    private void updateSelectedItem(int index, boolean selected){
        if(selected){
            selectItem(index);
        }else{
            unselectItem(index);
        }
    }

    public void setSelected(E item){
        selectItem(myItems.indexOf(item));
    }

    public void selectItem(int selectedItem) {
        if (selectedItem < MIN_SELECTED ) {
            mySelectedItem = UNSELECTED;
        } else if (selectedItem > getLastSelectableItemIndex()){
            mySelectedItem = getLastSelectableItemIndex();
        } else {
            mySelectedItem = selectedItem;
        }
    }

    public boolean isEmpty(){
        return this.myItems.isEmpty();
    }

    public boolean hasSelectedItem(){
        return mySelectedItem > UNSELECTED && mySelectedItem < myItems.size();
    }

    private int getLastSelectableItemIndex(){
        return isEmpty() ? UNSELECTED : myItems.size() - 1;
    }

    private void unselectItem(int index){
        if(index == mySelectedItem){
            selectItem(myItems.size() == 1 ? MIN_SELECTED : (index - 1));
        }
    }

}
