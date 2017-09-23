# Chatty 

![Build Status](https://travis-ci.org/Dragas/Chatty.svg?branch=master)  

Elegant Java/Kotlin chat bot framework.

### How does it look?

```kotlin

class MockController() : Controller<MockRequest, MockResponse>()
{
    @On(MockEvent::class) // marks the method as callback and tests if request is or has a super class of the argument
    @When("mockTest") // marks which method is used to test the request
    @Description("A mock method brought to you by readme.md") // a description [shrug]
    fun mockResponseGenerator(mockRequest: MockRequest)
    {
        writeResponse(MockResponse(mockRequest.fieldValue, mockRequest.fieldName))
    }

    fun mockTest(mockRequest: MockRequest): Boolean
    {
        return true
    }
}
```

## Usage

As of 4.0.0 Chatty uses event queue based APIs so all you need to do create your own event set and listen to them.
To add your own protocol listener you should just implement the `ConnectionController` abstraction and provide it
while creating your Client.

## Downloading 

Make sure you have sonatype as one of your checked repositories
```groovy
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/releases" }
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
}
```
And then just list `chatty-core`/`chatty-websocket` as your dependency:
```groovy
dependencies {
    compile "lt.saltyjuice.dragas:chatty-core:4.0.1-SNAPSHOT"
}
```

There's also discord API implementation that's currently WIP, but is stable enough to be used
and is implemented by [Jeeves, the Tavern Keeper](https://github.com/Dragas/Biscord). You can download it
from sonatype snapshot repository
```groovy
dependencies{
    compile "lt.saltyjuice.dragas:chatty-discord:0.4.1-SNAPSHOT"
}

```
