import * as React from "react";
import {Constants} from "../Constants";
import Ajax from "./Ajax";

export default class ImageRemote extends React.Component {

  static listImages(onSuccess, onError) {
    Ajax.get(Constants.url_remote_image + "/list", null,
      onSuccess, onError);
  }

  static upload(data, onSuccess, onError) {
    Ajax.postFile(Constants.url_remote_image, null, data, onSuccess, onError);
  }
}
