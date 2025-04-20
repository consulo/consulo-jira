package com.intellij.jira.util;

import com.intellij.openapi.util.Condition;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DividedListTest {

    private static final Condition<String> IS_FRUIT = StringUtils::isAlpha;
    private static final String BANANA = "Banana";
    private static final String APPLE = "Apple";
    private static final String ORANGE = "Orange";
    private static final String ONE = "1";
    private static final String TWO = "2";
    private static final String THREE = "3";

    private List<String> myFruits = new ArrayList<>(Arrays.asList(BANANA, APPLE, ORANGE));
    private List<String> myNumbers = new ArrayList<>(Arrays.asList(ONE, TWO, THREE));

    private DividedList<String> myDividedList;

    @Before
    public void setUp() {
        myDividedList = new DividedListImpl(myFruits, myNumbers);
    }

    @Test
    public void returnSelectedWhenIndexIsInRange() {
        myDividedList.setSelected(2);
        Assert.assertEquals(2, myDividedList.getSelectedIndex());
    }

    @Test
    public void returnTwoWhenOrangeIsSelected() {
        myDividedList.setSelected(ORANGE);
        Assert.assertEquals(2, myDividedList.getSelectedIndex());
    }

    @Test
    public void returnUnselectedWhenIndexIsOutRange() {
        myDividedList.setSelected(8);
        Assert.assertEquals(0, myDividedList.getSelectedIndex());
    }

    @Test
    public void selectedIndexIsNotModifiedWhenNewFruitIsAdded() {
        // Given
        myDividedList.setSelected(2);
        Assert.assertEquals(2, myDividedList.getSelectedIndex());

        // When
        myDividedList.add("Mango", IS_FRUIT);

        // Then
        Assert.assertEquals(2, myDividedList.getSelectedIndex());
    }

    @Test
    public void selectedIndexIsModifiedWhenNewNumberIsAdded() {
        // Given
        myDividedList.setSelected(4);
        Assert.assertEquals(4, myDividedList.getSelectedIndex());

        // When
        myDividedList.add("Mango", IS_FRUIT);

        // Then
        Assert.assertEquals(5, myDividedList.getSelectedIndex());
    }

    @Test
    public void selectedItemNotChangeWhenNotSelectedItemIsRemoved() {
        // Given
        myDividedList.setSelected(ONE);
        Assert.assertEquals(ONE, myDividedList.getSelected());

        // When
        myDividedList.remove(ORANGE);

        // Then
        Assert.assertEquals(ONE, myDividedList.getSelected());
    }

    @Test
    public void selectedItemNotChangeWhenLastItemIsRemoved() {
        // Given
        myDividedList.setSelected(ONE);
        Assert.assertEquals(ONE, myDividedList.getSelected());

        // When
        myDividedList.remove(THREE);

        // Then
        Assert.assertEquals(ONE, myDividedList.getSelected());
    }

    @Test
    public void selectedItemChangeWhenSelectedItemIsRemoved() {
        // Given
        myDividedList.setSelected(APPLE);
        Assert.assertEquals(APPLE, myDividedList.getSelected());

        // When
        myDividedList.remove(APPLE);

        // Then
        Assert.assertEquals(ORANGE, myDividedList.getSelected());
    }

    @Test
    public void selectedItemChangeWhenLastItemIsRemoved() {
        // Given
        myDividedList.setSelected(THREE);
        Assert.assertEquals(THREE, myDividedList.getSelected());

        // When
        myDividedList.remove(THREE);

        // Then
        Assert.assertEquals(TWO, myDividedList.getSelected());
    }

    @Test
    public void shouldSelected() {
        myDividedList.setSelected(THREE);
        Assert.assertTrue(myDividedList.isSelected(THREE));
    }

    @Test
    public void shouldNotSelected() {
        myDividedList.setSelected(THREE);
        Assert.assertFalse(myDividedList.isSelected(TWO));
    }

    @Test
    public void selectedItemNotChangeWhenSelectedItemIsMovedToFirstList() {
        // Given
        myDividedList.setSelected(THREE);

        // When
        myDividedList.moveToFirst(THREE);

        // Then
        Assert.assertTrue(myDividedList.isSelected(THREE));
    }

    @Test
    public void selectedItemNotChangeWhenNotSelectedItemIsMovedToFirstList() {
        // Given
        myDividedList.setSelected(THREE);

        // When
        myDividedList.moveToFirst(TWO);

        // Then
        Assert.assertTrue(myDividedList.isSelected(THREE));
    }

}