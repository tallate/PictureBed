import * as React from "react";
import {Constants} from "../Constants";
import Ajax from "./Ajax";

export default class TargetRemote extends React.Component {

  static save(target, onSuccess, onError) {
    Ajax.post(Constants.url_remote_target + "/save", null, target,
      onSuccess, onError);
  }

  // 获取某一目标详细信息
  static get(id, onSuccess, onError) {
    let params = {id: id};
    Ajax.get(Constants.url_remote_target + "/get", params,
      onSuccess, onError);
  }

  // 根据id查询目标
  static list(ids, onSuccess, onError) {
    let params = {ids: ids};
    Ajax.get(Constants.url_remote_target + "/list", params,
      onSuccess, onError);
  }

  // 列出下一级的目标
  static listByPid(pid, onSuccess, onError) {
    let params = {pid: pid};
    Ajax.get(Constants.url_remote_target + "/listByPid", params,
      onSuccess, onError);
  }

  static listByPids(pids, onSuccess, onError) {
    let params = {pids: pids};
    Ajax.get(Constants.url_remote_target + "/listByPids", params,
      onSuccess, onError);
  }

}
