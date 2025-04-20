package com.intellij.tasks.jira;


import consulo.util.lang.VersionComparatorUtil;
import jakarta.annotation.Nonnull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mikhail Golubev
 */
public class JiraVersion implements Comparable<JiraVersion> {
  // Fix for IDEA-113944
  private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(?:[^\\d]+(\\d+))?(?:[^\\d]+(\\d+))?.*");

  private final int myMajorNumber, myMinorNumber, myMicroNumber;

  public JiraVersion(int majorNumber) {
    this(majorNumber, 0, 0);
  }

  public JiraVersion(int majorNumber, int minorNumber) {
    this(majorNumber, minorNumber, 0);
  }

  public JiraVersion(int majorNumber, int minorNumber, int microNumber) {
    myMajorNumber = majorNumber;
    myMinorNumber = minorNumber;
    myMicroNumber = microNumber;
  }

  public JiraVersion(@Nonnull String version) {
    Matcher m = VERSION_PATTERN.matcher(version);
    if (!m.matches()) {
      throw new IllegalArgumentException("Illegal JIRA version number: " + version);
    }
    myMajorNumber = m.group(1) == null ? 0 : Integer.parseInt(m.group(1));
    myMinorNumber = m.group(2) == null ? 0 : Integer.parseInt(m.group(2));
    myMicroNumber = m.group(3) == null ? 0 : Integer.parseInt(m.group(3));
  }

  public int getMajorNumber() {
    return myMajorNumber;
  }

  public int getMinorNumber() {
    return myMinorNumber;
  }

  public int getMicroNumber() {
    return myMicroNumber;
  }

  @Override
  public String toString() {
    return String.format("%d.%d.%d", myMajorNumber, myMinorNumber, myMicroNumber);
  }

  @Override
  public int compareTo(@Nonnull JiraVersion o) {
    return VersionComparatorUtil.compare(toString(), o.toString());
  }
}
