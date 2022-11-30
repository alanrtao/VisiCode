# VisiCode Project Report

## Introduction 
Jupyter notebook is an immensely useful technology for all sorts of python projects, from advanced machine learning projects to students learning the language for the first time. In a teaching scenario, the functionality of Jupyter notebook even allows instructors to include graphs, figures, instructions, and more in projects assigned to students. However, not all programming courses are taught in python. It would be nice if this functionality and ease of use could be brought to other programming courses. Therefore, we propose VisiCode, a Visual Studio Code extension and web application that brings the visual strengths of Jupyter to other language courses, without relying on the notebook itself. VisiCode will allow instructors to embed images and formulas within code. Additionally, these persistent notes will always be recoverable in a given project without the need for version control. This will be especially useful for introductory programming courses where the students may not have any prerequisite knowledge of VCS like Github. VisiCode will bridge the gap between students and instructors by prioritizing a clear and concise design, low technical skill barrier to entry, and no restrictions on programming language used in a given course. This project is intended for small-scale personal/educational use and will not require much scalability.

## Motivation
Jupyter notebook, and similar notebook document editors that currently exist on the market caters to a more advanced audience that than of VisiCode. This concept is further discussed in the Literature Review, but VisiCode fits into a niche that is currently uncaptured: small scale, introductory learners. This could be anything from self taught programmers to university students in low-level courses, such as CS1331 here at Georgia Tech. VisiCode brings a unique mix of ease-of-use, collaboration, and efficiency to enhance the workflow of both students and instructors.

## Project Scope

VisiCode is accesible as both a VSCode extension and a web application hosted through the Google Cloud Platform (GCP). It provides the functionality of user creation/login, creation/editing/deletion of notebook documents, and creation/editing/deletion of notes within a notebook document. These notes may be in the form of computer code, plain text, images, graphs, figures, etc. User credentials and projects are stored using Google Datastore.

## Project Management

VisiCode development is broken down between team members in the following roles:

- Project Manager - Alan Tao
- Backend Lead - Hanzhang Liu
- Frontend Lead -Yunhao Hu
- Testing Lead - Dom Fernando
- Report/Presentation Lead - Garrett Cooley


Additionally, project tasks and bugs are tracked using the Trello project tracking tool. This allows team members to update each other on task/bug progress, completion, and creation in real time. We also use Discord for instant communcation in a group setting and virtual meetings.

## Literature Review

VisiCode’s primary purpose is to extend the functionality of the Jupyter notebook, with an emphasis on ease of use. The extension is designed for small scale use that is easy to jump into for beginners. Therefore, it is important to first understand what the Jupyter notebook is capable of, and some basic terms within the software. Everything begins with the notebook document, as described in the *Jupyter/IPython Notebook Quickstart Guide*. It states “Notebook documents are documents produced by the Jupyter Notebook App, which contain both computer code and rich text elements (paragraph, equations, figures, links, etc…).” In other words, a notebook document contains various “notes,” as they are referred to in VisiCode. These notes can contain the types of data/information listed in the quote. The Jupyter Notebook App is software that “allows editing and running notebook documents via a web browser.” It is here that users are able to display, edit, and run notebook documents. However, trouble arises for inexperienced uses when attempting to use notebook documents for languages other than Python. In order to use Jupyter notebook for other languages, users must manually change the kernel used in their instance of the Jupyter Notebook App. Jupyter defines a notebook kernel as “a computational engine that executes the code contained in a notebook document.” All notebook documents require an associated kernel to be run and edited. VisiCode allows beginner users to bypass this headache and create their own notebooks in whichever language they want without manually changing the kernel. This is especially useful because it allows introductory users in lower-level classes to participate in projects and receive instructions/information from instructors. We allow this ease-of-use while maintaining the useful functions of Jupyter notebook, such as adding images, figures, tables, and more. Web forums like StackOverflow and Quora contain many posts from users seeking help with running a notebook document in other languages. With VisiCode, it is as simple as downloading an extension from VSCode, or navigating to the website hosted by Google Cloud Platform (GCP). 

