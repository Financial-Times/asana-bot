# AsanaBot 

A [bot](http://en.wikipedia.org/wiki/Internet_bot) for use with [Asana](http://asana.com) so when tasks are 
assigned to particular users it add those tasks to a corresponding project and a comment to the origional task
when done.

* GraphicsBot -> when assigned adds tasks to the Graphics project
* PicturesBot -> when assigned adds tasks to the Pictures project

## Setup

Two environment variables must be set in order for the bot to be able to communicate with:

* ASANA_GRAPHICS_KEY
* ASANA_PICTURES_KEY

These need to be set with the corresponding [api](https://asana.com/guide/help/api/api) key of the user which the tasks will be moved as. 
This user needs to have access to projects where tasks are being assigned and also the projects where tasks are being added. 

on linux/mac these can be set with the cmds:

`export ASANA_GRAPHICS_KEY=[API KEY]`

## Running locally

This project is built on spring boot and to run locally just run the command:

`mvn spring-boot:run`

## Deploying to Heroku and using CircleCI

Two config files are included:

* Procfile for [Heroku](http://docs.spring.io/spring-boot/docs/current/reference/html/cloud-deployment-heroku.html) deployment.
* circle.yml for [CircleCI](http://circleci.com) continous integration/deployment configuration. 




