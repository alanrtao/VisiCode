# Deploying to GCP
- register Google account credentials and billing
- to create project and deploy, go to `https://console.cloud.google.com/`
  - top bar to select project, click "NEW PROJECT"
  - in new project, put in project name
  - search `appengine` in search bar, click App Engine and enable the API
  - click "create application" and select `us-east1` for Southeast
  - follow link to download google SDK and run `gcloud init`, select right credentials and project
  - then, run `./mvnw spring-boot:run -P dev` and check things under localhost, if nothing goes wrong run `./mvnw package appengine:deploy` to deploy the app
- to log in with key instead of credential, go to `https://console.cloud.google.com/`
  - on top left button, click and go to IAM & Admin -> Service Accounts
  - click "Create Service Account" and put in credentials
  - go to "KEYS" and "ADD KEY" -> "Create new key", select "json"
  - check your download directory and there should be a `.json` file that can be shared with other people in the group, but not with the public
- project currently deployed under `https://visi-code.ue.r.appspot.com/`

# Testing the Project
## Backend Testing
- open the project using IntelliJ, navigate to `src/test/java`
  - open test files and run test cases contained in the classes / methods
  - optionally, run with coverage
    - the current coverage is around 93%
## Frontend Testing
- to run E2E tests, navigate to `nightwatch_test` and run `npx nightwatch`
  - be sure to have the app already running locally when testing localhost (see later sections)

# Running Part of the Website Locally
## Frontend
- install NodeJs, which comes with `npm`
- navigate to `app` directory under project directory, and run `npm install`, `npm run start`
- navigate to `localhost:3000` which shows the homepage, but login and signup shouldn't be possible since API isn't running
## Backend
- under project directory, run `./mvnw spring-boot:run -P dev`
- sending requests to `localhost:8080/...` should receive valid responses (not necessarily OK ones, but makes sense in context of API)

# Running the Entire Website Locally
- under project directory, run `./mvnw spring-boot:run -P prod`
- go to `localhost:8080` and the main page should show
- make sure to already be logged in to GCloud or have a private `.json` key at the project root folder
  - the GCloud account must be added as contributor on the project

# Running the Extension

- navigate to `visicode` and run `npm install`
- open `visicode` folder with VSCode, optionally install the Code Runner extension
- press F5 and select to debug the extension
  - a new VSCode window should open with a purple bar at the bottom, this is the debug target
  - in the debug target, open the main repository folder (`VisiCode`, not `VisiCode/visicode`, opening the `visicode` folder again will just jump back to the original window)
  - open the file `visicode/examples/example.cpp`
    - to obtain note comments, navigate to the website and create your project, then add a note to it and click "View Link" with the share icon
      - this should copy something like the following to your clipboard:
        ``` C
          //!<visicode>
          // https://visi-code.ue.r.appspot.com/api/note/dc45e3fc-5ad3-40f6-b35f-c4b1a30f0c7e
          //!</visicode>
        ```
      - paste the special format comment into `example.cpp`
      - there are already comments associated with the test user's "project 1" presented during the demo, the viewerId for that is `a6de1fe6-b596-4aea-9410-55c7c16fcb1b` (see next section for use)
  - go to VSCode top bar, navigate to `File/Preferences/Settings/Extensions/Visicode/ViewerOrEditorId`, and input the project viewer id to the settings
  - go back to `example.cpp` and input command (using `Ctrl+Shift+P`) `VisiCode:Render Source Code`, a new tab should open with the renderered code

# Issues
- some issues we had with `mvnw` is the execution permission
  - for Mac, add execution permission using `chmod +x mvnw`
- issues involving Google default credentials
  - acquire the project json key and the application.properties file from project owner