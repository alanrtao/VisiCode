// VisiCode e2e (blackbox) testing suite
function projectSelector(name) {
    return {
        selector: `.//h3[.="${name}"]`,
        locateStrategy: 'xpath'
      }
}
module.exports = {
    '@tags': ['base-tests'],
    // "0. Visit VisiCode Login Page and check brand text": browser => {
    //     browser.url("http://localhost:8080")
    //     browser.pause(6000)
    //     .waitForElementVisible(".navbar-brand")
    //     .assert.containsText(".navbar-brand", "VisiCode")
    // },
    // "1. Log in with valid credentials and check that home page opens with title": browser => {
    //     const usernameInputSelector = 'input[name="username"]';
    //     const passwordInputSelector = 'input[name="password"]';
    //     const loginButton = '.btn-primary[name="Login"]';

    //     browser.url("http://localhost:8080")
    //     .setValue(usernameInputSelector, "test")
    //     .pause(1000)
    //     .setValue(passwordInputSelector, "123456")
    //     .pause(1000)
    //     .click(loginButton)
    //     .waitForElementVisible(".nav-link")
    //     .assert.containsText(".nav-link", "Home")
    //     .pause(3000)
    // },
    // "2. Login with valid username but wrong password and check for error message": browser => {
    //     const usernameInputSelector = 'input[name="username"]';
    //     const passwordInputSelector = 'input[name="password"]';
    //     const loginButton = '.btn-primary[name="Login"]';

    //     browser.url("http://localhost:8080")
    //     .setValue(usernameInputSelector, "test")
    //     .pause(1000)
    //     .setValue(passwordInputSelector, "wrongpw")
    //     .pause(1000)
    //     .click(loginButton)
    //     .waitForElementVisible(".alert")
    //     .assert.containsText(".alert", "Authentication Failed!")
    //     .pause(3000)
    // },
    // "3. Go to home page url without logging in and check for error message": browser => {
    //     browser.url("http://localhost:8080/projects")
    //     .waitForElementVisible(".error-code")
    //     .assert.containsText(".error-code", "HTTP ERROR 401")
    //     .pause(2000)
    // },
    // "4. Login with valid credentials, then create a duplicate project": browser => {
    //     const usernameInputSelector = 'input[name="username"]';
    //     const passwordInputSelector = 'input[name="password"]';
    //     const loginButton = '.btn-primary[name="Login"]';
    //     const addButton = '.operate';
        

    //     browser.url("http://localhost:8080")
    //     .setValue(usernameInputSelector, "test")
    //     .pause(1000)
    //     .setValue(passwordInputSelector, "123456")
    //     .pause(1000)
    //     .click(loginButton)
    //     .click(addButton)
    //     .pause(3000)
    //     .setAlertText("project1")
    //     .acceptAlert()
    //     .pause(5000)
    //     .assert.elementsCount(projectSelector())
        
        

    // },
    // "5. Sign up a new user with username: newUser and password: 123456": browser => {
        
    //     const usernameInputSelector = 'input[name="username"]';
    //     const passwordInputSelector = 'input[name="password"]';
    //     const signUpButton = '.btn-primary';
    //     const username = "newUser" + (Math.floor(Date.now()/60000)).toString()
        
    //     browser.url("http://localhost:8080/register")
    //     .setValue(usernameInputSelector, username)
    //     .pause(1000)
    //     .setValue(passwordInputSelector, "123456")
    //     .pause(1000)
    //     .waitForElementVisible(signUpButton)
    //     .click(signUpButton)
    //     .pause(3000)
    //     .waitForElementVisible(".alert")
    //     .assert.containsText(".alert", "User registered successfully!")
    // },
    "6. Create new unique project and check if its displayed": browser => {
        const usernameInputSelector = 'input[name="username"]';
        const passwordInputSelector = 'input[name="password"]';
        const loginButton = '.btn-primary[name="Login"]';
        const addButton = '.operate';
        const landingDiv = '#landing'
        const timeStamp = Math.floor(Date.now() / 1000)
        let projectName = "project" + timeStamp.toString()
        //const projectButton = 'h3[name="' + projectName + '"]'

        browser.url("http://localhost:8080")
        .setValue(usernameInputSelector, "test")
        .pause(1000)
        .setValue(passwordInputSelector, "123456")
        .pause(1000)
        .click(loginButton)
        .assert.not.elementPresent(projectSelector(projectName))
        .click(addButton)
        .pause(3000)
        .setAlertText(projectName)
        .acceptAlert()
        .pause(3000)
        .acceptAlert()
        .waitForElementVisible(landingDiv)
        .assert.elementPresent(landingDiv)
        .assert.elementsCount(projectSelector(projectName), 1)
    }
}