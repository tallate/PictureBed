import * as React from "react";


export default class ObjectUtil extends React.Component {

  /**
   * 浅拷贝
   * 遍历对象，把属性和属性值都放在一个新对象里
   */
  static shallowCopy(obj) {
    // 只拷贝对象
    if (typeof obj !== 'object') return;
    // 根据obj的类型判断是新建一个数组还是一个对象
    var newObj = obj instanceof Array ? [] : {};
    // 遍历obj,并且判断是obj的属性才拷贝
    for (var key in obj) {
      if (obj.hasOwnProperty(key)) {
        newObj[key] = obj[key];
      }
    }
    return newObj;
  }

  /**
   * 深拷贝
   * 拷贝的时候判断属性值的类型，如果是对象，则递归调用深拷贝函数
   */
  static deepCopy(obj) {
    // 只拷贝对象
    if (typeof obj !== 'object') return;
    // 根据obj的类型判断是新建一个数组还是一个对象
    var newObj = obj instanceof Array ? [] : {};
    for (var key in obj) {
      // 遍历obj,并且判断是obj的属性才拷贝
      if (obj.hasOwnProperty(key)) {
        // 判断属性值的类型，如果是对象递归调用深拷贝
        newObj[key] = typeof obj[key] === 'object' ? deepCopy(obj[key]) : obj[key];
      }
    }
    return newObj;
  }

}

