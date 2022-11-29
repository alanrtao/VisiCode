// VisiCode e2e (blackbox) testing suite
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
    "4. Login with valid credentials, then create a duplicate project": browser => {
        const usernameInputSelector = 'input[name="username"]';
        const passwordInputSelector = 'input[name="password"]';
        const loginButton = '.btn-primary[name="Login"]';
        const addButton = '.operate';
        

        browser.url("http://localhost:8080")
        .setValue(usernameInputSelector, "test")
        .pause(1000)
        .setValue(passwordInputSelector, "123456")
        .pause(1000)
        .click(loginButton)
        .click(addButton)
        // .getAlertText((results) => {
        //     console.log("get alert")
        // })
        // .setAlertText("bob")
        .windowHandles((result) => {
            let handle = result.value[0]
            console.log(handle)
            browser.switchToWindow(handle)
            .source((result) => {
                console.log(result.value)
            })
        })
        // .source((result) => {
        //     console.log(result.value)
        // })
        .pause(15000)
        .keys(browser.Keys.ENTER)
        

    }
    // "5. Sign up a new user with username: newUser and password: 123456": browser => {
        
    //     const usernameInputSelector = 'input[name="username"]';
    //     const passwordInputSelector = 'input[name="password"]';
    //     const signUpButton = '.btn-primary[name="Sign Up"]';
        
    //     browser.url("http://localhost:8080/register")
    //     .setValue(usernameInputSelector, "newUser")
    //     .pause(1000)
    //     .setValue(passwordInputSelector, "123456")
    //     .pause(1000)
    //     .click(signUpButton)
    //     .waitForElementVisible(".alert")
    //     .assert.containsText(".alert", "Authentication Failed!")
    // },
    // "6. Create new unique project and check if its displayed": browser => {
    //     const usernameInputSelector = 'input[name="username"]';
    //     const passwordInputSelector = 'input[name="password"]';
    //     const loginButton = '.btn-primary[name="Login"]';
    //     const addButton = '.operate';
    //     const timeStamp = Math.floor(Date.now() / 60000)

    //     browser.url("http://localhost:8080")
    //     .setValue(usernameInputSelector, "test")
    //     .pause(1000)
    //     .setValue(passwordInputSelector, "123456")
    //     .pause(1000)
    //     .click(loginButton)
    //     .click(addButton)
    //     .setAlertText(timeStamp.toString())
    //     .keys(browser.Keys.ENTER)
    //     .url(response => {
    //         browser.assert.equal(response.statusCode, 200)
    //     }
    //     )
    // }
}