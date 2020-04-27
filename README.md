## What is this?

This is a quick and dirty piece of code to (try to) download all of the [SpringerOpen](https://www.springeropen.com/) books available.

## ...but why Java!? Surely you could have used curl or wget?

I tried, believe me... but the Springer website has something weird going on and I couldn't download the files using Bash at all.

If somebody who knows more figures it out, LMK. I'm curious about what the deal with that was.

## Set-up instructions

1. Download the latest [Selenium Gecko Driver](https://github.com/mozilla/geckodriver/releases) for your operating system.
2. Download the Selenium Java libraries.
3. Set up the project on your favorite IDE with the libraries above.
4. Download Firefox, or change the project to use your browser of choice.
4. Update the class with all your own settings for folders and the location of the Gecko driver.
5. Let it run!

## Known issues

* Selenium keeps opening a new window for each download. No idea why. I just made it close the driver after every page to balance clutter vs performance.
* Sometimes downloads get stuck with no progress. I added a time-out so it doesn't get stuck. Adjust the time to your personal preference.
* This is janky as hell. I know. I'm not gonna make it prettier or easier to use.
