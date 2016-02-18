package com.xiaobukuaipao.vzhi.view;

import java.util.regex.Pattern;

/**
 * Custom validator for Regexes
 */
public class RegexpValidator extends METValidator {

  private Pattern pattern;

  public RegexpValidator(String errorMessage, String regex) {
    super(errorMessage);
    pattern = Pattern.compile(regex);
  }

  @Override
  public boolean isValid(CharSequence text, boolean isEmpty) {
    return pattern.matcher(text).matches();
  }
  
}