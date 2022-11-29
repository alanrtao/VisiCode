import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import {Icon} from "@iconify/react";
import "./project.component.css";

function clipboardCopy(str) {
    navigator.clipboard.writeText(str)
    alert(`link ${str} copied`)
}

function Id(props) {
    return <div className="hoverable">{props.label}<Icon icon="material-symbols:share" width="1.5em" onClick={()=>clipboardCopy(props.value)}/></div>
}

export default Id;