There are other similar projects out there as well. One example is Deepnote, a notebook document service designed specifically for data scientists. This product shares our focus on collaboration, but its target audience is experienced, industry-level programmers and data scientists. In the *Deepnote Documentation*, the authors state “Deepnote allows you to easily work on your data science projects, together in real-time and in one place with your friends and colleagues… Deepnote is built for the browser so you can use it across any platform.” To reiterate, this purpose outlined by the creators prioritizes connected use between collaborators, but it is not suitable for beginners. VisiCode will allow first-time programmers to jump into collaboration with peers and instructors at a smaller scale, and fit into a different niche than Deepnote. Other products like Deepnote exist on the market, however there is an apparent trend of catering to a higher-level audience that has outgrown Jupyter notebook, rather than an audience that has not yet grown into it.

## Software Technologies

### Frontend

We have set up React.JS JavaScript framework, HTML, and CSS to build the frontend of the web application. The interactive nature of React.JS will allow us to let users create and delete notes on the fly, and display the desired data within. Additionally, we use the VSCode API in order to construct the extension within the environment of VScode.

### Backend

We have set up Maven for dependency management, along with SpringBoot framework for developing the backend server architecture and running the application. We use Google Cloud Platform for website hosting and Google Datastore to create and store user information and user project notebooks.

### Tracking, Version Control, & Testing

We use a Trello board for tracking project schedule, assigning team members to tasks, meeting deadlines, and fixing bugs. We use our Discord for general communication and team updates. We have also created a github repository for VisiCode and will use git for version control. Additionally, we use a combination of whitebox and blackbox testing using frameworks like JUnit, Mockito, and TSLgenerator. We also use NightwatchJS to tend frontend functionality.

## Project Lifecycle

We use the Waterfall life cycle model to structure our project such that we are completing a sequential series of tasks that build on one another. This model is a good fit for VisiCode, as the requirments and technolgies to be used were clearly defined early on. Other benefits the waterfall model are:

1. Early error detection
2. Clear and orderly progression in development
3. Concise project management.

## Requirements

### User Characteristics

The assumed skill-level of VisiCode users is inherently low. Users are expected to know how to access a website given a url, or how to navigate the VSCode user interface and extension browser. Beyond this, the only other assumptions made by the user are that of text editors, including typing code, informative text, inserting images, etc.

### Functional

- User Actions
   1. User shall register with a username and password
   2. User shall input credentials to access application
   3. User may create notebook document
   4. User may delete notebook document
   5. User may add notes containing computer code in other languages beyond python, paragraph text, images, graphs figures, etc.
   6.  User may delete notes from a document
   7.  User may share link to a document with other users
   8.  Multiple users may share a single document

- User Machine
  1. User machine must have stable network connection
  2. User must have either VSCode and VisiCode extension installed, or have web browser installed to access web application format

- Technical Requirements
  1. Application only supports non-python and non-frontend languages (i.e. no CSS/HTML)
  2. A user's project collection must fit in 1mb of data
  3. Notes per project must fit in 1mb of data
  4. Content of single note must fit in 1mb of data 

### Non-Functional

- Security
    1. User passwords are salted and hashed
    2. User entities are stored in Google Datastore with security benefits contained therein
    3. User documents and editing history are not available to other users
- Usability
    1. VisiCode can be used by anyone with VSCode or web browser installed
    2. No prior coding or Jupyter notebook experience is required
    3. Users are able to collaborate on notebook documents
    4. Application UI is responsive and intuitive
    5. Application is collaborative in nature and documents will thus be shareable


## Design

### Architectural (High-Level) Design

### Class Diagram

### Sequence Diagram

### Component Diagram

## Testing

### Black-box

