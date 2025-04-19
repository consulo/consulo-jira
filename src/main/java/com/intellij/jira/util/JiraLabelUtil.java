package com.intellij.jira.util;

import com.intellij.jira.rest.model.JiraIssuePriority;
import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.jira.ui.labels.JiraLinkLabel;
import com.intellij.jira.ui.labels.JiraStatusDarkLabel;
import com.intellij.jira.ui.labels.JiraStatusLightLabel;
import consulo.ui.ex.JBColor;
import consulo.ui.ex.awt.JBFont;
import consulo.ui.ex.awt.JBLabel;
import consulo.ui.ex.awt.JBUI;
import consulo.ui.ex.awt.UIUtil;
import consulo.ui.image.Image;

import javax.swing.*;
import java.awt.*;

import static java.util.Objects.nonNull;
import static javax.swing.SwingConstants.LEFT;

public class JiraLabelUtil {

    public static final String EMPTY_TEXT = "";
    public static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    public static final JBFont BOLD = JBUI.Fonts.label().asBold();
    public static final JBFont ITALIC = JBUI.Fonts.label().asItalic();

    public static final Color BLACK = JBColor.BLACK;
    public static final Color WHITE = JBColor.WHITE;

    public static final Color LINK_COLOR = JBColor.BLUE;

    public static final Color DACULA_DEFAULT_COLOR = new Color(60, 63, 65);

    public static final Color DEFAULT_ISSUE_COLOR = new Color(211, 232, 240  );
    public static final Color DEFAULT_SELECTED_ISSUE_COLOR = new Color(26, 125, 196  );
    public static final Color DARCULA_ISSUE_COLOR = new Color(69, 72, 74);
    public static final Color DARCULA_SELECTED_ISSUE_COLOR = new Color(72, 81, 83);

    // Links
    public static final Color ISSUE_LINK_COLOR = new Color(240, 216, 226);
    public static final Color DARK_ISSUE_LINK_COLOR = new Color(240, 187, 219);


    public static final Color DARCULA_ISSUE_LINK_COLOR = new Color(133, 76, 100);
    public static final Color DARK_DARCULA_ISSUE_LINK_COLOR = new Color(133, 34, 77);

    public static final Color DARCULA_TEXT_COLOR = new Color(200, 200, 200);

    // Status
    public static final Color UNDEFINED_COLOR = new Color(192, 192, 192);
    public static final Color TO_DO_COLOR = new Color(74, 103, 133);
    public static final Color IN_PROGRESS_COLOR = new Color(255, 211, 81);
    public static final Color IN_PROGRESS_TEXT_COLOR = new Color(89, 67, 0);
    public static final Color DONE_COLOR = new Color(20, 137, 44);

    // Attachments
    public static final Color ISSUE_ATTACHMENT_COLOR = new Color(235, 240, 170);
    public static final Color DARK_ISSUE_ATTACHMENT_COLOR = new Color(240, 233, 111);

    public static final Color SEARCHER_SELECTED_COLOR = new Color(232, 229, 168);


    public static JBLabel createEmptyLabel(){
        return createLabel(EMPTY_TEXT);
    }

    public static JLabel space() {
        return createLabel(" ");
    }

    public static JBLabel createLabel(String text){
        return createLabel(text, LEFT);
    }

    public static JBLabel createLabel(String text, int horizontalAlignment){
        JBLabel label = new JBLabel(text);
        label.setHorizontalAlignment(horizontalAlignment);
        return label;
    }

    public static JBLabel createIconLabel(Image icon, String text){
       return new JBLabel(text, icon, LEFT);
    }

    public static JBLabel createBoldLabel(String text){
        return createLabel(text).withFont(BOLD);
    }

    public static JBLabel createItalicLabel(String text){
        return createLabel(text).withFont(ITALIC);
    }

    public static JBLabel createPriorityLabel(JiraIssuePriority priority) {
        return nonNull(priority) ? createIconLabel(JiraIconUtil.getIcon(priority.getIconUrl()), priority.getName()) : createEmptyLabel();
    }

    public static JBLabel createLinkLabel(String text, String url){
        return new JiraLinkLabel(text, url);
    }

    public static JBLabel createStatusLabel(JiraIssueStatus status){
        return StartupUiUtil.isUnderDarcula() ? new JiraStatusLightLabel(status) : new JiraStatusDarkLabel(status);
    }

    public static JBLabel createEmptyStatusLabel(){
        JBLabel label = new JBLabel(EMPTY_TEXT, LEFT);
        label.setFont(JBFont.create(new Font("SansSerif", Font.BOLD, 9)));
        label.setBorder(JBUI.Borders.empty(2, 2, 2, 3));
        label.setOpaque(true);
        return label;
    }

    public static Color getBgRowColor(){
        return UIUtil.isUnderDarcula()? DARCULA_ISSUE_COLOR : DEFAULT_ISSUE_COLOR;
    }

    public static Color getFgRowColor(){
        return UIUtil.isUnderDarcula()? DARCULA_TEXT_COLOR : BLACK;
    }

    public static Color getBgSelectedRowColor(){
        return UIUtil.isUnderDarcula()? DARCULA_SELECTED_ISSUE_COLOR : DEFAULT_SELECTED_ISSUE_COLOR;
    }

    public static Color getFgSelectedRowColor(){
        return UIUtil.isUnderDarcula()? DARCULA_TEXT_COLOR : WHITE;
    }

    public static Color getBgRowColor(boolean isSelected){
        return isSelected ? getBgSelectedRowColor() : getBgRowColor();
    }

    public static Color getFgRowColor(boolean isSelected){
        return isSelected ? getFgSelectedRowColor() : getFgRowColor();
    }


}
