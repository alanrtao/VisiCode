import React, { Component } from "react";

import { withRouter } from '../common/with-router';
import { Link } from "react-router-dom";

import axios from "axios";

import "../Project.css";
import "bootstrap/dist/css/bootstrap.min.css";

function projApi(str) {
  return `/api/project${str}`
}

class Landing extends Component {
  constructor(props) {
    super(props);
    this.state = {
      projects: []
    }
    this.addProject = this.addProject.bind(this);
    this.externalProject = this.externalProject.bind(this);
    this.removeProject = this.removeProject.bind(this);
  }

  componentDidMount() {
    axios
      .get(projApi(''))
      .then(response => {
        if (response.data.error == null) {
          this.setState({ projects: response.data })
        }
        return response.data;
      });
  }

  addProject() {
    const name = prompt('Create Project');
    if (name) {
      axios
        .post(projApi('/create'), { name })
        .then(response => {
          if (response.data.error == null) {
            alert(`Project ${name} created successfully`)
            this.state.projects.unshift(name)
            this.setState({ projects: this.state.projects })
          } else {
            alert(`Project ${name} cannot be created`)
          }
        })
    }
  }

  removeProject(name) {
    const confirmation = prompt(`Type in the project name ${name} in full to confirm deletion...`)
    if (confirmation === name) {
      axios
      .post(projApi("/remove"), { name })
      .then(response => {
        if (response.data.error == null) {
          alert(`Project ${name} deleted successfullly`)
          const i = this.state.projects.indexOf(name)
          if (i > -1) {
            this.state.projects.splice(this.state.projects.indexOf(name), 1)
            this.setState({ projects: this.state.projects })
          }
        } else {
          alert(`Project ${name} cannot be deleted`)
        }
      })
    }
    else if (confirmation) {
      alert('Incorrect name typed, deletion canceled')
    }
  }

  externalProject() {
    const link = prompt('Edit Link');
    if (link) {
      axios
        .get(projApi(`/visit/${link}`))
        .then(response => {
          if (response.data.error == null) {
            sessionStorage.setItem("external", link);
            this.props.router.navigate(`/visit/${response.data.name}`);
          }
        });
    }
  }

  render() {
    return <div>
      <button onClick={this.externalProject}>Edit or View With Link</button>
      <div id="landing">
        <button className="operate" onClick={this.addProject}>+</button>
        {(this.state?.projects || []).map(project => (
            <div className="frame">
              <Link to={`${project}`} onClick={()=>sessionStorage.removeItem('external')}>
              <h3>{project}</h3>
              </Link>
              <div class="hoverable" onClick={() => this.removeProject(project)}>🗑️</div>
            </div>)
        )}
      </div>
    </div>
  }
}

export default withRouter(Landing);