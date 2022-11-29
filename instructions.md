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
- open the project using IntelliJ, navigate to `src/test/java`
  - open test files and run test cases contained in the classes / methods
- to run E2E tests, navigate to `nightwatch_tests` and run `npx nightwatch`
  - be sure to have the app already running locally when testing localhost (see later sections)

# Running Part of the Project Locally
## Frontend
- install NodeJs, which comes with `npm`
- navigate to `app` directory under project directory, and run `npm install`, `npm run start`
- navigate to `localhost:3000` which shows the homepage, but login and signup shouldn't be possible since API isn't running
## Backend
- under project directory, run `./mvnw spring-boot:run -P dev`
- sending requests to `localhost:8080/...` should receive valid responses (not necessarily OK ones, but makes sense in context of API)

# Running the Entire Project Locally
- under project directory, run `./mvnw spring-boot:run -P prod`
- go to `localhost:8080` and the main page should show
- make sure to already be logged in to GCloud or have a private `.json` key at the project root folder
  - the GCloud account must be added as contributor on the project

# Issues
- some issues we had with `mvnw` is the execution permission
  - for Mac, add execution permission using `chmod +x mvnw`