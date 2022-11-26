# inspectus

## Problem to solve

### The term "Materials"

When you run your automated UI tests, you are likely to create many files and want to utilize them for various inspections.

For example, Selenium WebDriver API enables you to

-   take screenshots of web pages displayed in web browser by a Selenium-builtin method [org.openqa.selenium.TakesScreenshot#getScreenshotAs(…​)](https://www.guru99.com/take-screenshot-selenium-webdriver.html). There are several other tools to do screen shooting: e.g, [AShot](https://testingchief.com/automated-visual-testing-with-ashot/)

-   get HTML source of web pages by a Selenium-builtin [org.openqa.selenium.Webdriver#getPageSource()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#getPageSource())

Also you sometimes would want to download files of various format: JSON, XML, CSV, Excel, PDF out of RESTful/SOAP services using [Apache HTTP Client API](https://www.baeldung.com/httpclient-guide).

I will refer to these files created by your automated UI tests as **"materials"** collectively.

### Materials use-cases

I would the materials as follows.

#### (1) I want lists of page screenshot of my web system

It is just nice to look at. It is useful for various administrative reporting purposes.

#### (2) I want to compare the page screenshots taken before and after the system change action

Few times a month, I work for changing my web systems. I want to make very sure that I did not make any stupid mistakes in the work. So, I want to take hundreds of screenshots BEFORE the change; and a few hours later I want to take the same number of screenshots AFTER the change. I want to compare the hundreds of screenshot pairs and make sure there aren’t any unexpected differences in the images. If any unexpected/significant image difference found, I will quickly bring the system back to the previous status, then I will study what I have done wrong. If no difference found, Yah!, I will be relieved and go home.

#### (3) I want to compare Development environment against Production environment

We, web developers, want to make our web system to be quality software. But comprehensive and strict testings cost us much. Quite often I can only do the poor man’s test method: just look at the pages of the development environment and try to find any difference from the production environment which is now on live.

Also, sometimes, looking at the pages is the only method of UI testing. For example, let me assume that my web application depends of dozens of external open source software libraries. Now I need to upgrade one of these external libs. I hope the upgrade would not change my system at all, but who knows? The only thing I can do is to try upgrading in the development environment, and compare as many pages as possible (several hundreds) with the counter-pages in the production environment.

Visual testing could be the poor man’s last and only resort.

When I planed to carry out comparing massive screenshot images, soon I realized it is a difficult task. Why, then?

## How I manage materials?

Aforementioned famous APIs certainly help you get files out of the UAT (Application Under Test). But **these libraries do not help you manage the files created by your tests**.

Where should your test store files? How should it name the folders and the file? How should it record the **metadata** ? How should it create a report where downloaded files are listed with links to retrieve them easily? All these tedious tasks are your job! You need to re-invent wheels everytime you initiate testing projects.

## metadata of materials

What do I mean "metadata" of downloaded files? Let me explain it by example.

First, in browser, my test will open a page \[<https://www.duckduckgo.com/>\](<https://www.duckduckgo.com/>). It will type a keyword "selenium". It will take a screenshot of the page. It will save the image into a local file `~/tmp/ddg1.png`. The PNG image will look something like \[this\](). Secondly, in the search page I typed SEND. Then the browser moved to a search result page \[<https://duckduckgo.com/?q=selenium>\](<https://duckduckgo.com/?q=selenium>). I took a screenshot and saved the image into a local file `~/tmp/ddg2.ng`. The PNG image will look something like \[this\]().

Yes. I will get 2 filesIt will create 2 files.

-   `~/tmp/google1.png`

-   `~/tmp/google2.png`