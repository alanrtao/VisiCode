import React, { useEffect, useState } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import "../../Project.css";
import axios from "axios";

function NoteContent(props) {
    const [content, setContent] = useState(null);
    // component did mount
    useEffect(()=>{
        axios
        .get(`/api/note/${props.id}?viewerOrEditorId=${props.link}`)
        .then(response => {
            if (response.data.error == null) {
                setContent(response.data);
            } else {
                setContent(response.data.error || 'invalid request')
            }
        })
    }, []);

    return <div>
    {
        content?.type==='IMAGE' ? <img src={`data:image/png;base64, ${content.data}`} alt={`note {props.id}`}/> : 
        (content?.type==='MARKDOWN' ? <p>{content.data}</p> : <p>{content}</p>)
    }
    </div>
}

export default NoteContent;