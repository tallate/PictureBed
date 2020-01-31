import React from 'react';

export default class Str extends React.Component {

  /**
   * 复制数组并去掉两边空格
   * 输入不是string类型，直接返回
   * 输入null，返回null
   * 输入" asd asd "，返回"asd asd"
   * 输入"  "，返回""
   */
  static trim(str) {
    if (null == str || !(str instanceof String)) {
      return str;
    }
    return str.replace(/(^\s*)|(\s*$)/g, "");
  }

  /**
   * 去掉左边空格
   */
  static ltrim(str) {
    if (null == str || !(str instanceof String)) {
      return str;
    }
    return str.replace(/(^\s*)/g, "");
  }

  /**
   * 去掉右边空格
   */
  static rtrim(str) {
    if (null == str || !(str instanceof String)) {
      return str;
    }
    return str.replace(/(\s*$)/g,"");
  }

}