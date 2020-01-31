import React from "react";
import Str from "../util/Str";

export default class Ajax extends React.Component {

  constructor() {
    super();
    this.state = {
      arr: {}
    };
    // this.get = this.get.bind(this);
  }

  static checkStatus(response) {
    if (response.status >= 200 && response.status < 300) {
      return response;
    } else {
      console.log("error msg: ", response.statusText);
      let error = new Error(response.statusText);
      error.response = response;
      throw error;
    }
  }

  static parseJSON(response) {
    return response.json();
  }

  static checkResult(response) {
    if (response.code === 0) {
      return response.data;
    }
    console.log("error msg: " + response.message);
    throw response.message;
  }

  static buildRequestUrl(url, params) {
    let i = 0;
    for (let key in params) {
      let value = params[key];
      if (typeof value === 'string') {
        // 如果是字符串参数则去掉两边的空格，
        value = Str.trim(value);
        if (value === null || value === '') {
          continue;
        }
      }
      url = url + (i === 0 ? "?" : "&") + key + "=" + value;
      i++;
    }
    console.log("请求链接：", url);
    return url;
  }

  static filterRequestData(data) {
    if (data instanceof Array) {
      let newArray = [];
      for (let i = 0; i < data.length; i++) {
        newArray.push(this.filterRequestData(data[i]));
      }
      return newArray;
    }
    let newData = {};
    for (let key in data) {
      let value = data[key];
      if (typeof value === 'string') {
        value = Str.trim(value);
        if (value === null || value === '') {
          continue;
        }
      }
      newData[key] = value;
    }
    console.log("请求体：", newData);
    return newData;
  }

  static get(url, params, onSuccess, onError) {
    url = this.buildRequestUrl(url, params);
    // mode='cors'的情况下，若服务器允许跨域访问（返回Header里有Access-Control-Allow-Origin）则可以读取响应体，否则抛出异常
    fetch(url,
      {
        method: "GET",
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
          'cache': 'default'
        },
        mode: "cors"
      })
    .then(this.checkStatus)
    .then(this.parseJSON)
    .then(this.checkResult)
    .then((res) => {
      if (typeof onSuccess === 'function') {
        onSuccess(res);
      } else {
        console.log("API请求Success回调函数必须是function类型，请检查");
      }
    })
    .catch((res) => {
      if (typeof onError === 'function') {
        onError(res);
      } else {
        console.log("API请求Error回调函数必须是function类型，请检查");
      }
    });
  }

  // data: 文件数据
  static postFile(url, params, data, onSuccess, onError) {
    url = this.buildRequestUrl(url, params);
    fetch(url,
      {
        method: "POST",
        // 不需要设置，设置了会上传失败...
        // headers: {'Content-Type': 'multipart/form-data'},
        mode: 'cors',
        body: data
      })
    .then(Ajax.checkStatus)
    .then(this.parseJSON)
    .then(this.checkResult)
    .then((res) => {
      if (typeof onSuccess === 'function') {
        onSuccess(res);
      } else {
        console.log("API请求Success回调函数必须是function类型，请检查");
      }
    })
    .catch((res) => {
      if (typeof onError === 'function') {
        onError(res);
      } else {
        console.log("API请求Error回调函数必须是function类型，请检查");
      }
    });
  }

  static post(url, params, data, onSuccess, onError) {
    url = this.buildRequestUrl(url, params);
    data = this.filterRequestData(data);
    fetch(url,
      {
        method: "POST",
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        mode: "cors",
        // 将对象转换为JSON字符串
        body: JSON.stringify(data)
      })
    .then(Ajax.checkStatus)
    .then(this.parseJSON)
    .then(this.checkResult)
    .then((res) => {
      if (typeof onSuccess === 'function') {
        onSuccess(res);
      } else {
        console.log("API请求Success回调函数必须是function类型，请检查");
      }
    })
    .catch((res) => {
      if (typeof onError === 'function') {
        onError(res);
      } else {
        console.log("API请求Error回调函数必须是function类型，请检查");
      }
    });
  }

  static put(url, params, data, onSuccess, onError) {
    url = this.buildRequestUrl(url, params);
    // data = this.filterRequestData(data);
    fetch(url,
      {
        method: "PUT",
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        mode: "cors",
        body: JSON.stringify(data)
      })
    .then(Ajax.checkStatus)
    .then(this.parseJSON)
    .then(this.checkResult)
    .then((res) => {
      if (typeof onSuccess === 'function') {
        onSuccess(res);
      } else {
        console.log("API请求Success回调函数必须是function类型，请检查");
      }
    })
    .catch((res) => {
      if (typeof onError === 'function') {
        onError(res);
      } else {
        console.log("API请求Error回调函数必须是function类型，请检查");
      }
    });
  }

  static delete(url, params, data, onSuccess, onError) {
    console.log(data);
    url = this.buildRequestUrl(url, params);
    data = this.filterRequestData(data);
    fetch(url,
      {
        method: "DELETE",
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        mode: "cors",
        body: JSON.stringify(data)
      })
    .then(Ajax.checkStatus)
    .then(this.parseJSON)
    .then(this.checkResult)
    .then((res) => {
      if (typeof onSuccess === 'function') {
        onSuccess(res);
      } else {
        console.log("API请求Success回调函数必须是function类型，请检查");
      }
    })
    .catch((res) => {
      if (typeof onError === 'function') {
        onError(res);
      } else {
        console.log("API请求Error回调函数必须是function类型，请检查");
      }
    });
  }

}