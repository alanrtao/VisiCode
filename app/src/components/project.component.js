import React, {useEffect, useState} from "react";
import "../Project.css"
import { json, useParams } from "react-router-dom";
import axios from "axios";
import "bootstrap/dist/css/bootstrap.min.css";
import Id from "./id.component";
import NoteAdd from "./note/NoteAdd/NoteAdd.component"
import NoteContent from "./note/noteContent.component";
import "./note/note.css"
import "./project.component.css";

function projApi(str) {
    return `/api/project${str}`
}

function noteApi(str) {
    return `/api/note${str}`
}

function Project(props) {
    const {projectName} = useParams();
    const external = sessionStorage.getItem('external');
    const [project, setProject] = useState(null);
    const [noteBookData, setNoteBookData] = useState([]);

    const deleteNote = (id) => {
        console.log(id);
        console.log(project?.editorId);
        axios
            .delete(noteApi(`/${id}`), {params: {editorId: project?.editorId}})
            .then(response => {
                if (response.data.error == null) {
                    // setProject(response.data)
                } else {
                    props.router.navigate("/projects")
                }
            });
    };

    // component did mount / unmount
    useEffect(() => {
        if (external && external !== '') {
            // using other person's project
            // the permission will be set in project.permission as either view or edit
            axios
                .get(projApi(`/visit/${external}`))
                .then(response => {
                    if (response.data.error == null) {
                        setProject(response.data)
                    } else {
                        props.router.navigate("/projects")
                    }
                });
        } else {
            // viewing my own project
            axios
                .get(projApi(`/${projectName}`))
                .then(response => {
                    if (response.data.error == null) {
                        setProject(response.data)
                    } else {
                        props.router.navigate("/projects")
                    }
                });
        }
        return function cleanup() {
            sessionStorage.removeItem('external');
        }
    }, []);

    return <div className="app">
        <h1>{projectName}</h1>
        <div className="project info">
            { project?.viewerId && <Id label="Viewing Link" value={project.viewerId}/> }
            { project?.editorId && <Id label="Editing Link" value={project.editorId}/> }
        </div>
        <div className="note-section">
            <NoteAdd editorId={project?.editorId}/>
            <section className="notebook-container">
                <div className="notebook">

        <div className="notebook">
            <div className="note-section">
                <NoteAdd editorId={project?.editorId}/>
            </div>{
            project?.notes.map((note) => (
                <React.Fragment key={note}>
                    <div className="notebookInfo" key={note}>
                        <div className="notebookInfo-title">
                            <Id label="Link" value={ new URL(`${window.location.origin}/api/note/${note}?viewerOrEditorId=${project.viewerId}`) } />
                            { project?.editorId && 
                            <div className="remove" onClick={() => deleteNote(note)}>🗑️</div>}
                        </div>
                        <NoteContent className="notebookInfo-description" id={note} link={project.editorId || project.viewerId}/>
                    </div>
                </React.Fragment>
            ))
            }
        </div>
    </div>
}

export default Project;