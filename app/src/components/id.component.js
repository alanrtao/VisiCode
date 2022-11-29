import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import {Icon} from "@iconify/react";
import "../Project.css";

function clipboardCopy(str) {
    navigator.clipboard.writeText(str)
    alert(`link ${str} copied`)
}

function Id(props) {
    return <p>{props.label}<Icon icon="material-symbols:ios-share-rounded" width="1.5em" onClick={()=>clipboardCopy(props.value)} className="hoverable" /></p>
}

export default Id;