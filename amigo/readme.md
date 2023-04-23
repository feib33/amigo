# AmiGo App

This is the repository for team Argentina's app for COMP30022 - IT Project Semester 2 2018

## Notes on Source Code

The app source code is split roughly 50/50 into Java and Kotlin files. 

The Carer and Auth sections are in Kotlin.
Assisted Person and Shared sections are mostly authored in Java.

## File Structure

Layout files can be found at [app/src/main/res/layout](https://github.com/COMP30022-18/ARGENTINA_app/tree/master/app/src/main/res/layout)

Additional drawables are found at [app/src/main/res/drawable](https://github.com/COMP30022-18/ARGENTINA_app/tree/master/app/src/main/res/drawable)

Logic files are found at [app/src/main/res/java/amigo/app](https://github.com/COMP30022-18/ARGENTINA_app/tree/master/app/src/main/java/amigo/app)
- Auth
    - [app/src/main/res/java/amigo/app/auth](https://github.com/COMP30022-18/ARGENTINA_app/tree/master/app/src/main/java/amigo/app/auth)
- Assisted Person
    - [app/src/main/res/java/amigo/ap/assisted](https://github.com/COMP30022-18/ARGENTINA_app/tree/master/app/src/main/java/amigo/app/assisted)
- Carer
    - [app/src/main/res/java/amigo/ap/carer](https://github.com/COMP30022-18/ARGENTINA_app/tree/master/app/src/main/java/amigo/app/carer)

## Testing

The repo does not contain any unit tests or regression tests. We were unable to build any comprehensive tests during development as much of our time was spent developing features.

Our method for testing releases was to build the app on a local branch and run through all features of the app each time to ensure functionality of existing features hadn't been disrupted.