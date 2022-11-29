// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import * as vscode from 'vscode'
const path = require('node:path')
const hljs = require('highlight.js') // https://highlightjs.org/
const tm = require('markdown-it-texmath');
const md = require('markdown-it')({html:true})
                  .use(tm, { engine: require('katex'),
                             delimiters: 'dollars',
                             katexOptions: { macros: {"\\RR": "\\mathbb{R}"} } });


// This method is called when your extension is activated
// Your extension is activated the very first time the command is executed
export function activate(context: vscode.ExtensionContext) {
	context.subscriptions.push(
		vscode.commands.registerCommand('visicode-alpha.render', async () => {
			// The code you place here will be executed every time your command is executed
			let activeEditor: vscode.TextEditor | undefined = vscode.window.activeTextEditor
			if (typeof activeEditor === 'undefined') {
				vscode.window.showErrorMessage('No active text editor.')
				return
			}
			const panel = vscode.window.createWebviewPanel(
				'webviewPanel',
				path.basename(activeEditor.document.fileName),
				vscode.ViewColumn.One,
				{}
			)
			panel.webview.html = await getHTMLContent(context, activeEditor, panel)
		})
	)
}

async function getHTMLContent(context: vscode.ExtensionContext, editor: vscode.TextEditor, panel: vscode.WebviewPanel): Promise<string> {
	const cssPath = vscode.Uri.joinPath(context.extensionUri, 'highlight', 'styles', 'atom-one-dark.min.css')
	const scriptPath = vscode.Uri.joinPath(context.extensionUri, 'highlight', 'highlight.min.js')
	const cssUri = panel.webview.asWebviewUri(cssPath)
	const scriptUri = panel.webview.asWebviewUri(scriptPath)

	const config = vscode.workspace.getConfiguration("visicode-alpha")
	const viewerID = config.get("viewerOrEditorId")

	if (viewerID == "") {
		vscode.window.showErrorMessage('Please provide viewer or editor id in settings')
		return ""
	}

	let code: string = editor.document.getText()
	let html = `<link rel="stylesheet" href="${cssUri}">\n<script src="${scriptUri}"></script>\n\n`
	html += '<link  rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex/dist/katex.min.css">\n<link rel="stylesheet" href="../css/texmath.css">\n\n'
	
	let startCodePos = 0
	let startTagPos = 0
	let endTagPos = 0
	while (true) {
		startTagPos = code.indexOf("//!<visicode>", startTagPos)
		if (startTagPos == -1)
			break
		endTagPos = code.indexOf("//!</visicode>", endTagPos)
		if (endTagPos == -1) {
			vscode.window.showErrorMessage("Unmatched Tag!")
			return ""
		}

		let codeSnippet: string = code.substring(startCodePos, startTagPos)
		let taggedCodeSnippet: string = hljs.highlightAuto(codeSnippet).value
		html = 	html
				+ `<pre><code class="language-typescript" style="font-family: 'Courier New', Courier, monospace; font-size: 18px;">\n`
				+ taggedCodeSnippet
				+ `\n</code></pre>\n\n`

		let tag: string = code.substring(startTagPos + "//!<visicode>".length, endTagPos)
		let url = tag.substring(tag.indexOf("//") + 2).trim()
		let content: string = await getHTMLContentFromURL(url + `?viewerOrEditorId=${viewerID}`)
		html = 	html + content

		startCodePos = endTagPos + "//!</visicode>".length

		startTagPos++
		endTagPos++
	}

	let codeSnippet = code.substring(endTagPos == -1 ? 0 : endTagPos + "//!</visicode>".length)
	let taggedCodeSnippet: string = hljs.highlightAuto(codeSnippet).value
	html = 	html
				+ `<pre><code class="language-typescript" style="font-family: 'Courier New', Courier, monospace; font-size: 18px;">\n`
				+ taggedCodeSnippet
				+ `\n</code></pre>\n\n`	

	return html
}

async function getHTMLContentFromURL(url: string): Promise<string> {
	let protocol
	if (url.indexOf("https://") > -1)
		protocol = "https"
	else
		protocol = "http"
	const http = require(protocol)

	let promise = new Promise((resolve, reject) => {
		http.get(url, (resp) => {
			let data = ''
			
			// A chunk of data has been received.
			resp.on('data', (chunk) => {
				data += chunk;
			})
			
			// The whole response has been received. Print out the result.
			resp.on('end', () => {
				resolve(data)
			})
			
			}).on("error", (err) => {
				reject(new Error(err))
			})
	})

	let content
	try {
		content = await promise
		content = JSON.parse(content)
	} catch(err) {
		console.log(err)
		return ''
	}

	if ('status' in content) {
		console.log(`Fetch error: ${content.message}. ${content.status}`)
		return ''
	}

	if (content.type == "IMAGE")
		return `<img src="data:image;base64,${content.data}">\n\n`
	else if (content.type == "MARKDOWN") {
		return md.render(content.data) + '\n\n'
	} else {
		console.log(`Unsupported type: ${content.type}`)
		return ''
	}
}

// This method is called when your extension is deactivated
export function deactivate() {}
