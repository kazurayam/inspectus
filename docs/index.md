# inspectus

@author kazurayam

The "inspectus" is a Java library that enables you to carry out what I call "Visual Inspection" using Selenium and other Web UI test automation tools.

## Problem to solve

### The term "Materials"

When you run your automated Web UI tests on top of Selenium, you are likely to create many files and want to utilize them for various inspections.

For example, Selenium WebDriver API enables you to take screenshots of web pages displayed in web browser by a Selenium-builtin method [org.openqa.selenium.TakesScreenshot#getScreenshotAs(…​)](https://www.guru99.com/take-screenshot-selenium-webdriver.html). There are several other tools to do screen shooting: e.g, [AShot](https://testingchief.com/automated-visual-testing-with-ashot/). You can also get the HTML source of web pages by a Selenium-builtin [org.openqa.selenium.Webdriver#getPageSource()](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#getPageSource()). It is quite likely the case that you want to download files of various format: JSON, XML, CSV, Excel, PDF out of RESTful/SOAP services using [Apache HTTP Client API](https://www.baeldung.com/httpclient-guide).

I will refer to these files created by your automated UI tests as **"materials"** collectively.

### Use cases of materials

I would utilize the materials as follows.

#### (1) I want lists of page screenshot of my web system

It is just nice to look at. It is useful for various administrative reporting purposes.

#### (2) I want to compare the page screenshots taken before and after a system change action

Few times a month, I work for changing my web systems. I want to make very sure that I did not make any stupid mistakes in the work. So, I want to take hundreds of screenshots of web pages of the system BEFORE the change; and a few hours later, I want to take the same number of screenshots AFTER the change. Then I want to compare the hundreds of screenshot pairs and make sure there aren’t any differences in the images. If any significant image difference found, I will quickly bring the system back to the previous status. Later next day I will study what I have done wrong. If no image difference found, Yah!, I would be relieved and go home.

#### (3) I want to compare Development environment against Production environment

We, web developers, want to make our web system to be quality software. But comprehensive and strict E2E testings cost us very much. Quite often I can only do the poor man’s test method: just look at the pages of the development environment and try to find any difference from the production environment which is now on live. Also, looking at the pages could be the only method of UI testing sometimes. For example, let me assume that my web application depends of dozens of external open source software libraries. Now I need to upgrade one of these external libs. I hope the upgrade would not change my system at all, but who knows? The only thing I can do is to try upgrading in the development environment, and compare as many pages as possible (several hundreds) with the counter-pages in the production environment.

### So, how can I manage massive materials?

Visual testing could be the poor man’s last and only resort. When I plan to carry out comparing some hundreds of screenshot images, soon I realized it is a difficult task. Why?

Aforementioned famous APIs certainly help you get files out of the UAT (Application Under Test). But **these famous libraries do not help you manage the files created by your tests**. By "managing the materials", I mean the following issues:

-   Where should your test store files?

-   How should it name the folders and the file?

-   How should it record the **metadata** ?

-   How should it create a report where downloaded files are listed with links to retrieve them easily?

All these tedious tasks are your job! You need to invent wheels everytime you initiate automated Web UI testing projects.

### What is "metadata" of material

What do I mean by "metadata" of materials? --- A typical metadata of a screenshot image file in PNG format is the URL of the web page. Let me explain this by example.

1.  My script opens a page [<https://www.duckduckgo.com/>](ttps://www.duckduckgo.com/)

2.  The script types a search keyword "selenium" into the `<input>` element.

3.  The script takes a screenshot of the page.

4.  I need to create a folder where I save the file. So I make a directory `~/tmp`. I need to name a file for the screenshot. So I name it as `01_duckduckgo.png`.

5.  The new `~/tmp/01_duckduckgo.png` file will look like this. ![01 duckduckgo](images/01_duckduckgo.png)

6.  The script created the image file and finished.

7.  **Now, I ran another program. The 2nd programs tries to consume the image file. It wants to print the URL string from which the screenshot was taken.** But how?

8.  No. It can not do it. The 2nd program can not get the URL string out of the created file `~/tmp/01_duckduckgo.png`. A PNG file is just an image. It can contain no metadata such as a URL. The file system of OS is not enough to associate metadata to the materials.

### So what I have developed …​

I realized that I need to invent a domain specific file system which enables me to associate rich set of metadata with the materials.

Should I use some SQL Databases for it? --- well, I do not like that idea; Just because I am not skilled enough about it.

Instead I invented a domain-specific Object-oriented file system for the materials. I named it [`materialstore`](https://github.com/kazurayam/materialstore).