We apply a systematic functional-testing approach in the black-box component of our testing strategy. We use Nightwatch.js end-to-end testing framework in order to verify request functionality and frontend response. The following tests were applied:

1. Log in with valid credentials and check that home page opens with title
   - Expected Result - brand text "VisiCode" is on navbar
   - Actual Result - passed as expected

2. Login with valid username but wrong password 
  - Expected Result - authentication error message
  - Actual Result - Passed as expected

3. Go to Homepage url without logging in
  - Expected Result - HTTP 401 error
  - Actual Result - Passed as expected

4. Login with valid credentials, then create duplicate project
   - Expected Result - project is added of the specified name
   - Acutal Result - Passed as expected

5. Sign up new user with unique username and password
   - Expected Result - alert message stating "user registered successfully"
    - Actual Result - Passed as expected

6. Create new project with unique name and check if displayed
   - Expected Result - new project div with correct name displayed
   - Actual Result - Passed as Expected

### White-box

We use Mockito and JUnit testing frameworks for the white-box component of our testing. This section of our testing strategy runs actual line of code to identify where a faulty statement is, rather than simply testing the output from a given input as in the last section. More specifically, our tests aim to verify the functionality of all possible user actions within the system. The following tests were applied:

#### Project Controller

- myProjectsUnauthorized
  - Action/Input - Attempt to authorize project that current user has no access to
  - Expected Result - Access denied, bad request
  - Actual Result - Pass as expected

- myProjectsEmpty
  - Action/Input - Attempt to access projects page when no projects exists
  - Expected Result - No projects exist when page loads
  - Actual Result - Pass as expected

- myProjectsMany
  - Action/Input - Attempt to access projects page when three projects exist
  - Expected Result - Three projects are displayed when page loads
  - Actual Result - Pass as expected

- createProject
  - Action/Input - Execute code for creating new project with arbitrary attributes
  - Expected Result - Project is created and stored successfully with specified attributes
  - Actual Result - Pass as expected

- createProjectDuplicateName
  - Action/Input - Execute code for creating new project where project name is specifed as pre-existing project name
  - Expected Result - Project creation fails with bad request
  - Actual Result - Pass as expected

- createProjectTooMany
  - Action/Input - Execute code for creating new project when maximum number of projects has already been reached
  - Expected Result - Project creation fails with bad request
  - Actual Result - Pass as expected

- removeProjectNonExistentInUser
  - Action/Input - Attempt to remove project from a user that does not exist in database
  - Expected Result - Removal fails with bad request
  - Actual Result - Pass as expected

- removeProjectUnauthorized
  - Action/Input - Execute code for project removal from unauthorized user
  - Expected Result - Removal fails with bad request
  - Actual Result - Pass as expected

- removeProjectValid
  - Action/Input - Execute code for project removal from authorized user
  - Expected Result - Removal succeeds
  - Actual Result - Pass as expected

- viewOwnProjectUnauthorized
  - Action/Input - Execute code to view project from unauthorized user
  - Expected Result - Project is not displayed with bad request
  - Actual Result - Pass as expected

- viewOwnProjectNonExistent
  - Action/Input - Execute code to view project using project name that does not exist
  - Expected Result - No project is displayed with bad request
  - Actual Result - Pass as expected

- viewOwnProjectValid
  - Action/Input - Execute code to view project from authorized user
  - Expected Result - Project notes are displayed to user
  - Actual - Pass as expected

- viewOtherProject
  - Action/Input - Execute code to view the project of another user after link has been shared
  - Expected Result - Other project notes are displayed
  - Actual - Pass as expected

- viewOtherProjectWithEditPermissions
  - Action/Input - Execute code for viewing other project and making arbitray sample edits
  - Expected Result - Project is displayed with edits shown
  - Actual - pass as expected

- viewOtherProjectNonExistent
  - Action/Input - Execute code for viewing the project of another user with project name that does not exist
  - Expected Result - No project is displayed with bad result
  - Actual - Pass as expected

