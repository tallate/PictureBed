import React from 'react';

export default class Time extends React.Component {

  static timestamp2date(timestamp) {
    let date = new Date(timestamp);
    let y = date.getFullYear();
    let m = '0' + (date.getMonth() + 1);
    let d = "0" + date.getDate();
    return y + '-' + m.substring(m.length - 2, m.length) + '-' + d.substring(d.length - 2, d.length);
  }

}