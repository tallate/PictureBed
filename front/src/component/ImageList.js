import React from "react";
import {Col, Row} from "react-bootstrap";
import ImageRemote from "../remote/ImageRemote";
import {Constants} from "../Constants";
import "./ImageList.css";
import {toast, ToastContainer} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import {Progress} from 'reactstrap';

export default class ImageList extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      imageURLs: [],
      selectedFile: null,
      loaded: 0
    };
  }

  /////////////////////////////////////////////////////////////////
  //////////////////////////// 图片列表 ////////////////////////////
  ////////////////////////////////////////////////////////////////

  /**
   * 初始化时调用
   */
  componentDidMount() {
    this.handleRefresh();
  }

  getImageGrid(imageURLs) {
    if (imageURLs === null || imageURLs === undefined) {
      return [];
    }
    let rows = [];
    let cols = [];
    for (let i = 0; i < imageURLs.length; i++) {
      cols.push(
        <Col className="ImageList-Col">
          <img className="ImageList-img" src={Constants.url_image_root + imageURLs[i]} alt="image"/>
          <p>图片路径：{Constants.url_image_root + imageURLs[i]}</p>
        </Col>
      );
      if ((i % 4 === 0 && i > 0)
        || i === imageURLs.length - 1) {
        rows.push(
          <div>
            <Row className="ImageList-Row">
              {cols}
            </Row>
          </div>
        );
        cols = [];
      }
    }
    return rows;
  };

  handleRefresh() {
    ImageRemote.listImages(
      (res) => {
        if (res === null || res === undefined) {
          alert("res 为空");
        }
        let newState = Object.assign({}, this.state,
          {
            imageURLs: res
          });
        this.setState(newState);
      },
      (res) => {
        alert("查询图片列表失败");
      }
    )
  }

  /////////////////////////////////////////////////////////////////
  //////////////////////////// 上传图片 ////////////////////////////
  ////////////////////////////////////////////////////////////////
  checkMimeType = (event) => {
    //getting file object
    let files = event.target.files;
    //define message container
    let err = [];
    // list allow mime type
    const types = ['image/png', 'image/jpeg', 'image/gif'];
    // loop access array
    for (var x = 0; x < files.length; x++) {
      // compare file type find doesn't matach
      if (types.every(type => files[x].type !== type)) {
        // create error message and assign to container
        err[x] = files[x].type + ' is not a supported format\n';
      }
    }
    for (let z = 0; z < err.length; z++) {// if message not same old that mean has error
      // discard selected file
      toast.error(err[z]);
      event.target.value = null;
    }
    return true;
  };
  maxSelectFile = (event) => {
    let files = event.target.files;
    if (files.length > 3) {
      const msg = 'Only 3 images can be uploaded at a time';
      event.target.value = null;
      toast.warn(msg);
      return false;
    }
    return true;
  };
  checkFileSize = (event) => {
    let files = event.target.files;
    let size = 2000000;
    let err = [];
    for (let x = 0; x < files.length; x++) {
      if (files[x].size > size) {
        err[x] = files[x].type + 'is too large, please pick a smaller file\n';
      }
    }
    for (let z = 0; z < err.length; z++) {// if message not same old that mean has error
      // discard selected file
      toast.error(err[z]);
      event.target.value = null
    }
    return true;
  };
  onChangeHandler = event => {
    let files = event.target.files;
    if (this.maxSelectFile(event) && this.checkMimeType(event)
      && this.checkFileSize(event)) {
      // if return true allow to setState
      this.setState({
        selectedFile: files,
        loaded: 0
      })
    }
  };
  onClickHandler = () => {
    const data = new FormData();
    for (let x = 0; x < this.state.selectedFile.length; x++) {
      data.append('file', this.state.selectedFile[x])
    }
    // 使用fetch上传文件，但是不支持获取进度
    ImageRemote.upload(data,
      (res) => {
        alert("上传文件成功");
      },
      (res) => {
        alert("上传文件失败")
      });
    // 原代码通过axios上传文件
    // axios.post(Constants.url_remote_image, data, {
    //   onUploadProgress: ProgressEvent => {
    //     this.setState({
    //       loaded: (ProgressEvent.loaded / ProgressEvent.total * 100),
    //     })
    //   },
    // })
    // .then(res => { // then print response status
    //   toast.success('upload success')
    // })
    // .catch(err => { // then print response status
    //   toast.error('upload fail')
    // })
  };

  getSubmitImageForm() {
    return (
      <div className="container">
        <div className="row">
          <div className="offset-md-3 col-md-6">
            <div className="form-group files">
              <label>Upload Your File </label>
              <input type="file" className="form-control" multiple
                     onChange={this.onChangeHandler}/>
            </div>
            <div className="form-group">
              <ToastContainer/>
              <Progress max="100" color="success"
                        value={this.state.loaded}>{Math.round(this.state.loaded,
                2)}%</Progress>

            </div>

            <button type="button" className="btn btn-success btn-block"
                    onClick={this.onClickHandler}>Upload
            </button>

          </div>
        </div>
      </div>
    );
  }

  render() {
    let submitImageForm = this.getSubmitImageForm();
    let imageListGrid = this.getImageGrid(this.state.imageURLs);

    return (
      <div className="ImageList-body">
        {submitImageForm}
        <br/>
        <br/>
        {imageListGrid}
      </div>
    );
  }
}
