import React, { Component } from "react";
import { Routes, Route, Link } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";

import AuthService from "./services/auth.service";

import Login from "./components/login.component";
import Register from "./components/register.component";
import Landing from "./components/landing.component";
import Project from "./components/project.component";

class App extends Component {
  constructor(props) {
    super(props);
    this.logOut = this.logOut.bind(this);
    this.state = {
      currentUser: undefined,
    };
  }

  componentDidMount() {
    const user = AuthService.getCurrentUser();

    if (user) {
      this.setState({
        currentUser: user,
      });
    }
  }

  logOut() {
    AuthService.logout();
    this.setState({
      currentUser: undefined,
    });
  }

  render() {
    const { currentUser } = this.state;
    const loggedIn = currentUser && currentUser.username;

    return (
      <div>
        <nav className="navbar navbar-expand navbar-dark bg-dark">
          <Link className="navbar-brand">VisiCode</Link>
          <div className="navbar-nav ml-auto">
            <li className="nav-item">{
              loggedIn ?
                <Link to={"/projects"} className="nav-link">Home</Link>
                : <Link to={"login"} className="nav-link">Sign In</Link>
            }</li>
            <li className="nav-item">{
              loggedIn ?
                <a href="/login" className="nav-link" onClick={this.logOut}>Log Out</a>
                : <Link to={"register"} className="nav-link">Sign Up</Link>
            }</li>
          </div>
        </nav>

        <div>
          <Routes>
            <Route path="" element={<Login />} />
            <Route path="login" element={<Login />} />
            <Route path="register" element={<Register />} />
            <Route path="projects" element={<Landing />} />
            <Route path="projects/:projectName" element={<Project />} />
            <Route path="visit/:projectName" element={<Project />} />
          </Routes>
        </div>
      </div>
    );
  }
}

export default App;