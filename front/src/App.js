import React, {Component} from 'react';
// import './App.css';
import {BrowserRouter, Route} from "react-router-dom";
import {Constants} from "./Constants";
import ImageList from "./component/ImageList";

class App extends Component {
  render() {
    return (
      <div className="App">
        {/*<header className="App-header">*/}
        {/*<img src={logo} className="App-logo" alt="logo" />*/}
        {/*<p>*/}
        {/*Edit <code>src/App.js</code> and save to reload.*/}
        {/*</p>*/}
        {/*<a*/}
        {/*className="App-link"*/}
        {/*href="https://reactjs.org"*/}
        {/*target="_blank"*/}
        {/*rel="noopener noreferrer"*/}
        {/*>*/}
        {/*Learn React*/}
        {/*</a>*/}
        {/*</header>*/}
        {/*<Routes/>*/}
        <BrowserRouter>
          <div className="Router">
            <Route path={Constants.url_page_root} component={ImageList}/>
          </div>
        </BrowserRouter>
      </div>
    );
  }
}

export default App;
