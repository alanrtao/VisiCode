// components/NoteAdd/NoteAdd.js
import React, {useEffect, useState} from "react";
import "./NoteAdd.css";
import axios from "axios";

function noteApi(str) {
    return `/api/note${str}`
}
const NoteAdd = (editorId) => {
    const [file, setFile] = useState(null);
    const [description, setDescription] = useState("");


    // useEffect(() => {
    //     formData.append("file", file);
    //     console.log(formData);
    // })
    const handleFileChange = (event) => {
        setFile(event.target.files[0]);
    };

    const handleDescriptionChange = (event) => {
        setDescription(event.target.value);
    };


    const addTextNote = () => {
        if (description !== "") {
            console.log(description);
            console.log(editorId.editorId);
            axios
                .post(noteApi('/text'), {text: description}, { headers: { "Content-Type": "application/json; charset=UTF-8" }, params: {editorId: editorId.editorId}})
                .then(response => {
                    if (response.data.error != null) {
                        alert("Fail to add note!")
                    } else {
                        console.log(response);
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
                        console.log(response);
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
                <div className="form-group">
                    <input
                        type="file"
                        className="noteadd-description"
                        name="noteadd-description"
                        placeholder="Click to add image"
                        onChange={handleFileChange}
                    />
                </div>
                <div className="form-group">
                  <textarea
                      name="noteadd-description"
                      className="noteadd-description"
                      placeholder="Note Description"
                      value={description}
                      onChange={(val) => handleDescriptionChange(val)}
                  ></textarea>
                </div>
                <div id="button-group">
                    <button onClick={() => addTextNote()}>Add a Text Note</button>
                    <button onClick={addImgNote}>Add an Img Note</button>
                </div>

            </div>
        </>
    );
};

export default NoteAdd;