import React, {useEffect, useState} from "react";
import { useNavigate } from "react-router-dom";
import "./NoteAdd.css";
import axios from "axios";

function noteApi(str) {
    return `/api/note${str}`
}
const NoteAdd = (editorId) => {
    let navigate = useNavigate();
    const [file, setFile] = useState(null);
    const [description, setDescription] = useState("");


    const handleFileChange = (event) => {
        setFile(event.target.files[0]);
    };

    const handleDescriptionChange = (event) => {
        setDescription(event.target.value);
    };


    const addTextNote = () => {
        if (description !== "") {
            axios
                .post(noteApi('/text'), {text: description}, { headers: { "Content-Type": "application/json; charset=UTF-8" }, params: {editorId: editorId.editorId}})
                .then(response => {
                    if (response.data.error != null) {
                        alert("Fail to add note!")
                    } else {
                        window.location.reload();
                    }
                }).catch((error) => {
                console.log("Problem submitting New Post", error);
            });
        }
    };

    const addImgNote = async(event) => {
        event.preventDefault();
        const formData = new FormData();
        formData.append("file", file);
        console.log(...formData);
        if (file !== "") {
            axios
                .post(noteApi('/file'),  formData, { params: {editorId: editorId.editorId}})
                .then(response => {
                    if (response.data.error != null) {
                        console.log(response.data.error);
                        alert("Fail to add note!")
                    } else {
                        window.location.reload();
                    }
                }).catch((error) => {
                console.log("Problem submitting New Post", error);
            });
        }
    };

    return (
        <>
            <div className="noteadd">
                <h3>Add a New Note</h3>
                <h5>Upload an img OR enter some text to create your note!</h5>
                <div className="form-group">
                    <input
                        type="file"
                        className="noteadd-description"
                        name="noteadd-description"
                        placeholder="Click to add image"
                        onChange={handleFileChange}
                    />
                    <button onClick={addImgNote}>Add an Img Note</button>
                </div>
                <div className="form-group">
                  <textarea
                      name="noteadd-description"
                      className="noteadd-description"
                      placeholder="Enter Text/Markdown for Note"
                      value={description}
                      onChange={(val) => handleDescriptionChange(val)}
                  ></textarea>
                    <button onClick={() => addTextNote()}>Add a Text Note</button>
                </div>
            </div>
        </>
    );
};

export default NoteAdd;