import * as React from "react";


export default class ArrUtil extends React.Component {

  /**
   * 使用指定值填充数组
   */
  static fills(arr, val) {
    if (!arr instanceof Array) {
      console.log("ArrUtil.fills.failed：填充出错，不是数组类型")
    }
    let newArr = arr.concat();
    newArr.fill(val);
    return newArr;
  }

  /**
   * 浅拷贝整个数组
   */
  static copy(arr) {
    return arr.concat();
  }

  /**
   * 直接用splice不能用于将整个数组插入另一个数组的指定位置
   */
  static inserts(arr0, i, arr1) {
    let tmpArr = new Array(arr0.length + arr1.length);
    for (let j = 0; j < tmpArr.length; j++) {
      if (j < i) {
        tmpArr[j] = arr0[j];
      } else if (j - i < arr1.length) {
        tmpArr[j] = arr1[j - i];
      } else {
        tmpArr[j] = arr0[j - i - arr1.length + 1];
      }
    }
    return tmpArr;
  }

  /* 下面这种方法用了很多API，但是有bug，不靠谱
  static inserts(arr0, i, arr1) {
    let tmpArr = arr0.concat();
    arr1.unshift(i, 0);
    Array.prototype.splice.apply(tmpArr, arr1);
    return tmpArr;
  }
   */

  static deletes(arr0, i, count) {
    let tmpArr = arr0.concat();
    tmpArr.splice(i, count);
    return tmpArr;
  }

}