- addTextNoteLarge
  - Action/Input - Execute code for adding large text note that does not exceed maximum note size
  - Expected Result - Note is added and displayed successfully
  - Actual - Pass as expected

- addTextNoteOversized
  - Action/Input - Execute code for adding text note of size greater than maximum length allowed
  - Expected Result - Note addition fails with bad request
  - Actual - Pass as expected

- addTextNoteNoPermission
  - Action/Input - Execute code for adding text note without proper permissions
  - Expected Result - Note addition fails with bad request
  - Actual - Pass as expected

- addTextNoteViewer
  - Action/Input - Execute code for adding note from user with only viewing permissions
  - Expected Result - Note addition fails with bad request
  - Actual - Pass as expected

- addTextNoteEditor
  - Action/Input - Execute code for adding text note from user with editing permisions
  - Expected Result - Note addition succeeds and is displayed in project
  - Actual - Pass as expected

- addNoteTooMany
  - Action/Input - Execute code for adding note to project that already contains maximum number of notes
  - Expected Result - Note addition fails with bad request
  - Actual - Pass as expected

- addFileNoteLarge
  - Action/Input - Execute code for adding a file note of size near but not exceeding maximum
  - Expected Result - Note addition succeeds and is displayed in project
  - Actual - Pass as expected

- addFileNoteOversized
  - Action/Input - Execute code for adding a file note of size exceeding maximum
  - Expected Result - Note addition fails with bad request
  - Actual - Pass as expected

- addFileNoteNoPermission
  - Action/Input - Execute code for adding a file note from user with no permissions
  - Expected Result - Note addition fails with bad request
  - Actual - Pass as expected

- addfileNoteViewer
  - Action/Input - Execute code for adding a file note from user with only viewing permissions
  - Expected Result - Note addition fails with bad request
  - Actual - Pass as expected

- addfileNoteEditor
  - Action/Input - Execute code for adding a file note from user with editing permissions
  - Expected Result - Note addition succeeds
  - Actual - Pass as expected

- removeNoteNoPermission
  - Action/Input - Execute code for removing a note from user with no permissions
  - Expected Result - Note removal fails with bad request
  - Actual - Pass as expected

- removeNoteViewer
  - Action/Input - Execute code for removing a note from user with only viewing permissions
  - Expected Result - Note removal fails with bad request
  - Actual - Pass as expected

- removeNoteNotInProject
  - Action/Input - Execute code for removing a note that does not exist within project
  - Expected Result - Note removal fails with bad request
  - Actual - Pass as expected

- removeNoteValid
  - Action/Input - Execute code for removing a note from user with editing permissions
  - Expected Result - Note removal succeeds
  - Actual - Pass as expected

- viewNoteNoPermission
  - Action/Input - Execute code for displaying a note to user with no permissions
  - Expected Result - Note is not displayed with bad request
  - Actual - Pass as expected

- viewNoteViewer
  - Action/Input - Execute code for displaying a note to user with viewing permissions
  - Expected Result - Note is displayed
  - Actual - Pass as expected

- viewNoteEditor
  - Action/Input - Exectue code for displaying a note to user with editing permissions
  - Expected Result - Note is displayed
  - Actual - Pass as expected

#### User Controller

- userCreatePass
  - Action/Input - Execute code for new user creation with correctly formatted username and password
  - Expected Result - User is created and added to datastore
  - Actual - Pass as expected

- userCreateFail
  - Action/Input - Execute code for new user creation with incorrectly formatted username and password
  - Expected Result - User creation fails and is not added to database
  - Actual - Pass as expected

- userLoginPass
  - Action/Input - Execute code for user login with valid credentials
  - Expected Result - Login successful
  - Actual - Pass as expected

- userLoginFail
  - Action/Input - Execute code for user login with invalid credentials
  - Expected Result - Login fails
  - Actual - Pass as expected

## User Interface

## References